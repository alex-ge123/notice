package com.wafersystems.notice.message.controller;

import com.alibaba.fastjson.support.spring.FastjsonSockJsMessageCodec;
import com.wafersystems.notice.message.interceptor.HandshakeInterceptor;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created with Intellij IDEA. Description: SockJS Author: waferzy DateTime: 2016/8/3 14:46 Company:
 * wafersystems
 */
@Log4j
@Configuration
@EnableWebMvc
@EnableWebSocket
public class SockJsConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    log.debug("Sockjs开始注册......");
    registry.addHandler(myHandler(), "/websocket").setAllowedOrigins("*")
        .addInterceptors(myInterceptor()).withSockJS()
        .setMessageCodec(new FastjsonSockJsMessageCodec());
    log.debug("Sockjs注册完成!");
  }

  @Bean
  public WebSocketHandler myHandler() {
    return new WebSocketEndPoint();
  }

  @Bean
  public HandshakeInterceptor myInterceptor() {
    return new HandshakeInterceptor();
  }
}
