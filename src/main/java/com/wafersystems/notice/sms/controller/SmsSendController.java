package com.wafersystems.notice.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.sms.model.SmsContentValueDto;
import com.wafersystems.notice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with Intellij IDEA. Description: 短信服务类 Author: waferzy DateTime: 2016/5/19 9:54 Company:
 * wafersystems
 */
@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsSendController extends BaseController {

  @Autowired
  private ApplicationContext resource;

//  @RequestMapping("/testSend")
//  public Object testSendSms(String telephones, String content, String lang) {
//    String url = "http://localhost:20110/sms/sendSms";
//    String result = null;
//    try {
//      if (StringUtil.isEmptyStr(telephones)) {
//        log.info("--发送号码[" + telephones + "]短信发送失败,结果:手机号码不能为空!");
//        return false;
//      }
//      telephones = telephones.replaceAll(";", ",");
//      telephones = telephones.replaceAll("\\+86", "").replaceAll("\\+852", "");
//      if (url.toLowerCase().startsWith("http://")) {
//        NameValuePair[] nameValuePair = new NameValuePair[9];
//        nameValuePair[0] = new NameValuePair("url",
//                "http://bill.virsical.cn/caas/caashelper/caasAbility/smsSendByTemplet");
//        nameValuePair[1] = new NameValuePair("token",
//                "E2327CDED0C6968438F1A1A936FE0B6A75121213470985B173B02066A5B0269E2A29598CF11D070BB8728088BFA01AC2");
//        nameValuePair[2] = new NameValuePair("calleeNbr", telephones);
//        if (!StringUtil.isEmptyStr(lang) && lang.contains("en")) {
//          nameValuePair[3] = new NameValuePair("templetId", "100428");
//        } else {
//          nameValuePair[3] = new NameValuePair("templetId", "100427");
//        }
//        nameValuePair[4] = new NameValuePair("clientId", "100002");
//        nameValuePair[5] = new NameValuePair("secret", "wafer");
//        nameValuePair[6] = new NameValuePair("value1", StringUtil.replaceStr(content));
//        nameValuePair[7] = new NameValuePair("requestTime", System.currentTimeMillis() + "");
//        nameValuePair[8] = new NameValuePair("lang", StringUtil.isEmptyStr(lang) ? "zh_CN" : lang);
//        result = HttpClientUtil.getPostResponseWithHttpClient(url, "utf-8", nameValuePair);//, null);
//      } else if (url.toLowerCase().startsWith("https://")) {
//        Map<String, String> createMap = new HashMap<>();
//        createMap.put("url",
//                "http://bill.virsical.cn/caas/caashelper/caasAbility/smsSendByTemplet");
//        createMap.put("token",
//                "E2327CDED0C6968438F1A1A936FE0B6A75121213470985B173B02066A5B0269E2A29598CF11D070BB8728088BFA01AC2");
//        createMap.put("calleeNbr", telephones);
//        if (!StringUtil.isEmptyStr(lang) && lang.contains("en")) {
//          createMap.put("templetId", "100428");
//        } else {
//          createMap.put("templetId", "100427");
//        }
//        createMap.put("clientId", "100002");
//        createMap.put("secret", "wafer");
//        createMap.put("value1", StringUtil.replaceStr(content));
//        createMap.put("requestTime", System.currentTimeMillis() + "");
//        createMap.put("lang", StringUtil.isEmptyStr(lang) ? "zh_CN" : lang);
//        result = HttpsPostClientUtil.doPost(url, createMap, "UTF-8");//, null);
//      }
//      if (!StringUtil.isEmptyStr(result)) {
//        JSONObject node = JSON.parseObject(result);
//        Integer res = (Integer) node.get("status");
//        if (0 == res) {
//          log.info("--发送短信[" + telephones + "]成功!");
//          return true;
//        } else {
//          log.info("--发送短信[" + telephones + "]失败,结果:" + result);
//        }
//      }
//    }catch (Exception e){
//      e.printStackTrace();
//    }
//    return result;
//  }

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
