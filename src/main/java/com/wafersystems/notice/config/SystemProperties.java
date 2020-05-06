package com.wafersystems.notice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 系统配置参数
 *
 * @author tandk
 * @date 2019/1/23 11:04
 */
@Getter
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "system")
public class SystemProperties {
  /**
   * 云服务是否开启
   */
  private boolean cloudService;

}
