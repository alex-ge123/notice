package com.wafersystems.notice.receiver;

import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.constants.ParamConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2019/12/27 10:17
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
public class ReceiverTest extends BaseTest {
  @Autowired
  private Receiver receiver;

  @BeforeClass
  public void initData() {
    ParamConstant.setUrlSmsServer("https://yapi.rd.virsical.cn/mock/121/sms");
  }

  @Test
  public void testMail() throws Exception {
    String message = "{\"clientId\":\"shennan@wafersystems.com\",\"data\":{\"lang\":\"zh_CN\",\"logo\":\"\",\"subject\":\"预约拒绝通知\",\"tempName\":\"mail.qd.audit.inform\",\"tenantId\":6,\"toMail\":\"liuyawei@wafersystems.com\",\"value1\":\"刘亚伟\",\"value10\":\"\",\"value11\":\"\",\"value12\":\"\",\"value13\":\"\",\"value14\":\"\",\"value15\":\"\",\"value16\":\"\",\"value17\":\"\",\"value18\":\"\",\"value19\":\"\",\"value2\":\"11\",\"value20\":\"\",\"value21\":\"\",\"value22\":\"\",\"value23\":\"\",\"value24\":\"\",\"value25\":\"\",\"value26\":\"\",\"value27\":\"\",\"value28\":\"\",\"value29\":\"\",\"value3\":\"2019-12-26 10:35\",\"value30\":\"\",\"value31\":\"\",\"value32\":\"\",\"value33\":\"\",\"value34\":\"\",\"value4\":\"访客\",\"value5\":\"\",\"value6\":\"\",\"value7\":\"\",\"value8\":\"\",\"value9\":\"\"},\"lang\":\"zh_CN\",\"msgAction\":\"ADD\",\"msgId\":\"86fe8167-1a86-4905-82e0-f5d313698aa1\",\"msgTime\":1577327459155,\"msgType\":\"ONE\",\"product\":\"vst\"}";
    receiver.mail(message);
  }

  @Test
  public void testSms() throws Exception {
    String message = "{\"clientId\":\"+8615529360323\",\"data\":{\"paramList\":[\"11\",\"2019-12-26 10:35\",\"访客\",\"\",\"\",\"\"],\"phoneList\":[\"+8615529360323\"],\"smsSign\":\"威发系统\",\"templateId\":\"105856\"},\"lang\":\"zh\",\"msgAction\":\"ADD\",\"msgId\":\"9773bdf1-d844-4278-ab85-ea896535ab43\",\"msgTime\":1577327459154,\"msgType\":\"ONE\",\"product\":\"vst\"}";
    receiver.sms(message);
  }
}