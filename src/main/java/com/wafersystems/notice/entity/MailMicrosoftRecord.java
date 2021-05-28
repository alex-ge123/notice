package com.wafersystems.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * microsoft事件id记录表
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mail_microsoft_record")
public class MailMicrosoftRecord extends Model<MailMicrosoftRecord> {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * uuid
   */
  private String uuid;

  /**
   * 事件id
   */
  private String eventid;

  /**
   * 创建时间
   */
  private LocalDateTime createtime;
}
