package com.wafersystems.notice.controller;

import cn.hutool.core.util.RandomUtil;
import com.wafersystems.notice.config.AsyncTaskManager;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.model.PaginationDTO;
import com.wafersystems.notice.model.SmsTemplateDTO;
import com.wafersystems.notice.model.TemplateStateUpdateDTO;
import com.wafersystems.notice.service.SmsService;
import com.wafersystems.notice.util.SmsUtil;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.security.annotation.Inner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * 短信控制器
 *
 * @author wafer
 */
@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsSendController {

  @Autowired
  private ApplicationContext resource;

  @Autowired
  private SmsUtil smsUtil;

  @Autowired
  private SmsService smsService;
  /**
   * 短信验证码缓存key
   */
  private static final String SMS_CAPTCHA_KEY = "SMS_CAPTCHA_KEY:";

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private AsyncTaskManager asyncTaskManager;

  @Value("${sms.captcha.length}")
  private int length;

  @Value("${sms.captcha.timeout}")
  private Long timeout;

  /**
   * 短信发送.
   *
   * @param smsDTO 短信内容
   * @param lang   语言
   * @return -
   */
  @Inner
  @PostMapping(value = "/sendSms")
  public R sendSms(@RequestBody SmsDTO smsDTO, String lang) {
    if (!ParamConstant.isSMS_SWITCH()) {
      log.warn("未配置短信服务调用地址！");
      return R.fail(resource.getMessage("msg.msg.smsServerNull", null, ParamConstant.getLocaleByStr(lang)));
    }
    if (smsDTO == null || smsDTO.getPhoneList().isEmpty()) {
      log.warn("接收短信的手机号不能为空！");
      return R.fail(resource.getMessage("msg.msg.recipientIdNull", null,
        ParamConstant.getLocaleByStr(lang)));
    }
    smsUtil.batchSendSms(smsDTO.getTemplateId(), smsDTO.getPhoneList(), smsDTO.getParamList(),
      smsDTO.getDomain(), smsDTO.getSmsSign());
    return R.ok();
  }

  /**
   * 发送手机验证码短信
   *
   * @param smsDTO 短信对象
   * @param lang   语言
   * @return 结果
   */
  @Inner
  @PostMapping(value = "/sendCaptcha")
  public R sendCaptcha(@RequestBody SmsDTO smsDTO, String lang) {
    if (smsDTO == null || smsDTO.getPhoneList().isEmpty()) {
      log.warn("接收短信的手机号不能为空！");
      return R.fail(resource.getMessage("msg.msg.recipientIdNull", null,
        ParamConstant.getLocaleByStr(lang)));
    }
    if (smsDTO.getPhoneList().size() != 1) {
      return R.fail("验证码短信不支持群发");
    }
    String phone = smsDTO.getPhoneList().get(0);
    String business = smsDTO.getExtend();
    String code = RandomUtil.randomNumbers(length);
    smsDTO.getParamList().add(0, code);
    try {
      sendSms(smsDTO, lang);
    } catch (Exception e) {
      log.warn("发送手机验证码短信失败");
      return R.fail(resource.getMessage("msg.sms.sendError", null, ParamConstant.getLocaleByStr(lang)));
    }
    stringRedisTemplate.opsForValue().set(SMS_CAPTCHA_KEY + business + ":" + phone, code, timeout, TimeUnit.SECONDS);
    return R.ok();
  }

  /**
   * 校验手机验证码
   *
   * @param code     验证码
   * @param phone    手机号
   * @param business 业务标识
   * @return 结果
   */
  @Inner
  @GetMapping(value = "/checkCaptcha")
  public R checkCaptcha(@RequestParam String code, @RequestParam String phone, @RequestParam String business) {
    String str = stringRedisTemplate.opsForValue().get(SMS_CAPTCHA_KEY + business + ":" + phone);
    if (code.equals(str)) {
      stringRedisTemplate.delete(SMS_CAPTCHA_KEY + business + ":" + phone);
      return R.ok();
    }
    return R.fail();
  }

  /**
   * 分页查询短信模板列表
   *
   * @param id         id
   * @param category   category
   * @param name       name
   * @param pageSize   分页大小
   * @param startIndex 起始页
   * @return
   */
  @GetMapping("/template/page")
  @PreAuthorize("@pms.hasPermission('')")
  public R templatePage(String id, String category, String name,
                        @RequestParam(defaultValue = ConfConstant.DATA_DEFAULT_LENGTH) Integer pageSize,
                        @RequestParam(defaultValue = ConfConstant.PAGE_DEFAULT_LENGTH) Integer startIndex) {
    PaginationDTO<SmsTemplateDTO> list = smsService.getTemp(id, category, name, pageSize, startIndex);
    return R.ok(list);
  }

  /**
   * 添加/修改短信模板
   *
   * @param dto 邮件模板
   * @return R
   */
  @PostMapping("/template/add")
  @PreAuthorize("@pms.hasPermission('')")
  public R templateAdd(@RequestBody SmsTemplateDTO dto) {
    smsService.saveTemp(dto);
    return R.ok();
  }

  /**
   * 删除短信模板
   *
   * @param id id
   * @return R
   */
  @GetMapping("/template/del/{id}")
  @PreAuthorize("@pms.hasPermission('')")
  public R templateAdd(@PathVariable("id") String id) {
    smsService.delTemp(id);
    return R.ok();
  }


  @PostMapping("/template/update/state")
  @PreAuthorize("@pms.hasPermission('')")
  public R templateUpdateState(@RequestBody TemplateStateUpdateDTO dto) {
    return smsService.updateTempState(dto) ? R.ok() : R.fail();
  }

  @PostMapping("/check")
  @PreAuthorize("@pms.hasPermission('')")
  public R check(@RequestBody BaseCheckDTO dto) {
    try {
      URL url = new URL(ParamConstant.getURL_SMS_SERVER());
      URLConnection co = url.openConnection();
      co.setConnectTimeout(5000);
      co.connect();
      sendCheckLog(null, CommonConstants.SUCCESS, dto.getTenantId());
      return R.ok();
    } catch (Exception e) {
      log.warn("短信检测失败！", e);
      StringWriter stringWriter = new StringWriter();
      e.printStackTrace(new PrintWriter(stringWriter));
      sendCheckLog(e.getMessage(), CommonConstants.FAIL, dto.getTenantId());
      return R.builder().code(CommonConstants.FAIL).msg(e.getMessage()).data(stringWriter.toString()).build();
    }
  }

  private void sendCheckLog(String message, Integer result, Integer tenantId) {
    LogDTO logDTO = new LogDTO();
    logDTO.setProductCode(ProductCodeEnum.COMMON.getCode());
    if (cn.hutool.core.util.StrUtil.isNotBlank(message)) {
      logDTO.setContent(message);
    }
    logDTO.setUsername(TenantContextHolder.getUsername());
    logDTO.setTenantId(TenantContextHolder.getTenantId());
    logDTO.setResult(result);
    logDTO.setTitle("短信配置检测");
    logDTO.setType("check-sms");
    logDTO.setUserId(TenantContextHolder.getUserId());
    logDTO.setObjectId(String.valueOf(tenantId));
    asyncTaskManager.asyncSendLogMessage(logDTO);
  }

}
