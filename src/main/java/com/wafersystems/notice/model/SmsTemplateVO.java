package com.wafersystems.notice.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wafer
 */
@Data
@ToString
public class SmsTemplateVO implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4136244528141345715L;

  private String id;

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
   * 类别
   */
  private String category;

  /**
   * 模板状态：0-正常，1-停用
   */
  private Integer state = 0;

  /**
   * 修改时间
   */
  private Date modtime;


  /**
   * 创建时间
   */
  private Date createtime;
}
