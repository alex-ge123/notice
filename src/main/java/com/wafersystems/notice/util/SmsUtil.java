package com.wafersystems.notice.util;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.config.SystemProperties;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.constants.SmsConstants;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.notice.sms.model.SmsRecordVo;
import com.wafersystems.security.SecurityUtils;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 短信工具类.
 *
 * @author wafer
 */
@Slf4j
@Service
public class SmsUtil {
  @Autowired
  private SendIntercept sendIntercept;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private SendInterceptProperties properties;

  @Autowired
  private SystemProperties systemProperties;

  @Value("${sms-num-search-url}")
  private String searchUrl;

  private SmsUtil() {
  }

  /**
   * 批量发送短信
   *
   * @param templetId 模板id
   * @param phoneList 电话集合
   * @param params    参数集合
   * @param domain    域名
   * @param smsSign   短信签名
   */
  public void batchSendSms(String templetId, List<String> phoneList, List<String> params,
                           String domain, String smsSign) {
    if (phoneList.isEmpty()) {
      log.warn("接收短信的手机号不能为空！");
      return;
    }
    if (systemProperties.isCloudService()) {
      int smsNumFromCache = getSmsNumFromCache(domain);
      if (smsNumFromCache <= 0) {
        int num = cacheSmsNumFromSmsService(domain);
        if (num <= 0) {
          log.info("短信可发送数量[{}]不足，不发短信", smsNumFromCache);
          return;
        }
      }
    }
    for (String phone : phoneList) {
      if (systemProperties.isCloudService()) {
        int smsNumFromCache1 = getSmsNumFromCache(domain);
        if (smsNumFromCache1 <= 0) {
          log.info("短信可发送数量[{}]不足，不发短信", smsNumFromCache1);
          return;
        }
      }
      String result = sendSms(templetId, phone, params, domain, smsSign);
      log.info("电话号码" + phone + "发送短信的结果为：" + result);
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
    log.info("开始发送短信：templetId={},phoneNum={},params={},domain={},smsSign={}",
      templetId, phoneNum, params, domain, smsSign);
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
      log.error("拦截重复发送短信[{}]", smsDto.toString());
      return "1";
    }

    String url = ParamConstant.getURL_SMS_SERVER();
    String clientId = ParamConstant.getURL_SMS_CLIENTID();
    String secret = ParamConstant.getURL_SMS_SECRET();
    log.info("短信接口服务为:{}", url);
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
    hashMap.put("domain", StringUtils.isBlank(domain) ? ParamConstant.getDEFAULT_DOMAIN() : domain);
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
    log.info("短信参数为{}", hashMap);
    sign = SecurityUtils.calSignatureMap(hashMap);
    url = url + sign;
    log.info("发送短信的服务接口为:{}", url);

    String result = send(url, privateKey, hashMap, domain);
    //记录发送信息
    if (ConfConstant.RESULT_SUCCESS.toString().equals(result)) {
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(smsDto), properties.getSmsTimeHorizon(), TimeUnit.MINUTES);
    }
    return result;
  }

  private String send(String url, String privateKey, Map<String, String> hashMap, String domain) {
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
      httpClient = HttpClientBuilder.create().build();
      response = httpClient.execute(method);
    } catch (Exception ex) {
      // 异常信息
      log.info("发送https短信异常:", ex);
    } finally {
      if (null != httpClient) {
        try {
          httpClient.close();
        } catch (IOException e) {
          log.error("关闭httpClient异常！", e);
        }
      }
    }
    if (response != null && response.getStatusLine() != null) {
      if (response.getStatusLine().getStatusCode() == SmsConstants.SUCCESS_CODE) {
        // 解析返回内容中剩余短信数量并更新缓存
        parseSmsBalanceCacheRedis(domain, response);
        return "0";
      } else {
        // 失败
        log.error(response.getStatusLine().getStatusCode() + "");
        try {
          log.error(EntityUtils.toString(response.getEntity()));
        } catch (ParseException | IOException pe) {
          // 异常信息
          log.info("发送短信异常:", pe);
        }
      }
    }
    return "1";
  }

  /**
   * 获取短信可发数量
   *
   * @param domain 域名
   * @return 短信数量
   */
  private int getSmsNumFromCache(String domain) {
    // 查询缓存可发数量
    String o = redisTemplate.opsForValue().get(SmsConstants.SMS_NUM_KEY + domain);
    if (o != null) {
      return Integer.parseInt(o);
    }
    return -1;
  }

  /**
   * 缓存短信服务获取可发数量，
   *
   * @param domain 域名
   */
  private int cacheSmsNumFromSmsService(String domain) {
    HttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      searchUrl = searchUrl + "?domain=" + domain;
      HttpGet method = new HttpGet(searchUrl);
      method.addHeader("Content-type", "application/json; charset=utf-8");
      method.setHeader("Accept", "application/json");
      httpClient = HttpClientBuilder.create().build();
      response = httpClient.execute(method);
    } catch (Exception ex) {
      // 异常信息
      log.info("查询短信数量异常:", ex);
    } finally {
      if (null != httpClient) {
        try {
          httpClient.close();
        } catch (IOException e) {
          log.error("关闭httpClient异常！", e);
        }
      }
    }
    if (response != null) {
      if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() == SmsConstants.SUCCESS_CODE) {
        // 解析返回内容中剩余短信数量并更新缓存
        return parseSmsBalanceCacheRedis(domain, response);
      }
    }
    return -1;
  }

  /**
   * 解析返回内容中剩余短信数量并更新缓存
   *
   * @param domain   域名
   * @param response 响应
   */
  private int parseSmsBalanceCacheRedis(String domain, HttpResponse response) {
    try {
      String json = EntityUtils.toString(response.getEntity());
      SmsRecordVo smsRecordVo = JSON.parseObject(json, SmsRecordVo.class);
      redisTemplate.opsForValue().set(SmsConstants.SMS_NUM_KEY + domain, smsRecordVo.getSmsBalance() + "");
      return (int) smsRecordVo.getSmsBalance();
    } catch (IOException e) {
      log.error("解析返回对象异常:", e);
    }
    return -1;
  }
}
