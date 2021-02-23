package com.wafersystems.notice.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.FreemarkerMacroMessage;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.config.loader.MysqlMailTemplateLoader;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.MailTemplateDTO;
import com.wafersystems.notice.model.enums.MailScheduleStatusEnum;
import com.wafersystems.notice.service.MailNoticeService;
import com.wafersystems.virsical.common.core.constant.NoticeMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgActionEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.dto.*;
import freemarker.template.Configuration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.amqp.core.AmqpTemplate;
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
import java.lang.reflect.Method;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
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

  @Autowired
  private AmqpTemplate rabbitTemplate;

  @Autowired
  private MailNoticeService mailService;

  /**
   * 发送邮件
   *
   * @param mailBean 邮件对象
   * @throws Exception Exception
   */
  public void send(MailBean mailBean, MailServerConf mailServerConf) throws Exception {
    final MailTemplateDTO template = mailService.getTempByName(mailBean.getTemplate());
    if (ObjectUtil.isNotNull(template) && ObjectUtil.equal(template.getState(), 1)) {
      log.warn("邮件模板[{}]禁用，邮件[{}]不发送！", mailBean.getTemplate(), mailBean.getSubject());
      return;
    }
    //重复发送拦截
    if (sendIntercept.mailBoolIntercept(mailBean)) {
      log.error("拦截重复发送邮件[{}]", mailBean.toString());
      return;
    }
    try {
      Properties props = System.getProperties();
      int i = 465;
      if (mailServerConf.getPort() == i) {
        // 发送SSL加密邮件
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        props.put("mail.smtp.socketFactory.port", mailServerConf.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
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
      props.put("mail.mime.charset", ParamConstant.getDEFAULT_MAIL_CHARSET());
      // 设置认证模式
      props.put("mail.smtp.auth", mailServerConf.getAuth());
      // 设置配置参数
      if (CollUtil.isNotEmpty(mailServerConf.getProps())) {
        props.putAll(mailServerConf.getProps());
      }
      // 获取会话信息
      Session session = Session.getDefaultInstance(props, null);
      // 构造邮件消息对象
      MimeMessage message = new MimeMessage(session);
      // 发件人
      message.setFrom(new InternetAddress(mailServerConf.getFrom(),
        mailServerConf.getName()));
      // 多个发送地址
      message.addRecipients(Message.RecipientType.TO,
        InternetAddress.parse(mailBean.getToEmails()));
      // 多个抄送地址
      if (StrUtil.isNotBlank(mailBean.getCopyTo())) {
        message.addRecipients(Message.RecipientType.CC,
          InternetAddress.parse(mailBean.getCopyTo()));
      }
      // 邮件主题
      message.setSubject(mailBean.getSubject());
      // 使用多个body体填充邮件内容。
      if (ObjectUtil.isNotNull(mailBean.getMailDTO().getMailScheduleDto())) {
        //事件（日程）邮件
        message.setContent(this.sendEventEmail(mailBean));
      } else {
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
        message.setContent(multipart);
      }
      // 使用认证模式发送邮件。
      Transport transport = session.getTransport("smtp");
      //设置端口
      transport.connect(mailServerConf.getHost(), mailServerConf.getPort(),
        mailServerConf.getFrom(),
        "true".equals(mailServerConf.getAuth())
          ? mailServerConf.getPassword() : null);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      log.debug(
        "mail send success: Subject:" + mailBean.getSubject() + ", TO:" + mailBean.getToEmails());
      //记录（拦截重复发送用）
      String redisKey = String.format(RedisKeyConstants.MAIL_KEY,
        mailBean.getToEmails(), mailBean.getTemplate(), mailBean.getSubject(), mailBean.hashCode());
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(mailBean), properties.getMailTimeHorizon(), TimeUnit.MINUTES);
      //发送 发送结果(成功)
      sendResult(mailBean.getUuid(), mailBean.getRouterKey(), true);
    } catch (Exception ex) {
      log.error("发送邮件异常【" + mailBean.getSubject() + "】", ex);
      //发送 发送结果(失败)
      sendResult(mailBean.getUuid(), mailBean.getRouterKey(), false);
      throw ex;
    }
  }

  /**
   * 邮件发送结果通知
   *
   * @param uuid      uuid
   * @param routerKey 路由键
   * @param result    邮件发送结果
   */
  private void sendResult(String uuid, String routerKey, boolean result) {
    if (StrUtil.isNotBlank(uuid) && StrUtil.isNotBlank(routerKey)) {
      MessageDTO dto = new MessageDTO(MsgTypeEnum.ONE.name(), MsgActionEnum.SHOW.name(), new MailResultDTO(uuid, result));
      rabbitTemplate.convertAndSend(NoticeMqConstants.EXCHANGE_DIRECT_NOTICE_RESULT_MAIL, routerKey, JSON.toJSONString(dto));
      log.debug("发送邮件发送结果uuid={}，result={}", uuid, result);
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
        .append(ParamConstant.getDEFAULT_MAIL_FROM())
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
//      icalAttachment
//        .setFileName(MimeUtility.encodeText(mailBean.getSubject() + ".ics", "UTF-8", null));
      multipart.addBodyPart(icalAttachment);
      // 以附件形式显示
//      multipart.setSubType("related");
    } catch (Exception ex) {
      log.error("事件邮件发送失败!", ex);
      throw ex;
    }
    return multipart;
  }

  /**
   * 格式化日期
   *
   * @param date     日期
   * @param timeZone 时区
   * @return 格式化后的日期串
   */
  private String formatDate(String date, String timeZone) {
    SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    sim.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sim.format(Long.valueOf(date));
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
        context.put("TemVal", mailBean.getMailDTO());
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

        final Map<String, Object> objectMap = this.attributeToMap(mailBean);
        //加载模板
        freemarker.template.Template template = configuration.getTemplate(mailBean.getTemplate(), mailBean.getMailDTO().getLocale());
        //模板渲染
        final String contextStr = FreeMarkerTemplateUtils.processTemplateIntoString(template, objectMap);
        //是否使用基础模板
        if (!mailBean.getMailDTO().isUseBaseTemplate()) {
          return contextStr;
        }
        //加载基础模板
        freemarker.template.Template baseTemplate = configuration.getTemplate("baseTemplate", mailBean.getMailDTO().getLocale());
        objectMap.put("productTemplateContent", contextStr);
        //渲染基础模板
        return FreeMarkerTemplateUtils.processTemplateIntoString(baseTemplate, objectMap);
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

  /**
   * 将mailDto中所有参数转至data（map）
   *
   * @param mailBean
   * @return
   */
  private Map<String, Object> attributeToMap(MailBean mailBean) {
    int dtoValueCount = 50;
    final MailDTO mailDTO = mailBean.getMailDTO();
    Map<String, Object> data = mailDTO.getData();
    if (ObjectUtil.isNull(data)) {
      data = new HashMap<>(dtoValueCount);
    }
    final Method[] declaredMethods = mailDTO.getClass().getDeclaredMethods();
    for (Method method : declaredMethods) {
      final String name = method.getName();
      try {

        if (method.getReturnType().getName().contains("String")
          && StrUtil.startWith(name, "get")
          && !StrUtil.equals("getData", name)) {
          String value = (String) method.invoke(mailDTO);
          if (StrUtil.isNotBlank(value)) {
            data.put(StrUtil.removePreAndLowerFirst(name, "get"), value);
          }
        }
      } catch (Exception e) {
        log.warn("反射获取值[{}]失败", name);
      }
    }

    final HashMap<String, Object> resultMap = new HashMap<>();
    resultMap.put("locale", mailDTO.getLocale());
    resultMap.putAll(data);
    return resultMap;
  }
}
