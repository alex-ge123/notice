package com.wafersystems.notice.util;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.constants.SmsConstants;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.security.SecurityUtils;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private SmsUtil() {
  }

  /**
   * 发送短信
   *
   * @param templetId 模板ID
   * @param phoneNum  短信号码
   * @param params    参数
   * @throws IOException IOException
   */
  public String sendSms(String templetId, String phoneNum, List<String> params,
                        String domain, String smsSign) {
    log.debug("发短信信息.templetId={},phoneNum={},params={},domain={},smsSign={}",
      templetId, phoneNum, params, domain, smsSign);
    //重复拦截
    SmsDTO smsDto = new SmsDTO();
    smsDto.setPhoneList(Arrays.asList(phoneNum));
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
    log.debug("短信参数为{}", hashMap);
    sign = SecurityUtils.calSignatureMap(hashMap);
    url = url + sign;
    log.info("发送短信的服务接口为:{}", url);

    String result = send(url, privateKey, hashMap);
    //记录发送信息
    if (ConfConstant.RESULT_SUCCESS.toString().equals(result)) {
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(smsDto), properties.getSmsTimeHorizon(), TimeUnit.MINUTES);
    }
    return result;
  }

  private static String send(String url, String privateKey, Map<String, String> hashMap) {
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
      log.info("发送https短信异常:{}", ex);
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
        return "0";
      } else {
        // 失败
        log.error(response.getStatusLine().getStatusCode() + "");
        try {
          log.error(EntityUtils.toString(response.getEntity()));
        } catch (ParseException pe) {
          // 异常信息
          log.info("发送短信异常:{}", pe);
        } catch (IOException io) {
          // 异常信息
          log.info("发送短信异常:{}", io);
        }
      }
    }
    return "1";
  }
}
