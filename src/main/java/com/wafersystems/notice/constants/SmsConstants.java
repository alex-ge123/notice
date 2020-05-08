package com.wafersystems.notice.constants;

/**
 * 短信常量
 *
 * @author shennan
 * @date 2019/11/21 9:07
 */
public class SmsConstants {
  private SmsConstants() {
  }

  /**
   * 中国电话区号
   */
  public static final String CHINA_AREA_CODE = "+86";

  /**
   * 区号前缀
   */
  public static final String AREA_CODE_PREFIX = "+";

  /**
   * https
   */
  public static final String HTTPS = "https";

  /**
   * 成功码
   */
  public static final Integer SUCCESS_CODE = 200;

  /**
   * 短信数量缓存key
   */
  public static final String SMS_NUM_KEY = "base:sms_num:";

}
