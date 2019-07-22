package com.wafersystems.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 国际化配置
 *
 * @author tandk
 * @date 2019/6/22 10:14
 */
@Configuration
public class MessageSourceConfig {
  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
    resourceBundleMessageSource.setBasename("messages");
    return resourceBundleMessageSource;
  }
}
