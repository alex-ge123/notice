package com.wafersystems.notice.constants;

/**
 * 邮件配置key常量
 *
 * @author tandk
 * @date 2021/1/13 11:36
 */
public class MailConstants {
  public static final String TYPE = "MAIL";
  public static final String MAIL_SERVER_TYPE = "MAIL_SERVER_TYPE";

  /**
   * smtp相关配置
   */
  public static final String MAIL_SERVER_TYPE_SMTP = "smtp";
  public static final String MAIL_HOST = "MAIL_HOST";
  public static final String MAIL_FROM = "MAIL_FROM";
  public static final String MAIL_PASSWORD = "MAIL_PASSWORD";
  public static final String MAIL_AUTH = "MAIL_AUTH";
  public static final String MAIL_MAILNAME = "MAIL_MAILNAME";
  public static final String MAIL_USERNAME = "MAIL_USERNAME";
  public static final String MAIL_PORT = "MAIL_PORT";
  public static final String MAIL_PROPS = "MAIL_PROPS";
  public static final String MAIL_ENCRYMODE = "MAIL_ENCRYMODE";

  /**
   * microsoft相关配置
   */
  public static final String MAIL_SERVER_TYPE_MICROSOFT = "microsoft";
  public static final String MAIL_MICROSOFT_CLIENTID = "MAIL_MICROSOFT_CLIENTID";
  public static final String MAIL_MICROSOFT_CLIENTSECRET = "MAIL_MICROSOFT_CLIENTSECRET";
  public static final String MAIL_MICROSOFT_TENANTID = "MAIL_MICROSOFT_TENANTID";
  public static final String MAIL_MICROSOFT_SCOPE = "MAIL_MICROSOFT_SCOPE";
  public static final String MAIL_MICROSOFT_FROM = "MAIL_MICROSOFT_FROM";


  /**
   * ews相关配置
   */
  public static final String MAIL_SERVER_TYPE_EWS = "ews";
  public static final String MAIL_EWS_URL = "MAIL_EWS_URL";
  public static final String MAIL_EWS_ACCOUNT = "MAIL_EWS_ACCOUNT";
  public static final String MAIL_EWS_PASSWORD = "MAIL_EWS_PASSWORD";

  /**
   * aws亚马逊邮件相关配置
   */
  public static final String MAIL_SERVER_TYPE_AMAZON = "amazon";
}

