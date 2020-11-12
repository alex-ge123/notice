package com.wafersystems.notice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 邮件参数配置
 *
 * @author shennan
 * @date 2020/2/4
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
  private Map<String, Object> props;
}
