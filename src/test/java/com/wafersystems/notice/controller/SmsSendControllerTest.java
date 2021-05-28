package com.wafersystems.notice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.entity.SmsTemplate;
import com.wafersystems.notice.model.TemplateStateUpdateDTO;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SmsSendControllerTest extends BaseTest {
  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeClass
  public void initData() {
    redisTemplate.opsForHash().put("base:notice:param:common", "URL_SMS_SERVER", "https://yapi.rd.virsical.cn/mock/121/sms");
    log.info("发送短信host:{}", redisTemplate.opsForHash().get("base:notice:param:common", "URL_SMS_SERVER"));
    //yapi 挡板
    ParamConstant.setUrlSmsServer("https://yapi.rd.virsical.cn/mock/121/sms");
  }

  @Test
  public void testSendSms() throws Exception {
    SmsDTO smsDTO = new SmsDTO();
    smsDTO.setTemplateId("105856");
    smsDTO.setSmsSign("威发系统");
    smsDTO.setParamList(Lists.newArrayList("11", "2019-12-26 10:35", "访客", "", "", ""));
    String url = "/sms/sendSms";
    String content = JSON.toJSONString(smsDTO);
    JSONObject jsonObject = doPost(url, content, null, true, false);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.FAIL);

    smsDTO.setPhoneList(Lists.newArrayList("qn49Eg57fYA0VPCO9u2K/Q==", "13439089878", "12289746575"));
    JSONObject jsonObject1 = doPost(url, JSON.toJSONString(smsDTO), null, true, false);
    Assert.assertEquals(jsonObject1.get("code"), CommonConstants.SUCCESS);
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
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
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

  @Test
  public void testTemplatePage() throws Exception {
    String url = "/sms/template/page";
    JSONObject jsonObject = doGet(url, true, false);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTemplateAdd() throws Exception {
    String url = "/sms/template/add";
    final SmsTemplate template = new SmsTemplate();
    template.setId("55555");
    template.setName("测试模板");
    template.setContent("我是要给测试模板");
    template.setDescription("描述");
    template.setCategory("通用");
    final String jsonString = JSONObject.toJSONString(template);
    JSONObject jsonObject = doPost(url, jsonString, null, false, true);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTemplateDel() throws Exception {
    String url = "/sms/template/del/55555";
    JSONObject jsonObject = doGet(url, false, true);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testUpdateState() throws Exception {
    String url = "/sms/template/update/state";
    final TemplateStateUpdateDTO dto = new TemplateStateUpdateDTO();
    dto.setState(0);
    dto.setUpdateAll(true);
    dto.setId("");
    final String jsonString = JSONObject.toJSONString(dto);
    JSONObject jsonObject = doPost(url, jsonString, null, false, true);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTemplateUpdateState() throws Exception {
    String url = "/sms/template/update/state";
    final TemplateStateUpdateDTO dto = new TemplateStateUpdateDTO();
    dto.setState(0);
    dto.setUpdateAll(true);
    String content = JSON.toJSONString(dto);
    JSONObject jsonObject = doPost(url, content, null);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testCheck() throws Exception {
    String url = "/sms/check";
    JSONObject jsonObject = doPost(url, null, null);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }
}