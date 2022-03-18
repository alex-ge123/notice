package com.wafersystems.notice.service.impl.smssend;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.config.SmsProperties;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.entity.SmsTemplate;
import com.wafersystems.notice.intercept.SendIntercept;
import com.wafersystems.notice.service.SmsService;
import com.wafersystems.virsical.common.core.config.AesKeyProperties;
import com.wafersystems.virsical.common.core.config.SystemProperties;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import com.wafersystems.virsical.common.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
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
@Service("contactsAmazonSmsService")
public class SmsSendAmazonServiceImpl extends SmsSendCommonAbstract{

  private Map<String, MessageAttributeValue> smsAttributes;

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
  private SmsProperties smsProperties;

  @Override
  public void smsSendService(SmsDTO smsDTO) {
    log.info("start send aws sms");
    if (smsDTO.getPhoneList() == null || smsDTO.getPhoneList().isEmpty()) {
      log.warn("接收短信的手机号不能为空！");
      return;
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
      String result = sendSmsAwsSns(smsDTO.getTemplateId(), phone, smsDTO.getParamList(), smsDTO.getDomain(), smsDTO.getSmsSign());
      log.info("电话号码" + StrUtil.hide(phone, phone.length() - 4, phone.length()) + "发送短信的结果为：" + result);
    }
  }

  private String sendSmsAwsSns(String templateId, String phoneNum, List<String> params, String domain, String smsSign) {
    final SmsTemplate template = smsService.getTempById(templateId);
    if (ObjectUtil.isNotNull(template) && ObjectUtil.equal(template.getState(), 1)) {
      log.warn("短信模板[{}]禁用，将不向电话[{}]发送短信！", templateId, phoneNum);
      return "1";
    }
    //重复拦截
    SmsDTO smsDto = new SmsDTO();
    smsDto.setPhoneList(Collections.singletonList(phoneNum));
    smsDto.setParamList(params);
    smsDto.setSmsSign(smsSign);
    smsDto.setDomain(domain);
    smsDto.setTemplateId(templateId);
    String redisKey = String.format(RedisKeyConstants.SMS_KEY,
      phoneNum, templateId, smsSign, smsDto.hashCode());
    if (sendIntercept.smsBoolIntercept(smsDto, redisKey)) {
      log.error("拦截重复发送短信[{}]", smsDto);
      return "1";
    }
    params.add(0, "");
    String[] args = new String[params.size()];
    String content = MessageFormat.format(template.getContent(), params.toArray(args));
    String messageId = sendSecurexSms(smsDto, phoneNum, content);
    //记录发送信息
    if (StrUtil.isNotBlank(messageId)) {
      redisTemplate.opsForValue().set(
        redisKey, JSON.toJSONString(smsDto), properties.getSmsTimeHorizon(), TimeUnit.MINUTES);
    }
    return messageId;
  }

  private String sendSecurexSms(SmsDTO smsDto, String phoneNum, String content) {
    log.info("开始发送短信 手机号为{}", StrUtil.hide(phoneNum, phoneNum.length() - 4, phoneNum.length()));
    log.info("参数发送者是{}, 内容是{}", smsProperties.getSenderID(), stringEncryptor.encrypt(content));
    PublishResult publishResult = sendSMSMessage(smsDto, 0, phoneNum, content);
    log.info("publishResult is {}", JSONObject.toJSONString(publishResult));
    return publishResult.getMessageId();
  }

  private Map<String, MessageAttributeValue> getDefaultSMSAttributes() {
    if (smsAttributes == null) {
      smsAttributes = new HashMap<>();
      smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
        .withStringValue(smsProperties.getSenderID())
        .withDataType("String"));
      smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
        .withStringValue(smsProperties.getMaxPrice())
        .withDataType("Number"));
      smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
        .withStringValue(smsProperties.getSmsType())
        .withDataType("String"));
    }
    return smsAttributes;
  }

  private PublishResult sendSMSMessage(SmsDTO smsDto, int count, String phoneNumber, String message) {
    return sendSMSMessage(smsDto, count, phoneNumber, message, getDefaultSMSAttributes());
  }

  private PublishResult sendSMSMessage(SmsDTO smsDto, int count, String phoneNumber, String message, Map<String, MessageAttributeValue> smsAttributes) {
    AWSCredentials awsCredentials = new AWSCredentials() {
      @Override
      public String getAWSAccessKeyId() {
        return smsProperties.getIamAccessKey(); // 带有发短信权限的 IAM 的 ACCESS_KEY
      }

      @Override
      public String getAWSSecretKey() {
        return smsProperties.getIamSecretKey(); // 带有发短信权限的 IAM 的 SECRET_KEY
      }
    };
    AWSCredentialsProvider provider = new AWSCredentialsProvider() {
      @Override
      public AWSCredentials getCredentials() {
        return awsCredentials;
      }

      @Override
      public void refresh() {
      }
    };
    AmazonSNS amazonSNS = null;
    try {
      // ap-southeast-1  us-east-1  ap-southeast-1
      amazonSNS = AmazonSNSClientBuilder.standard().withCredentials(provider).withRegion(smsProperties.getRegionKey()).build();
    } catch (Exception exception) {
      // 失败记录
      count++;
      if (count < ParamConstant.getSmsRepeatCount()) {
        log.warn("短信发送失败：", exception);
        phoneNumber = StrUtil.hide(phoneNumber, phoneNumber.length() - 4, phoneNumber.length());
        log.warn("发往[" + phoneNumber + "]的短信第" + count + "次重发......");
        this.sendSMSMessage(smsDto, count, phoneNumber, message);
      } else {
        log.error("短信发送失败：", exception);
        // 失败处理
        failProcessor(smsDto, exception);
      }
    }
    return amazonSNS.publish(
      new PublishRequest()
        .withMessage(message)
        .withPhoneNumber(phoneNumber)
        .withMessageAttributes(smsAttributes)
    );
  }

}
