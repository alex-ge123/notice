package com.wafersystems.notice.constants;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

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
      case "en":
        locale = Locale.ENGLISH;
        break;
      case "zh_hk":
      case "zh_tw":
        locale = Locale.TRADITIONAL_CHINESE;
        break;
      default:
        locale = Locale.CHINA;
        break;
    }
    return locale;
  }
}
