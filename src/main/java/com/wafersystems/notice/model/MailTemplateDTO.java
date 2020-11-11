package com.wafersystems.notice.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class MailTemplateDTO implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4136244528141345715L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 模板名称
   */
  private String name;

  /**
   * 模板内容
   */
  private String content;

  /**
   * 描述
   */
  private String description;

  /**
   * 修改时间
   */
  @UpdateTimestamp
  @Column(nullable = false)
  private Date modtime;

  /**
   * 类别
   */
  private String category;

  /**
   * 模板状态：0-正常，1-停用
   */
  private Integer state = 0;

  /**
   * 创建时间
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Date createtime;
}
