package com.wafersystems.notice.receiver;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.RabbitMqConfig;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.SmsUtil;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 消息消费者
 *
 * @author tandk
 * @date 2019/6/11 11:09
 */
@Slf4j
@Component
public class Receiver {

  @Autowired
  private MailNoticeService mailNoticeService;

  @Autowired
  private ApplicationContext resource;

  /**
   * 监听邮件消息队列
   *
   * @param message 消息
   */
  @RabbitListener(queues = RabbitMqConfig.QUEUE_NOTICE_MAIL)
  public void mail(@Payload String message) {
    log.info("【{}监听到邮件消息】{}", RabbitMqConfig.QUEUE_NOTICE_MAIL, message);
    try {
      MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);
      if (MsgTypeEnum.ONE.name().equals(messageDTO.getMsgType())) {
        MailDTO mailDTO = JSON.parseObject(messageDTO.getData().toString(), MailDTO.class);
        log.info("邮件消息：[{}]", mailDTO.toString());

        Locale locale = ParamConstant.getLocaleByStr(mailDTO.getLang());
        if (!ParamConstant.isEMAIL_SWITCH()) {
          log.warn("邮件服务参数未配置，将忽略主题【" + mailDTO.getSubject() + "】的邮件发送");
          return;
        }
        if (StrUtil.isEmptyStr(mailDTO.getSubject())) {
          log.warn("邮件主题不能为空");
          return;
        }
        if (StrUtil.isEmptyStr(mailDTO.getToMail())) {
          log.warn("邮件接收人不能为空");
          return;
        }
        if (StrUtil.isEmptyStr(mailDTO.getTempName())) {
          log.warn("邮件模板名称不能为空");
          return;
        }
        TemContentVal con = new TemContentVal();
        BeanUtils.copyProperties(mailDTO, con);
        con.setLogo(
          StrUtil.isEmptyStr(con.getLogo()) ? ParamConstant.getLOGO_DEFALUT() : con.getLogo());
        log.debug("logo地址为：" + con.getLogo());
        con.setLocale(locale);
        con.setResource(resource);
        con.setImageDirectory(ParamConstant.getIMAGE_DIRECTORY());
        try {
          mailNoticeService.sendMail(StrUtil.regStr(mailDTO.getSubject()),
            mailDTO.getToMail(), mailDTO.getCopyTo(),
            ConfConstant.TypeEnum.VM,
            mailDTO.getTempName().contains(".vm") ? mailDTO.getTempName() : mailDTO.getTempName() + ".vm", con, 0);
        } catch (Exception e) {
          log.error("发送邮件失败：", e);
        }
      } else {
        log.info("消息类型未识别，无法发送邮件");
      }
    } catch (Exception e) {
      log.info("消息监听处理异常", e);
    }
  }

  /**
   * 监听短信消息队列
   *
   * @param message 消息
   */
  @RabbitListener(queues = RabbitMqConfig.QUEUE_NOTICE_SMS)
  public void sms(@Payload String message) {
    log.info("【{}监听到短信消息】{}", RabbitMqConfig.QUEUE_NOTICE_SMS, message);
    try {
      MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);
      SmsDTO smsDTO = JSON.parseObject(messageDTO.getData().toString(), SmsDTO.class);
      log.info("短信消息：[{}]", smsDTO.toString());
      if (!ParamConstant.isSMS_SWITCH()) {
        log.warn("未配置短信服务调用地址！");
        return;
      }
      if (smsDTO.getPhoneList().isEmpty()) {
        log.warn("接收短信的手机号不能为空！");
        return;
      }
      String result;
      if (smsDTO.getPhoneList().size() > 1) {
        for (String phone : smsDTO.getPhoneList()) {
          result = SmsUtil.sendSms(smsDTO.getTemplateId(), phone, smsDTO.getParamList(), smsDTO.getDomain(),
            smsDTO.getSmsSign());
          log.info("电话号码" + phone + "发送短信的结果为：" + result);
        }
      } else if (smsDTO.getPhoneList().size() == 1) {
        result = SmsUtil.sendSms(smsDTO.getTemplateId(), smsDTO.getPhoneList().get(0), smsDTO.getParamList(),
          smsDTO.getDomain(), smsDTO.getSmsSign());
        log.info("电话号码" + smsDTO.getPhoneList().get(0) + "发送短信的结果为：" + result);
      }
    } catch (Exception e) {
      log.info("消息监听处理异常", e);
    }
  }
}