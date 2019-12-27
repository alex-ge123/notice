package com.wafersystems.notice.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.base.model.ParameterDTO;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2019/12/27 10:05
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class GlobalParamControllerTest extends BaseTest {

  @Test
  public void testInitSysParam() throws Exception {
    String url = "/init";
    JSONObject jsonObject = doPost(url, null, null);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testGet() throws Exception {
    String url = "/parameter/get";
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testSet() throws Exception {
    ParameterDTO parameter = new ParameterDTO();
    parameter.setParamKey("DEFAULT_MAIL_AUTH");
    parameter.setParamValue("test");
    parameter.setLang("zh_CN");
    String url = "/parameter/set";
    String content = JSON.toJSONString(parameter);
    JSONObject jsonObject = doPost(url, content, null);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }
}