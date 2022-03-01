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
public class SmtpEmailManagerTest extends BaseTest {
  @Autowired
  private SmtpEmailManager smtpEmailManager;
  private static MailServerConf mailServerConf;

  static {
    mailServerConf = new MailServerConf();
    mailServerConf.setServerType("smtp");
    mailServerConf.setHost("smtp.qiye.aliyun.com");
    mailServerConf.setPort(25);
    mailServerConf.setUsername("dev@virsical.net");
    mailServerConf.setPassword("AyBuxa#2033");
    mailServerConf.setAuth("true");
    mailServerConf.setName("单元测试");
    mailServerConf.setFrom("dev@virsical.net");
    mailServerConf.setEncryMode(0);
  }


  @Test
  public void testSend() throws Exception {
    final RecurrenceRuleDTO ruleDTO = new RecurrenceRuleDTO();
    ruleDTO.setUntil("1629535770000");
    ruleDTO.setFreq("WEEKLY");
    ruleDTO.setInterval(1);

    final MailScheduleDto scheduleDto = new MailScheduleDto();
    scheduleDto.setUuid("1234567");
    scheduleDto.setSquence(1);
    scheduleDto.setStartDate("1629535770000");
    scheduleDto.setEndDate("1629535770000");
    scheduleDto.setEnventType("REQUEST");
    scheduleDto.setTimeZone("Asia/Shanghai");
    scheduleDto.setRecurrenceRuleDTO(ruleDTO);

    final ArrayList<String> accessoryList = new ArrayList<>(1);
    accessoryList.add("https://pic4.zhimg.com/v2-4d5cafde45d9f275fec17c3496299f0b_r.jpg");

    final MailDTO mailDTO = new MailDTO();
    mailDTO.setUuid("1234567");
    mailDTO.setRouterKey("router_key");
    mailDTO.setSubject("单元测试");
    mailDTO.setToMail("shennan@wafersystems.com");
    mailDTO.setCopyTo("shennan1@wafersystems.com");
    mailDTO.setTempName("commonForgetPwd");
    mailDTO.setLang("zh_CN");
    mailDTO.setTenantId(1001);
    mailDTO.setMailScheduleDto(scheduleDto);
    mailDTO.setSystemName("系统");
    mailDTO.setAccessoryList(accessoryList);

    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    smtpEmailManager.send(mailBean, mailServerConf);
  }

  @Test
  public void testCheck() {
    final BaseCheckDTO dto = new BaseCheckDTO();
    dto.setToMail("shennan@wafersystems.com");
    smtpEmailManager.check(dto, 1, mailServerConf);
  }
}