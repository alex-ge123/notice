package com.wafersystems.notice.mail.service.impl;

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
import com.wafersystems.virsical.common.core.constant.UpmsMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgActionEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.entity.SysTenant;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
      throw new RuntimeException("未查询到id为["+mailTemplateDto.getId()+"]的邮件模板");

    }
  }

  @Override
  public PaginationDto<MailTemplateSearchListDto> getTemp(Long id, String category, String name, Integer pageSize, Integer startIndex) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplateSearchListDto.class);
    if (null != id) {
      criteria.add(Restrictions.eq("id", id));
    }
    if (null != name) {
      criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
    }
    if (null != category) {
      criteria.add(Restrictions.eq("category", category));
    }
    criteria.addOrder(Order.desc("id"));
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
}
