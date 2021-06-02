package com.wafersystems.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 提醒记录表
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("alert_record")
public class AlertRecord extends Model<AlertRecord> {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 告警ID，业务系统定义
   */
  private String alertId;

  /**
   * 产品标识
   */
  private String product;

  /**
   * 产品名称
   */
  @TableField(exist = false)
  private String productName;

  /**
   * 标题
   */
  private String title;

  /**
   * 内容
   */
  private String content;

  /**
   * 告警类型1-站内，2-短信，3-邮件
   */
  private Integer alertType;

  /**
   * 接收人信息用户ID/手机号/邮箱
   */
  private String recipient;

  /**
   * 产品名称
   */
  @TableField(exist = false)
  private String userName;

  /**
   * 状态1-未读，2-已读
   */
  private Integer status;

  /**
   * 投递状态 1-已投递，2-未投递(投递开关关闭)，3-投递异常
   */
  private Integer deliveryStatus;

  /**
   * 租户id
   */
  private Integer tenantId;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 修改时间
   */
  private LocalDateTime updateTime;


}
