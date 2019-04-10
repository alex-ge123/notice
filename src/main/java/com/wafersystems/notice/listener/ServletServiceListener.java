package com.wafersystems.notice.listener;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ServletServiceListener
 *
 * @author wafer
 */
@WebListener
@Slf4j
public class ServletServiceListener implements ServletContextListener {

  /**
   * Description: DateTime 2016年2月6日 上午11:17:25.
   *
   * @param event -
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    log.debug("------ServletContext start-------");
  }

  /**
   * Description: DateTime 2016年2月6日 上午11:17:15.
   *
   * @param event -
   */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    log.debug("--ServletContext end------");
  }
}
