package com.wafersystems.notice.base.controller;

import com.wafersystems.notice.base.model.GlobalParameter;
import com.wafersystems.notice.base.service.GlobalParamService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:30 Company:
 * wafersystems
 */
@Log4j
@RestController
@RequestMapping("/init")
public class GlobalParamController extends BaseController {

  @Autowired
  private GlobalParamService globalParamService;
  @Autowired
  private ApplicationContext resource;

  /**
   * Description: author dingfeng DateTime 2016年3月10日 下午2:18:39
   * 
   * @param obj 返回的数据,异常时表示异常信息
   * @param status 是否出现异常
   * @return 返回给前端
   */
  protected Map<String, Object> returnBackMap(Object obj, int status) {
    Map<String, Object> map = new HashMap<>();
    if (ConfConstant.RESULT_SUCCESS == status) {
      map.put("status", status);
      map.put("data", obj);
    } else {
      map.put("status", status);
      map.put("msg", obj);
    }
    map.put("time", new Date());
    return map;
  }

  @RequestMapping("/getSmsSingName")
  public Object getSmsSingName() {
    GlobalParameter gp = globalParamService.getSystemParamByParamKey("SMS_SIGN_NAME");
    if (gp == null) {
      return returnBackMap("短息签名不存在", ConfConstant.RESULT_FAIL);
    }
    return returnBackMap(gp, ConfConstant.RESULT_SUCCESS);
  }

  @RequestMapping("/setSmsSingName")
  public Object setSmsSingName(String paramValue, String lang) {
    GlobalParameter gp = globalParamService.getSystemParamByParamKey("SMS_SIGN_NAME");
    if (gp == null) {
      return returnBackMap("短息签名不存在", ConfConstant.RESULT_FAIL);
    }
    gp.setParamValue(paramValue);
    globalParamService.save(gp);
    initSysParam(lang);
    return returnBackMap(null, ConfConstant.RESULT_SUCCESS);
  }

  /**
   * 更新系统配置信息到缓存.
   * 
   * @param lang -
   * @return -
   */
  @RequestMapping(method = RequestMethod.POST)
  public Object initSysParam(String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    try {
      globalParamService.initSystemParam();
      return returnBackMap(null, ConfConstant.RESULT_SUCCESS);
    } catch (Exception ex) {
      log.error("更新系统配置信息异常：", ex);
      return returnBackMap(resource.getMessage("msg.action.fail", null, locale),
          ConfConstant.RESULT_SUCCESS);
    }
  }
}
