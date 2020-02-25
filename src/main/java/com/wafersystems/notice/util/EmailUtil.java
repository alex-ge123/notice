package com.wafersystems.notice.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.FreemarkerMacroMessage;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.config.loader.MysqlMailTemplateLoader;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.model.enums.MailScheduleStatusEnum;
import com.wafersystems.virsical.common.core.dto.MailScheduleDto;
import freemarker.template.Configuration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.StringWriter;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 邮件工具类
 *
 * @author wafer
 */
@Slf4j
@Component
public class EmailUtil {

  @Setter
  private VelocityEngine velocityEngine;

  @Autowired
  private Configuration configuration;

  @Autowired
  private MysqlMailTemplateLoader mysqlMailTemplateLoader;

  @Autowired
  private FreemarkerMacroMessage messageService;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private SendInterceptProperties properties;
  
  @Autowired
  private SendIntercept sendIntercept;

  /**
   * 发送邮件
   *
   * @param mailBean 邮件对象
   * @throws Exception Exception
   */
  public void send(MailBean mailBean) throws Exception {
    //重复发送拦截
    if (sendIntercept.mailBoolIntercept(mailBean)) {
      log.error("拦截重复发送邮件[{}]", mailBean.toString());
      return;
    }
    try {
      Properties props = System.getProperties();
      int i = 465;
      if (ParamConstant.getDEFAULT_MAIL_PORT() == i) {
        // 发送SSL加密邮件
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        props.put("mail.smtp.socketFactory.port", ParamConstant.getDEFAULT_MAIL_PORT());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
      }
      // 设置SMTP服务器地址
      props.put("mail.smtp.host", ParamConstant.getDEFAULT_MAIL_HOST());
      // 设置端口
      props.put("mail.smtp.port", ParamConstant.getDEFAULT_MAIL_PORT());
      // 设置邮件的字符集为GBK
      props.put("mail.mime.charset", ParamConstant.getDEFAULT_MAIL_CHARSET());
      // 设置认证模式
      props.put("mail.smtp.auth", ParamConstant.getDEFAULT_MAIL_AUTH());
      // 获取会话信息
      Session session = Session.getDefaultInstance(props, null);
      // 构造邮件消息对象
      MimeMessage message = new MimeMessage(session);
      // 发件人
      message.setFrom(new InternetAddress(ParamConstant.getDEFAULT_MAIL_FROM(),
        ParamConstant.getDEFAULT_MAIL_MAILNAME()));
      // 多个发送地址
      message.addRecipients(Message.RecipientType.TO,
        InternetAddress.parse(mailBean.getToEmails()));
      // 多个抄送地址
      if (!StrUtil.isEmptyStr(mailBean.getCopyTo())) {
        message.addRecipients(Message.RecipientType.CC,
          InternetAddress.parse(mailBean.getCopyTo()));
      }
      // 邮件主题
      message.setSubject(mailBean.getSubject());
      // 邮件正文
      Multipart multipart = new MimeMultipart();
      // 第一个为文本内容。
      BodyPart bodyPart = new MimeBodyPart();
      if (checkMailBean(mailBean)) {
        String mailMessage = this.getMessage(mailBean);
        bodyPart.setContent(mailMessage, "text/html;charset=UTF-8");
      }
      // 添加第一个body内容
      multipart.addBodyPart(bodyPart);
      // 使用多个body体填充邮件内容。
      if (ObjectUtil.isNotNull(mailBean.getTemVal().getMailScheduleDto())) {
        //事件（日程）邮件
        message.setContent(this.sendEventEmail(mailBean));
      } else {
        message.setContent(multipart);
      }
      // 使用认证模式发送邮件。
      Transport transport = session.getTransport("smtp");
      //设置端口
      transport.connect(ParamConstant.getDEFAULT_MAIL_HOST(), ParamConstant.getDEFAULT_MAIL_PORT(),
        ParamConstant.getDEFAULT_MAIL_FROM(),
        ParamConstant.getDEFAULT_MAIL_PASSWORD());
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      log.debug(
        "mail send success: Subject:" + mailBean.getSubject() + ", TO:" + mailBean.getToEmails());
      //记录（拦截重复发送用）
      String redisKey = String.format(RedisKeyConstants.MAIL_KEY,
        mailBean.getToEmails(), mailBean.getTemplate(), mailBean.getSubject(), mailBean.hashCode());
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(mailBean), properties.getMailTimeHorizon(), TimeUnit.MINUTES);
    } catch (Exception ex) {
      log.error("发送邮件异常【" + mailBean.getSubject() + "】", ex);
      throw ex;
    }
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
      TemContentVal temVal = mailBean.getTemVal();
      //格式化时间日程日期
      MailScheduleDto mailScheduleDo = temVal.getMailScheduleDto();
      log.debug("mailScheduleDo:{}", mailScheduleDo);
      final String startStr = formatDate(mailScheduleDo.getStartDate());
      final String endStr = formatDate(mailScheduleDo.getEndDate());
      String enventType = mailScheduleDo.getEnventType();
      MailScheduleStatusEnum statusEnum = MailScheduleStatusEnum.valueOf(enventType);
      String method = statusEnum.getEventType();
      int squenceCode = statusEnum.getSquence();
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
        .append(ParamConstant.getDEFAULT_MAIL_FROM())
        //开始时间
        .append("\nDTSTART:").append(startStr)
        //结束时间
        .append("Z\nDTEND:").append(endStr)
        //地址
        .append("Z\nLOCATION:").append(mailScheduleDo.getLocation())
        //uuid 会议唯一标识
        .append("\nUID:").append(mailScheduleDo.getUuid())
        //设置优先级 其中cancel>update>create
        .append("\nSEQUENCE:").append(squenceCode)
        .append("\nCATEGORIES:SuccessCentral Reminder\nDESCRIPTION:\n\nSUMMARY:")
        //主题
        .append(mailBean.getSubject())
        .append("\n");
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
   * 格式化日期
   *
   * @param date 日期
   * @return 格式化后的日期串
   */
  private String formatDate(String date) {
    java.util.Calendar cal;
    cal = java.util.Calendar.getInstance();
    cal.setTime(DateUtil.formatDateTime(date));
    cal.add(java.util.Calendar.HOUR, -8);
    SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    return sim.format(cal.getTime());
  }

  /**
   * 模板解析
   *
   * @param mailBean
   * @return
   */
  public String getMessage(MailBean mailBean) throws Exception {
    VelocityContext context;
    try (StringWriter writer = new StringWriter();) {
      if (ConfConstant.TypeEnum.VM.equals(mailBean.getType())) {
        log.debug("使用模版" + mailBean.getTemplate());
        context = new VelocityContext();
        context.put("TemVal", mailBean.getTemVal());
        Template temple = velocityEngine.getTemplate(mailBean.getTemplate(), "UTF-8");
        temple.merge(context, writer);
        return writer.toString();
      } else if (ConfConstant.TypeEnum.FM.equals(mailBean.getType())) {
        //freemarker
        log.debug("使用模版" + mailBean.getTemplate());
        //freemarker 配置模板加载器
        configuration.setTemplateLoader(mysqlMailTemplateLoader);
        //配置共享变量
        configuration.setSharedVariable("loccalMessage", messageService);
        //加载模板
        freemarker.template.Template template = configuration.getTemplate(mailBean.getTemplate());
        //模板渲染
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, mailBean.getTemVal());
      } else {
        log.debug("使用html模版" + mailBean.getTemplate());
        context = new VelocityContext(mailBean.getData());
        velocityEngine.evaluate(context, writer, "", mailBean.getTemplate());
        return writer.toString();
      }
    } catch (VelocityException ex) {
      log.error(" VelocityException : " + mailBean.getSubject(), ex);
      throw ex;
    }
  }

  /**
   * check 邮件
   */
  private boolean checkMailBean(MailBean mailBean) {
    if (mailBean == null) {
      log.warn("Warn mailBean is null (Thread name=" + Thread.currentThread().getName() + ") ");
      return false;
    }
    if (StrUtil.isEmptyStr(mailBean.getSubject())) {
      log.warn("Warn mailBean.getSubject() is null (Thread name=" + Thread.currentThread().getName()
        + ") ");
      return false;
    }
    if (mailBean.getToEmails() == null) {
      log.warn("Warn mailBean.getToEmails() is null (Thread name="
        + Thread.currentThread().getName() + ") ");
      return false;
    }
    if (StrUtil.isEmptyStr(mailBean.getTemplate()) && StrUtil.isNullObject(mailBean.getData())) {
      log.warn("Warn mailBean.getTemplate() is null (Thread name="
        + Thread.currentThread().getName() + ") ");
      return false;
    }
    return true;
  }
}
