package com.wafersystems.notice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
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
  /**
   * 亚马逊邮件开关
   */
  @Value("${mail.amazon.amazon-type}")
  private boolean amazonType;
  /**
   * 带有发邮件权限的 IAM 的 ACCESS_KEY
   */
  @Value("${mail.amazon.access-key}")
  private String accessKey;
  /**
   * 带有发邮件权限的 IAM 的 SECRET_KEY
   */
  @Value("${mail.amazon.secret-key}")
  private String secretKey;
}
