package com.wafersystems.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务线程池配置
 *
 * @author tandk
 * @date 2019/6/22 10:45
 */
@EnableAsync
@Configuration
public class TaskExecutorConfig {

  /**
   * 线程池维护线程的最小数量
   */
  private static final int CORE_POOL_SIZE = 5;

  /**
   * 最大线程数
   */
  private static final int MAX_POOL_SIZE = 100;

  /**
   * 队列容量
   */
  private static final int QUEUE_CAPACITY = 500;
  /**
   * 空闲时间
   */
  private static final int KEEP_ALIVE = 300;

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(CORE_POOL_SIZE);
    executor.setMaxPoolSize(MAX_POOL_SIZE);
    executor.setQueueCapacity(QUEUE_CAPACITY);
    executor.setKeepAliveSeconds(KEEP_ALIVE);
    executor.setThreadNamePrefix("taskExecutor-");
    executor.initialize();
    return executor;
  }
}
