package com.wafersystems.notice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 短信拦截配置类
 *
 * @author shennan
 * @date 2020/2/4
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "intercept")
public class SendInterceptProperties {
  private boolean smsEnabled;
  private Integer smsTimeHorizon;
  private boolean mailEnabled;
  private Integer mailTimeHorizon;
}
