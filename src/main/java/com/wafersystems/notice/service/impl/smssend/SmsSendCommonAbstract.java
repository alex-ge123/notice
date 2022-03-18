package com.wafersystems.notice.service.impl.smssend;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.config.SmsProperties;
import com.wafersystems.notice.constants.AlertConstants;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.constants.SmsConstants;
import com.wafersystems.notice.model.SmsRecordVO;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.AlertDTO;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import com.wafersystems.virsical.common.entity.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public abstract class SmsSendCommonAbstract {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private IAlertRecordService recordService;

  @Autowired
  private SmsProperties smsProperties;

  @Resource
  private SmsSendCommonAbstract contactsAmazonSmsService;

  @Resource
  private SmsSendCommonAbstract contactsProductSmsService;

  @Value("${sms-num-search-url}")
  private String searchUrl;

  /**
   * 短信实现类
   * @return message
   */
  public abstract void smsSendService(SmsDTO smsDTO);

  /**
   * 选择短信类型
   *
   */
  public SmsSendCommonAbstract batchSendSmsSelector() {
    if (smsProperties.isAmazonType()) {
      // 亚马逊短信对接
      return contactsAmazonSmsService;
    } else {
      // 标准产品对接
      return contactsProductSmsService;
    }
  }

  /**
   * 获取短信可发数量
   *
   * @param domain 域名
   * @return 短信数量
   */
  public int  getSmsNumFromCache(String domain) {
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
  public int cacheSmsNumFromSmsService(String domain) {
    HttpResponse response = null;
    CloseableHttpClient httpClient = null;
    // 响应码
    int responseStatusCode = 0;
    // 响应结果
    String responseResult = "";
    try {
      String url = searchUrl + "?domain=" + domain;
      HttpGet method = new HttpGet(url);
      method.addHeader("Content-type", "application/json; charset=utf-8");
      method.setHeader("Accept", "application/json");

      RequestConfig defaultRequestConfig = RequestConfig.custom()
        .setSocketTimeout(5000)
        .setConnectTimeout(5000)
        .setConnectionRequestTimeout(5000)
        .build();
      httpClient = HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig).build();
      response = httpClient.execute(method);
      if (response != null && response.getStatusLine() != null) {
        responseStatusCode = response.getStatusLine().getStatusCode();
        responseResult = EntityUtils.toString(response.getEntity());
        log.info("查询短信可发数量结果：状态码[{}]，响应结果[{}]", responseStatusCode, responseResult);
      }
    } catch (Exception ex) {
      // 异常信息
      log.info("查询短信数量异常:", ex);
    } finally {
      if (null != httpClient) {
        try {
          httpClient.close();
        } catch (IOException e) {
          log.error("查询短信数量关闭httpClient异常！", e);
        }
      }
    }
    if (responseStatusCode != 0) {
      if (responseStatusCode == SmsConstants.SUCCESS_CODE) {
        // 解析返回内容中剩余短信数量并更新缓存
        return parseSmsBalanceCacheRedis(domain, responseResult, false);
      } else {
        log.error("查询短信可发数量失败，状态码[{}]，响应结果[{}]", responseStatusCode, responseResult);
      }
    }
    return -1;
  }

  /**
   * 解析返回内容中剩余短信数量并更新缓存
   *
   * @param domain         域名
   * @param responseResult 响应内容
   */
  public int parseSmsBalanceCacheRedis(String domain, String responseResult, boolean isParseSmsSend) {
    try {
      SmsRecordVO smsRecordVo;
      if (isParseSmsSend) {
        smsRecordVo = JSON.parseObject(responseResult, SmsRecordVO.class);
      } else {
        JSONObject jsonObject = JSON.parseObject(responseResult);
        Object msg = jsonObject.get("msg");
        smsRecordVo = JSON.parseObject(msg.toString(), SmsRecordVO.class);
      }
      redisTemplate.opsForValue().set(SmsConstants.SMS_NUM_KEY + domain, smsRecordVo.getSmsBalance() + "");
      return (int) smsRecordVo.getSmsBalance();
    } catch (Exception e) {
      log.error("解析返回对象异常:", e);
    }
    return -1;
  }

  /**
   * 失败处理
   *
   * @param smsDto         dto
   * @param exception 异常信息
   */
  public void failProcessor(SmsDTO smsDto, Exception exception) {
    //记录失败邮件信息
    final String smsDtoJSONStr = JSON.toJSONString(smsDto);
    String id = "SMS-FAIL-" + System.currentTimeMillis();
    redisTemplate.opsForHash().put(RedisKeyConstants.SMS_FAIL_KEY, id, smsDtoJSONStr);
    //发送站内通知系统运维人员
    final AlertDTO alertDTO = new AlertDTO();
    alertDTO.setAlertType(AlertConstants.LOCAL.getType());

    // 通过domain获取租户ID
    Integer tenantId = 0;
    if (StrUtil.isNotBlank(smsDto.getDomain())) {
      String tenantJson = (String) redisTemplate.opsForHash().get(CommonConstants.TENANT_DOMAIN_INFO_KEY, smsDto.getDomain());
      if (StrUtil.isNotBlank(tenantJson)) {
        TenantDTO tenantDTO = JSON.parseObject(tenantJson, TenantDTO.class);
        if (ObjectUtil.isNotEmpty(tenantDTO)) {
          tenantId = tenantDTO.getId();
        }
      }
    }
    alertDTO.setTenantId(tenantId);
    alertDTO.setAlertId(id);
    alertDTO.setProduct(ProductCodeEnum.COMMON.getCode());
    alertDTO.setTitle("发送短信至[" + smsDto.getPhoneList().get(0) + "]失败！");
    alertDTO.setContent("发送短信至[" + smsDto.getPhoneList().get(0) + "]失败！,失败原因[" + exception.getMessage() + "]");
    recordService.processAlertMessage(alertDTO);
  }

}
