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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2021/4/23 14:15
 */
@Rollback(value = false)
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class MicrosoftEmailManagerTest extends BaseTest {
  @Autowired
  private MicrosoftEmailManager microsoft;
  private static MailServerConf mailServerConf;

  static {
    mailServerConf = new MailServerConf();
    mailServerConf.setClientId("clientid");
    mailServerConf.setClientSecret("clientsecret");
    mailServerConf.setOfficeTenantId("tenantid");
    mailServerConf.setScope("scope");
    mailServerConf.setMicrosoftFrom("from");
  }

  @BeforeClass
  public void initData() {
    MicrosoftEmailManager.DOMAIN = "https://yapi.rd.virsical.cn/mock/121";
    MicrosoftEmailManager.LOGIN = "https://yapi.rd.virsical.cn/mock/121";
  }

  /**
   * 发送普通邮件
   *
   * @throws Exception
   */
  @Test
  public void send1() throws Exception {
    final MailDTO mailDTO = new MailDTO();
    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    microsoft.send(mailBean, mailServerConf);
  }

  /**
   * 发送日程事件邮件
   *
   * @throws Exception
   */
  @Test
  public void send2() throws Exception {
    final RecurrenceRuleDTO ruleDTO = new RecurrenceRuleDTO();
    ruleDTO.setUntil("1629535770000");
    ruleDTO.setFreq("MONTHLY");
    ruleDTO.setInterval(1);

    final MailScheduleDto scheduleDto = new MailScheduleDto();
    scheduleDto.setUuid("1234567");
    scheduleDto.setSquence(1);
    scheduleDto.setStartDate("1629535770000");
    scheduleDto.setEndDate("1629535770000");
    scheduleDto.setEnventType("REQUEST");
    scheduleDto.setTimeZone("Asia/Shanghai");
    scheduleDto.setRecurrenceRuleDTO(ruleDTO);


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

    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    microsoft.send(mailBean, mailServerConf);
  }

  /**
   * 修改日程事件邮件
   *
   * @throws Exception
   */
  @Test(dependsOnMethods = "send2")
  public void send3() throws Exception {
    final RecurrenceRuleDTO ruleDTO = new RecurrenceRuleDTO();
    ruleDTO.setUntil("1629535770000");
    ruleDTO.setFreq("MONTHLY");
    ruleDTO.setInterval(1);

    final MailScheduleDto scheduleDto = new MailScheduleDto();
    scheduleDto.setUuid("1234567");
    scheduleDto.setSquence(2);
    scheduleDto.setStartDate("1629535770000");
    scheduleDto.setEndDate("1629535770000");
    scheduleDto.setEnventType("REQUEST");
    scheduleDto.setTimeZone("Asia/Shanghai");
    scheduleDto.setRecurrenceRuleDTO(ruleDTO);


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

    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    microsoft.send(mailBean, mailServerConf);
  }

  /**
   * 删除日程事件邮件
   *
   * @throws Exception
   */
  @Test(dependsOnMethods = "send3")
  public void send4() throws Exception {
    final RecurrenceRuleDTO ruleDTO = new RecurrenceRuleDTO();
    ruleDTO.setUntil("1629535770000");
    ruleDTO.setFreq("MONTHLY");
    ruleDTO.setInterval(1);

    final MailScheduleDto scheduleDto = new MailScheduleDto();
    scheduleDto.setUuid("1234567");
    scheduleDto.setSquence(3);
    scheduleDto.setStartDate("1629535770000");
    scheduleDto.setEndDate("1629535770000");
    scheduleDto.setEnventType("CANCEL");
    scheduleDto.setTimeZone("Asia/Shanghai");
    scheduleDto.setRecurrenceRuleDTO(ruleDTO);


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

    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    microsoft.send(mailBean, mailServerConf);
  }

  /**
   * 发送日程事件邮件
   *
   * @throws Exception
   */
  @Test
  public void send5() throws Exception {
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

    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    microsoft.send(mailBean, mailServerConf);
  }

  /**
   * 发送日程事件邮件
   *
   * @throws Exception
   */
  @Test
  public void send6() throws Exception {
    final RecurrenceRuleDTO ruleDTO = new RecurrenceRuleDTO();
    ruleDTO.setUntil("1629535770000");
    ruleDTO.setFreq("DAILY");
    ruleDTO.setInterval(1);

    final MailScheduleDto scheduleDto = new MailScheduleDto();
    scheduleDto.setUuid("1234567");
    scheduleDto.setSquence(1);
    scheduleDto.setStartDate("1629535770000");
    scheduleDto.setEndDate("1629535770000");
    scheduleDto.setEnventType("REQUEST");
    scheduleDto.setTimeZone("Asia/Shanghai");
    scheduleDto.setRecurrenceRuleDTO(ruleDTO);


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

    final MailBean mailBean = MailBean.builder()
      .type(ConfConstant.TypeEnum.FM)
      .template("commonForgetPwd")
      .mailDTO(mailDTO)
      .subject("单元测试")
      .toEmails("shennan@wafersystems.com")
      .copyTo("shennan1@wafersystems.com")
      .build();
    microsoft.send(mailBean, mailServerConf);
  }

  /**
   * 发送日程事件邮件
   *
   * @throws Exception
   */
  @Test
  public void check() {
    final BaseCheckDTO dto = new BaseCheckDTO();
    dto.setToMail("shennan@wafersystems.com");
    microsoft.check(dto, 1, mailServerConf);
  }
}