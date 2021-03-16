package com.wafersystems.notice.receiver;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.RabbitMqConfig;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.notice.service.MailNoticeService;
import com.wafersystems.notice.util.SmsUtil;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
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

  @Autowired
  private SmsUtil smsUtil;

  @Autowired
  private StringEncryptor stringEncryptor;

  @Autowired
  private GlobalParamService globalParamService;

  @Autowired
  private TaskExecutor taskExecutor;

  /**
   * 监听邮件消息队列
   *
   * @param message 消息
   */
  @RabbitListener(queues = RabbitMqConfig.QUEUE_NOTICE_MAIL)
  public void mail(@Payload String message) {
    taskExecutor.execute(() -> {
      try {
        log.info("【{}监听到邮件消息】{}", RabbitMqConfig.QUEUE_NOTICE_MAIL, stringEncryptor.encrypt(message));
        if (!ParamConstant.isEmailSwitch()) {
          // 系统刚启动，消费到消息，未检测到参数时，等待3秒，待参数初始化
          Thread.sleep(3000);
          if (!ParamConstant.isEmailSwitch()) {
            log.warn("邮件服务参数未配置,忽略邮件发送!");
            return;
          }
        }

        MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);
        // 业务追踪id
        MDC.put(CommonConstants.LOG_BIZ_ID, messageDTO.getBizId());
        log.debug("监听到邮件消息，MsgId:{}", messageDTO.getMsgId());
        if (MsgTypeEnum.ONE.name().equals(messageDTO.getMsgType())) {
          MailDTO mailDTO = JSON.parseObject(messageDTO.getData().toString(), MailDTO.class);
          Locale locale = ParamConstant.getLocaleByStr(mailDTO.getLang());

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
          mailDTO.setLocale(cn.hutool.core.util.StrUtil.isBlank(mailDTO.getLang()) ? null : locale);
          mailDTO.setResource(resource);
          mailNoticeService.sendMail(MailBean.builder()
            .uuid(mailDTO.getUuid())
            .routerKey(mailDTO.getRouterKey())
            .subject(mailDTO.getSubject())
            .toEmails(mailDTO.getToMail())
            .copyTo(mailDTO.getCopyTo())
            .type(ConfConstant.TypeEnum.FM)
            .template(mailDTO.getTempName())
            .mailDTO(mailDTO)
            .build(), 0, globalParamService.getMailServerConf(mailDTO.getTenantId()));
          log.debug("邮件消息处理完成，MsgId:{}", messageDTO.getMsgId());
        } else {
          log.warn("消息类型未识别，无法发送邮件");
        }
      } catch (Exception e) {
        log.error("消息监听处理异常", e);
      }
    });
  }

  /**
   * 监听短信消息队列
   *
   * @param message 消息
   */
  @RabbitListener(queues = RabbitMqConfig.QUEUE_NOTICE_SMS)
  public void sms(@Payload String message) {
    taskExecutor.execute(() -> {
      try {
        log.info("【{}监听到短信消息】{}", RabbitMqConfig.QUEUE_NOTICE_SMS, stringEncryptor.encrypt(message));
        if (!ParamConstant.isSmsSwitch()) {
          // 系统刚启动，消费到消息，未检测到参数时，等待3秒，待参数初始化
          Thread.sleep(3000);
          if (!ParamConstant.isSmsSwitch()) {
            log.warn("未配置短信服务调用地址，忽略短信发送！");
            return;
          }
        }

        MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);
        // 业务追踪id
        MDC.put(CommonConstants.LOG_BIZ_ID, messageDTO.getBizId());
        log.debug("监听到短信消息，msgId:{}", messageDTO.getMsgId());
        if (MsgTypeEnum.ONE.name().equals(messageDTO.getMsgType())) {
          SmsDTO smsDTO = JSON.parseObject(messageDTO.getData().toString(), SmsDTO.class);
          smsUtil.batchSendSms(smsDTO.getTemplateId(), smsDTO.getPhoneList(), smsDTO.getParamList(),
            smsDTO.getDomain(), smsDTO.getSmsSign());
        } else if (MsgTypeEnum.BATCH.name().equals(messageDTO.getMsgType())) {
          final List<SmsDTO> dtoList = JSON.parseArray(messageDTO.getData().toString(), SmsDTO.class);
          dtoList.forEach(smsDTO ->
            smsUtil.batchSendSms(smsDTO.getTemplateId(), smsDTO.getPhoneList(),
              smsDTO.getParamList(), smsDTO.getDomain(), smsDTO.getSmsSign())
          );
        } else {
          log.warn("消息类型未识别，无法发送短信");
        }
        log.debug("短信消息处理完成，msgId:{}", messageDTO.getMsgId());
      } catch (Exception e) {
        log.error("消息监听处理异常", e);
      }
    });
  }
}