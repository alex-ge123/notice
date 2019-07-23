package com.wafersystems.notice.sms.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 短信内容数据传输对象
 *
 * @author wafer
 */
@Data
public class SmsDTO implements Serializable {

  /**
   * clientId
   */
  private String clientId;
  /**
   * secret
   */
  private String secret;
  /**
   * 短信模板id
   */
  private String templateId;
  /**
   * 域
   */
  private String domain;
  /**
   * 短信签名
   */
  private String smsSign;
  /**
   * 手机号集合
   */
  private List<String> phoneList;
  /**
   * 参数集合
   */
  private List<String> paramList;
  /**
   * 扩展字段
   */
  private String extend;
}
