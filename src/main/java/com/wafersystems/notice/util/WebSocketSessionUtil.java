package com.wafersystems.notice.util;

import lombok.extern.log4j.Log4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/18 11:45 Company:
 * wafersystems
 */
@Log4j
public class WebSocketSessionUtil {


  private static Map<String, List<WebSocketSession>> clients = new ConcurrentHashMap<>();

  /**
   * 保存一个连接.
   * 
   * @param userId -
   * @param domain -
   * @param session -
   */
  public static void add(String userId, String domain, WebSocketSession session) {
    List<WebSocketSession> list = get(userId, domain);
    if (StrUtil.isEmptyList(list)) {
      list = new ArrayList<>();
    }
    list.add(session);
    clients.put(getKey(userId, domain), list);
  }

  /**
   * 获取一个连接.
   * 
   * @param userId -
   * @param domain -
   * @return -
   */
  public static List<WebSocketSession> get(String userId, String domain) {
    return clients.get(getKey(userId, domain));
  }

  /**
   * 移除一个连接.
   * 
   * @param userId -
   * @param domain -
   */
  public static void remove(String userId, String domain, WebSocketSession session) {
    log.debug("断开连接：" + getKey(userId, domain));
    List<WebSocketSession> list = clients.get(getKey(userId, domain));
    if (!StrUtil.isEmptyList(list)) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        if (session.equals(iterator.next())) {
          if (1 == list.size()) {
            clients.remove(getKey(userId, domain));
          } else {
            iterator.remove();
          }
          log.debug("用户名【" + userId + "】.域名【" + domain + "】socket连接用户数为：" + getSize(userId, domain));
          break;
        }
      }
    }
  }

  /**
   * 组装sessionId.
   * 
   * @param userId -
   * @return -
   */
  public static String getKey(String userId, String domain) {
    return "webSocketSession.domain[" + domain + "].userId[" + userId + "]";
  }

  /**
   * 判断是否有效连接.
   * 
   * @param userId -
   * @param domain -
   * @return -
   */
  public static boolean hasConnection(String userId, String domain) {
    return !StrUtil.isEmptyList(get(userId, domain));
  }

  /**
   * 获取连接数的数量.
   * 
   * @return -
   */
  public static int getSize(String userId, String domain) {
    List<WebSocketSession> list = get(userId, domain);
    if (StrUtil.isEmptyList(list)) {
      return 0;
    } else {
      return list.size();
    }
  }

  /**
   * 发送消息到客户端.
   * 
   * @param userId -
   * @param domain -
   * @param message -
   */
  public static void sendMessage(String userId, String domain, String message) {
    String key = getKey(userId, domain);
    if (!hasConnection(userId, domain)) {
      log.warn("当前连接：" + key + "未打开......");
      // 后续处理将此次消息存库，待重新握手成功后再推送消息
      // throw new NullPointerException(getKey(userId, domain) + " connection does not exist");
    } else {
      List<WebSocketSession> list = get(userId, domain);
      try {
        log.debug("发送消息给连接 --->" + key);
        Iterator<WebSocketSession> iterator = list.iterator();
        while (iterator.hasNext()) {
          WebSocketSession session = iterator.next();
          try {
            if (!StrUtil.isNullObject(session) && session.isOpen()) {
              session.sendMessage(new TextMessage(message));
            } else {
              iterator.remove();
            }
          } catch (Exception ex) {
            iterator.remove();
          }
        }
        log.debug("发送websocket消息给帐号【" + key + "】,帐号共连接" + getSize(userId, domain) + "台设备，本次共有"
            + list.size() + "台设备接收消息！");
      } catch (Exception ex) {
        log.error("websocket sendMessage exception: " + key, ex);
      }
    }
  }
}
