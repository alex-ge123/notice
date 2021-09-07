package com.wafersystems.notice.util;

import cn.hutool.core.util.ObjectUtil;
import com.wafersystems.notice.config.SmsProperties;
import com.wafersystems.notice.entity.SmsTemplate;
import com.wafersystems.notice.service.SmsService;
import com.wafersystems.virsical.common.core.config.AesKeyProperties;
import com.wafersystems.virsical.common.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RefreshScope
public class SmsHttpUtil {

  @Autowired
  private AesKeyProperties aesKeyProperties;
  @Autowired
  private SmsProperties smsProperties;

  @Autowired
  private SMSClientSocket smsClientSocket;

  @Autowired
  private SmsService smsService;

  public void batchSendSms(String templateId, List<String> phoneList, List<String> params,
                           String domain, String smsSign){
    if (phoneList == null || phoneList.isEmpty()) {
      log.warn("接收短信的手机号不能为空！");
      return;
    }
    for (String phone : phoneList) {
      //手机号解密
      try {
        phone = AesUtils.decryptAes(phone, aesKeyProperties.getKey());
      }catch (Exception ignore) {
      }
      if(smsProperties.isEnabled()){
        for (String str : smsProperties.getIgnore()) {
            if(phone.contains(str))
              phone = phone.replaceAll(str,"");
        }
      }
      try{
        smsClientSocket.sendSMSMessageInfo(smsProperties.getSmsIp(),smsProperties.getSmsPort(),phone,createContent(templateId,phone,params));
      } catch (Exception e) {
        log.error("{}手机号发送短信失败!",phone,e);
      }
    }

  }

  private String createContent(String templetId, String phoneNum, List<String> params){
    final SmsTemplate template = smsService.getTempById(templetId);
    if (ObjectUtil.isNotNull(template) && ObjectUtil.equal(template.getState(), 1)) {
      log.warn("短信模板[{}]禁用，将不向电话[{}]发送短信！", templetId, phoneNum);
      return "";
    }
    String content = template.getContent();
    for (int i = 0; i < params.size(); i++) {
      content = content.replaceAll("\\{"+(i+1)+"\\}",params.get(i));
    }
    return content;
  }

//  public static void main(String[] args) {
//    List<String> list = Arrays.asList("wjl","cyq","tan");
//    SmsHttpUtil smsHttpUtil = new SmsHttpUtil();
//    smsHttpUtil.httpUrl = "http://127.0.0.1:9000";
//    smsHttpUtil.batchSendSms("",list,null,"","");
//
//
//  }
}
