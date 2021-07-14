package com.wafersystems.notice;

import com.wafersystems.virsical.common.security.annotation.EnableCustomFeignClients;
import com.wafersystems.virsical.common.security.annotation.EnableCustomResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * SpringBoot启动类
 *
 * @author tandk
 */
@SpringCloudApplication
@EnableCustomFeignClients
@EnableCustomResourceServer
@EnableWebMvc
@EnableScheduling
@EnableAsync
public class NoticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(NoticeApplication.class, args);
  }
}
