package com.wafersystems.notice.base.controller;

import com.wafersystems.notice.base.model.GlobalParameter;
import com.wafersystems.notice.base.model.ParameterDTO;
import com.wafersystems.notice.base.service.GlobalParamService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:30 Company:
 * wafersystems
 */
@Slf4j
@RestController
public class GlobalParamController {
  @Autowired
  private GlobalParamService globalParamService;
  @Autowired
  private StringEncryptor stringEncryptor;

  /**
   * 更新系统配置信息到缓存.
   *
   * @return -
   */
  @PostMapping(value = "/init")
  public Object initSysParam() {
    try {
      globalParamService.initSystemParam();
      return R.ok(ConfConstant.RESULT_SUCCESS);
    } catch (Exception ex) {
      log.error("更新系统配置信息异常：", ex);
      return R.fail();
    }
  }

  /**
   * 获取所有参数
   *
   * @return Object
   */
  @GetMapping("/parameter/get")
  public R get() {
    List<GlobalParameter> systemParamList = globalParamService.getSystemParamList();
    systemParamList.forEach(globalParameter -> {
      try {
        globalParameter.setParamValue(stringEncryptor.decrypt(globalParameter.getParamValue()));
      } catch (Exception e) {
        log.info("参数值解密异常：key[{}]，value[{}]", globalParameter.getParamKey(), globalParameter.getParamValue());
      }
    });
    return R.ok(systemParamList);
  }

  /**
   * 设置某个参数值
   *
   * @param param 参数对象
   * @return Object
   */
  @PostMapping("/parameter/set")
  public R set(@RequestBody ParameterDTO param) {
    GlobalParameter gp = globalParamService.getSystemParamByParamKey(param.getParamKey());
    if (gp == null) {
      return R.fail("当前配置参数key不存在：" + param.getParamKey());
    }
    gp.setParamValue(stringEncryptor.encrypt(param.getParamValue()));
    globalParamService.save(gp);
    initSysParam();
    return R.ok();
  }
}
