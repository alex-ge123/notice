package com.wafersystems.notice.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知告警类型枚举
 *
 * @author shennan
 * @date 2021年5月31日
 */
@AllArgsConstructor
@Getter
public enum AlertTypeEnum {

  /**
   * 站内消息
   */
  ALERT_LOCAL("local", 1),

  /**
   * 短信通知
   */
  ALERT_SMS("sms", 2),

  /**
   * 邮件通知
   */
  ALERT_MAIL("mail", 3);

  /**
   * 通知告警类型名称
   */
  private String typeName;

  /**
   * 通知告警类型
   */
  private int type;
}
