package com.wafersystems.notice.sms.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/5/19 10:00 Company:
 * wafersystems
 */
@SuppressWarnings("PMD.UnusedPrivateField")
@Data
public class SmsContentValueDto implements Serializable {

  private String clientId;
  private String templetId;
  private String secret;
  private String domain;
  private String calleeNbr;
  private String value1;
  private String value2;
  private String value3;
  private String value4;
  private String value5;
  private String value6;
}
