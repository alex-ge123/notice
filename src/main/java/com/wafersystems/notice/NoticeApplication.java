package com.wafersystems.notice;

import com.wafersystems.virsical.common.security.annotation.EnableCustomFeignClients;
import com.wafersystems.virsical.common.security.annotation.EnableCustomResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * SpringBoot启动类
 *
 * @author tandk
 */
@SpringCloudApplication
@EnableCustomFeignClients
@EnableCustomResourceServer
public class NoticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(NoticeApplication.class, args);
  }
}
