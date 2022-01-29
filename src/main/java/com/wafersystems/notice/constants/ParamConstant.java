package com.wafersystems.notice.constants;

import cn.hutool.core.util.StrUtil;
import com.wafersystems.virsical.common.core.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Locale;

/**
 * @author wafer
 */
@Slf4j
public class ParamConstant {
  private ParamConstant() {
  }

  //短信相关配置
  /**
   * 短信接口clientId.
   */
  private static String urlSmsClientId;
  /**
   * 短信接口secret.
   */

  private static String urlSmsSecret;

  /**
   * 短信接口服务.
   */

  private static String urlSmsServer;

  /**
   * 短信服务是否开启(服务必须参数是否配置).
   */

  private static boolean smsSwitch;

  /**
   * 短信签名.
   */

  private static String smsSignName;

  /**
   * 短信重发次数
   */

  private static Integer smsRepeatCount;


  //邮件相关配置
  /**
   * 默认logo.
   */

  private static String logoDefault;

  /**
   * 系统邮件发送邮件.
   */

  private static String defaultMailFrom;

  /**
   * 系统邮件服务.
   */

  private static String defaultMailHost;


  /**
   * 系统邮件端口.
   */

  private static Integer defaultMailPort;

  /**
   * 系统邮件密码.
   */

  private static String defaultMailPassword;

  /**
   * 系统邮件认证.
   */

  private static String defaultMailAuth;

  /**
   * 系统邮件用户名
   */

  private static String defaultMailUsername;

  /**
   * 系统邮件编码.
   */

  private static String defaultMailCharset;

  /**
   * 系统邮件显示名称.
   */

  private static String defaultMailMailName;

  /**
   * 默认重发次数.
   */
  private static Integer defaultRepeatCount;

  /**
   * 默认邮件传输加密方式
   */
  private static Integer defaultMailEncryMode;

  /**
   * 邮件服务是否开启(服务必须参数是否配置).
   */
  private static boolean emailSwitch;


  //其他
  /**
   * 电话.
   */

  private static String phone;

  /**
   * 系统名称.
   */

  private static String systemName;

  /**
   * 系统默认域名.
   */

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

  private static String getCommonCacheByKey(String key) {
    StringRedisTemplate redisTemplate = SpringContextHolder.getBean("stringRedisTemplate");
    String cacheKey = RedisKeyConstants.CACHE_KEY + RedisKeyConstants.CACHE_HASH_KEY;
    String value = (String) redisTemplate.opsForHash().get(cacheKey, key);
    log.debug("redis缓存获取key={},value={}", key, value);
    return value;
  }

  public static String getUrlSmsClientId() {
    final String value = getCommonCacheByKey(CommonParamConstants.URL_SMS_CLIENTID);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.URL_SMS_CLIENTID, urlSmsClientId);
      return urlSmsClientId;
    }
    return value;
  }

  public static void setUrlSmsClientId(String urlSmsClientId) {
    ParamConstant.urlSmsClientId = urlSmsClientId;
  }

  public static String getUrlSmsSecret() {
    final String value = getCommonCacheByKey(CommonParamConstants.URL_SMS_SECRET);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.URL_SMS_SECRET, urlSmsSecret);
      return urlSmsSecret;
    }
    return value;
  }

  public static void setUrlSmsSecret(String urlSmsSecret) {
    ParamConstant.urlSmsSecret = urlSmsSecret;
  }

  public static String getUrlSmsServer() {
    final String value = getCommonCacheByKey(CommonParamConstants.URL_SMS_SERVER);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.URL_SMS_SERVER, urlSmsServer);
      return urlSmsServer;
    }
    return value;
  }

  public static void setUrlSmsServer(String urlSmsServer) {
    ParamConstant.urlSmsServer = urlSmsServer;
  }

  public static boolean isSmsSwitch() {
    return smsSwitch;
  }

  public static void setSmsSwitch(boolean smsSwitch) {
    ParamConstant.smsSwitch = smsSwitch;
  }

  public static String getSmsSignName() {
    return smsSignName;
  }

  public static void setSmsSignName(String smsSignName) {
    ParamConstant.smsSignName = smsSignName;
  }

  public static Integer getSmsRepeatCount() {
    final String value = getCommonCacheByKey(CommonParamConstants.SMS_REPEAT_COUNT);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.SMS_REPEAT_COUNT, smsRepeatCount);
      return smsRepeatCount;
    }
    return Integer.valueOf(value);
  }

  public static void setSmsRepeatCount(Integer smsRepeatCount) {
    ParamConstant.smsRepeatCount = smsRepeatCount;
  }

  public static String getLogoDefault() {
    final String value = getCommonCacheByKey(CommonParamConstants.LOGO_DEFALUT);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.LOGO_DEFALUT, logoDefault);
      return logoDefault;
    }
    return value;
  }

  public static void setLogoDefault(String logoDefault) {
    ParamConstant.logoDefault = logoDefault;
  }

  public static String getDefaultMailFrom() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_FROM);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_FROM, defaultMailFrom);
      return defaultMailFrom;
    }
    return value;
  }

  public static void setDefaultMailFrom(String defaultMailFrom) {
    ParamConstant.defaultMailFrom = defaultMailFrom;
  }

  public static String getDefaultMailHost() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_HOST);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_HOST, defaultMailHost);
      return defaultMailHost;
    }
    return value;
  }

  public static void setDefaultMailHost(String defaultMailHost) {
    ParamConstant.defaultMailHost = defaultMailHost;
  }

  public static Integer getDefaultMailPort() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_PORT);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_PORT, defaultMailPort);
      return defaultMailPort;
    }
    return Integer.valueOf(value);
  }

  public static void setDefaultMailPort(Integer defaultMailPort) {
    ParamConstant.defaultMailPort = defaultMailPort;
  }

  public static String getDefaultMailPassword() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_PASSWORD);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_PASSWORD, defaultMailPassword);
      return defaultMailPassword;
    }
    return value;
  }

  public static void setDefaultMailPassword(String defaultMailPassword) {
    ParamConstant.defaultMailPassword = defaultMailPassword;
  }

  public static String getDefaultMailAuth() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_AUTH);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_AUTH, defaultMailAuth);
      return defaultMailAuth;
    }
    return value;
  }

  public static void setDefaultMailAuth(String defaultMailAuth) {
    ParamConstant.defaultMailAuth = defaultMailAuth;
  }

  public static String getDefaultMailUsername() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_USERNAME);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_USERNAME, defaultMailUsername);
      return defaultMailUsername;
    }
    return value;
  }

  public static void setDefaultMailUsername(String defaultMailUsername) {
    ParamConstant.defaultMailUsername = defaultMailUsername;
  }

  public static String getDefaultMailCharset() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_CHARSET);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_CHARSET, defaultMailCharset);
      return defaultMailCharset;
    }
    return value;
  }

  public static void setDefaultMailCharset(String defaultMailCharset) {
    ParamConstant.defaultMailCharset = defaultMailCharset;
  }

  public static String getDefaultMailMailName() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_MAILNAME);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_MAILNAME, defaultMailMailName);
      return defaultMailMailName;
    }
    return value;
  }

  public static void setDefaultMailMailName(String defaultMailMailName) {
    ParamConstant.defaultMailMailName = defaultMailMailName;
  }

  public static Integer getDefaultRepeatCount() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_REPEAT_COUNT);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_REPEAT_COUNT, defaultRepeatCount);
      return defaultRepeatCount;
    }
    return Integer.valueOf(value);
  }

  public static void setDefaultRepeatCount(Integer defaultRepeatCount) {
    ParamConstant.defaultRepeatCount = defaultRepeatCount;
  }

  public static boolean isEmailSwitch() {
    return emailSwitch;
  }

  public static void setEmailSwitch(boolean emailSwitch) {
    ParamConstant.emailSwitch = emailSwitch;
  }

  public static String getPhone() {
    final String value = getCommonCacheByKey(CommonParamConstants.PHONE);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.PHONE, phone);
      return phone;
    }
    return value;
  }

  public static void setPhone(String phone) {
    ParamConstant.phone = phone;
  }

  public static String getSystemName() {
    final String value = getCommonCacheByKey(CommonParamConstants.SYSTEM_NAME);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.SYSTEM_NAME, systemName);
      return systemName;
    }
    return value;
  }

  public static void setSystemName(String systemName) {
    ParamConstant.systemName = systemName;
  }

  public static String getDefaultDomain() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_DOMAIN);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_DOMAIN, defaultDomain);
      return defaultDomain;
    }
    return value;
  }

  public static void setDefaultDomain(String defaultDomain) {
    ParamConstant.defaultDomain = defaultDomain;
  }

  public static Integer getDefaultMailEncryModen() {
    final String value = getCommonCacheByKey(CommonParamConstants.DEFAULT_MAIL_ENCRYMODEN);
    if (StrUtil.isBlank(value)) {
      log.debug("redis缓存查询{}为空，取本地缓存值：{}", CommonParamConstants.DEFAULT_MAIL_ENCRYMODEN, defaultMailEncryMode);
      return defaultMailEncryMode;
    }
    return Integer.valueOf(value);
  }

  public static void setDefaultMailEncryMode(Integer defaultMailEncryMode) {
    ParamConstant.defaultMailEncryMode = defaultMailEncryMode;
  }
}
