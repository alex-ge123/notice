package com.wafersystems.notice.model;

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
public class MailTemplateSearchListDTO implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4136234528141345715L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
   * 模板状态：0-正常，1-停用
   */
  private Integer state = 0;

  /**
   * 创建时间
   */
  @Column(updatable = false)
  private Date createtime;
}
