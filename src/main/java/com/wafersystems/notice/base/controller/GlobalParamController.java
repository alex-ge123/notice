package com.wafersystems.notice.base.controller;

import com.wafersystems.notice.base.model.GlobalParameter;
import com.wafersystems.notice.base.service.GlobalParamService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:30 Company:
 * wafersystems
 */
@Slf4j
@RestController
public class GlobalParamController extends BaseController {

  @Autowired
  private GlobalParamService globalParamService;
  @Autowired
  private ApplicationContext resource;

  /**
   * Description: author dingfeng DateTime 2016年3月10日 下午2:18:39
   *
   * @param obj    返回的数据,异常时表示异常信息
   * @param status 是否出现异常
   * @return 返回给前端
   */
  @Override
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

  @RequestMapping("/init/getSmsSingName")
  public Object getSmsSingName() {
    GlobalParameter gp = globalParamService.getSystemParamByParamKey("SMS_SIGN_NAME");
    if (gp == null) {
      return returnBackMap("短息签名不存在", ConfConstant.RESULT_FAIL);
    }
    return returnBackMap(gp, ConfConstant.RESULT_SUCCESS);
  }

  @RequestMapping("/init/setSmsSingName")
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
  @RequestMapping(value = "/init", method = RequestMethod.POST)
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

  /**
   * 获取所有参数
   *
   * @return Object
   */
  @GetMapping("/parameter/get")
  public R get() {
    return R.ok(globalParamService.getSystemParamList());
  }

  /**
   * 设置某个参数值
   *
   * @param paramKey   参数key
   * @param paramValue 参数值
   * @param lang       语言
   * @return Object
   */
  @PostMapping("/parameter/set")
  public R set(@RequestParam String paramKey, @RequestParam String paramValue, @RequestParam String lang) {
    GlobalParameter gp = globalParamService.getSystemParamByParamKey(paramKey);
    if (gp == null) {
      return R.fail("当前配置参数key不存在：" + paramKey);
    }
    gp.setParamValue(paramValue);
    globalParamService.save(gp);
    initSysParam(lang);
    return R.ok();
  }
}
