package com.wafersystems.notice.service.impl.smssend;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.constants.*;
import com.wafersystems.notice.entity.SmsTemplate;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.notice.service.SmsService;
import com.wafersystems.security.SecurityUtils;
import com.wafersystems.virsical.common.core.config.AesKeyProperties;
import com.wafersystems.virsical.common.core.config.SystemProperties;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import com.wafersystems.virsical.common.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Primary
@Service("contactsProductSmsService")
public class SmsSendProductServiceImpl extends SmsSendCommonAbstract {

  @Autowired
  private SendIntercept sendIntercept;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private SendInterceptProperties properties;

  @Autowired
  private SystemProperties systemProperties;

  @Autowired
  private SmsService smsService;

  @Autowired
  private AesKeyProperties aesKeyProperties;

  @Autowired
  private StringEncryptor stringEncryptor;

  @Autowired
  private IAlertRecordService recordService;

  @Value("${sms-num-search-url}")
  private String searchUrl;

  @Override
  public void smsSendService(SmsDTO smsDTO) {
    if (smsDTO.getPhoneList() == null || smsDTO.getPhoneList().isEmpty()) {
      log.warn("接收短信的手机号不能为空！");
      return;
    }
    if (systemProperties.isCloudService()) {
      int smsNumFromCache = getSmsNumFromCache(smsDTO.getDomain());
      if (smsNumFromCache <= 0) {
        int num = cacheSmsNumFromSmsService(smsDTO.getDomain());
        if (num <= 0) {
          log.info("短信可发送数量[{}]不足，不发短信", smsNumFromCache);
          return;
        }
      }
    }
    for (String phone : smsDTO.getPhoneList()) {
      if (systemProperties.isCloudService()) {
        int smsNumFromCache1 = getSmsNumFromCache(smsDTO.getDomain());
        if (smsNumFromCache1 <= 0) {
          log.info("短信可发送数量[{}]不足，不发短信", smsNumFromCache1);
          return;
        }
      }
      //手机号解密
      try {
        phone = AesUtils.decryptAes(phone, aesKeyProperties.getKey());
      } catch (Exception ignore) {
      }
      String result = sendSms(smsDTO.getTemplateId(), phone, smsDTO.getParamList(), smsDTO.getDomain(), smsDTO.getSmsSign());
      log.info("电话号码" + StrUtil.hide(phone, phone.length() - 4, phone.length()) + "发送短信的结果为：" + result);
    }
  }

  /**
   * 发送短信
   *
   * @param templetId 模板ID
   * @param phoneNum  短信号码
   * @param params    参数
   */
  private String sendSms(String templetId, String phoneNum, List<String> params,
                         String domain, String smsSign) {
    final SmsTemplate template = smsService.getTempById(templetId);
    if (ObjectUtil.isNotNull(template) && ObjectUtil.equal(template.getState(), 1)) {
      log.warn("短信模板[{}]禁用，将不向电话[{}]发送短信！", templetId, phoneNum);
      return "1";
    }
    //重复拦截
    SmsDTO smsDto = new SmsDTO();
    smsDto.setPhoneList(Collections.singletonList(phoneNum));
    smsDto.setParamList(params);
    smsDto.setSmsSign(smsSign);
    smsDto.setDomain(domain);
    smsDto.setTemplateId(templetId);
    String redisKey = String.format(RedisKeyConstants.SMS_KEY,
      phoneNum, templetId, smsSign, smsDto.hashCode());
    if (sendIntercept.smsBoolIntercept(smsDto, redisKey)) {
      log.error("拦截重复发送短信[{}]", smsDto);
      return "1";
    }

    String url = ParamConstant.getUrlSmsServer();
    String clientId = ParamConstant.getUrlSmsClientId();
    String secret = ParamConstant.getUrlSmsSecret();
    log.debug("短信接口服务为:{}", url);
    url += '/' + clientId + '/' + secret;

    String prefix = "";
    int areaCodeLength = 3;
    if (phoneNum.contains(SmsConstants.AREA_CODE_PREFIX)) {
      if (!SmsConstants.CHINA_AREA_CODE.equals(phoneNum.substring(0, areaCodeLength))) {
        prefix = phoneNum.substring(0, areaCodeLength);
      }
      phoneNum = phoneNum.substring(areaCodeLength);
    }
    Map<String, String> hashMap = new HashMap<>(20);
    // 不支持带+86的
    hashMap.put("calleeNbr", phoneNum);
    hashMap.put("templetId", templetId);
    hashMap.put("domain", StringUtils.isBlank(domain) ? ParamConstant.getDefaultDomain() : domain);
    hashMap.put("value1", params.get(0));
    for (int i = 1; i < params.size(); i++) {
      if (StringUtils.isNotBlank(params.get(i))) {
        int index = i + 1;
        hashMap.put("value" + index, params.get(i));
      }
    }
    // 签名
    hashMap.put("smsSign", smsSign);
    // 签名是否前缀
    hashMap.put("suffix", "true");
    // 必须和服务器时间差三分钟以内的时间戳才能通过验证
    hashMap.put("timestamp", System.currentTimeMillis() + "");
    String privateKey = "47a31cf3b5bea2e4e9a12659f4181283";
    String sign = "";
    // 私钥
    hashMap.put(privateKey, "");
    if (StringUtils.isNotBlank(prefix)) {
      // 国际短信使用
      hashMap.put("countorycode", prefix);
      url += "/internationalSend?sign=";
    } else {
      url += "/send?sign=";
    }
    final String encrypt = stringEncryptor.encrypt(hashMap.toString());
    log.info("开始发送短信：templetId={},phoneNum={},短信参数为{}",
      templetId, StrUtil.hide(phoneNum, phoneNum.length() - 4, phoneNum.length()), encrypt);
    sign = SecurityUtils.calSignatureMap(hashMap);
    url = url + sign;
    log.debug("发送短信的服务接口为:{}", url);

    String result = send(url, privateKey, hashMap, domain, smsDto);
    //记录发送信息
    if (ConfConstant.RESULT_SUCCESS.toString().equals(result)) {
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(smsDto), properties.getSmsTimeHorizon(), TimeUnit.MINUTES);
    }
    return result;
  }

  private String send(String url, String privateKey, Map<String, String> hashMap, String domain, SmsDTO smsDto) {
    HttpResponse response = send(0, url, privateKey, hashMap, smsDto);
    // 响应码
    int responseStatusCode = 0;
    if (response != null && response.getStatusLine() != null) {
      responseStatusCode = response.getStatusLine().getStatusCode();
      log.info("发送https短信结果：状态码[{}]，响应结果[{}]", responseStatusCode, response.getFirstHeader("responseResult").getValue());
    } else {
      log.warn("response == null 或 response.getStatusLine() == null");
    }
    if (responseStatusCode != 0) {
      if (responseStatusCode == SmsConstants.SUCCESS_CODE) {
        if (systemProperties.isCloudService()) {
          // 解析返回内容中剩余短信数量并更新缓存
          parseSmsBalanceCacheRedis(domain, response.getFirstHeader("responseResult").getValue(), true);
        }
        return "0";
      } else {
        log.error("发送短信失败，状态码[{}]，响应结果[{}]", responseStatusCode, response.getFirstHeader("responseResult").getValue());
      }
    }
    return "1";
  }

  private HttpResponse send(Integer count, String url, String privateKey, Map<String, String> hashMap, SmsDTO smsDto) {
    HttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      HttpPost method = new HttpPost(url);
      method.addHeader("Content-type", "application/json; charset=utf-8");
      method.setHeader("Accept", "application/json");
      // 调用前删除私钥
      hashMap.remove(privateKey);
      method.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(hashMap),
        StandardCharsets.UTF_8));
      RequestConfig defaultRequestConfig = RequestConfig.custom()
        .setSocketTimeout(65000)
        .setConnectTimeout(5000)
        .setConnectionRequestTimeout(5000)
        .build();
      httpClient = HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig).build();
      response = httpClient.execute(method);
      String responseResult = EntityUtils.toString(response.getEntity());
      //将entity临时放到header里，供后面使用。  关闭httpClient后将获取不到
      response.setHeader("responseResult", responseResult);
    } catch (Exception exception) {
      count++;
      if (count < ParamConstant.getSmsRepeatCount()) {
        log.warn("短信发送失败：", exception);
        String phone = hashMap.get("calleeNbr");
        phone = StrUtil.hide(phone, phone.length() - 4, phone.length());
        log.warn("模板ID[" + hashMap.get("templetId") + "],发往[" + phone + "]的短信第" + count + "次重发......");
        response = this.send(count, url, privateKey, hashMap, smsDto);
      } else {
        log.error("短信发送失败：", exception);
        // 失败处理
        failProcessor(smsDto, exception);
      }
    } finally {
      if (null != httpClient) {
        try {
          httpClient.close();
        } catch (IOException e) {
          log.error("发送https短信关闭httpClient异常！", e);
        }
      }
    }
    return response;
  }





}