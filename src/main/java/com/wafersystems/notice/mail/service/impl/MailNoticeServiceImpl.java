package com.wafersystems.notice.mail.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.base.dao.BaseDao;
import com.wafersystems.notice.base.model.GlobalParameter;
import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.MailTemplateDto;
import com.wafersystems.notice.mail.model.MailTemplateSearchListDto;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.EmailUtil;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.SecurityConstants;
import com.wafersystems.virsical.common.core.constant.UpmsMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgActionEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.entity.SysTenant;
import com.wafersystems.virsical.common.entity.TenantDTO;
import com.wafersystems.virsical.common.feign.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteTimeoutException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RemoteTenantService tenantService;

  /**
   * Description: 邮件发送 author waferzy DateTime 2016-3-10 下午2:37:55.
   *
   * @param subject 邮件主题
   * @param to      邮件接收方(多个之间用逗号隔开)
   * @param copyTo  邮件抄送(多个之间用逗号隔开)
   * @param type    邮件模版类型(使用VM模版或html格式邮件)
   * @param temple  邮件模版
   * @param con     邮件填充内容
   * @param count   邮件重发次数
   */
  @Override
  public void sendMail(String subject, String to, String copyTo, ConfConstant.TypeEnum type,
                       String temple, TemContentVal con, Integer count) throws Exception {
    log.debug("开始发送邮件。。。。。。");
    // 创建邮件
    MailBean mailBean = new MailBean();
    mailBean.setSubject(subject);
    mailBean.setToEmails(to);
    mailBean.setCopyTo(copyTo);
    mailBean.setType(type);
    mailBean.setTemplate(temple);
    mailBean.setTemVal(con);
    //填充租户信息
    mailBean = this.fillTenantInfo(mailBean);
    // 发送邮件
    try {
      mailUtil.send(mailBean);
    } catch (Exception exception) {
      count++;
      if (count < ParamConstant.getDEFAULT_REPEAT_COUNT()) {
        log.debug("主题[" + subject + "],发往[" + to + "]的邮件第" + count + "次重发......");
        this.sendMail(subject, to, copyTo, type, temple, con, count);
      } else {
        log.error("邮件发送失败：", exception);
        throw exception;
      }
    }
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
      sendLog(mailTemplateDto, "模板:[" + mailTemplateDto.getName() + "]更新。");
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
    PaginationDto<MailTemplateSearchListDto> paginationDto = baseDao.selectPage(criteria, pageSize, startIndex);
    return paginationDto;
  }

  @Override
  public MailTemplateDto getTempById(Long id) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplateDto.class);
    criteria.add(Restrictions.eq("id", id));
    List<MailTemplateDto> list = baseDao.findByCriteria(criteria);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public MailTemplateDto getTempByName(String name) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplateDto.class);
    criteria.add(Restrictions.eq("name", name));
    List<MailTemplateDto> list = baseDao.findByCriteria(criteria);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 记录日志
   *
   * @param mailTemplateDto
   */
  @Async("mqAsync")
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
    rabbitTemplate.convertAndSend(UpmsMqConstants.EXCHANGE_DIRECT_UPMS_LOG, UpmsMqConstants.ROUTINT_KEY_LOG,
      JSON.toJSONString(new MessageDTO(MsgTypeEnum.ONE.name(), MsgActionEnum.ADD.name(), logDTO)));
  }

  @Override
  public MailBean fillTenantInfo(MailBean mailBean) {
    //填充系统默认参数
    TemContentVal val = mailBean.getTemVal();
    val.setLogo(StrUtil.isEmptyStr(val.getLogo()) ? ParamConstant.getLOGO_DEFALUT() : val.getLogo());
    val.setSystemName(ParamConstant.getSYSTEM_NAME());
    val.setPhone(ParamConstant.getPHONE());
//    val.setLocale(ParamConstant.getLocaleByStr("zh_CN"));
    if (ObjectUtil.isNotNull(val.getTenantId())) {
      R<TenantDTO> tenantByIdForInner = tenantService.getTenantByIdForInner(val.getTenantId(), SecurityConstants.FROM_IN);
      if (ObjectUtil.isNotNull(tenantByIdForInner)) {
        TenantDTO tenant = tenantByIdForInner.getData();
        //设置租户logo
        if (!StrUtil.isEmptyStr(tenant.getLogo())) {
          val.setLogo(tenant.getLogo());
        }
        //设置租户系统名
        if (!StrUtil.isEmptyStr(tenant.getSystemName())) {
          val.setSystemName(tenant.getSystemName());
        }
        //设置租户电话号
        if (!StrUtil.isEmptyStr(tenant.getContactNumber())) {
          val.setPhone(tenant.getContactNumber());
        }
        //设置环境编码
        if (ObjectUtil.isNull(val.getLocale()) && !StrUtil.isEmptyStr(tenant.getLang())) {
          val.setLocale(ParamConstant.getLocaleByStr(tenant.getLang()));
        }
      }
    }
    mailBean.setTemVal(val);
    log.debug("发送邮件logo地址为{}，系统名称为{}，电话号码为{}" + val.getLogo(), val.getSystemName(), val.getPhone());
    return mailBean;
  }
}
