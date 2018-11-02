package com.wafersystems.notice.listener;

import lombok.extern.log4j.Log4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/26 15:07 Company:
 * wafersystems
 */
@WebListener
@Log4j
public class ServletServiceListener implements ServletContextListener {

  /**
   * Description: DateTime 2016年2月6日 上午11:17:25.
   * 
   * @param event -
   */
  public void contextInitialized(ServletContextEvent event) {
    log.debug("------ServletContext start-------");
  }

  /**
   * Description: DateTime 2016年2月6日 上午11:17:15.
   * 
   * @param event -
   */
  public void contextDestroyed(ServletContextEvent event) {
    log.debug("--ServletContext end------");
  }
}
