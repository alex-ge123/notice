package com.wafersystems.notice;

import com.wafersystems.virsical.common.security.annotation.EnableCustomFeignClients;
import com.wafersystems.virsical.common.security.annotation.EnableCustomResourceServer;
import org.mybatis.spring.annotation.MapperScan;
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
//@EnableWebMvc
@MapperScan("com.wafersystems.*.mapper")
public class NoticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(NoticeApplication.class, args);
  }
}
