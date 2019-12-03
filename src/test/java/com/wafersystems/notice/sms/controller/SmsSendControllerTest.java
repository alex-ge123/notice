package com.wafersystems.notice.sms.controller;

import static org.testng.Assert.*;

import com.wafersystems.notice.BaseTest;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2019/12/2 16:31
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class SmsSendControllerTest extends BaseTest {

  @Test
  public void testSendSms() throws Exception {
    String url = "/mail/SendSms";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testSendCaptcha() throws Exception {
    String url = "/mail/SendCaptcha";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testCheckCaptcha() throws Exception {
    String url = "/mail/CheckCaptcha";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }
}