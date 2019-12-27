package com.wafersystems.notice.util;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author wafer
 */
public class ParamConstant {
  private ParamConstant() {
  }

  /**
   * 系统默认域名.
   */
  @Getter
  @Setter
  private static String DEFAULT_DOMAIN;

  /**
   * 默认logo.
   */
  @Getter
  @Setter
  private static String LOGO_DEFALUT;

  /**
   * 静态图片资源外网访问路径如http://www.virsical.cn/images
   */
  @Getter
  @Setter
  private static String IMAGE_DIRECTORY;

  /**
   * 短信接口clientId.
   */
  @Getter
  @Setter
  private static String URL_SMS_CLIENTID;
  /**
   * 短信接口secret.
   */
  @Getter
  @Setter
  private static String URL_SMS_SECRET;

  /**
   * 短信接口服务.
   */
  @Getter
  @Setter
  private static String URL_SMS_SERVER;

  /**
   * 系统邮件发送邮件.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_FROM;

  /**
   * 系统邮件服务.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_HOST;


  /**
   * 系统邮件服务.
   */
  @Getter
  @Setter
  private static Integer DEFAULT_MAIL_PORT;

  /**
   * 系统邮件密码.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_PASSWORD;

  /**
   * 系统邮件认证.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_AUTH;

  /**
   * 系统邮件超时时间.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_TIMEOUT;

  /**
   * 系统邮件编码.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_CHARSET;

  /**
   * 系统邮件显示名称.
   */
  @Getter
  @Setter
  private static String DEFAULT_MAIL_MAILNAME;

  /**
   * 后台服务默认时区.
   */
  @Getter
  @Setter
  private static String DEFAULT_TIMEZONE;

  /**
   * 默认重发次数.
   */
  @Getter
  @Setter
  private static Integer DEFAULT_REPEAT_COUNT;

  /**
   * 个推appId.
   */
  @Getter
  @Setter
  private static String GETUI_APPID;

  /**
   * 个推appKey.
   */
  @Getter
  @Setter
  private static String GETUI_APPKEY;

  /**
   * 个推masterSecret.
   */
  @Getter
  @Setter
  private static String GETUI_MASTRE_SECRET;

  /**
   * 个推服务地址.
   */
  @Getter
  @Setter
  private static String GETUI_URL;

  /**
   * 个推消息离线时间.
   */
  @Getter
  @Setter
  private static String GETUI_OFFLINE_TIME;

  /**
   * 阿里API AppCode.
   */
  @Getter
  @Setter
  private static String ALI_APP_CODE;

  /**
   * 天气信息间隔更新时间(分钟).
   */
  @Getter
  @Setter
  private static String WEATHER_UPDATE_INTERVAL;

  /**
   * 邮件服务是否开启(服务必须参数是否配置).
   */
  @Getter
  @Setter
  private static boolean EMAIL_SWITCH;

  /**
   * 短信服务是否开启(服务必须参数是否配置).
   */
  @Getter
  @Setter
  private static boolean SMS_SWITCH;

  /**
   * 个推服务是否开启(服务必须参数是否配置).
   */
  @Getter
  @Setter
  private static boolean GETUI_SWITCH;

  /**
   * 短信签名.
   */
  @Getter
  @Setter
  private static String SMS_SIGN_NAME;

  /**
   * 电话.
   */
  @Getter
  @Setter
  private static String PHONE;

  /**
   * 系统名称.
   */
  @Getter
  @Setter
  private static String SYSTEM_NAME;

  /**
   * Title: getLocaleByStr Description: 获取本地语言资源.
   *
   * @param lang - 语言
   * @return Locale
   */
  public static Locale getLocaleByStr(String lang) {
    Locale locale;
    if (StrUtil.isEmpty(lang)) {
      return Locale.CHINA;
    }
    switch (lang.toLowerCase()) {
      case "en_us":
        locale = Locale.ENGLISH;
        break;
      case "en":
        locale = Locale.ENGLISH;
        break;
      case "zh_cn":
        locale = Locale.CHINA;
        break;
      case "zh_hk":
        locale = Locale.TRADITIONAL_CHINESE;
        break;
      case "zh_tw":
        locale = Locale.TRADITIONAL_CHINESE;
        break;
      default:
        locale = Locale.CHINA;
        break;
    }
    return locale;
  }

  /**
   * Title: sepDomainUser Description: 将带域的用户ID分解为用户Id和域.
   *
   * @param fullUserId - 带域的用户Id
   * @return String[] String[0] = userId, String[1] = domain
   */
  public static String[] sepDomainUser(String fullUserId) {
    if (fullUserId.contains("@")) {
      return fullUserId.split("@");
    } else {
      return new String[]{fullUserId, ParamConstant.getDEFAULT_DOMAIN()};
    }
  }

  @Getter
  @Setter
  private static Set<String> UNCHECK_URL = new HashSet<>();

  static {
    UNCHECK_URL.add("/mail/sendMail");
    UNCHECK_URL.add("/sms/sendSms");
    UNCHECK_URL.add("/weather");
    UNCHECK_URL.add("/druid");
  }
}
