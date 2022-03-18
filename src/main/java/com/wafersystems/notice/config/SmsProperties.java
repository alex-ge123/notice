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
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
   /**
   * 亚马逊短信开关
   */
   @Value("${sms.amazon.amazon-type}")
   private boolean amazonType;
   /**
   * 发件人id
   */
   @Value("${sms.amazon.senderID}")
   private String senderID;

   /**
   * 愿意用于发送 SMS 消息的最高价格（以美元为单位）
   */
   @Value("${sms.amazon.maxPrice}")
   private String maxPrice;

   /**
   * Transactional 重要消息  Promotional（默认）– 不重要的消息，例如营销消息。
   */
   @Value("${sms.amazon.smsType}")
   private String smsType;

   /**
   * 带有发短信权限的 IAM 的 ACCESS_KEY
   */
   @Value("${sms.amazon.iamAccessKey}")
   private String iamAccessKey;

   /**
   * 带有发短信权限的 IAM 的 SECRET_KEY
   */
   @Value("${sms.amazon.iamSecretKey}")
   private String iamSecretKey;

   /**
   * ARN Key 地区
   */
   @Value("${sms.amazon.regionKey}")
   private String regionKey;
}
