package com.wafersystems.notice.sms.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 短信内容数据传输对象
 *
 * @author wafer
 */
@Data
public class SmsContentValueDto implements Serializable {

  private String clientId;
  private String secret;
  private String templateId;
  private String domain;
  private String smsSign;
  private String calleeNbr;
  private String value1;
  private String value2;
  private String value3;
  private String value4;
  private String value5;
  private String value6;
}
