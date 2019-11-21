package com.wafersystems.notice.util;

import com.wafersystems.notice.constants.SmsConstants;
import com.wafersystems.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信工具类.
 */
@Slf4j
public class SmsUtil {

  /**
   * 发送短信
   *
   * @param templetId 模板ID
   * @param phoneNum  短信号码
   * @param params    参数
   * @throws IOException IOException
   */
  public static String sendSms(String templetId, String phoneNum, List<String> params,
                               String domain, String smsSign) {
    log.debug("发短信信息.templetId={},phoneNum={},params={},domain={},smsSign={}",
      templetId, phoneNum, params, domain, smsSign);
    String url = ParamConstant.getURL_SMS_SERVER();
    String clientId = ParamConstant.getURL_SMS_CLIENTID();
    String secret = ParamConstant.getURL_SMS_SECRET();
    log.info("短信接口服务为:{}", url);
    url += '/' + clientId + '/' + secret;
    String prefix = "";
    if (phoneNum.contains(SmsConstants.AREA_CODE_PREFIX)) {
      if (!SmsConstants.CHINA_AREA_CODE.equals(phoneNum.substring(0, 3))) {
        prefix = phoneNum.substring(0, 3);
      }
      phoneNum = phoneNum.substring(3);
    }
    Map<String, String> hashMap = new HashMap<String, String>();
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
    hashMap.put("suffix", "false");
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
    HttpResponse response = null;
    if (url.startsWith(SmsConstants.HTTPS)) {
      try {
        HttpPost method = new HttpPost(url);
        method.addHeader("Content-type", "application/json; charset=utf-8");
        method.setHeader("Accept", "application/json");
        // 调用前删除私钥
        hashMap.remove(privateKey);
        method.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(hashMap),
          Charset.forName("UTF-8")));
        HttpClient httpsClient = new SslClient();
        response = httpsClient.execute(method);
      } catch (Exception ex) {
        // 异常信息
        ex.printStackTrace();
        log.info("发送https短信异常:{}", ex.getMessage());
      }
    } else {
      try {
        HttpPost method = new HttpPost(url);
        method.addHeader("Content-type", "application/json; charset=utf-8");
        method.setHeader("Accept", "application/json");
        //调用前删除私钥
        hashMap.remove(privateKey);
        method.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(hashMap),
          Charset.forName("UTF-8")));
        HttpClient httpClient = new DefaultHttpClient();
        response = httpClient.execute(method);
      } catch (Exception ex) {
        // 异常信息
        ex.printStackTrace();
        log.info("发送其它类型短信异常:{}", ex.getMessage());
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
          pe.printStackTrace();
          log.info("发送短信异常:{}", pe.getMessage());
        } catch (IOException io) {
          // 异常信息
          io.printStackTrace();
          log.info("发送短信异常:{}", io.getMessage());
        }
      }
    }
    return "1";
  }

//  public static void main(String[] args) {
//    List<String> params = new ArrayList<String>();
//    params
//        .add("Meeting Remind:Hi [yz], This is a remainder that you have an updated appointment with [人名] at [织田] on [2018-04-09 11:30 ~ 11:45], Please reply with Click to confirm http://t.cn/RmVpvp9 .【威发系统】");
//    String flag = sendSms("100427", "+8618729861476", params, "wafersystems.com");
//    System.out.println(flag);
//  }
}
