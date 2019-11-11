package com.wafersystems.notice.sms.controller;

import cn.hutool.core.util.RandomUtil;
import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.SmsUtil;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.security.annotation.Inner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 短信控制器
 *
 * @author wafer
 */
@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsSendController extends BaseController {

  @Autowired
  private ApplicationContext resource;

  /**
   * 短信验证码缓存key
   */
  private static final String SMS_CAPTCHA_KEY = "SMS_CAPTCHA_KEY:";

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

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
  @RequestMapping(value = "/sendSms", method = RequestMethod.POST)
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
    String result = "1";
    if (smsDTO.getPhoneList().size() > 1) {
      for (String phone : smsDTO.getPhoneList()) {
        result = SmsUtil.sendSms(smsDTO.getTemplateId(), phone, smsDTO.getParamList(), smsDTO.getDomain(),
          smsDTO.getSmsSign());
        log.debug("电话号码" + phone + "发送短信的结果为：" + result);
      }
    } else if (smsDTO.getPhoneList().size() == 1) {
      result = SmsUtil.sendSms(smsDTO.getTemplateId(), smsDTO.getPhoneList().get(0), smsDTO.getParamList(),
        smsDTO.getDomain(), smsDTO.getSmsSign());
      log.debug("电话号码" + smsDTO.getPhoneList().get(0) + "发送短信的结果为：" + result);
    }
    if (ConfConstant.RESULT_SUCCESS.toString().equals(result)) {
      return R.ok();
    }
    return R.fail(resource.getMessage("msg.sms.sendError", null, ParamConstant.getLocaleByStr(lang)));
  }

  /**
   * 发送手机验证码短信
   *
   * @param smsDTO 短信对象
   * @param lang   语言
   * @return 结果
   */
  @Inner
  @RequestMapping(value = "/sendCaptcha", method = RequestMethod.POST)
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
  @RequestMapping(value = "/checkCaptcha", method = RequestMethod.GET)
  public R checkCaptcha(@RequestParam String code, @RequestParam String phone, @RequestParam String business) {
    String str = stringRedisTemplate.opsForValue().get(SMS_CAPTCHA_KEY + business + ":" + phone);
    if (code.equals(str)) {
      stringRedisTemplate.delete(SMS_CAPTCHA_KEY + business + ":" + phone);
      return R.ok();
    }
    return R.fail();
  }
}
