package com.wafersystems.notice.listener;

import com.wafersystems.notice.base.service.GlobalParamService;
import com.wafersystems.notice.message.service.impl.GeTuiServiceImpl;
import com.wafersystems.notice.util.ParamConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Spring初始化类.
 *
 * @author wafer
 */
@Slf4j
@Component
public class InitSpringServletContext implements ServletContextAware {

  @Autowired
  private GlobalParamService globalParamService;
  @Autowired
  private GeTuiServiceImpl geTuiService;

  /**
   * 初始化参数.
   *
   * @param context -
   */
  @Override
  public void setServletContext(ServletContext context) {
    System.setProperty("java.awt.headless", "true");
    // 初始化系统参数
    globalParamService.initSystemParam();
    // 初始化个推服务
    if (ParamConstant.isGETUI_SWITCH()) {
      log.debug("初始化个推服务");
      try {
        geTuiService.init();
        log.debug("个推服务初始化完毕");
      } catch (Exception exception) {
        log.debug("个推服务初始化失败!请检查个推配置项！" + exception.getMessage());
      }
    }
  }
}
