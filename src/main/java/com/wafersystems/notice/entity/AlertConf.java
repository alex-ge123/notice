package com.wafersystems.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.wafersystems.virsical.common.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 提醒配置表
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("alert_conf")
public class AlertConf extends Model<AlertConf> {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 租户id
   */
  private Integer tenantId;

  /**
   * 告警类型1-站内，2-短信，3-邮件
   */
  private Integer type;

  /**
   * 状态1-开启，2-关闭
   */
  private Integer state;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 修改时间
   */
  private LocalDateTime updateTime;

  /**
   * 接收人列表
   */
  @TableField(exist = false)
  private List<String> recipients;

  /**
   * 站内接收人详细信息
   */
  @TableField(exist = false)
  private List<SysUser> users;

}
