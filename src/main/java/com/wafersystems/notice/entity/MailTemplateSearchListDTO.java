package com.wafersystems.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 邮件模版表
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mail_template")
public class MailTemplateSearchListDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 模板名称
   */
  private String name;

  /**
   * 模板描述
   */
  private String description;

  /**
   * 类别
   */
  private String category;

  /**
   * 修改时间
   */
  private LocalDateTime modtime;

  /**
   * 创建时间
   */
  private LocalDateTime createtime;

  /**
   * 状态：0-正常，1-停用
   */
  private Integer state;

}
