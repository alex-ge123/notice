package com.wafersystems.notice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2019/12/2 16:31
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class SmsSendControllerTest extends BaseTest {
  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeClass
  public void initData() {
    ParamConstant.setURL_SMS_SECRET("123");
  }

  @Test
  public void testSendSms() throws Exception {
    SmsDTO smsDTO = new SmsDTO();
    smsDTO.setTemplateId("105856");
    smsDTO.setSmsSign("威发系统");
    smsDTO.setPhoneList(Lists.newArrayList("15529360323"));
    smsDTO.setParamList(Lists.newArrayList("11", "2019-12-26 10:35", "访客", "", "", ""));
    String url = "/sms/sendSms";
    String content = JSON.toJSONString(smsDTO);
    JSONObject jsonObject = doPost(url, content, null, true, false);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.FAIL);
  }

  @Test
  public void testSendCaptcha() throws Exception {
    String url = "/sms/sendCaptcha";
    SmsDTO smsDTO = new SmsDTO();
    smsDTO.setTemplateId("105856");
    smsDTO.setSmsSign("威发系统");
    smsDTO.setPhoneList(Lists.newArrayList("15529360323"));
    smsDTO.setParamList(Lists.newArrayList("11", "2019-12-26 10:35", "访客", "", "", ""));
    String content = JSON.toJSONString(smsDTO);
    JSONObject jsonObject = doPost(url, content, null, true, false);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.FAIL);
  }

  @Test
  public void testCheckCaptcha() throws Exception {
    redisTemplate.opsForValue().set("SMS_CAPTCHA_KEY:123:15529360324", "1234");
    String url = "/sms/checkCaptcha";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>(3);
    params.add("code", "1234");
    params.add("phone", "15529360324");
    params.add("business", "123");
    JSONObject jsonObject = doGet(url, true, false, params);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }
}