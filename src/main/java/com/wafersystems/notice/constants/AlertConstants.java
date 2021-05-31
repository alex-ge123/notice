package com.wafersystems.notice.constants;

import com.wafersystems.notice.model.enums.AlertTypeEnum;

/**
 * 告警通知常量
 *
 * @author shennan
 * @date 2021年5月31日
 */
public class AlertConstants {
  public static final AlertTypeEnum LOCAL = AlertTypeEnum.ALERT_LOCAL;
  public static final AlertTypeEnum SMS = AlertTypeEnum.ALERT_SMS;
  public static final AlertTypeEnum MAIL = AlertTypeEnum.ALERT_MAIL;

  public static final String URL_SMS_SECRET = "URL_SMS_SECRET";
  public static final String DEFAULT_MAIL_FROM = "DEFAULT_MAIL_FROM";
  public static final String DEFAULT_MAIL_HOST = "DEFAULT_MAIL_HOST";
  public static final String DEFAULT_MAIL_PASSWORD = "DEFAULT_MAIL_PASSWORD";
  public static final String DEFAULT_MAIL_AUTH = "DEFAULT_MAIL_AUTH";
  public static final String DEFAULT_MAIL_CHARSET = "DEFAULT_MAIL_CHARSET";
  public static final String DEFAULT_MAIL_MAILNAME = "DEFAULT_MAIL_MAILNAME";
  public static final String DEFAULT_MAIL_USERNAME = "DEFAULT_MAIL_USERNAME";
  public static final String DEFAULT_REPEAT_COUNT = "DEFAULT_REPEAT_COUNT";
  public static final String DEFAULT_DOMAIN = "DEFAULT_DOMAIN";
  public static final String DEFAULT_MAIL_PORT = "DEFAULT_MAIL_PORT";
  public static final String SYSTEM_NAME = "SYSTEM_NAME";
  public static final String PHONE = "PHONE";
  public static final String SMS_REPEAT_COUNT = "SMS_REPEAT_COUNT";
}
