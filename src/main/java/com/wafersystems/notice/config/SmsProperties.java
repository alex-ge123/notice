package com.wafersystems.notice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 邮件参数配置
 *
 * @author shennan
 * @date 2020/2/4
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "cmbc.sms")
public class SmsProperties {
  private String smsIp;
  private Integer smsPort;
  //是否需要国家号  true 不需要
  private boolean enabled;
  private List<String> ignore;
}
