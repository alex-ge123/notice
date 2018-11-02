package com.wafersystems.notice.message.interceptor;

import com.wafersystems.notice.util.*;
import lombok.extern.log4j.Log4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/18 10:42 Company:
 * wafersystems
 */
@Log4j
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    log.debug("Before Handshake ===> "
        + DateUtil.getDateTimeStr(DateUtil.getCurrentTimeInMillis(), null));
    if (request instanceof ServletServerHttpRequest) {
      ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
      final String token = servletRequest.getServletRequest().getParameter("token");
      log.debug("本次链接的token为：" + token);
      if (!StrUtil.isEmptyStr(token)) {
        // 使用token区分WebSocketHandler，以便定向发送消息
        try {
          attributes.put(ConfConstant.WEBSOCKET_USERID,
              AccesstokenUtil.parseToken(token, Accesstoken.TokenType.FULL).getUid());
        } catch (Exception ex) {
          log.error("websocket握手时token解析用户名异常", ex);
          return false;
        }
      } else {
        return false;
      }
    }
    super.beforeHandshake(request, response, wsHandler, attributes);
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception ex) {
    log.debug(
        "After Handshake ===> " + DateUtil.getDateTimeStr(DateUtil.getCurrentTimeInMillis(), null));
    super.afterHandshake(request, response, wsHandler, ex);
  }
}
