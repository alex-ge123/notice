package com.wafersystems.notice.config;

import com.wafersystems.virsical.common.core.constant.NoticeMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置
 *
 * @author tandk
 * @date 2019/6/11 11:09
 */
@Configuration
public class RabbitMqConfig {

  /**
   * 配置消息转换器转Json
   *
   * @return 消息转换器
   */
  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * 通知服务邮件队列
   */
  public static final String QUEUE_NOTICE_MAIL = "queue.notice.mail";

  /**
   * 通知服务短信队列
   */
  public static final String QUEUE_NOTICE_SMS = "queue.notice.sms";

  /**
   * 租户队列
   */
  public static final String QUEUE_NOTICE_UPMS_TENANT = "queue.notice.upms.tenant";

  /**
   * 告警通知消息队列
   */
  public static final String QUEUE_NOTICE_ALERT = "queue.notice.alert";

  /**
   * DirectExchange
   *
   * @return DirectExchange
   */
  @Bean
  public DirectExchange noticeDirectExchange() {
    return new DirectExchange(NoticeMqConstants.EXCHANGE_DIRECT_NOTICE);
  }

  /**
   * 邮件队列
   *
   * @return Queue
   */
  @Bean
  public Queue queueNoticeMail() {
    return new Queue(QUEUE_NOTICE_MAIL);
  }

  /**
   * 短信队列
   *
   * @return Queue
   */
  @Bean
  public Queue queueNoticeSms() {
    return new Queue(QUEUE_NOTICE_SMS);
  }

  /**
   * 绑定邮件队列到交换机
   *
   * @return Binding
   */
  @Bean
  public Binding mailQueueBinding() {
    return BindingBuilder.bind(queueNoticeMail()).to(noticeDirectExchange()).with(NoticeMqConstants.ROUTINT_KEY_MAIL);
  }

  /**
   * 绑定短信队列到交换机
   *
   * @return Binding
   */
  @Bean
  public Binding smsQueueBinding() {
    return BindingBuilder.bind(queueNoticeSms()).to(noticeDirectExchange()).with(NoticeMqConstants.ROUTINT_KEY_SMS);
  }

}