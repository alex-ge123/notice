package com.wafersystems.notice.service.impl;

import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.ParameterDTO;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * @author shennan
 * @date 2021/4/27 11:33
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class GlobalParamServiceImplTest extends BaseTest {

  @Autowired
  private GlobalParamService service;

  private ParameterDTO initParameter(String key, String value) {
    final ParameterDTO globalParameter = new ParameterDTO();
    globalParameter.setParamKey(key);
    globalParameter.setParamValue(value);
    globalParameter.setType("MAIL");
    return globalParameter;
  }

  @Test
  @Rollback(false)
  public void testGetMailServerConf()  {
    ArrayList<ParameterDTO> list = new ArrayList<>(10);
    list.add(initParameter("MAIL_SSL", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_SERVER_TYPE", "fU8bpUPSaiid6gWu2pSXJg=="));
    list.add(initParameter("MAIL_HOST", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_PORT", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_MAILNAME", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_FROM", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_USERNAME", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_PASSWORD", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_AUTH", "x9FxZqK2abVLNiF2BHxa9w=="));
    TenantContextHolder.setTenantId(2000);
    service.saveBatch(list);
    final MailServerConf mailServerConf = service.getMailServerConf(2000);
    Assert.assertEquals(mailServerConf.getServerType(), "smtp");

    list = new ArrayList<>(10);
    list.add(initParameter("MAIL_SERVER_TYPE", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_MICROSOFT_CLIENTID", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_MICROSOFT_CLIENTSECRET", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_MICROSOFT_TENANTID", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_MICROSOFT_SCOPE", "x9FxZqK2abVLNiF2BHxa9w=="));
    list.add(initParameter("MAIL_MICROSOFT_FROM", "x9FxZqK2abVLNiF2BHxa9w=="));
    TenantContextHolder.setTenantId(3000);
    service.saveBatch(list);
    final MailServerConf mailServerConf1 = service.getMailServerConf(3000);
    Assert.assertEquals(mailServerConf1.getServerType(), "microsoft");
  }
}