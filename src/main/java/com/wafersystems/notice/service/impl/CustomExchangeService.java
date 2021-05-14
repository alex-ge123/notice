package com.wafersystems.notice.service.impl;

import microsoft.exchange.webservices.data.EWSConstants;
import microsoft.exchange.webservices.data.core.EwsSSLProtocolSocketFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.DefaultExtendedPropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.ExtendedProperty;
import microsoft.exchange.webservices.data.property.complex.ExtendedPropertyCollection;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:扩展Exchange
 */
public class CustomExchangeService extends ExchangeService {

  /**
   * 描述:无参构造方法
   */
  public CustomExchangeService() {
    super();
  }

  /**
   * 描述:有参构造方法
   */
  public CustomExchangeService(ExchangeVersion requestedServerVersion) {
    super(requestedServerVersion);
  }

  /**
   * 描述:默认属性
   */
  protected List<PropertyDefinition> defaultPropertyDefinition() {
    List<PropertyDefinition> params = new ArrayList<PropertyDefinition>();
    params.add(ItemSchema.MimeContent);
    params.add(ItemSchema.Id);
    params.add(ItemSchema.ParentFolderId);
    params.add(ItemSchema.ItemClass);
    params.add(ItemSchema.Subject);
    params.add(ItemSchema.Sensitivity);
    params.add(ItemSchema.Body);
    params.add(ItemSchema.Attachments);
    params.add(ItemSchema.DateTimeReceived);
    params.add(ItemSchema.Size);
    params.add(ItemSchema.Categories);
    params.add(ItemSchema.Importance);
    params.add(ItemSchema.InReplyTo);
    params.add(ItemSchema.IsSubmitted);
    params.add(ItemSchema.IsDraft);
    params.add(ItemSchema.IsFromMe);
    params.add(ItemSchema.IsResend);
    params.add(ItemSchema.IsUnmodified);
    params.add(ItemSchema.InternetMessageHeaders);
    params.add(ItemSchema.DateTimeSent);
    params.add(ItemSchema.DateTimeCreated);
    params.add(ItemSchema.AllowedResponseActions);
    params.add(ItemSchema.ReminderDueBy);
    params.add(ItemSchema.IsReminderSet);
    params.add(ItemSchema.ReminderMinutesBeforeStart);
    params.add(ItemSchema.DisplayCc);
    params.add(ItemSchema.DisplayTo);
    params.add(ItemSchema.HasAttachments);
    params.add(ItemSchema.Culture);
    params.add(ItemSchema.EffectiveRights);
    params.add(ItemSchema.LastModifiedName);
    params.add(ItemSchema.LastModifiedTime);
    if (!getRequestedServerVersion().equals(ExchangeVersion.Exchange2007_SP1)) {
      params.add(ItemSchema.IsAssociated);
      params.add(ItemSchema.WebClientReadFormQueryString);
      params.add(ItemSchema.WebClientEditFormQueryString);
      params.add(ItemSchema.ConversationId);
      params.add(ItemSchema.UniqueBody);
    }
    return params;
  }

  /**
   * 描述:默认属性集
   */
  public PropertySet defaultPropertySet() {
    List<PropertyDefinition> params = defaultPropertyDefinition();
    PropertyDefinition[] property = new PropertyDefinition[params.size()];
    params.toArray(property);
    return new PropertySet(property);
  }

  /**
   * 描述:默认会议属性集
   */
  public PropertySet defaultAppointmentPropertySet() {
    List<PropertyDefinition> params = new ArrayList<PropertyDefinition>();
    params.addAll(defaultPropertyDefinition());// 默认属性集
    params.add(AppointmentSchema.Start);
    params.add(AppointmentSchema.End);
    params.add(AppointmentSchema.OriginalStart);
    params.add(AppointmentSchema.IsAllDayEvent);
    params.add(AppointmentSchema.LegacyFreeBusyStatus);
    params.add(AppointmentSchema.Location);
    params.add(AppointmentSchema.When);
    params.add(AppointmentSchema.IsMeeting);
    params.add(AppointmentSchema.IsCancelled);
    params.add(AppointmentSchema.IsRecurring);
    params.add(AppointmentSchema.MeetingRequestWasSent);
    params.add(AppointmentSchema.IsResponseRequested);
    params.add(AppointmentSchema.AppointmentType);
    params.add(AppointmentSchema.MyResponseType);
    params.add(AppointmentSchema.Organizer);
    params.add(AppointmentSchema.RequiredAttendees);
    params.add(AppointmentSchema.OptionalAttendees);
    params.add(AppointmentSchema.Resources);
    params.add(AppointmentSchema.ConflictingMeetingCount);
    params.add(AppointmentSchema.AdjacentMeetingCount);
    params.add(AppointmentSchema.ConflictingMeetings);
    params.add(AppointmentSchema.AdjacentMeetings);
    params.add(AppointmentSchema.Duration);
    params.add(AppointmentSchema.TimeZone);
    params.add(AppointmentSchema.AppointmentReplyTime);
    params.add(AppointmentSchema.AppointmentSequenceNumber);
    params.add(AppointmentSchema.AppointmentState);
    params.add(AppointmentSchema.Recurrence);
    params.add(AppointmentSchema.FirstOccurrence);
    params.add(AppointmentSchema.LastOccurrence);
    params.add(AppointmentSchema.ModifiedOccurrences);
    params.add(AppointmentSchema.DeletedOccurrences);
    params.add(AppointmentSchema.ConferenceType);
    params.add(AppointmentSchema.AllowNewTimeProposal);
    params.add(AppointmentSchema.IsOnlineMeeting);
    params.add(AppointmentSchema.MeetingWorkspaceUrl);
    params.add(AppointmentSchema.NetShowUrl);
    params.add(AppointmentSchema.ICalUid);
    params.add(AppointmentSchema.ICalRecurrenceId);
    params.add(AppointmentSchema.ICalDateTimeStamp);
    // 不支持属性
    // params.add(AppointmentSchema.StartTimeZone);
    // params.add(AppointmentSchema.EndTimeZone);
    // params.add(AppointmentSchema.StoreEntryId);
    // params.add(AppointmentSchema.MeetingTimeZone);
    // params.add(AppointmentSchema.extendedProperties);

    PropertyDefinition[] property = new PropertyDefinition[params.size()];
    params.toArray(property);
    return new PropertySet(property);
  }

  /**
   * 描述:自定义会议属性集
   */
  public PropertySet customAppointmentPropertySet() throws Exception {
    PropertySet properties = defaultAppointmentPropertySet();
    properties.add(
        new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "meetingId",
            MapiPropertyType.String));
    properties.add(
        new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "recurringId",
            MapiPropertyType.String));
    properties.add(new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "roomIds",
        MapiPropertyType.String));
    properties.add(
        new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "roomNames",
            MapiPropertyType.String));
    properties.add(new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "members",
        MapiPropertyType.String));
    properties.add(
        new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "memberNames",
            MapiPropertyType.String));
    properties.add(new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "fcIds",
        MapiPropertyType.String));
    properties.add(new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, "fcNames",
        MapiPropertyType.String));
    return properties;
  }

  /**
   * 描述:扩展Exchange会议属性
   *
   * @param appointment Exchange会议对象
   * @param key         扩展属性字段
   * @param value       扩展属性值
   */
  public void setExtendedProperty(Appointment appointment, String key, String value)
      throws Exception {
    if (appointment == null || StringUtils.isBlank(key)) {
      return;
    }
    appointment.setExtendedProperty(
        new ExtendedPropertyDefinition(DefaultExtendedPropertySet.Appointment, key,
            MapiPropertyType.String), value);
  }

  /**
   * 描述:获取Exchange会议扩展属性值
   *
   * @param appointment Exchange会议对象
   * @param key         扩展属性字段
   */
  public String getExtendedProperty(Appointment appointment, String key)
      throws ServiceLocalException {
    String value = null;
    if (appointment == null || StringUtils.isBlank(key)) {
      return value;
    }
    ExtendedPropertyCollection propertys = appointment.getExtendedProperties();
    if (propertys != null && propertys.getCount() > 0) {
      for (ExtendedProperty property : propertys) {
        if (property == null || property.getValue() == null) {
          continue;
        }
        ExtendedPropertyDefinition propertydefinition = property.getPropertyDefinition();
        if (propertydefinition != null && key.equals(propertydefinition.getName())) {
          value = property.getValue().toString();
        }
      }
    }
    return value;
  }

  @Override
  protected Registry<ConnectionSocketFactory> createConnectionSocketFactoryRegistry() {
    try {
      return RegistryBuilder.<ConnectionSocketFactory>create()
          .register(EWSConstants.HTTP_SCHEME, new PlainConnectionSocketFactory())
          .register(EWSConstants.HTTPS_SCHEME,
              EwsSSLProtocolSocketFactory.build(null, NoopHostnameVerifier.INSTANCE)).build();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(
          "Could not initialize ConnectionSocketFactory instances for HttpClientConnectionManager",
          e);
    }
  }
}

