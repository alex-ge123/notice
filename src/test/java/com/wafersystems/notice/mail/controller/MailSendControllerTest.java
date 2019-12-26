package com.wafersystems.notice.mail.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.base.model.TestSendMailDTO;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.dto.TemContentVal;
import com.wafersystems.virsical.common.core.util.FileUtils;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.entity.TenantDTO;
import com.wafersystems.virsical.common.feign.fallback.RemoteTenantServiceFallbackImpl;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;

/**
 * @author shennan
 * @date 2019/12/2 16:32
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class MailSendControllerTest extends BaseTest {

  @Autowired
  MailSendController mailSendController;

  @MockBean
  RemoteTenantServiceFallbackImpl remoteTenantServiceFallbackImpl;

  //  @MockBean
//  private MailNoticeServiceImpl mailNoticeService;

  //  @MockBean
//  @Qualifier("mailUtil")
//  private EmailUtil mailUtil;
  
  @BeforeClass
  public void initData() {
    ParamConstant.setDEFAULT_MAIL_HOST("12345");
  }

  @Test
  public void testTemplateList() throws Exception {
    String url = "/mail/template/list";
    JSONObject jsonObject = doGet(url);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @AfterClass
  public void clean() {
    File file = new File("./testTemplateUpload.flt");
    if (file.exists()) {
      file.delete();
    }
    File file1 = new File("./testTemplateUpload1.flt");
    if (file1.exists()) {
      file1.delete();
    }
  }

  @Test
  public void testTemplateUpload() throws Exception {
    File file = new File("./testTemplateUpload.flt");
    if (!file.exists()) {
      file.createNewFile();
    }
    try (FileWriter writer = new FileWriter(file);) {
      writer.append("aaaaa");
      writer.flush();
    }
    R r = mailSendController.templateUpload(FileUtils.file2MultipartFile(file), "分类", "描述");
    Assert.assertEquals(r.getCode(), CommonConstants.SUCCESS.intValue());
  }

  @Test
  public void testTemplateUpldate() throws Exception {
    File file = new File("./testTemplateUpload1.flt");
    if (!file.exists()) {
      file.createNewFile();
    }
    try (FileWriter writer = new FileWriter(file);) {
      writer.append("aaaaa");
      writer.flush();
    }
    final LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("id", "1");
    map.add("category", "1123");
    map.add("description", "34gjsd");
    R r = mailSendController.templateUpdate(FileUtils.file2MultipartFile(file), 1, "aaaa", "bbbb");
    Assert.assertEquals(r.getCode(), CommonConstants.SUCCESS.intValue());
  }

  @Test
  public void testTemplatePreview() throws Exception {
    String url = "/mail/template/preview";
    final LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("tempName", "commonForgetPwd");
    final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);
    requestBuilder.params(map);
    ResultActions result = super.mockMvc.perform(requestBuilder);
    result.andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testSendMail() throws Exception {
//    Mockito.doNothing().when(mailNoticeService).sendMail
//      (Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    TenantDTO tenantDTO = new TenantDTO();
    tenantDTO.setLogo("aaa");
    tenantDTO.setSystemName("bbb");
    tenantDTO.setContactNumber("111111");
    R<TenantDTO> r = new R<>();
    r.setCode(CommonConstants.SUCCESS);
    r.setData(tenantDTO);
    Mockito.when(remoteTenantServiceFallbackImpl.getTenantByIdForInner(Mockito.any(), Mockito.any())).thenReturn(r);
    String url = "/mail/sendMail";
    final LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("subject", "test");
    map.add("toMail", "shennan@wafersystems.com");
    map.add("tempName", "commonForgetPwd");
    final TemContentVal val = new TemContentVal();
    val.setTenantId(0);
    val.setValue1("1");
    val.setValue2("2");
    val.setValue3("3");
    val.setValue4("4");
    val.setValue5("5");
    String content = JSON.toJSONString(val);
    JSONObject jsonObject = doPost(url, content, map, true, true);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }

  @Test
  public void testTestMailSend() throws Exception {
//    Mockito.doNothing().when(mailNoticeService).sendMail
//      (Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    TestSendMailDTO testSendMailDTO = new TestSendMailDTO();
    testSendMailDTO.setTitle("测试");
    testSendMailDTO.setToMail("shennan@wafersystems.com");
    testSendMailDTO.setTempName("commonForgetPwd");
    testSendMailDTO.setLang("zh_CN");
    String url = "/mail/testSend";
    String content = JSON.toJSONString(testSendMailDTO);
    JSONObject jsonObject = doPost(url, content, null);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);
  }
}