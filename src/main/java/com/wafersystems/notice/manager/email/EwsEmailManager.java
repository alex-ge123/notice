package com.wafersystems.notice.manager.email;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.MicrosoftRecordDTO;
import com.wafersystems.notice.model.enums.MailScheduleStatusEnum;
import com.wafersystems.notice.service.MicrosoftRecordService;
import com.wafersystems.notice.service.impl.CustomExchangeService;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.FreqConstants;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MailScheduleDto;
import com.wafersystems.virsical.common.core.dto.RecurrenceRuleDTO;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeek;
import microsoft.exchange.webservices.data.core.enumeration.service.*;
import microsoft.exchange.webservices.data.core.exception.misc.ArgumentOutOfRangeException;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service(MailConstants.MAIL_SERVER_TYPE_EWS)
public class EwsEmailManager extends AbstractEmailManager {
  @Autowired
  private MicrosoftRecordService microsoftRecordService;

  @Override
  public void send(MailBean mailBean, MailServerConf conf) throws Exception {
    final ExchangeService service = getExchangeService(conf);
    final MailDTO mailDTO = mailBean.getMailDTO();
    if (ObjectUtil.isNotNull(mailDTO.getMailScheduleDto())) {
      final MailScheduleDto schedule = mailDTO.getMailScheduleDto();
      final int sequence = schedule.getSquence();
      final String eventType = schedule.getEnventType();
      if (MailScheduleStatusEnum.REQUEST.getEventType().equals(eventType) && 1 == sequence) {
        // 新增
        send(mailBean, service, schedule);
      } else if (MailScheduleStatusEnum.REQUEST.getEventType().equals(eventType) && 1 < sequence) {
        // 修改
        final MicrosoftRecordDTO recordDTO = microsoftRecordService.getById(schedule.getUuid());
        if (ObjectUtil.isNotNull(recordDTO) && StrUtil.isNotBlank(recordDTO.getEventid())) {
          final Appointment meeting = mailBeanToAppointment(mailBean, service, recordDTO.getEventid());
          meeting.update(ConflictResolutionMode.AlwaysOverwrite, SendInvitationsOrCancellationsMode.SendToAllAndSaveCopy);
        } else {
          send(mailBean, service, schedule);
        }
      } else if (MailScheduleStatusEnum.CANCEL.getEventType().equals(eventType)) {
        // 取消
        final MicrosoftRecordDTO recordDTO = microsoftRecordService.getById(schedule.getUuid());
        if (ObjectUtil.isNotNull(recordDTO)) {
          Appointment appointment = Appointment.bind(service, ItemId.getItemIdFromString(recordDTO.getEventid()), new PropertySet());
          // Delete the meeting by using the Delete method.
          // 取消后，之前创建的会议标题前会加上已取消三个字
          appointment.delete(DeleteMode.MoveToDeletedItems, SendCancellationsMode.SendToAllAndSaveCopy);
          microsoftRecordService.delById(recordDTO.getId());
        } else {
          log.error("通过uuid={},查询事件id为空，忽略取消！", mailBean.getUuid());
        }
      } else {
        log.error("非法事件类型{}，忽略", eventType);
      }
    } else {
      // 普通邮件
      final EmailMessage msg = generateMessage(mailBean, service);
      msg.sendAndSaveCopy();
    }
  }

  private void send(MailBean mailBean, ExchangeService service, MailScheduleDto schedule) throws Exception {
    // 构造Appointment
    final Appointment meeting = mailBeanToAppointment(mailBean, service, null);
    // 发送
    meeting.save(SendInvitationsMode.SendToAllAndSaveCopy);
    // 保存邮件id
    saveMeetingId(schedule, meeting);
  }

  private void saveMeetingId(MailScheduleDto schedule, Appointment meeting) throws ServiceLocalException {
    // 保存邮件id
    final ItemId id = meeting.getId();
    final MicrosoftRecordDTO microsoftRecordDTO = new MicrosoftRecordDTO();
    microsoftRecordDTO.setEventid(id.toString());
    microsoftRecordDTO.setUuid(schedule.getUuid());
    microsoftRecordService.saveTemp(microsoftRecordDTO);
  }

  /**
   * @param mailBean mailBean
   * @param service  service
   * @return
   * @throws Exception
   */
  private Appointment mailBeanToAppointment(MailBean mailBean, ExchangeService service, String id) throws Exception {
    final MailDTO mailDTO = mailBean.getMailDTO();
    final MailScheduleDto schedule = mailDTO.getMailScheduleDto();
    Appointment meeting;
    if (StrUtil.isBlank(id)) {
      meeting = new Appointment(service);
    } else {
      meeting = Appointment.bind(service, ItemId.getItemIdFromString(id), new PropertySet());
    }
    meeting.setSubject(mailBean.getSubject());
    meeting.getRequiredAttendees().clear();
    meeting.getResources().clear();
    meeting.setReminderMinutesBeforeStart(15);
    final Date startDate = new Date(Long.valueOf(schedule.getStartDate()));
    meeting.setStart(startDate);
    final Date endDate = new Date(Long.valueOf(schedule.getEndDate()));
    meeting.setEnd(endDate);
    meeting.setLocation(schedule.getLocation());
    meeting.setIsAllDayEvent(false);
    MessageBody body = MessageBody.getMessageBodyFromText(getMessage(mailBean));
    body.setBodyType(BodyType.HTML);
    meeting.setBody(body);

    // 收件人
    if (StrUtil.isNotBlank(mailBean.getToEmails())) {
      String[] receiverIdItems = mailBean.getToEmails().split(CommonConstants.COMMA);
      for (String receiverId : receiverIdItems) {
        if (StrUtil.isNotBlank(receiverId)) {
          Attendee receiver = new Attendee();
          receiver.setAddress(receiverId.trim());
          receiver.setName(receiverId.trim());
          meeting.getRequiredAttendees().add(receiver);
        }
      }
    }

    // 抄送
    if (StrUtil.isNotBlank(mailBean.getCopyTo())) {
      String[] receiverIdItems = mailBean.getCopyTo().split(CommonConstants.COMMA);
      for (String receiverId : receiverIdItems) {
        if (StrUtil.isNotBlank(receiverId)) {
          Attendee receiver = new Attendee();
          receiver.setAddress(receiverId.trim());
          receiver.setName(receiverId.trim());
          meeting.getOptionalAttendees().add(receiver);
        }
      }
    }

    // 重复日程
    if (ObjectUtil.isNotNull(schedule.getRecurrenceRuleDTO())) {
      meeting.setRecurrence(getRecurrence(startDate, schedule.getRecurrenceRuleDTO()));
    }

    // 附件
    addAccessory(mailDTO, meeting);
    return meeting;
  }

  /**
   * 将 {@link Calendar}星期相关值转换为DayOfTheWeek枚举对象<br>
   *
   * @param calendarWeekIntValue calendarWeekIntValue
   * @return DayOfTheWeek
   */
  private DayOfTheWeek of(int calendarWeekIntValue) {
    switch (calendarWeekIntValue) {
      case Calendar.SUNDAY:
        return DayOfTheWeek.Sunday;
      case Calendar.MONDAY:
        return DayOfTheWeek.Monday;
      case Calendar.TUESDAY:
        return DayOfTheWeek.Tuesday;
      case Calendar.WEDNESDAY:
        return DayOfTheWeek.Wednesday;
      case Calendar.THURSDAY:
        return DayOfTheWeek.Thursday;
      case Calendar.FRIDAY:
        return DayOfTheWeek.Friday;
      case Calendar.SATURDAY:
        return DayOfTheWeek.Saturday;
      default:
        return null;
    }
  }

  private Recurrence getRecurrence(Date start, RecurrenceRuleDTO recurrenceDTO) throws ArgumentOutOfRangeException {
    Recurrence recurrences;
    int interval = ObjectUtil.isNotNull(recurrenceDTO.getInterval()) ? recurrenceDTO.getInterval() : 1;
    if (FreqConstants.DAILY.equals(recurrenceDTO.getFreq())) {
      recurrences = new Recurrence.DailyPattern(start, interval);
      recurrences.setEndDate(new Date(Long.valueOf(recurrenceDTO.getUntil())));
    } else if (FreqConstants.WEEKLY.equals(recurrenceDTO.getFreq())) {
      final int i = DateUtil.dayOfWeek(start);
      final DayOfTheWeek dayOfTheWeek = of(i);
      recurrences = new Recurrence.WeeklyPattern(start, interval, dayOfTheWeek);
      recurrences.setEndDate(new Date(Long.valueOf(recurrenceDTO.getUntil())));
    } else {
      final int i = DateUtil.dayOfMonth(start);
      recurrences = new Recurrence.MonthlyPattern(start, interval, i);
      recurrences.setEndDate(new Date(Long.valueOf(recurrenceDTO.getUntil())));
    }
    return recurrences;
  }

  private ExchangeService getExchangeService(MailServerConf conf) throws URISyntaxException {
    CustomExchangeService service = new CustomExchangeService();
    ExchangeCredentials credentials = new WebCredentials(conf.getEwsAccount(), conf.getEwsPassword());
    service.setCredentials(credentials);
    service.setUrl(new URI(conf.getEwsUrl()));
    service.setTimeout(60 * 1000);
    return service;
  }

  private EmailMessage generateMessage(MailBean mailBean, ExchangeService exchangService) throws Exception {
    // 构造邮件体
    EmailMessage msg = new EmailMessage(exchangService);
    msg.setSubject(mailBean.getSubject());
    MessageBody body = new MessageBody(BodyType.HTML, getMessage(mailBean));
    msg.setBody(body);

    // 收件人
    if (StrUtil.isNotBlank(mailBean.getToEmails())) {
      final String[] toEmails = mailBean.getToEmails().split(CommonConstants.COMMA);
      for (String toEmail : toEmails) {
        msg.getToRecipients().add(toEmail);
      }
    }

    // 抄送人
    if (StrUtil.isNotBlank(mailBean.getCopyTo())) {
      final String[] copyTos = mailBean.getCopyTo().split(CommonConstants.COMMA);
      for (String copyTo : copyTos) {
        msg.getCcRecipients().add(copyTo);
      }
    }

    // 附件
    final MailDTO mailDTO = mailBean.getMailDTO();
    addAccessory(mailDTO, msg);
    return msg;
  }

  private void addAccessory(MailDTO mailDTO, Item item) {
    final List<String> accessoryList = mailDTO.getAccessoryList();
    final String domain = getDomain();
    Optional.ofNullable(accessoryList).orElse(Collections.emptyList())
      .forEach(accessory -> {
        try {
          if (StrUtil.isNotBlank(accessory)) {
            if (!StrUtil.startWithIgnoreCase(accessory, "http")) {
              accessory = domain + accessory;
            }
            log.debug("开始添加附件：{}", accessory);
            final URL url = new URL(accessory);
            final String name = FileUtil.getName(accessory);
            item.getAttachments().addFileAttachment(name, url.openStream());
          }
        } catch (Exception e) {
          log.error("添加附件{}失败，忽略", accessory, e);
        }
      });
  }

  @Override
  public R check(BaseCheckDTO dto, Integer tenantId, MailServerConf mailServerConf) {
    try {
      final ExchangeService service = getExchangeService(mailServerConf);
      EmailMessage msg = new EmailMessage(service);
      msg.setSubject(checkMailSubject);
      MessageBody body = new MessageBody(BodyType.Text, checkMailBodyText);
      msg.setBody(body);
      msg.getToRecipients().add(dto.getToMail());
      msg.sendAndSaveCopy();
      sendCheckLog(null, null, CommonConstants.SUCCESS, tenantId);
      return R.ok();
    } catch (Exception e) {
      return checkFail(tenantId, e);
    }
  }
}
