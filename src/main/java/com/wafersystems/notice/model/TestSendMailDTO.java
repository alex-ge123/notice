package com.wafersystems.notice.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试发送邮件对象
 *
 * @author wafer
 */
@Data
public class TestSendMailDTO implements Serializable {

  private String title;
  private String toMail;
  private String tempName;
  private String lang;
}
