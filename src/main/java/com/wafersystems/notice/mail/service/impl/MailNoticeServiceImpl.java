package com.wafersystems.notice.mail.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.base.dao.BaseDao;
import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.config.AsyncTaskManager;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.MailSendLogDto;
import com.wafersystems.notice.mail.model.MailTemplateDto;
import com.wafersystems.notice.mail.model.MailTemplateSearchListDto;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.EmailUtil;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.SecurityConstants;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.entity.TenantDTO;
import com.wafersystems.virsical.common.feign.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service
public class MailNoticeServiceImpl implements MailNoticeService {

  @Autowired
  private BaseDao baseDao;

  @Autowired
  private EmailUtil mailUtil;

  @Autowired
  private RemoteTenantService tenantService;

  @Autowired
  private AsyncTaskManager asyncTaskManager;

  @Autowired
  private StringRedisTemplate redisTemplate;

  /**
   * Description: 邮件发送 author waferzy DateTime 2016-3-10 下午2:37:55.
   *
   * @param mailBean 邮件填充内容
   * @param count    邮件重发次数
   */
  @Override
  public void sendMail(MailBean mailBean, Integer count, String msgId) throws Exception {
    log.info("开始发送邮件 {} {}",mailBean.getToEmails(), mailBean.getSubject());
    //填充租户信息
    mailBean = this.fillTenantInfo(mailBean);
    // 发送邮件
    try {
      int status = mailUtil.send(mailBean);
      saveOrUpdateMailLog(mailBean, status, msgId);
    } catch (Exception exception) {
      count++;
      saveOrUpdateMailLog(mailBean, count, msgId);
      if (count < ParamConstant.getDEFAULT_REPEAT_COUNT()) {
        log.debug("主题[" + mailBean.getSubject() + "],发往[" + mailBean.getToEmails() + "]的邮件第" + count + "次重发......");
        this.sendMail(mailBean, count, msgId);
      } else {
        log.error("邮件发送失败：" + mailBean.getUuid(), exception);
        throw exception;
      }
    }
  }

  private void saveOrUpdateMailLog(MailBean mailBean, int status, String mailKey) {
    if(StrUtil.isEmptyStr(mailKey)){ //使用msgid做为邮件key值，无msgkey使用邮件内容拼接
      mailKey = String.format("%s:%s:%s", mailBean.hashCode(), mailBean.getSubject(),
        mailBean.getToEmails().length() > 50 ? mailBean.getToEmails().substring(0, 50) : mailBean.getToEmails());
    }
    MailSendLogDto logDto = getLogDtoByMailKey(mailKey);
    if (null == logDto) {
      logDto = new MailSendLogDto();
      logDto.setMailKey(mailKey);
      logDto.setMailStr(JSONObject.toJSONString(mailBean));
      logDto.setSendTime(new Date());
      logDto.setStatus(status);
      baseDao.save(logDto);
    } else {
      logDto.setStatus(status);
      baseDao.update(logDto);
    }
  }

  private MailSendLogDto getLogDtoByMailKey(String mailKey) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailSendLogDto.class);
    criteria.add(Restrictions.eq("mailKey", mailKey));
    List<MailSendLogDto> list = baseDao.findByCriteria(criteria);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @EventListener({WebServerInitializedEvent.class})
  protected void initMailSchedule() {
    redisTemplate.delete(RedisKeyConstants.MAIL_SCHEDULED); //删除邮件定时任务锁
    log.info("启动清理邮件发送定时任务缓存完成");
  }

  @Scheduled(cron = "0 0/1 * * * ?")
  @Transactional
  protected void handleInstance() {
    if(redisTemplate.hasKey(RedisKeyConstants.MAIL_SCHEDULED)) {
      return;
    }
    redisTemplate.opsForValue().set(RedisKeyConstants.MAIL_SCHEDULED, "running");
    List<MailSendLogDto> lists = getLogDtoByTime();
    for (MailSendLogDto logDto : lists) {
      MailBean mailBean = JSONObject.parseObject(logDto.getMailStr(), MailBean.class);
      log.info("开始补发邮件 {} {}", mailBean.getToEmails(), mailBean.getSubject());
      try {
        this.sendMail(mailBean, 0, logDto.getMailKey());
      } catch (Exception ex) {
        log.error("邮件定时任务补发异常：{} {} {}", mailBean.getToEmails(), mailBean.getSubject(), ex.getMessage());
      }
    }
    redisTemplate.delete(RedisKeyConstants.MAIL_SCHEDULED);
  }

  private List<MailSendLogDto> getLogDtoByTime() {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailSendLogDto.class);
    criteria.add(Restrictions.gt("status", 0));
    criteria.add(Restrictions.gt("sendTime", DateUtil.offsetMinute(new Date(), -10)));
    criteria.addOrder(Order.asc("sendTime"));
    List<MailSendLogDto> list = baseDao.findByCriteria(criteria);
    return list;
  }

  @Override
  public void saveTemp(MailTemplateDto mailTemplateDto) {
    MailTemplateDto dto = getTempByName(mailTemplateDto.getName());
    if (null == dto) {
      baseDao.save(mailTemplateDto);
      sendLog(mailTemplateDto, "模板:[" + mailTemplateDto.getName() + "]新增。");
    } else {
      dto.setDescription(mailTemplateDto.getDescription());
      dto.setCategory(mailTemplateDto.getCategory());
      dto.setContent(mailTemplateDto.getContent());
      dto.setModtime(null);
      baseDao.update(dto);
      sendLog(mailTemplateDto, "模板:[" + mailTemplateDto.getName() + "]更新。");
    }
    log.debug("新增/修改{}模板成功！", mailTemplateDto.getName());
  }

  @Override
  public void updateTemp(MailTemplateDto mailTemplateDto) {
    MailTemplateDto dto = getTempById(mailTemplateDto.getId());
    if (null != dto) {
      if (!StrUtil.isEmptyStr(mailTemplateDto.getName())) {
        dto.setName(mailTemplateDto.getName());
      }
      if (!StrUtil.isEmptyStr(mailTemplateDto.getDescription())) {
        dto.setDescription(mailTemplateDto.getDescription());
      }
      if (!StrUtil.isEmptyStr(mailTemplateDto.getCategory())) {
        dto.setCategory(mailTemplateDto.getCategory());
      }
      if (!StrUtil.isEmptyStr(mailTemplateDto.getContent())) {
        dto.setContent(mailTemplateDto.getContent());
      }
      dto.setModtime(null);
      baseDao.update(dto);
      sendLog(mailTemplateDto, "模板:[" + dto.getName() + "]更新。");
      log.debug("修改{}模板成功！", mailTemplateDto.getName());
    } else {
      throw new RuntimeException("未查询到id为[" + mailTemplateDto.getId() + "]的邮件模板");

    }
  }

  @Override
  public PaginationDto<MailTemplateSearchListDto> getTemp(Long id, String category, String name, Integer pageSize, Integer startIndex) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplateSearchListDto.class);
    if (null != id) {
      criteria.add(Restrictions.eq("id", id));
    }
    if (null != name) {
      criteria.add(Restrictions.ilike("name", name.trim(), MatchMode.ANYWHERE));
    }
    if (null != category) {
      criteria.add(Restrictions.eq("category", category.trim()));
    }
    criteria.addOrder(Order.desc("modtime"));
    return baseDao.selectPage(criteria, pageSize, startIndex);
  }

  @Override
  public MailTemplateDto getTempById(Long id) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplateDto.class);
    criteria.add(Restrictions.eq("id", id));
    List<MailTemplateDto> list = baseDao.findByCriteria(criteria);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public MailTemplateDto getTempByName(String name) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplateDto.class);
    criteria.add(Restrictions.eq("name", name));
    List<MailTemplateDto> list = baseDao.findByCriteria(criteria);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 记录日志
   *
   * @param mailTemplateDto
   */
  public void sendLog(MailTemplateDto mailTemplateDto, String content) {
    LogDTO logDTO = new LogDTO();
    logDTO.setProductCode(ProductCodeEnum.COMMON.getCode());
    logDTO.setTitle("邮件模板更新");
    logDTO.setContent(content);
    logDTO.setType("template-update");
    logDTO.setResult(CommonConstants.SUCCESS);
    logDTO.setUserId(TenantContextHolder.getUserId());
    logDTO.setObjectId(mailTemplateDto.getName());
    logDTO.setTenantId(TenantContextHolder.getTenantId());
    logDTO.setUsername(TenantContextHolder.getUsername());
    asyncTaskManager.asyncSendLogMessage(logDTO);
  }

  @Override
  public MailBean fillTenantInfo(MailBean mailBean) {
    //填充系统默认参数
    MailDTO mailDTO = mailBean.getMailDTO();
    mailDTO.setLogo(StrUtil.isEmptyStr(mailDTO.getLogo()) ? ParamConstant.getLOGO_DEFALUT() : mailDTO.getLogo());
    mailDTO.setSystemName(ParamConstant.getSYSTEM_NAME());
    if (ObjectUtil.isNotNull(mailDTO.getTenantId())) {
      R<TenantDTO> tenantByIdForInner = tenantService.getTenantByIdForInner(mailDTO.getTenantId(), SecurityConstants.FROM_IN);
      if (ObjectUtil.isNotNull(tenantByIdForInner)) {
        TenantDTO tenant = tenantByIdForInner.getData();
        //设置租户logo
        if (!StrUtil.isEmptyStr(tenant.getLogo())) {
          mailDTO.setLogo(tenant.getLogo());
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
}
