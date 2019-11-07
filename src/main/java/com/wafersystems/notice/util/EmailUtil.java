package com.wafersystems.notice.util;

import com.wafersystems.notice.config.FreemarkerMacroMessage;
import com.wafersystems.notice.config.loader.MysqlMailTemplateLoader;
import com.wafersystems.notice.constants.EmailTemplateNameConstants;
import com.wafersystems.notice.mail.model.MailBean;
import freemarker.template.Configuration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

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

  /**
   * 发送邮件
   *
   * @param mailBean 邮件对象
   * @throws Exception Exception
   */
  public void send(MailBean mailBean) throws Exception {
    try {
      Properties props = System.getProperties();
      if (ParamConstant.getDEFAULT_MAIL_PORT() == 465) {
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
        String message1 = this.getMessage(mailBean);
        bodyPart.setContent(message1, "text/html;charset=UTF-8");
      }
      // 添加第一个body内容
      multipart.addBodyPart(bodyPart);
      // 使用多个body体填充邮件内容。
      if (EmailTemplateNameConstants.SMTMEETING.equals(mailBean.getTemplate())
        || EmailTemplateNameConstants.VIRSICAL.equals(mailBean.getTemplate())) {
        message.setContent(this.sendEventEmail(mailBean));
      } else {
        message.setContent(multipart);
      }
      // 免认证模式
      // Transport.send(message, message.getAllRecipients());
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
    } catch (Exception ex) {
      log.error("发送邮件异常【" + mailBean.getSubject() + "】", ex);
      throw ex;
    }
  }

  /**
   * 发送事件邮件(不支持html格式).
   *
   * @param mailBean -
   * @return -
   */
  /*
   * private Multipart getContentText(MailBean mailBean) throws Exception { DateTime start = new
   * DateTime(Long.parseLong(mailBean.getTemVal().getValue2())); // 开始时间 DateTime end = new
   * DateTime(Long.parseLong(mailBean.getTemVal().getValue3())); // 结束时间 VEvent vevent = new
   * VEvent(start, end, mailBean.getSubject()); // 时区 // TimeZoneRegistry registry =
   * TimeZoneRegistryFactory.getInstance().createRegistry(); // TimeZone timezone =
   * registry.getTimeZone("Asia/Shanghai"); //
   * vevent.getProperties().add(timezone.getVTimeZone().getTimeZoneId());// 时区
   * vevent.getProperties().add(new Location(mailBean.getTemVal().getValue1()));// 会议地点
   * vevent.getProperties().add(new Summary(mailBean.getSubject()));// 邮件主题
   * vevent.getProperties().add(new Description(this.getMessage(mailBean)));// 邮件内容
   * vevent.getProperties().add(new UidGenerator("meeting invite").generateUid());// 设置uid
   * vevent.getProperties() .add(new Organizer(URI.create("mailto:" +
   * ParamConstant.getDEFAULT_MAIL_FROM()))); // 与会人 Set<String> emailSet = new HashSet<String>();
   * emailSet.add(ParamConstant.getDEFAULT_MAIL_FROM()); if
   * (mailBean.getToEmails().contains(ConfConstant.COMMA)) { emailSet.addAll(Arrays
   * .asList(mailBean.getToEmails().split(ConfConstant.COMMA))); } else {
   * emailSet.add(mailBean.getToEmails()); } int index = 1; for (String email : emailSet) { Attendee
   * attendee = new Attendee(URI.create("mailto:" + email)); if (1 == index) {
   * attendee.getParameters().add(Role.REQ_PARTICIPANT); } else {
   * attendee.getParameters().add(Role.OPT_PARTICIPANT); } attendee.getParameters().add(new
   * Cn("Developer" + index)); vevent.getProperties().add(attendee); index++; } // --------VEvent
   * Over---------- // --------VAlarm Start---------- // 提醒,提前10分钟 VAlarm valarm = new VAlarm(new
   * Dur(0, 0, -10, 0)); valarm.getProperties().add(new Repeat(1)); valarm.getProperties().add(new
   * Duration(new Dur(0, 0, 10, 0))); // 提醒窗口显示的文字信息 valarm.getProperties().add(new Summary(
   * "Event Alarm")); valarm.getProperties().add(Action.DISPLAY); valarm.getProperties().add(new
   * Description("Progress Meeting at 9:30am")); vevent.getAlarms().add(valarm);// 将VAlarm加入VEvent
   * // --------VAlarm Over------------- // --------日历对象 Start--------------- Calendar icsCalendar =
   * new Calendar(); icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"
   * )); icsCalendar.getProperties().add(CalScale.GREGORIAN);
   * icsCalendar.getProperties().add(Version.VERSION_2_0);
   * icsCalendar.getProperties().add(Method.REQUEST); icsCalendar.getComponents().add(vevent);//
   * 将VEvent加入Calendar // 将日历对象转换为二进制流 CalendarOutputter co = new CalendarOutputter(false);
   * ByteArrayOutputStream os = new ByteArrayOutputStream(); co.output(icsCalendar, os); byte[]
   * mailbytes = os.toByteArray(); // --------日历对象 Over------------------ BodyPart mbp = new
   * MimeBodyPart(); mbp.setContent(mailbytes, "text/calendar;method=REQUEST;charset=UTF-8");
   * MimeMultipart mm = new MimeMultipart(); mm.setSubType("related"); mm.addBodyPart(mbp); return
   * mm; }
   */

  /**
   * 发送会议邀请邮件(支持html格式)
   *
   * @param mailBean -
   * @throws Exception 异常
   */
  private Multipart sendEventEmail(MailBean mailBean) throws Exception {
    MimeMultipart multipart = new MimeMultipart();
    BodyPart bodyPart = new MimeBodyPart();
    try {
      java.util.Calendar cal;
      cal = java.util.Calendar.getInstance();
      cal.setTime(DateUtil.formatDateTime(mailBean.getTemVal().getValue1()));
      cal.add(java.util.Calendar.HOUR, -8);
      SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
      final String startStr = sim.format(cal.getTime());
      cal = java.util.Calendar.getInstance();
      cal.setTime(DateUtil.formatDateTime(mailBean.getTemVal().getValue2()));
      cal.add(java.util.Calendar.HOUR, -8);
      SimpleDateFormat resultSim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      final String endStr = sim.format(cal.getTime());
      mailBean.getTemVal()
        .setValue1(resultSim.format(DateUtil.formatDateTime(mailBean.getTemVal().getValue1())));
      mailBean.getTemVal()
        .setValue2(resultSim.format(DateUtil.formatDateTime(mailBean.getTemVal().getValue2())));
      String method;
      //value3 (status) 状态(-1.纯文本信息,0.邮件事件[带邀请按钮],1.邮件事件[无按钮])
      Integer status = Integer.parseInt(mailBean.getTemVal().getValue3());
      //value5 (type) 邮件类型 (1-新建|2-修改|3-取消|4-会前提醒|5-减少参会人|6-回执接受|7-回执拒绝|8-创建确认|9-修改确认|10-取消确认|11 -审批通过|12-审批拒绝|99-保洁员提醒)
      Integer type = Integer.parseInt(mailBean.getTemVal().getValue5());
      //参考https://blog.csdn.net/han949417140/article/details/90206475
      int squence = 2;
      if (status == 0) {
        method = "REQUEST";
      } else if (status == 1) {
        method = "PUBLISH";
      } else {
        if (type == 3 || type == 10) {//会议取消
          method = "CANCEL";
          squence = 3;
        } else {
          bodyPart.setContent(this.getMessage(mailBean), "text/html;charset=UTF-8");
          multipart.addBodyPart(bodyPart);
          return multipart;
        }
      }

      // Exchange发送邮件时，要求正文部分必须放在附件部分的前面，不然会把正文也作为附件一起发送
      bodyPart.setDataHandler(new DataHandler(
        new ByteArrayDataSource(this.getMessage(mailBean), "text/html;charset=UTF-8")));
      multipart.addBodyPart(bodyPart);

      // 在正文后面，增加附件部分，邮件日程提醒是以附件形式实现的
      StringBuilder buffer = new StringBuilder("");
      buffer.append("BEGIN:VCALENDAR\n").append("PRODID:-//Events Calendar//iCal4j 1.0//EN\n")
        .append("VERSION:2.0\n").append("METHOD:").append(method).append("\nBEGIN:VEVENT\n")
        // 屏蔽日历时间 "接受"、"暂定"、"拒绝" 按钮
        .append("ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:").append(
        mailBean.getToEmails().contains(ConfConstant.COMMA) ?
          mailBean.getToEmails().split(ConfConstant.COMMA) :
          mailBean.getToEmails()).append("\nORGANIZER:MAILTO:")
        .append(ParamConstant.getDEFAULT_MAIL_FROM()).append("\nDTSTART:").append(startStr)
        .append("Z\nDTEND:").append(endStr).append("Z\nLOCATION:")
        .append(mailBean.getTemVal().getValue7()).append("\nUID:").append(mailBean.getTemVal().getUuid())
        .append("\nSEQUENCE:").append(squence)
        .append("\nCATEGORIES:SuccessCentral Reminder\nDESCRIPTION:\n\nSUMMARY:")
        .append(mailBean.getSubject()).append("\n");
      buffer.append("PRIORITY:5\nCLASS:PUBLIC\nBEGIN:VALARM\nTRIGGER:-PT10M\nREPEAT:3\nDURATION:"
        + "PT5M\nACTION:DISPLAY\nDESCRIPTION:Reminder\nEND:VALARM\nEND:VEVENT\nEND:VCALENDAR");
      String contentType;
      if (status == 0 || status == -22) {
        contentType = "text/calendar;method=REQUEST;charset=UTF-8";
      } else {
        contentType = "text/calendar;method=CANCEL;charset=UTF-8";
      }
//      MimeBodyPart icalAttachment = new MimeBodyPart();
//      icalAttachment
//          .setDataHandler(new DataHandler(new ByteArrayDataSource(buffer.toString(), contentType)));
//      icalAttachment
//          .setFileName(MimeUtility.encodeText(mailBean.getSubject() + ".ics", "UTF-8", null));
//      multipart.addBodyPart(icalAttachment);

      MimeBodyPart icalAttachment = new MimeBodyPart();
      icalAttachment
        .setDataHandler(new DataHandler(new ByteArrayDataSource(buffer.toString(), contentType)));
      multipart.addBodyPart(icalAttachment);
      // multipart.setSubType("related"); // 以附件形式显示
    } catch (Exception ex) {
      log.error("事件邮件发送失败!", ex);
      throw ex;
    }
    return multipart;
  }

  /**
   * 模板解析
   *
   * @param mailBean
   * @return
   */
  public String getMessage(MailBean mailBean) throws Exception {
    StringWriter writer = new StringWriter();
    VelocityContext context;
    try {
      if (ConfConstant.TypeEnum.VM.equals(mailBean.getType())) {
        log.debug("使用模版" + mailBean.getTemplate());
        writer = new StringWriter();
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
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailBean.getTemVal());
        return html;
      } else {
        log.debug("使用html模版" + mailBean.getTemplate());
        context = new VelocityContext(mailBean.getData());
        velocityEngine.evaluate(context, writer, "", mailBean.getTemplate());
        return writer.toString();
      }
    } catch (VelocityException ex) {
      log.error(" VelocityException : " + mailBean.getSubject(), ex);
      throw ex;
    } finally {
      try {
        writer.close();
      } catch (IOException ex) {
        log.error("StringWriter close error ... ", ex);
        throw ex;
      }
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
