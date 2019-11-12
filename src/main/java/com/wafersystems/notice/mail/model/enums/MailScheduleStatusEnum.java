package com.wafersystems.notice.mail.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日程邮件类型枚举
 *
 * @author shennan
 * @date 2019/11/11 16:48
 */
@AllArgsConstructor
@Getter
public enum MailScheduleStatusEnum {

  /**
   * 创建、更新会议(带邀请按钮)
   */
  REQUEST("REQUEST", 2),

  /**
   * 创建、更新会议(无邀请按钮)
   */
  PUBLISH("PUBLISH", 2),

  /**
   * 取消会议
   */
  CANCEL("CANCEL", 3);

  /**
   * 事件类型
   */
  private String eventType;

  /**
   * 事件优先级
   */
  private int squence;
}
