package com.wafersystems.notice.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.BaseTest;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.model.ParameterDTO;
import com.wafersystems.virsical.common.core.config.SystemProperties;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * @author shennan
 * @date 2019/12/27 10:17
 */
@Rollback
@WithMockUser(authorities = {"admin@platform@upms_sys_tenant_add"})
@Slf4j
public class ReceiverTest extends BaseTest {
  @Autowired
  private Receiver receiver;
  @Autowired
  private StringRedisTemplate redisTemplate;
  @Autowired
  private SystemProperties systemProperties;

  @BeforeClass
  public void initData() {
    redisTemplate.opsForHash().put("base:notice:param:common", "URL_SMS_SERVER", "https://yapi.rd.virsical.cn/mock/121/sms");
    log.info("发送短信host:{}", redisTemplate.opsForHash().get("base:notice:param:common", "URL_SMS_SERVER"));
    ParamConstant.setUrlSmsServer("https://yapi.rd.virsical.cn/mock/121/sms");

    redisTemplate.opsForHash().put("base:notice:param:common", "DEFAULT_MAIL_HOST", "12345");
    log.info("发送邮件host:{}", redisTemplate.opsForHash().get("base:notice:param:common", "DEFAULT_MAIL_HOST"));
  }

  @Test
  public void testMail() throws Exception {
    String message = "{\"clientId\":\"shennan@wafersystems.com\",\"data\":{\"lang\":\"zh_CN\",\"logo\":\"\",\"subject\":\"预约拒绝通知\",\"tempName\":\"mail.qd.audit.inform\",\"tenantId\":6,\"toMail\":\"liuyawei@wafersystems.com\",\"value1\":\"刘亚伟\",\"value10\":\"\",\"value11\":\"\",\"value12\":\"\",\"value13\":\"\",\"value14\":\"\",\"value15\":\"\",\"value16\":\"\",\"value17\":\"\",\"value18\":\"\",\"value19\":\"\",\"value2\":\"11\",\"value20\":\"\",\"value21\":\"\",\"value22\":\"\",\"value23\":\"\",\"value24\":\"\",\"value25\":\"\",\"value26\":\"\",\"value27\":\"\",\"value28\":\"\",\"value29\":\"\",\"value3\":\"2019-12-26 10:35\",\"value30\":\"\",\"value31\":\"\",\"value32\":\"\",\"value33\":\"\",\"value34\":\"\",\"value4\":\"访客\",\"value5\":\"\",\"value6\":\"\",\"value7\":\"\",\"value8\":\"\",\"value9\":\"\"},\"lang\":\"zh_CN\",\"msgAction\":\"ADD\",\"msgId\":\"86fe8167-1a86-4905-82e0-f5d313698aa1\",\"msgTime\":1577327459155,\"msgType\":\"ONE\",\"product\":\"vst\"}";
    receiver.mail(message);
  }

  @Test
  public void testMailForTenantConf() throws Exception {
    String url = "/parameter/batch-set";
    String content = "[{\"id\":29,\"paramKey\":\"MAIL_SSL\",\"paramValue\":\"FaDR+Wh1NaQkuNClC3+JYg==\"," +
      "\"paramDesc\":null,\"type\":\"MAIL\",\"tenantId\":1001},{\"id\":30,\"paramKey\":\"MAIL_SERVER_TYPE\"," +
      "\"paramValue\":\"fU8bpUPSaiid6gWu2pSXJg==\",\"paramDesc\":null,\"type\":\"MAIL\",\"tenantId\":1001}," +
      "{\"id\":31,\"paramKey\":\"MAIL_HOST\",\"paramValue\":\"l49XecwDhzApl3TvLs0vaJybb2lUZIGcDID4SGsyHIk=\"," +
      "\"paramDesc\":null,\"type\":\"MAIL\",\"tenantId\":1001},{\"id\":32,\"paramKey\":\"MAIL_PORT\"," +
      "\"paramValue\":\"TAiqJiFLhMcOuTVxg0259A==\",\"paramDesc\":null,\"type\":\"MAIL\",\"tenantId\":1001}," +
      "{\"id\":33,\"paramKey\":\"MAIL_MAILNAME\",\"paramValue\":\"k4Jwc/lkh9kpi9Aisuv2tw==\",\"paramDesc\":null," +
      "\"type\":\"MAIL\",\"tenantId\":1001},{\"id\":34,\"paramKey\":\"MAIL_FROM\"," +
      "\"paramValue\":\"v5efFsTJ+CeN2SGyxAm5ujCkbtyonzvMoDuxRoRj1Nc=\",\"paramDesc\":null,\"type\":\"MAIL\"," +
      "\"tenantId\":1001},{\"id\":35,\"paramKey\":\"MAIL_USERNAME\"," +
      "\"paramValue\":\"v5efFsTJ+CeN2SGyxAm5ujCkbtyonzvMoDuxRoRj1Nc=\",\"paramDesc\":null,\"type\":\"MAIL\"," +
      "\"tenantId\":1001},{\"id\":36,\"paramKey\":\"MAIL_PASSWORD\",\"paramValue\":\"RLF75hKKtfVVssZsIP1J5g==\"," +
      "\"paramDesc\":null,\"type\":\"MAIL\",\"tenantId\":1001},{\"id\":37,\"paramKey\":\"MAIL_AUTH\"," +
      "\"paramValue\":\"FaDR+Wh1NaQkuNClC3+JYg==\",\"paramDesc\":null,\"type\":\"MAIL\",\"tenantId\":1001}]";
    JSONObject jsonObject = doPost(url, content, null, false, true);
    Assert.assertEquals(jsonObject.get("code"), CommonConstants.SUCCESS);

    String message = "{\"clientId\":\"shennan@wafersystems.com\",\"data\":{\"lang\":\"zh_CN\",\"logo\":\"\",\"subject\":\"预约拒绝通知\",\"tempName\":\"mail.qd.audit.inform\",\"tenantId\":1001,\"toMail\":\"liuyawei@wafersystems.com\",\"value1\":\"刘亚伟\",\"value10\":\"\",\"value11\":\"\",\"value12\":\"\",\"value13\":\"\",\"value14\":\"\",\"value15\":\"\",\"value16\":\"\",\"value17\":\"\",\"value18\":\"\",\"value19\":\"\",\"value2\":\"11\",\"value20\":\"\",\"value21\":\"\",\"value22\":\"\",\"value23\":\"\",\"value24\":\"\",\"value25\":\"\",\"value26\":\"\",\"value27\":\"\",\"value28\":\"\",\"value29\":\"\",\"value3\":\"2019-12-26 10:35\",\"value30\":\"\",\"value31\":\"\",\"value32\":\"\",\"value33\":\"\",\"value34\":\"\",\"value4\":\"访客\",\"value5\":\"\",\"value6\":\"\",\"value7\":\"\",\"value8\":\"\",\"value9\":\"\"},\"lang\":\"zh_CN\",\"msgAction\":\"ADD\",\"msgId\":\"86fe8167-1a86-4905-82e0-f5d313698aa1\",\"msgTime\":1577327459155,\"msgType\":\"ONE\",\"product\":\"vst\"}";
    receiver.mail(message);
  }

  @Test
  public void testSms() throws Exception {
    String message = "{\"clientId\":\"+8615529360323\",\"data\":{\"paramList\":[\"11\",\"2019-12-26 10:35\",\"访客\",\"\",\"\",\"\"],\"phoneList\":[\"+8615529360323\"],\"smsSign\":\"威发系统\",\"templateId\":\"105856\"},\"lang\":\"zh\",\"msgAction\":\"ADD\",\"msgId\":\"9773bdf1-d844-4278-ab85-ea896535ab43\",\"msgTime\":1577327459154,\"msgType\":\"ONE\",\"product\":\"vst\"}";
    receiver.sms(message);
  }

  @Test
  public void testSmsForCloud() throws Exception {
    String message = "{\"clientId\":\"+8615529360323\",\"data\":{\"paramList\":[\"11\",\"2019-12-26 10:35\",\"访客\",\"\",\"\",\"\"],\"phoneList\":[\"+8615529360323\"],\"smsSign\":\"威发系统\",\"templateId\":\"105856\"},\"lang\":\"zh\",\"msgAction\":\"ADD\",\"msgId\":\"9773bdf1-d844-4278-ab85-ea896535ab43\",\"msgTime\":1577327459154,\"msgType\":\"ONE\",\"product\":\"vst\"}";
    systemProperties.setCloudService(true);
    receiver.sms(message);
  }

  @Test
  public void testSmsBstch() throws Exception {
    String message = "{\"clientId\":\"+8615529360323\",\"data\":[{\"paramList\":[\"11\",\"2019-12-26 10:35\",\"访客\",\"\",\"\",\"\"],\"phoneList\":[\"+8615529360323\"],\"smsSign\":\"威发系统\",\"templateId\":\"105856\"}],\"lang\":\"zh\",\"msgAction\":\"ADD\",\"msgId\":\"9773bdf1-d844-4278-ab85-ea896535ab43\",\"msgTime\":1577327459154,\"msgType\":\"BATCH\",\"product\":\"vst\"}";
    receiver.sms(message);
  }
}