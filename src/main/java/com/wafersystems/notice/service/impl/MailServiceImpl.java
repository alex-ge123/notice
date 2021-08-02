package com.wafersystems.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wafersystems.notice.config.AsyncTaskManager;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.constants.AlertConstants;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.entity.MailTemplate;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.notice.manager.email.AbstractEmailManager;
import com.wafersystems.notice.mapper.MailTemplateMapper;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.PaginationDTO;
import com.wafersystems.notice.model.TemplateStateUpdateDTO;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.notice.service.MailService;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.config.AesKeyProperties;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.SecurityConstants;
import com.wafersystems.virsical.common.core.constant.SysDictConstants;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.AlertDTO;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.entity.TenantDTO;
import com.wafersystems.virsical.common.feign.RemoteTenantService;
import com.wafersystems.virsical.common.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service
public class MailServiceImpl extends ServiceImpl<MailTemplateMapper, MailTemplate> implements MailService {

  @Autowired
  private RemoteTenantService tenantService;

  @Autowired
  private AsyncTaskManager asyncTaskManager;

  @Autowired
  private AesKeyProperties aesKeyProperties;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private GlobalParamService globalParamService;

  @Autowired
  private SendIntercept sendIntercept;

  @Autowired
  private SendInterceptProperties properties;

  @Autowired
  private Map<String, AbstractEmailManager> emailManagerMap;

  @Autowired
  private IAlertRecordService recordService;

  /**
   * 邮件发送
   *
   * @param mailBean       邮件填充内容
   * @param count          邮件重发次数
   * @param mailServerConf 邮件服务器配置参数
   * @throws Exception Exception
   */
  @Override
  public void send(MailBean mailBean, Integer count, MailServerConf mailServerConf) throws Exception {
    log.debug("开始发送邮件。");
    if (0 == count) {
      //邮箱解密
      mailBean.setToEmails(this.mailsDecrypt(mailBean.getToEmails()));
      mailBean.setCopyTo(this.mailsDecrypt(mailBean.getCopyTo()));
      //填充租户信息
      mailBean = this.fillTenantInfo(mailBean);
    }

    // 校验邮件状态
    final MailTemplate template = this.getTempByName(mailBean.getTemplate());
    if (ObjectUtil.isNotNull(template) && ObjectUtil.equal(template.getState(), 1)) {
      log.warn("邮件模板[{}]禁用，邮件[{}]不发送！", mailBean.getTemplate(), mailBean.getSubject());
      return;
    }
    //重复发送拦截
    if (sendIntercept.mailBoolIntercept(mailBean)) {
      log.error("拦截重复发送邮件[{}]", mailBean.toString());
      return;
    }

    // 传递副本，防止send方法中对mailBean修改，导致hashcode改变，影响重复邮件拦截
    final MailBean sendMailBean = ObjectUtil.cloneByStream(mailBean);
    AbstractEmailManager emailManager = emailManagerMap.get(mailServerConf.getServerType());
    // 发送邮件
    try {
      log.debug("用户使用{}发送邮件", mailServerConf.getServerType());
      emailManager.send(sendMailBean, mailServerConf);
      //记录（拦截重复发送用）
      String redisKey = String.format(RedisKeyConstants.MAIL_KEY,
        mailBean.getToEmails(), mailBean.getTemplate(), mailBean.getSubject(), mailBean.hashCode());
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(mailBean), properties.getMailTimeHorizon(), TimeUnit.MINUTES);
      //发送 发送结果(成功)
      emailManager.sendResult(mailBean.getUuid(), mailBean.getRouterKey(), true);
    } catch (Exception exception) {
      count++;
      if (count < ParamConstant.getDefaultRepeatCount()) {
        log.debug("主题[" + mailBean.getSubject() + "],发往[" + mailBean.getToEmails() + "]的邮件第" + count + "次重发......");
        this.send(mailBean, count, mailServerConf);
      } else {
        log.error("邮件发送失败：", exception);
        //发送 发送结果(失败)
        emailManager.sendResult(mailBean.getUuid(), mailBean.getRouterKey(), false);
        // 失败处理
        failProcessor(mailBean, exception);
        throw exception;
      }
    }
  }

  private void failProcessor(MailBean mailBean, Exception exception) {
    //记录失败邮件信息
    final String mailBeanJSONStr = JSON.toJSONString(mailBean);
    String id = "MAIL-FAIL-" + System.currentTimeMillis();
    redisTemplate.opsForHash().put(RedisKeyConstants.MAIL_FAIL_KEY, id, mailBeanJSONStr);
    //发送站内通知系统运维人员
    final AlertDTO alertDTO = new AlertDTO();
    alertDTO.setAlertType(AlertConstants.LOCAL.getType());
    alertDTO.setTenantId(ObjectUtil.isNull(TenantContextHolder.getTenantId()) ? 0 : TenantContextHolder.getTenantId());
    alertDTO.setAlertId(id);
    alertDTO.setProduct(ProductCodeEnum.COMMON.getCode());
    alertDTO.setTitle("邮件[" + mailBean.getSubject() + "]发送失败！");
    alertDTO.setContent("邮件[" + mailBean.getSubject() + "]发送失败！,失败原因[" + exception.getMessage() + "]");
    recordService.processAlertMessage(alertDTO);
  }

  @Override
  public void saveTemp(MailTemplate mailTemplate) {
    MailTemplate dto = getTempByName(mailTemplate.getName());
    if (null == dto) {
      this.save(mailTemplate);
      sendLog(mailTemplate.getName(), "模板:[" + mailTemplate.getName() + "]新增。");
    } else {
      dto.setDescription(mailTemplate.getDescription());
      dto.setCategory(mailTemplate.getCategory());
      dto.setContent(mailTemplate.getContent());
      dto.setModtime(null);
      this.updateById(dto);
      sendLog(mailTemplate.getName(), "模板:[" + mailTemplate.getName() + "]更新。");
    }
    log.debug("新增/修改{}模板成功！", mailTemplate.getName());
  }

  @Override
  public void updateTemp(MailTemplate mailTemplate) {
    MailTemplate dto = getTempById(mailTemplate.getId());
    if (null != dto) {
      if (!StrUtil.isEmptyStr(mailTemplate.getName())) {
        dto.setName(mailTemplate.getName());
      }
      if (!StrUtil.isEmptyStr(mailTemplate.getDescription())) {
        dto.setDescription(mailTemplate.getDescription());
      }
      if (!StrUtil.isEmptyStr(mailTemplate.getCategory())) {
        dto.setCategory(mailTemplate.getCategory());
      }
      if (!StrUtil.isEmptyStr(mailTemplate.getContent())) {
        dto.setContent(mailTemplate.getContent());
      }
      dto.setModtime(null);
      this.updateById(dto);
      sendLog(mailTemplate.getName(), "模板:[" + dto.getName() + "]更新。");
      log.debug("修改{}模板成功！", mailTemplate.getName());
    } else {
      throw new RuntimeException("未查询到id为[" + mailTemplate.getId() + "]的邮件模板");

    }
  }

  @Override
  public PaginationDTO<MailTemplate> getTemp(Long id, String category, String name, Integer pageSize, Integer startIndex) {
    final LambdaQueryWrapper<MailTemplate> query = new LambdaQueryWrapper<>();
    query.setEntity(new MailTemplate());
    query.select(i -> !"content".equals(i.getColumn()));
    if (null != id) {
      query.eq(MailTemplate::getId, id);
    }
    if (null != name) {
      query.like(MailTemplate::getName, "%" + name.trim() + "%");
    }
    if (null != category) {
      query.eq(MailTemplate::getCategory, category.trim());
    }
    query.orderByDesc(MailTemplate::getModtime);
    query.orderByAsc(MailTemplate::getId);
    final Page<MailTemplate> page = new Page<>();
    page.setCurrent(startIndex);
    page.setSize(pageSize);
    final Page<MailTemplate> result = this.page(page, query);
    final PaginationDTO<MailTemplate> resultDto = new PaginationDTO<>();
    resultDto.setRows(result.getRecords());
    resultDto.setLimit(Long.valueOf(result.getSize()).intValue());
    resultDto.setPage(Long.valueOf(result.getCurrent()).intValue());
    resultDto.setRecords(Long.valueOf(result.getTotal()).intValue());
    resultDto.setTotal(Long.valueOf(result.getPages()).intValue());
    return resultDto;
  }

  @Override
  public MailTemplate getTempById(Integer id) {
    final LambdaQueryWrapper<MailTemplate> query = new LambdaQueryWrapper<>();
    query.eq(MailTemplate::getId, id);
    List<MailTemplate> list = this.list(query);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public MailTemplate getTempByName(String name) {
    final LambdaQueryWrapper<MailTemplate> query = new LambdaQueryWrapper<>();
    query.eq(MailTemplate::getName, name);
    List<MailTemplate> list = this.list(query);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 记录日志
   *
   * @param name
   */
  private void sendLog(String name, String content) {
    LogDTO logDTO = new LogDTO();
    logDTO.setProductCode(ProductCodeEnum.COMMON.getCode());
    logDTO.setTitle("邮件模板更新");
    logDTO.setContent(content);
    logDTO.setType("template-update");
    logDTO.setResult(CommonConstants.SUCCESS);
    logDTO.setUserId(TenantContextHolder.getUserId());
    logDTO.setObjectId(name);
    logDTO.setTenantId(TenantContextHolder.getTenantId());
    logDTO.setUsername(TenantContextHolder.getUsername());
    asyncTaskManager.asyncSendLogMessage(logDTO);
  }

  @Override
  public MailBean fillTenantInfo(MailBean mailBean) {
    //填充系统默认参数
    MailDTO mailDTO = mailBean.getMailDTO();
    mailDTO.setLogo(StrUtil.isEmptyStr(mailDTO.getLogo()) ? ParamConstant.getLogoDefault() : mailDTO.getLogo());
    mailDTO.setSystemName(ParamConstant.getSystemName());
    if (ObjectUtil.isNotNull(mailDTO.getTenantId())) {
      R<TenantDTO> tenantByIdForInner = tenantService.getTenantByIdForInner(mailDTO.getTenantId(), SecurityConstants.FROM_IN);
      if (ObjectUtil.isNotNull(tenantByIdForInner)) {
        TenantDTO tenant = tenantByIdForInner.getData();
        //设置租户logo
        if (!StrUtil.isEmptyStr(tenant.getLogo())) {
          String logo = tenant.getLogo();
          if (!cn.hutool.core.util.StrUtil.startWith(logo, "http")) {
            try {
              String domain = (String) redisTemplate.opsForHash().get(
                CommonConstants.SYS_DICT + SysDictConstants.DOMAIN_TYPE, SysDictConstants.DOMAIN_TYPE);
              if (cn.hutool.core.util.StrUtil.isNotBlank(domain)) {
                logo = domain + "/" + logo;
              }
            } catch (Exception e) {
              log.warn("缓存中查询用户域信息异常！", e);
            }
          }
          mailDTO.setLogo(logo);
        }
        //设置租户系统名
        if (!StrUtil.isEmptyStr(tenant.getName())) {
          mailDTO.setSystemName(tenant.getName());
        }
        //设置租户电话号
        if (!StrUtil.isEmptyStr(tenant.getContactNumber())) {
          mailDTO.setPhone(tenant.getContactNumber());
        }
        //设置环境编码
        if (ObjectUtil.isNull(mailDTO.getLocale()) && !StrUtil.isEmptyStr(tenant.getLang())) {
          mailDTO.setLocale(ParamConstant.getLocaleByStr(tenant.getLang()));
        }
      }
    }
    if (ObjectUtil.isNull(mailDTO.getLocale())) {
      mailDTO.setLocale(ParamConstant.getLocaleByStr("zh_CN"));
    }
    mailBean.setMailDTO(mailDTO);
    log.debug("发送邮件logo地址为{}，系统名称为{}，电话号码为{}" + mailDTO.getLogo(), mailDTO.getSystemName(), mailDTO.getPhone());
    return mailBean;
  }

  @Override
  public boolean updateTempState(TemplateStateUpdateDTO dto) {
    final LambdaUpdateWrapper<MailTemplate> update = new LambdaUpdateWrapper<>();
    update.set(MailTemplate::getState, dto.getState());
    update.ne(MailTemplate::getCategory, "common");
    if (dto.isUpdateAll()) {
      //全部修改
      this.update(update);
      sendLog("all", "修改全部邮件模板状态为：" + dto.getState());
      log.debug("修改邮件模板状态：{}", dto.getState());
    } else {
      //通过id修改
      update.eq(MailTemplate::getId, dto.getId());
      this.update(update);
      sendLog(dto.getId(), "修改邮件模板状态为：" + dto.getState());
      log.debug("修改邮件模板:{}状态：{}", dto.getId(), dto.getState());
    }
    return true;
  }

  private String mailsDecrypt(String mails) {
    if (cn.hutool.core.util.StrUtil.isNotBlank(mails)) {
      //多个收件人/抄送人
      final String[] split = mails.split(CommonConstants.COMMA);
      if (split.length > 0) {
        return Stream.of(split).map(mail ->
          {
            try {
              //解密
              return AesUtils.decryptAes(mail, aesKeyProperties.getKey());
            } catch (Exception e) {
              //明文，不需要解密
              return mail;
            }
          }
        ).collect(Collectors.joining(CommonConstants.COMMA));
      }
    }
    return mails;
  }

  @Override
  public R check(BaseCheckDTO dto) {
    Integer tenantId;
    if (ObjectUtil.isNull(dto)) {
      tenantId = TenantContextHolder.getTenantId();
    } else {
      tenantId = dto.getTenantId();
    }
    final MailServerConf conf = globalParamService.getMailServerConf(tenantId);
    final AbstractEmailManager emailManager = emailManagerMap.get(conf.getServerType());
    return emailManager.check(dto, tenantId, conf);
  }
}
