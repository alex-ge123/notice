package com.wafersystems.notice.message.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wafersystems.notice.message.model.InitUnReadMsg;
import com.wafersystems.notice.message.model.MessageToUserDto;
import com.wafersystems.notice.message.model.MessageType;
import com.wafersystems.notice.message.service.MessagesService;
import com.wafersystems.notice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/18 10:39 Company: wafersystems
 */

@Slf4j
@Component
public class WebSocketEndPoint extends TextWebSocketHandler {

  @Autowired
  private MessagesService messagesService;

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    try {
      if (0 == message.getPayloadLength() || "p".equalsIgnoreCase(message.getPayload().toString())) {
        return;
      }
      if (0 != message.getPayloadLength()) {
        String userId = MapUtils.getString(session.getAttributes(), ConfConstant.WEBSOCKET_USERID);
        String[] userIdDomain = ParamConstant.sepDomainUser(userId);
        MessageToUserDto msg = JSON.parseObject(message.getPayload().toString(), MessageToUserDto.class);
        msg.getMessage().setTimeStamp(DateUtil.getCurrentTimeInMillis());
        if (!StrUtil.isNullObject(msg.getAction())) {
          messagesService.updateMsg(msg, userIdDomain[0], userIdDomain[1]);
        } else {
          log.debug("WebSocketEndPoint.handlerTextMessage... === > "
              + (DateUtil.getDateTimeStr(DateUtil.getCurrentTimeInMillis(), null) 
                  + ",接收用户为：【" + session.getId() + "】" + WebSocketSessionUtil
                  .getKey(userIdDomain[0], userIdDomain[1])));
          WebSocketSessionUtil.sendMessage(userIdDomain[0], userIdDomain[1], message.getPayload().toString());
        }
      }
    } catch (JSONException exception) {
      log.debug("非json信息" + message.getPayload().toString());
    } catch (Exception exception) {
      log.error("handleTextMessage error：", exception);
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    try {
      String[] userIdDomain =
          ParamConstant.sepDomainUser(MapUtils.getString(session.getAttributes(), ConfConstant.WEBSOCKET_USERID));
      WebSocketSessionUtil.add(userIdDomain[0], userIdDomain[1], session);
      log.debug("当前用户名【" + userIdDomain[0] + "】.域名【" + userIdDomain[1] + "】socket连接【" + session.getId() + "】用户数为"
          + WebSocketSessionUtil.getSize(userIdDomain[0], userIdDomain[1]));
      // WebSocketSessionUtil.sendMessage(userIdDomain[0], userIdDomain[1], JSON.toJSONString(
      // this.getUnReadMsgs(userIdDomain[0], userIdDomain[1], ConfConstant.MESSAGE_NORMAL)));
      // 连接后只给当前连接设备发送消息，防止重连次数过多导致 broken pipe
      session.sendMessage(new TextMessage(JSON.toJSONString(this.getUnReadMsgs(userIdDomain[0], userIdDomain[1],
          ConfConstant.MESSAGE_NORMAL))));
    } catch (Exception exception) {
      log.error("afterConnectionEstablished error：", exception);
    }
  }

  private Map<String, InitUnReadMsg> getUnReadMsgs(String userId, String domain, Integer state) {
    // 获取未读消息数并加载初始化消息
    Map<String, InitUnReadMsg> unRead = new HashMap<>();
    int count = messagesService.getCount(userId, domain, state, new Integer[] { MessageType.handel, MessageType.task });
    List<MessageToUserDto> list =
        messagesService.getUserMessages(userId, domain, new Integer[] { state },
            new Integer[] { MessageType.handel, MessageType.task }, Integer.parseInt(ConfConstant.PAGE_DEFAULT_LENGTH),
            3, 0).getRows();
    unRead.put(MessageType.handelRen, new InitUnReadMsg(count, list));
    count = messagesService.getCount(userId, domain, state, new Integer[] { MessageType.text });
    list =
        messagesService.getUserMessages(userId, domain, new Integer[] { state }, new Integer[] { MessageType.text },
            Integer.parseInt(ConfConstant.PAGE_DEFAULT_LENGTH), 3, 0).getRows();
    unRead.put(MessageType.textRen, new InitUnReadMsg(count, list));
    count = messagesService.getCount(userId, domain, state, new Integer[] { MessageType.task });
    list =
        messagesService.getUserMessages(userId, domain, new Integer[] { state }, new Integer[] { MessageType.task },
            Integer.parseInt(ConfConstant.PAGE_DEFAULT_LENGTH), 3, 0).getRows();
    unRead.put(MessageType.taskRen, new InitUnReadMsg(count, list));
    return unRead;
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    try {
      String[] userIdDomain =
          ParamConstant.sepDomainUser(MapUtils.getString(session.getAttributes(), ConfConstant.WEBSOCKET_USERID));
      log.debug("【" + session.getId() + "】已关闭连接,关闭码[" + status.getCode() + "],缘由[" + status.getReason() + "]");
      WebSocketSessionUtil.remove(userIdDomain[0], userIdDomain[1], session);
    } catch (Exception exception) {
      log.error("afterConnectionClosed error：", exception);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    try {
      log.warn("【" + session.getId() + "】websocket connection error：", exception);
      String[] userIdDomain =
          ParamConstant.sepDomainUser(MapUtils.getString(session.getAttributes(), ConfConstant.WEBSOCKET_USERID));
      if (session.isOpen()) {
        session.close();
        log.debug("websocket connection closed......");
      }
      WebSocketSessionUtil.remove(userIdDomain[0], userIdDomain[1], session);
    } catch (Exception exception2) {
      log.error("handleTransportError error：", exception2);
    }
  }

  @Override
  public boolean supportsPartialMessages() {
    return true;
  }

  /**
   * 给某个用户发送消息.
   * 
   * @param userId -
   * @param domain -
   * @param message -
   */
  public void sendMessageToUser(String userId, String domain, TextMessage message) {
    // WebSocketSessionUtil.sendMessage(userId, domain, message.getPayload());
    WebSocketSessionUtil.sendMessage(userId, domain, JSON.toJSONString(
        this.getUnReadMsgs(userId, domain, ConfConstant.MESSAGE_NORMAL),
        SerializerFeature.DisableCircularReferenceDetect));
  }
}
