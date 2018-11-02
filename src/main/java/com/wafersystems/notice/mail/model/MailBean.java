package com.wafersystems.notice.mail.model;

import com.wafersystems.notice.util.ConfConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("PMD.UnusedPrivateField")
@Data
public class MailBean implements Serializable {

  private String toEmails;
  private String copyTo;
  private String subject;
  private Map data; // 邮件数据(html格式)
  private TemContentVal temVal; // 邮件数据(模版形式)
  private String template; // 邮件传入模板
  private ConfConstant.TypeEnum type; // 使用模版类型
}
