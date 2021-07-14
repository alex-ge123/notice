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

  public static final String MAIL_SCHEDULED = "base:notice:mail:scheduled";

}
