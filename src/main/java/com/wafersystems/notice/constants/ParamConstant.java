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
  //短信相关配置
  /**
   * 短信接口clientId.
   */
  @Getter
  @Setter
  private static String urlSmsClientId;
  /**
   * 短信接口secret.
   */
  @Getter
  @Setter
  private static String urlSmsSecret;

  /**
   * 短信接口服务.
   */
  @Getter
  @Setter
  private static String urlSmsServer;

  /**
   * 短信服务是否开启(服务必须参数是否配置).
   */
  @Getter
  @Setter
  private static boolean smsSwitch;

  /**
   * 短信签名.
   */
  @Getter
  @Setter
  private static String smsSignName;

  /**
   * 短信重发次数
   */
  @Getter
  @Setter
  private static Integer smsRepeatCount;



  //邮件相关配置
  /**
   * 默认logo.
   */
  @Getter
  @Setter
  private static String logoDefault;

  /**
   * 系统邮件发送邮件.
   */
  @Getter
  @Setter
  private static String defaultMailFrom;

  /**
   * 系统邮件服务.
   */
  @Getter
  @Setter
  private static String defaultMailHost;


  /**
   * 系统邮件端口.
   */
  @Getter
  @Setter
  private static Integer defaultMailPort;

  /**
   * 系统邮件密码.
   */
  @Getter
  @Setter
  private static String defaultMailPassword;

  /**
   * 系统邮件认证.
   */
  @Getter
  @Setter
  private static String defaultMailAuth;

  /**
   * 系统邮件用户名
   */
  @Getter
  @Setter
  private static String defaultMailUsername;

  /**
   * 系统邮件编码.
   */
  @Getter
  @Setter
  private static String defaultMailCharset;

  /**
   * 系统邮件显示名称.
   */
  @Getter
  @Setter
  private static String defaultMailMailName;

  /**
   * 默认重发次数.
   */
  @Getter
  @Setter
  private static Integer defaultRepeatCount;

  /**
   * 邮件服务是否开启(服务必须参数是否配置).
   */
  @Getter
  @Setter
  private static boolean emailSwitch;



  //其他
  /**
   * 电话.
   */
  @Getter
  @Setter
  private static String phone;

  /**
   * 系统名称.
   */
  @Getter
  @Setter
  private static String systemName;

  /**
   * 系统默认域名.
   */
  @Getter
  @Setter
  private static String defaultDomain;


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
