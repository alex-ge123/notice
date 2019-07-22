package com.wafersystems.notice.config;

import com.wafersystems.notice.util.EmailUtil;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Velocity配置
 *
 * @author tandk
 * @date 2019/6/22 08:59
 */
@Configuration
public class VelocityConfig {

  @Value("${mail.template.path}")
  String mailTemplatePath;

  @Bean
  public VelocityEngine velocityEngineFactoryBean() {
    Properties p = new Properties();
    //设置输入输出编码类型。和这次说的解决的问题无关
    p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
    p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
    //指定一个绝对路径，所有的模板都放在该路径下
    p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, mailTemplatePath);
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init(p);
    return velocityEngine;
  }

  @Bean
  public EmailUtil mailUtil(VelocityEngine velocityEngine) {
    EmailUtil emailUtil = new EmailUtil();
    emailUtil.setVelocityEngine(velocityEngine);
    return emailUtil;
  }
}
