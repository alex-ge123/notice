package com.wafersystems.notice.message.service;

import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.message.model.MessageDto;
import com.wafersystems.notice.message.model.MessageToUserDto;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/25 13:44 Company:
 * wafersystems
 */
public interface MessagesService {

  /**
   * 消息发送.
   * 
   * @param message 消息体
   * @param recipientId 消息接收者ID
   * @param domain 域名
   */
  void sendMsg(MessageDto message, String recipientId, String domain, String clientId);

  /**
   * 消息同步推送.
   * 
   * @param userId -
   * @param domain -
   */
  void mergeMsg(String userId, String domain);

  /**
   * 更新消息.
   * 
   * @param msg -
   * @param userId -
   * @param domain -
   */
  Object updateMsg(MessageToUserDto msg, String userId, String domain);

  /**
   * 获取用户的消息数.
   * 
   * @param userId -
   * @param domain -
   * @param state -
   * @param type -
   * @return -
   */
  int getCount(String userId, String domain, Integer state, Integer[] type);

  /**
   * 修改消息状态.
   * 
   * @param id -
   * @param state -
   */
  void updateState(Long id, Integer state);

  /**
   * 分页获取用户消息.
   * 
   * @param userId 用户名
   * @param domain 域名
   * @param state 消息状态
   * @param type 消息类型
   * @param page 当前页数
   * @param row 每页显示行数
   * @param timeStamp 从id为多少的开始查
   * @return -
   */
  PaginationDto<MessageToUserDto> getUserMessages(String userId, String domain, Integer[] state,
                                                  Integer[] type, Integer page, Integer row, long timeStamp);
}
