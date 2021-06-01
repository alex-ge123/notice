package com.wafersystems.notice.constants;

import com.wafersystems.notice.model.enums.AlertTypeEnum;

/**
 * 告警通知常量
 *
 * @author shennan
 * @date 2021年5月31日
 */
public class AlertConstants {
  public static final AlertTypeEnum LOCAL = AlertTypeEnum.ALERT_LOCAL;
  public static final AlertTypeEnum SMS = AlertTypeEnum.ALERT_SMS;
  public static final AlertTypeEnum MAIL = AlertTypeEnum.ALERT_MAIL;
  public static final Integer ALERT_RECORD_STATUS_UNREAD = 1;
  public static final Integer ALERT_RECORD_STATUS_READ = 2;
}
