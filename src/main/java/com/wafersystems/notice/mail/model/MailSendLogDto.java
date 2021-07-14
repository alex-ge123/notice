package com.wafersystems.notice.mail.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "mail_sendlog")
public class MailSendLogDto implements Serializable {

  private static final long serialVersionUID = 1353510083963002669L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String mailKey;

  /**
   * 发送状态（-1 重复邮件  0 成功  1+ 失败）
   */
  private int status;

  /**
   * 发送时间
   */
  private Date sendTime;

  /**
   * 邮件内容
   */
  private String mailStr;

}
