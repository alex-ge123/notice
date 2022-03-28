package com.wafersystems.notice.manager.email;

import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MailScheduleDto;
import com.wafersystems.virsical.common.core.dto.RecurrenceRuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * @author shennan
 * @date 2021/4/23 16:30
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class AmazonEmailManagerTest extends BaseTest {
  @Autowired
  private AmazonEmailManager amazonEmailManager;
  private static MailServerConf mailServerConf;

  static {
    mailServerConf = new MailServerConf();
    mailServerConf.setServerType("amazon");
    mailServerConf.setName("单元测试");
    mailServerConf.setFrom("dev@virsical.net");
  }


  @Test
  public void testSend() throws Exception {
    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .subject("单元测试")
      .toEmails("lileibo@wafersystems.com")
      .copyTo("wangjialun@wafersystems.com")
      .build();
    amazonEmailManager.send(mailBean, mailServerConf);
  }

}