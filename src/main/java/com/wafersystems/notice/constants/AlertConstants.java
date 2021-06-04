package com.wafersystems.notice.constants;

import com.wafersystems.notice.model.enums.AlertTypeEnum;

/**
 * 告警通知常量
 *
 * @author shennan
 * @date 2021年5月31日
 */
public class AlertConstants {
  // 告警类型1-站内，2-短信，3-邮件

  public static final AlertTypeEnum LOCAL = AlertTypeEnum.ALERT_LOCAL;
  public static final AlertTypeEnum SMS = AlertTypeEnum.ALERT_SMS;
  public static final AlertTypeEnum MAIL = AlertTypeEnum.ALERT_MAIL;

  // 状态1-未读，2-已读

  public static final Integer ALERT_RECORD_STATUS_UNREAD = 1;
  public static final Integer ALERT_RECORD_STATUS_READ = 2;

  // 投递状态 1-已投递，2-未投递，3-投递异常

  public static final Integer ALERT_RECORD_DELIVERY_STATUS_SEND = 1;
  public static final Integer ALERT_RECORD_DELIVERY_STATUS_UNSEND = 2;
  public static final Integer ALERT_RECORD_DELIVERY_STATUS_ERROR = 3;

  // 配置状态 1-开启，2-关闭
  public static final Integer ALERT_CONF_STATUS_OPENED = 1;
  public static final Integer ALERT_CONF_STATUS_CLOSED = 2;
}
