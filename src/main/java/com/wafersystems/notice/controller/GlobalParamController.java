package com.wafersystems.notice.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.NoticeMsgConstants;
import com.wafersystems.notice.entity.NtcParameter;
import com.wafersystems.notice.model.ParameterDTO;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.virsical.common.core.config.AesKeyProperties;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.security.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:30 Company:
 * wafersystems
 *
 * @author wafer
 */
@Slf4j
@RestController
@RequestMapping("/parameter")
public class GlobalParamController {
  @Autowired
  private GlobalParamService globalParamService;
  @Autowired
  private StringEncryptor stringEncryptor;

  @Autowired
  private AesKeyProperties aesKeyProperties;

  private static final String DEFAULT_MAIL_PORT = "DEFAULT_MAIL_PORT";
  private static final String DEFAULT_REPEAT_COUNT = "DEFAULT_REPEAT_COUNT";

  /**
   * 更新系统配置信息到缓存.
   *
   * @return -
   */
  @PostMapping(value = "/init")
  @PreAuthorize("@pms.hasPermission('')")
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
   * 查询租户配置参数
   *
   * @return Object
   */
  @GetMapping("/get")
  @PreAuthorize("@pms.hasPermission('admin@common@mail_setting,admin@platform@notice_parameter')")
  public R get(String type) {
    List<NtcParameter> systemParamList = globalParamService.getSystemParamList(TenantContextHolder.getTenantId(),
      type);
    systemParamList.forEach(globalParameter -> {
      String value = globalParameter.getParamValue();
      try {
        value = stringEncryptor.decrypt(globalParameter.getParamValue());
      } catch (Exception e) {
        log.warn("参数值解密异常：key[{}]，value[{}]", globalParameter.getParamKey(), globalParameter.getParamValue());
      }
      globalParameter.setParamValue(AesUtils.encryptAes(value, aesKeyProperties.getKey()));
    });
    return R.ok(systemParamList);
  }

  /**
   * 设置某个参数值
   *
   * @param param 参数对象
   * @return Object
   */
  @PostMapping("/set")
  @PreAuthorize("@pms.hasPermission('admin@common@mail_setting,admin@platform@notice_parameter')")
  public R set(@RequestBody @Valid ParameterDTO param) {
    NtcParameter gp = globalParamService.getSystemParamByParamKey(param.getParamKey());
    if (gp == null) {
      return R.fail("当前配置参数key不存在：" + param.getParamKey());
    }
    if (StrUtil.equalsAnyIgnoreCase(param.getParamKey(), DEFAULT_MAIL_PORT, DEFAULT_REPEAT_COUNT)
      && !NumberUtil.isNumber(param.getParamValue())) {
      return R.fail("数字类型参数请输入正确数字");
    }
    gp.setParamValue(stringEncryptor.encrypt(param.getParamValue()));
    gp.setParamDesc(param.getParamDesc());
    gp.setTenantId(TenantContextHolder.getTenantId());
    globalParamService.saveParameter(gp);
    if (CommonConstants.PLATFORM_ADMIN_TENANT_ID.equals(TenantContextHolder.getTenantId())) {
      initSysParam();
    }
    return R.ok();
  }

  /**
   * 批量设置参数值
   *
   * @param list 参数集合
   * @return Object
   */
  @PostMapping("/batch-set")
  @PreAuthorize("@pms.hasPermission('admin@common@mail_setting,admin@platform@notice_parameter')")
  public R set(@RequestBody List<ParameterDTO> list) {
    if (list != null && !list.isEmpty()) {
      globalParamService.saveBatch(list);
      if (CommonConstants.PLATFORM_ADMIN_TENANT_ID.equals(TenantContextHolder.getTenantId())) {
        initSysParam();
      }
      return R.ok();
    }
    return R.fail(NoticeMsgConstants.PARAM_ERROR);
  }

  /**
   * 删除参数
   *
   * @param id id
   * @return R
   */
  @PostMapping("/del")
  @PreAuthorize("@pms.hasPermission('admin@common@mail_setting,admin@platform@notice_parameter')")
  public R del(@RequestParam Integer id) {
    if (id != null) {
      globalParamService.del(id);
      return R.ok();
    }
    return R.fail(NoticeMsgConstants.PARAM_ERROR);
  }
}
