package com.wafersystems.notice;

import com.pig4cloud.pigx.common.security.annotation.EnablePigxFeignClients;
import com.pig4cloud.pigx.common.security.annotation.EnablePigxResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * SpringBoot启动类
 *
 * @author tandk
 */
@SpringCloudApplication
@EnablePigxFeignClients
@EnablePigxResourceServer
@ImportResource(locations = {"classpath:applicationContext.xml"})
public class NoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeApplication.class, args);
    }
}
