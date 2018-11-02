package com.wafersystems.notice.sms.controller;

import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.sms.model.SmsContentValueDto;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.SmsUtil;
import com.wafersystems.notice.util.StrUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Intellij IDEA. Description: 短信服务类 Author: waferzy DateTime: 2016/5/19 9:54 Company:
 * wafersystems
 */
@Log4j
@RestController
@RequestMapping("/sms")
public class SmsSendController extends BaseController {

  @Autowired
  private ApplicationContext resource;

  /**
   * 短信发送.
   * 
   * @param bean 短信内容
   * @param lang 语言
   * @return -
   */
  @RequestMapping(value = "/sendSms", method = RequestMethod.POST)
  public Object sendSms(SmsContentValueDto bean, String lang) {
    try {
      if (!ParamConstant.isSMS_SWITCH()) {
        log.warn("未配置短信服务调用地址！");
        return returnBackMap(
            resource.getMessage("msg.msg.smsServerNull", null, ParamConstant.getLocaleByStr(lang)),
            ConfConstant.RESULT_FAIL);
      }
      if (!StrUtil.isNullObject(bean) && !StrUtil.isEmptyStr(bean.getCalleeNbr())) {
        String result = "1";
        List<String> params = new ArrayList<String>();
        params.add(StrUtil.regStr(String.valueOf(bean.getValue1())));
        if (StringUtils.isNotBlank(bean.getValue2())) {
          params.add(StrUtil.regStr(String.valueOf(bean.getValue2())));
        }
        if (bean.getCalleeNbr().contains(ConfConstant.COMMA)) {
          String[] no = bean.getCalleeNbr().split(ConfConstant.COMMA);
          for (String temp : no) {
            result = SmsUtil.sendSms(bean.getTempletId(), temp, params, "");
            log.debug("电话号码" + temp + "发送短信的结果为：" + result);
          }
        } else {
          result = SmsUtil.sendSms(bean.getTempletId(), bean.getCalleeNbr(), params, "");
          log.debug("电话号码" + bean.getCalleeNbr() + "发送短信的结果为：" + result);
        }
        if (result.equals("0")) {
          return returnBackMap(result, ConfConstant.RESULT_SUCCESS);
        } else {
          return returnBackMap(
              resource.getMessage("msg.sms.sendError", null, ParamConstant.getLocaleByStr(lang)),
              ConfConstant.RESULT_FAIL);
        }
      } else {
        log.warn("接收短信的手机号不能为空！");
        return returnBackMap(resource.getMessage("msg.msg.recipientIdNull", null,
            ParamConstant.getLocaleByStr(lang)), ConfConstant.RESULT_FAIL);
      }
    } catch (RuntimeException ex) {
      log.error("发送域激活短信验证码失败" + ex);
      return returnBackMap(
          resource.getMessage("msg.sms.sendError", null, ParamConstant.getLocaleByStr(lang)),
          ConfConstant.RESULT_FAIL);
    }
  }

}
