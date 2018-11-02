package com.wafersystems.notice.message.model;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/22 10:51 Company:
 * wafersystems
 */
public class MessageType {

  /**
   * 待办消息.
   */
  public static final Integer handel = 1;
  public static final String handelRen = "HANDEL";

  /**
   * 纯文本消息.
   */
  public static final Integer text = 2;
  public static final String textRen = "TEXT";

  /**
   * 任务消息.
   */
  public static final Integer task = 3;
  public static final String taskRen = "TASK";

  /**
   * 消息内容类型.
   */
  public enum ContentType {
    /**
     * 未加密文本消息.
     */
    TEXT_PLAIN,

    /**
     * 加密文本消息.
     */
    TEXT_CYBE
  }
}
