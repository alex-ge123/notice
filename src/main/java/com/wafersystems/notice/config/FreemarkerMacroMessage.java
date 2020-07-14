package com.wafersystems.notice.config;

import com.wafersystems.notice.constants.ParamConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author shennan
 * @date 2019/10/11 15:03
 * freemarker宏使用
 */
@Component
public class FreemarkerMacroMessage {

  @Autowired
  private ApplicationContext resource;

  public String getMessage(String s, String locale) {
    return resource.getMessage(s, null, ParamConstant.getLocaleByStr(locale));
  }

}
