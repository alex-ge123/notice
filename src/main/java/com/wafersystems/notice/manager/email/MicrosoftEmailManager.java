package com.wafersystems.notice.manager.email;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.graph.core.DateOnly;
import com.microsoft.graph.models.*;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.MicrosoftRecordDTO;
import com.wafersystems.notice.model.enums.MailScheduleStatusEnum;
import com.wafersystems.notice.service.MicrosoftRecordService;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.FreqConstants;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MailScheduleDto;
import com.wafersystems.virsical.common.core.dto.RecurrenceRuleDTO;
import com.wafersystems.virsical.common.core.exception.BusinessException;
import com.wafersystems.virsical.common.core.util.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service(MailConstants.MAIL_SERVER_TYPE_MICROSOFT)
public class MicrosoftEmailManager extends AbstractEmailManager {
  @Autowired
  private MicrosoftRecordService microsoftRecordService;
  @Autowired
  private StringRedisTemplate redisTemplate;


  private final static String DOMAIN = "https://graph.microsoft.com/v1.0";

  @Override
  public void send(MailBean mailBean, MailServerConf conf) throws Exception {
    try {
      startSend(mailBean, conf);
    } catch (Exception e) {
      // 有异常，删除token,走重试机制
      redisTemplate.delete(RedisKeyConstants.CACHE_MICROSOFT_TOKEN + conf.getClientId());
      throw e;
    }
  }

  private void startSend(MailBean mailBean, MailServerConf conf) throws Exception {
    // 暂不支持附件
    // https://stackoverflow.com/questions/50714919/office365-rest-api-calendar-event-attachments-not-visible-for-recipients
    final MailDTO mailDTO = mailBean.getMailDTO();
    if (ObjectUtil.isNotNull(mailDTO.getMailScheduleDto())) {
      final MailScheduleDto schedule = mailDTO.getMailScheduleDto();
      final int sequence = schedule.getSquence();
      final String eventType = schedule.getEnventType();
      final String token = getToken(conf);
      if (MailScheduleStatusEnum.REQUEST.getEventType().equals(eventType) && 1 == sequence) {
        // 新增
        Event event = mailBeanToEvent(mailBean);
        final String eventId = createEvent(conf.getMicrosoftFrom(), token, JSON.toJSONString(event));
        final MicrosoftRecordDTO microsoftRecordDTO = new MicrosoftRecordDTO();
        microsoftRecordDTO.setEventid(eventId);
        microsoftRecordDTO.setUuid(mailBean.getUuid());
        microsoftRecordService.saveTemp(microsoftRecordDTO);

      } else if (MailScheduleStatusEnum.REQUEST.getEventType().equals(eventType) && 1 < sequence) {
        // 修改
        final MicrosoftRecordDTO recordDTO = microsoftRecordService.getById(mailBean.getUuid());
        Event event = mailBeanToEvent(mailBean);
        if (ObjectUtil.isNotNull(recordDTO) && StrUtil.isNotBlank(recordDTO.getEventid())) {
          event.transactionId = null;
          updateEvent(recordDTO.getEventid(), conf.getMicrosoftFrom(), token, JSON.toJSONString(event));
        } else {
          final String newEventId = createEvent(conf.getMicrosoftFrom(), token, JSON.toJSONString(event));
          final MicrosoftRecordDTO microsoftRecordDTO = new MicrosoftRecordDTO();
          microsoftRecordDTO.setEventid(newEventId);
          microsoftRecordDTO.setUuid(mailBean.getUuid());
          microsoftRecordService.saveTemp(microsoftRecordDTO);
        }

      } else if (MailScheduleStatusEnum.CANCEL.getEventType().equals(eventType)) {
        // 取消
        final MicrosoftRecordDTO recordDTO = microsoftRecordService.getById(mailBean.getUuid());
        if (ObjectUtil.isNotNull(recordDTO)) {
          final HttpResponse response = delEvent(recordDTO.getEventid(), conf.getMicrosoftFrom(), token);
          if (isOk(response.getStatus())) {
            microsoftRecordService.delById(recordDTO.getUuid());
          }
        } else {
          log.error("通过uuid={},查询事件id为空，忽略取消！", mailBean.getUuid());
        }
      } else {
        log.error("非法事件类型{}，忽略", eventType);
      }
    } else {
      // 普通邮件
      final Message message = generateMessage(mailBean);
      sendMail(conf.getMicrosoftFrom(), getToken(conf), message);
      // 附件
    }
  }

  private Event mailBeanToEvent(MailBean mailBean) throws Exception {
    final MailDTO mailDTO = mailBean.getMailDTO();
    final MailScheduleDto schedule = mailDTO.getMailScheduleDto();

    Event event = new Event();

    event.subject = mailBean.getSubject();
    ItemBody body = new ItemBody();

    body.contentType = BodyType.HTML;
    body.content = getMessage(mailBean);
    event.body = body;

    DateTimeTimeZone start = new DateTimeTimeZone();
    start.dateTime = formatDate(schedule.getStartDate(), schedule.getTimeZone());
    start.timeZone = schedule.getTimeZone();
    event.start = start;

    DateTimeTimeZone end = new DateTimeTimeZone();
    end.dateTime = formatDate(schedule.getEndDate(), schedule.getTimeZone());
    end.timeZone = schedule.getTimeZone();
    event.end = end;

    Location location = new Location();
    location.displayName = schedule.getLocation();
    event.location = location;

    event.reminderMinutesBeforeStart = 15;
    event.isReminderOn = true;

    LinkedList<Attendee> attendeesList = new LinkedList<>();
    // 收件人
    final String[] toEmails = mailBean.getToEmails().split(CommonConstants.COMMA);
    for (String toEmail : toEmails) {
      Attendee attendees = new Attendee();
      EmailAddress emailAddress = new EmailAddress();
      emailAddress.address = toEmail;
//      emailAddress.name = toEmail;
      attendees.emailAddress = emailAddress;
      attendees.type = AttendeeType.REQUIRED;
      attendeesList.add(attendees);
    }

    // 抄送人
    final String[] copyTos = mailBean.getCopyTo().split(CommonConstants.COMMA);
    for (String copyTo : copyTos) {
      Attendee attendees = new Attendee();
      EmailAddress emailAddress = new EmailAddress();
      emailAddress.address = copyTo;
//      emailAddress.name = copyTo;
      attendees.emailAddress = emailAddress;
      attendees.type = AttendeeType.OPTIONAL;
      attendeesList.add(attendees);
    }

    event.attendees = attendeesList;
    event.allowNewTimeProposals = true;
    event.transactionId = schedule.getUuid();

    if (ObjectUtil.isNotNull(schedule.getRecurrenceRuleDTO())) {
      addRecurrence(schedule, event);
    }
    log.debug("事件邮件体为{}", JSON.toJSONString(event));
    return event;
  }

  private void addRecurrence(MailScheduleDto schedule, Event event) throws ParseException {
    final RecurrenceRuleDTO recurrenceDTO = schedule.getRecurrenceRuleDTO();
    // 循环日程
    PatternedRecurrence recurrence = new PatternedRecurrence();
    event.recurrence = recurrence;

    // 事件的持续时间
    RecurrenceRange range = new RecurrenceRange();
    range.startDate = DateOnly.parse(formatDateByPattern(schedule.getStartDate(), "yyyy-MM-dd"));

    // 事件发生的频率
    RecurrencePattern pattern = new RecurrencePattern();
    pattern.interval = recurrenceDTO.getInterval();
    if (FreqConstants.DAILY.equals(recurrenceDTO.getFreq())) {
      pattern.type = RecurrencePatternType.DAILY;
      range.numberOfOccurrences = recurrenceDTO.getCount();
      range.type = RecurrenceRangeType.NUMBERED;
    } else if (FreqConstants.WEEKLY.equals(recurrenceDTO.getFreq())) {
      pattern.type = RecurrencePatternType.WEEKLY;
      range.endDate = DateOnly.parse(formatDateByPattern(recurrenceDTO.getUntil(), "yyyy-MM-dd"));
      range.type = RecurrenceRangeType.END_DATE;
    } else {
      pattern.type = RecurrencePatternType.ABSOLUTE_MONTHLY;
      range.numberOfOccurrences = recurrenceDTO.getCount();
      range.type = RecurrenceRangeType.NUMBERED;
    }
    recurrence.pattern = pattern;
    recurrence.range = range;
  }

  private HttpResponse delEvent(String eventId, String from, String token) {
    final HttpResponse execute = del(DOMAIN + "/users/" + from + "/events/" + eventId, token);
    log.debug("删除事件，响应状态{}，响应结果{}", execute.getStatus(), execute.body());
    return execute;
  }


  private void updateEvent(String eventId, String from, String token, String jsonBody) {
    final HttpResponse execute = patch(DOMAIN + "/users/" + from + "/events/" + eventId, token, jsonBody);
    log.debug("更新事件，响应状态{}，响应结果{}", execute.getStatus(), execute.body());
    if (!isOk(execute.getStatus())) {
      log.error("Microsoft事件邮件修改失败，status={},body={}", execute.getStatus(), execute.body());
      throw new BusinessException("Microsoft事件邮件修改失败");
    }
  }

  /**
   * 事件添加附件，暂不使用
   *
   * @param eventId  事件ID
   * @param from     发件人
   * @param token    token
   * @param jsonBody body
   */
  private void eventAddAttachments(String eventId, String from, String token, String jsonBody) {
    final HttpResponse execute = post(DOMAIN + "/users/" + from + "/events/" + eventId + "/attachments",
      token, jsonBody);
    log.debug("事件添加附件，响应状态{}，响应结果{}", execute.getStatus(), execute.body());
  }

  private String createEvent(String from, String token, String jsonBody) {
    final HttpResponse execute = post(DOMAIN + "/users/" + from + "/events", token, jsonBody);
    log.debug("创建事件，响应状态为{}，响应结果为{}", execute.getStatus(), execute.body());
    final int status = execute.getStatus();
    if (!isOk(status)) {
      log.error("Microsoft邮件发送失败，status={},body={}", status, execute.body());
      throw new BusinessException("Microsoft邮件发送失败");
    } else {
      // 发送成功，返回事件id
      return String.valueOf(JSON.parseObject(execute.body()).get("id"));
    }
  }

  private void sendMail(String from, String token, Message message) {
    final MyMessage myMessage = new MyMessage(message);
    // 发送邮件
    final HttpResponse execute = post(DOMAIN + "/users/" + from + "/sendMail", token, JSON.toJSONString(myMessage));
    log.debug("发送邮件，响应状态为{}，响应结果为{}", execute.getStatus(), execute.body());
    // 发送结果
    final int status = execute.getStatus();
    if (!isOk(status)) {
      log.error("Microsoft邮件发送失败，status={},body={}", status, execute.body());
      throw new BusinessException("Microsoft邮件发送失败");
    }
  }

  @Data
  @AllArgsConstructor
  private class MyMessage {
    private Message message;
  }

  private Message generateMessage(MailBean mailBean) throws Exception {
    // 构造邮件体
    Message message = new Message();
    message.subject = mailBean.getSubject();
    ItemBody body = new ItemBody();
    body.contentType = BodyType.HTML;
    body.content = getMessage(mailBean);
    message.body = body;

    // 收件人
    LinkedList<Recipient> toRecipientsList = new LinkedList<>();
    final String[] toEmails = mailBean.getToEmails().split(CommonConstants.COMMA);
    for (String toEmail : toEmails) {
      Recipient toRecipients = new Recipient();
      EmailAddress emailAddress = new EmailAddress();
      emailAddress.address = toEmail;
      toRecipients.emailAddress = emailAddress;
      toRecipientsList.add(toRecipients);
    }
    message.toRecipients = toRecipientsList;

    // 抄送人
    LinkedList<Recipient> ccRecipientsList = new LinkedList<>();
    final String[] copyTos = mailBean.getCopyTo().split(CommonConstants.COMMA);
    for (String copyTo : copyTos) {
      Recipient ccRecipients = new Recipient();
      EmailAddress emailAddress1 = new EmailAddress();
      emailAddress1.address = copyTo;
      ccRecipients.emailAddress = emailAddress1;
      ccRecipientsList.add(ccRecipients);
    }
    message.ccRecipients = ccRecipientsList;

    return message;
  }

  private String getToken(MailServerConf conf) {
    String key = RedisKeyConstants.CACHE_MICROSOFT_TOKEN + conf.getClientId();
    final String token = redisTemplate.opsForValue().get(key);
    if (StrUtil.isNotBlank(token)) {
      return token;
    }
    final HttpResponse execute = HttpRequest.post("https://login.microsoftonline.com/" + conf.getOfficeTenantId() + "/oauth2/v2.0/token")
      .header("Content-type", "application/x-www-form-urlencoded")
      .form("grant_type", "client_credentials")
      .form("client_id", conf.getClientId())
      .form("scope", conf.getScope())
      .form("client_secret", conf.getClientSecret())
      .timeout(10000)
      .execute();
    final JSONObject object = JSON.parseObject(execute.body());
    final String accessToken = String.valueOf(object.get("access_token"));
    final Integer expiresIn = object.getInteger("expires_in");
    redisTemplate.opsForValue().set(key, accessToken, expiresIn, TimeUnit.SECONDS);
    return accessToken;
  }

  @Override
  public R check(BaseCheckDTO dto, Integer tenantId, MailServerConf mailServerConf) {
    try {
      // 构造邮件体
      Message message = new Message();
      message.subject = "邮件配置测试";
      ItemBody body = new ItemBody();
      body.contentType = BodyType.TEXT;
      body.content = "该邮件用于验证邮件配置，收到该邮件，则您的邮箱配置正确！";
      message.body = body;

      // 收件人
      LinkedList<Recipient> toRecipientsList = new LinkedList<>();
      Recipient toRecipients = new Recipient();
      EmailAddress emailAddress = new EmailAddress();
      emailAddress.address = dto.getToMail();
      toRecipients.emailAddress = emailAddress;
      toRecipientsList.add(toRecipients);
      message.toRecipients = toRecipientsList;

      // 发送邮件
      sendMail(mailServerConf.getMicrosoftFrom(), getToken(mailServerConf), message);

      sendCheckLog(null, null, CommonConstants.SUCCESS, tenantId);
      return R.ok();
    } catch (Exception e) {
      log.warn("邮箱检测失败！", e);
      StringWriter stringWriter = new StringWriter();
      e.printStackTrace(new PrintWriter(stringWriter));
      sendCheckLog(e.getMessage(), stringWriter.toString(), CommonConstants.FAIL, tenantId);
      return R.builder().code(CommonConstants.FAIL).msg(e.getMessage()).data(stringWriter.toString()).build();
    }
  }

  private HttpResponse post(String url, String token, String jsonBody) {
    log.debug("post请求 url={},token={},jsonBody={}", url, token, jsonBody);
    return HttpRequest.post(url)
      .body(jsonBody)
      .header("Content-type", "application/json")
      .header("Authorization", "Bearer " + token)
      .header("Accept", "application/json")
      .timeout(10000)
      .execute();
  }

  private HttpResponse patch(String url, String token, String jsonBody) {
    log.debug("patch请求 url={},token={},jsonBody={}", url, token, jsonBody);
    return HttpRequest.patch(url)
      .body(jsonBody)
      .header("Content-type", "application/json")
      .header("Authorization", "Bearer " + token)
      .header("Accept", "application/json")
      .timeout(10000)
      .execute();
  }

  private HttpResponse del(String url, String token) {
    log.debug("delete请求 url={},token={}", url, token);
    return HttpRequest.delete(url)
      .header("Content-type", "application/json")
      .header("Authorization", "Bearer " + token)
      .header("Accept", "application/json")
      .timeout(10000)
      .execute();
  }

  /**
   * 请求是否成功，判断依据为：状态码范围在200~299内。
   *
   * @return 是否成功请求
   * @since 4.1.9
   */
  private boolean isOk(int status) {
    return status >= 200 && status < 300;
  }
}
