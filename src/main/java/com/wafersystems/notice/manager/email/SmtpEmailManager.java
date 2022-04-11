package com.wafersystems.notice.manager.email;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.enums.MailScheduleStatusEnum;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MailScheduleDto;
import com.wafersystems.virsical.common.core.dto.RecurrenceRuleDTO;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.net.URL;
import java.security.Security;
import java.util.List;
import java.util.Properties;

/**
 * Smtp邮件工具类
 *
 * @author wafer
 */
@Slf4j
@Primary
@Service(MailConstants.MAIL_SERVER_TYPE_SMTP)
public class SmtpEmailManager extends AbstractEmailManager {

  /**
   * 发送邮件
   *
   * @param mailBean 邮件对象
   * @throws Exception Exception
   */
  @Override
  public void send(MailBean mailBean, MailServerConf mailServerConf) throws Exception {
    try {
      final Session session = getSession(mailServerConf);
      // 构造message
      MimeMessage message = generateMessage(mailBean, mailServerConf, session);
      // 使用认证模式发送邮件。
      Transport transport = session.getTransport("smtp");
      //设置端口
      String username = StrUtil.isBlank(mailServerConf.getUsername()) ? mailServerConf.getFrom() : mailServerConf.getUsername();
      transport.connect(
        mailServerConf.getHost(),
        mailServerConf.getPort(),
        username,
        "true".equals(mailServerConf.getAuth()) ? mailServerConf.getPassword() : null);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      log.debug("mail send success: Subject:" + mailBean.getSubject() + ", TO:" + mailBean.getToEmails());
    } catch (Exception ex) {
      log.error("发送邮件异常【" + mailBean.getSubject() + "】", ex);
      throw ex;
    }
  }

  private MimeMessage generateMessage(MailBean mailBean, MailServerConf mailServerConf, Session session) throws Exception {
    // 构造邮件消息对象
    MimeMessage message = new MimeMessage(session);
    // 发件人
    message.setFrom(new InternetAddress(mailServerConf.getFrom(), mailServerConf.getName()));
    // 多个发送地址
    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailBean.getToEmails()));
    // 多个抄送地址
    if (StrUtil.isNotBlank(mailBean.getCopyTo())) {
      message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(mailBean.getCopyTo()));
    }
    // 邮件主题
    message.setSubject(mailBean.getSubject());

    // body构造
    if (ObjectUtil.isNotNull(mailBean.getMailDTO().getMailScheduleDto())) {
      //事件（日程）邮件
      message.setContent(this.sendEventEmail(mailBean));
    } else {
      // 普通邮件
      Multipart multipart = new MimeMultipart();
      // 第一个为文本内容。
      BodyPart bodyPart = new MimeBodyPart();
      if (checkMailBean(mailBean)) {
        String mailMessage = this.getMessage(mailBean);
        bodyPart.setContent(mailMessage, "text/html;charset=UTF-8");
      }
      // 添加第一个body内容
      multipart.addBodyPart(bodyPart);
      message.setContent(multipart);
    }

    //附件
    final List<String> accessoryList = mailBean.getMailDTO().getAccessoryList();
    if (CollUtil.isNotEmpty(accessoryList)) {
      Multipart multipart = (Multipart) message.getContent();
      addAccessory(multipart, accessoryList);
    }
    return message;
  }

  /**
   * 添加附件，最多支持5个附件，单个附件最大10M，超过则忽略
   *
   * @param multipart     multipart
   * @param accessoryList accessoryList
   */
  private void addAccessory(Multipart multipart, List<String> accessoryList) {
    final String domain = getDomain();
    accessoryList.forEach(accessory -> {
      if (!StrUtil.startWithIgnoreCase(accessory, "http")) {
        accessory = domain + accessory;
      }
      log.debug("开始添加附件：{}", accessory);
      try {
        URL url = new URL(accessory);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(new DataHandler(url));
        mimeBodyPart.setFileName(MimeUtility.encodeWord(FileUtil.getName(accessory)));
        multipart.addBodyPart(mimeBodyPart);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  private Session getSession(MailServerConf mailServerConf) {
    Properties props = System.getProperties();
    int i = 465;
    if (mailServerConf.getPort() == i) {
      // 发送SSL加密邮件
      Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
      props.put("mail.smtp.socketFactory.port", mailServerConf.getPort());
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.socketFactory.fallback", "false");
      props.put("mail.smtp.ssl.checkserveridentity", true);
    }
    int j = 587;
    if (mailServerConf.getPort() == j) {
      // TLS加密
      props.put("mail.smtp.starttls.enable", "true");
    }
    // 设置SMTP服务器地址
    props.put("mail.smtp.host", mailServerConf.getHost());
    // 设置端口
    props.put("mail.smtp.port", mailServerConf.getPort());
    // 设置邮件的字符集为GBK
    props.put("mail.mime.charset", ParamConstant.getDefaultMailCharset());
    // 设置认证模式
    props.put("mail.smtp.auth", mailServerConf.getAuth());
    // 设置超时时间
    props.put("mail.smtp.timeout", "25000");
    props.setProperty("mail.smtp.timeout", "30000");
    // 设置配置参数
    if (CollUtil.isNotEmpty(mailServerConf.getProps())) {
      props.putAll(mailServerConf.getProps());
    }
    // 获取会话信息
    return Session.getDefaultInstance(props, null);
  }

  /**
   * 发送会议邀请邮件(支持html格式)
   *
   * @param mailBean -
   * @throws Exception 异常
   */
  private Multipart sendEventEmail(MailBean mailBean) throws Exception {
    //参考https://blog.csdn.net/han949417140/article/details/90206475
    log.debug("开始创建事件邮件。");
    MimeMultipart multipart = new MimeMultipart();
    BodyPart bodyPart = new MimeBodyPart();
    try {
      MailDTO temVal = mailBean.getMailDTO();
      //格式化时间日程日期
      MailScheduleDto mailScheduleDto = temVal.getMailScheduleDto();
      log.debug("mailScheduleDo:{}", mailScheduleDto);
      final String startStr = formatDate(mailScheduleDto.getStartDate(), mailScheduleDto.getTimeZone());
      final String endStr = formatDate(mailScheduleDto.getEndDate(), mailScheduleDto.getTimeZone());
      String enventType = mailScheduleDto.getEnventType();
      MailScheduleStatusEnum statusEnum = MailScheduleStatusEnum.valueOf(enventType);
      String method = statusEnum.getEventType();
      int squenceCode = mailScheduleDto.getSquence();
      // Exchange发送邮件时，要求正文部分必须放在附件部分的前面，不然会把正文也作为附件一起发送
      bodyPart.setDataHandler(new DataHandler(
        new ByteArrayDataSource(this.getMessage(mailBean), "text/html;charset=UTF-8")));
      multipart.addBodyPart(bodyPart);
      // 在正文后面，增加附件部分，邮件日程提醒是以附件形式实现的
      StringBuilder buffer = new StringBuilder("");
      buffer
        .append("BEGIN:VCALENDAR\n")
        .append("PRODID:-//Events Calendar//iCal4j 1.0//EN\n")
        .append("VERSION:2.0\n")
        //METHOD:CANCEL 取消会议  METHOD:REQUEST 创建和更新会议
        .append("METHOD:").append(method)
        .append("\nBEGIN:VEVENT\n")
        // 屏蔽日历事件 "接受"、"暂定"、"拒绝" 按钮
        .append("ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:")
        .append(mailBean.getToEmails().contains(ConfConstant.COMMA) ?
          mailBean.getToEmails().split(ConfConstant.COMMA) : mailBean.getToEmails())
        .append("\nORGANIZER:MAILTO:")
        .append(ParamConstant.getDefaultMailFrom())
        //开始时间
        .append("\nDTSTART:").append(startStr)
        //结束时间
        .append("Z\nDTEND:").append(endStr)
        //地址
        .append("Z\nLOCATION:").append(mailScheduleDto.getLocation())
        //uuid 会议唯一标识
        .append("\nUID:").append(mailScheduleDto.getUuid())
        //设置优先级 其中cancel>update>create
        .append("\nSEQUENCE:").append(squenceCode)
        .append("\nCATEGORIES:SuccessCentral Reminder\nDESCRIPTION:\n\nSUMMARY:")
        //主题
        .append(mailBean.getSubject())
        .append("\n");
      if (ObjectUtil.isNotNull(mailScheduleDto.getRecurrenceRuleDTO())) {
        //日历循环规则 RRULE:FREQ=WEEKLY;UNTIL=20210304T080228Z;INTERVAL=2
        final RecurrenceRuleDTO rRule = mailScheduleDto.getRecurrenceRuleDTO();
        buffer.append("RRULE:FREQ=").append(rRule.getFreq()).append(";UNTIL=").append(formatDate(rRule.getUntil(), mailScheduleDto.getTimeZone()));
        if (ObjectUtil.isNotNull(rRule.getInterval())) {
          buffer.append(";INTERVAL=").append(rRule.getInterval());
        }
        buffer.append("\n");
      }
      buffer
        .append("PRIORITY:5\nCLASS:PUBLIC\nBEGIN:VALARM\nTRIGGER:-PT10M\nREPEAT:3\nDURATION:"
          + "PT5M\nACTION:DISPLAY\nDESCRIPTION:Reminder\nEND:VALARM\nEND:VEVENT\nEND:VCALENDAR");
      String contentType;
      if (MailScheduleStatusEnum.REQUEST.getEventType().equals(method)) {
        contentType = "text/calendar;method=REQUEST;charset=UTF-8";
      } else {
        contentType = "text/calendar;method=CANCEL;charset=UTF-8";
      }
      log.debug("创建事件邮件完成:{}" + buffer.toString());
      MimeBodyPart icalAttachment = new MimeBodyPart();
      icalAttachment
        .setDataHandler(new DataHandler(new ByteArrayDataSource(buffer.toString(), contentType)));
      multipart.addBodyPart(icalAttachment);
    } catch (Exception ex) {
      log.error("事件邮件发送失败!", ex);
      throw ex;
    }
    return multipart;
  }

  /**
   * check 邮件
   */
  private boolean checkMailBean(MailBean mailBean) {
    if (mailBean == null) {
      log.warn("Warn mailBean is null (Thread name=" + Thread.currentThread().getName() + ") ");
      return false;
    }
    if (StrUtil.isBlank(mailBean.getSubject())) {
      log.warn("Warn mailBean.getSubject() is null (Thread name=" + Thread.currentThread().getName()
        + ") ");
      return false;
    }
    if (mailBean.getToEmails() == null) {
      log.warn("Warn mailBean.getToEmails() is null (Thread name="
        + Thread.currentThread().getName() + ") ");
      return false;
    }
    if (StrUtil.isBlank(mailBean.getTemplate()) && ObjectUtil.isNull(mailBean.getData())) {
      log.warn("Warn mailBean.getTemplate() is null (Thread name="
        + Thread.currentThread().getName() + ") ");
      return false;
    }
    return true;
  }


  @Override
  public R check(BaseCheckDTO dto, Integer tenantId, MailServerConf conf) {
    Transport transport = null;
    try {
      final Session session = this.getSession(conf);
      transport = session.getTransport("smtp");
      String username = StrUtil.isBlank(conf.getUsername()) ? conf.getFrom() : conf.getUsername();
      transport.connect(conf.getHost(), conf.getPort(), username, "true".equals(conf.getAuth()) ? conf.getPassword() : null);
      // 构造邮件消息对象
      MimeMessage message = new MimeMessage(session);
      message.setSubject(checkMailSubject);
      // 发件人
      message.setFrom(new InternetAddress(conf.getFrom(), conf.getName()));
      // 收件人
      message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getToMail()));
      message.setContent(checkMailBodyText, "text/html;charset=UTF-8");
      transport.sendMessage(message, message.getAllRecipients());
      sendCheckLog(null, null, CommonConstants.SUCCESS, tenantId);
      return R.ok();
    } catch (Exception e) {
      return checkFail(tenantId, e);
    } finally {
      if (ObjectUtil.isNotNull(transport)) {
        try {
          transport.close();
        } catch (MessagingException e) {
          log.error("关闭transport异常！", e);
        }
      }
    }
  }
}
