package com.wafersystems.notice.message.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/18 14:22 Company:
 * wafersystems
 */
@SuppressWarnings("PMD.UnusedPrivateField")
@Data
@ToString
@Entity
@Table(name = "ntc_messages")
public class MessageDto implements Serializable {

  /**
	 *
	 */
  private static final long serialVersionUID = -4136244528141331715L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  /**
   * 发送者ID
   */
  private String senderId;
  /**
   * 发送者姓名
   */
  private String senderName;
  /**
   * 消息内容
   */
  @Column(length = 3000)
  private String content;
  /**
   * 消息内容类型
   */
  private String contentType;
  /**
   * 消息标题
   */
  private String title;
  /**
   * 消息logo
   */
  private String logo;
  /**
   * 按钮
   */
  @Column(length = 500)
  private String urls;
  /**
   * 消息类型
   */
  private Integer type;
  /**
   * 时间
   */
  private Long timeStamp;
}
