package com.wafersystems.notice;

import com.wafersystems.virsical.common.security.annotation.EnableCustomFeignClients;
import com.wafersystems.virsical.common.security.annotation.EnableCustomResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * SpringBoot启动类
 *
 * @author tandk
 */
@SpringCloudApplication
@EnableCustomFeignClients
@EnableCustomResourceServer
@ImportResource(locations = {"classpath:applicationContext.xml"})
public class NoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeApplication.class, args);
    }
}
