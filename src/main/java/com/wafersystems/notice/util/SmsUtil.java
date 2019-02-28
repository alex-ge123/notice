package com.wafersystems.notice.util;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信工具类.
 */
@Slf4j
public class SmsUtil {

  /**
   * @param templetId 模板ID
   * @param phoneNum 短信号码
   * @param params 参数
   * @throws IOException IOException
   */
  public static String sendSms(String templetId, String phoneNum, List<String> params,
      String domain) {
    // String url = "https://work.virsical.cn/sms/ability";
    // String clientId = "meeting";
    // String secret = "23cbf2b615184418";
    String url = ParamConstant.getURL_SMS_SERVER();
    String clientId = ParamConstant.getURL_SMS_CLIENTID();
    String secret = ParamConstant.getURL_SMS_SECRET();
    url += '/' + clientId + '/' + secret;
    String prefix = "";
    if (phoneNum.indexOf("+") != -1) {
      if (!phoneNum.substring(0, 3).equals("+86")) {
        prefix = phoneNum.substring(0, 3);
      }
      phoneNum = phoneNum.substring(3);
    }
    Map<String, String> hashMap = new HashMap<String, String>();
    hashMap.put("calleeNbr", phoneNum);// 不支持带+86的
    hashMap.put("templetId", templetId);
    hashMap.put("domain", StringUtils.isBlank(domain) ? ParamConstant.getDEFAULT_DOMAIN() : domain);
    hashMap.put("value1", params.get(0));
    if (params.size() > 1 && StringUtils.isNotBlank(params.get(1))) {
      hashMap.put("value2", params.get(1));
    }
    if (params.size() > 2 && StringUtils.isNotBlank(params.get(2))) {
      hashMap.put("value3", params.get(2));
    }
    if (params.size() > 3 && StringUtils.isNotBlank(params.get(3))) {
      hashMap.put("value4", params.get(3));
    }
    if (params.size() > 4 && StringUtils.isNotBlank(params.get(4))) {
      hashMap.put("value5", params.get(4));
    }
    if (params.size() > 5 && StringUtils.isNotBlank(params.get(5))) {
      hashMap.put("value6", params.get(5));
    }
    hashMap.put("smsSign", ParamConstant.getSMS_SIGN_NAME());// 签名
    hashMap.put("suffix", "false");// 签名是否前缀
    hashMap.put("timestamp", System.currentTimeMillis() + "");// 必须和服务器时间差三分钟以内的时间戳才能通过验证
    String privateKey = "47a31cf3b5bea2e4e9a12659f4181283";
    String sign = "";
    hashMap.put(privateKey, "");// 私钥
    if (StringUtils.isNotBlank(prefix)) {
      // 国际短信使用
      hashMap.put("countorycode", prefix);
      url += "/internationalSend?sign=";
    } else {
      url += "/send?sign=";
    }
    sign = SecurityUtils.calSignatureMap(hashMap);
    url = url + sign;
    HttpResponse response = null;
    if (url.startsWith("https")) {
      try {
        HttpPost method = new HttpPost(url);
        method.addHeader("Content-type", "application/json; charset=utf-8");
        method.setHeader("Accept", "application/json");
        hashMap.remove(privateKey);// 调用前删除私钥
        method.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(hashMap),
            Charset.forName("UTF-8")));
        HttpClient httpsClient = new SslClient();
        response = httpsClient.execute(method);
      } catch (Exception ex) {
        ex.printStackTrace();// 异常信息
      }
    } else {
      try {
        HttpPost method = new HttpPost(url);
        method.addHeader("Content-type", "application/json; charset=utf-8");
        method.setHeader("Accept", "application/json");
        hashMap.remove(privateKey);// 调用前删除私钥
        method.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(hashMap),
            Charset.forName("UTF-8")));
        HttpClient httpClient = new DefaultHttpClient();
        response = httpClient.execute(method);
      } catch (Exception ex) {
        ex.printStackTrace();// 异常信息
      }
    }
    if (response != null) {
      if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200) {
        return "0";
      } else {
        // 失败
        log.error(response.getStatusLine().getStatusCode() + "");
        try {
          log.error(EntityUtils.toString(response.getEntity()));
        } catch (ParseException pe) {
          pe.printStackTrace();// 异常信息
        } catch (IOException io) {
          io.printStackTrace();// 异常信息
        }
      }
    }
    return "1";
  }

  public static void main(String[] args) {
    List<String> params = new ArrayList<String>();
    params
        .add("Meeting Remind:Hi [yz], This is a remainder that you have an updated appointment with [人名] at [织田] on [2018-04-09 11:30 ~ 11:45], Please reply with Click to confirm http://t.cn/RmVpvp9 .【威发系统】");
    String flag = sendSms("100427", "+8618729861476", params, "wafersystems.com");
    System.out.println(flag);
  }
}
