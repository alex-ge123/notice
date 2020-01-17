package com.wafersystems.notice.mail.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wafer
 */
@Data
@ToString
@Entity
@Table(name = "mail_template")
public class MailTemplateSearchListDto implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4136234528141345715L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /**
   * 模板名称
   */
  private String name;

  /**
   * 描述
   */
  private String description;

  /**
   * 修改时间
   */
  private Date modtime;

  /**
   * 类别
   */
  private String category;

  /**
   * 创建时间
   */
  @Column(updatable = false)
  private Date createtime;
}
