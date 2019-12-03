package com.wafersystems.notice.mail.controller;


import com.wafersystems.notice.BaseTest;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.util.FileUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author shennan
 * @date 2019/12/2 16:32
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class MailSendControllerTest extends BaseTest {

  @Test
  public void testTemplateList() throws Exception {
    String url = "/mail/template/list";
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTemplateUpload() throws Exception {
    String url = "/mail/template/upload";
    File tempFile = new File("test");
    MultipartFile multipartFile = FileUtils.file2MultipartFile(tempFile);
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

//    JSONObject jsonObject = doPost(url, null, map);
//    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTemplateUpldate() throws Exception {
    String url = "/mail/TemplateUpldate";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTemplatePreview() throws Exception {
    String url = "/mail/TemplatePreview";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testSendMail() throws Exception {
    String url = "/mail/SendMail";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTestMailSend() throws Exception {
    String url = "/mail/TestMailSend";
    //String content = JSON.toJSONString(sysDept);
    //JSONObject jsonObject = doPost(url, content, null);
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }
}