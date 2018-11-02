package com.wafersystems.notice.message.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/18 15:45 Company:
 * wafersystems
 */
@SuppressWarnings("PMD.UnusedPrivateField")
@Data
@Entity
@Table(name = "ntc_message_to_user")
public class MessageToUserDto implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 6638156774950558668L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String userId;
  @JoinColumn
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private MessageDto message;
  /**
   * 消息状态(1-未读|2-已读|3-已删除|4-已处理)
   */
  private Integer state;
  private String domain;
  /**
   * 操作(0-置为已读|1-执行链接)
   */
  @JSONField(serialize = false)
  @Transient
  private Integer action;
  @JSONField(serialize = false)
  @Transient
  private String url;
}
