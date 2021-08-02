package com.wafersystems.notice.constants;

/**
 * redis key常量
 *
 * @author shennan
 * @date 2020/02/04
 */
public class RedisKeyConstants {

  private RedisKeyConstants() {
  }

  /**
   * 短信发送记录key : base:notice:sms:电话:模板id:签名:SmsDto hashcode
   * key包含SmsDto hashcode为了记录电话列表，模板id，签名一致，但内容不一致的信息
   */
  public static final String SMS_KEY = "base:notice:sms:%s:%s:%s:%s";

  /**
   * 邮件发送记录key : base:notice:mail:收件人:模板id:主题:mailBean hashcode
   * key包含mailBean hashcode为了记录收件人列表，模板id，主题一致，但内容不一致的信息
   */
  public static final String MAIL_KEY = "base:notice:mail:%s:%s:%s:%s";

  /**
   * 参数缓存前缀
   */
  public static final String CACHE_KEY = "base:notice:param:";
  public static final String CACHE_HASH_KEY = "common";

  /**
   * 微软token缓存
   */
  public static final String CACHE_MICROSOFT_TOKEN = "base:notice:microsoft:token:";

  /**
   * 失败记录key
   */
  public static final String SMS_FAIL_KEY = "base:notice:sms:failed";
  public static final String MAIL_FAIL_KEY = "base:notice:mail:failed";
}
