package com.wafersystems.notice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * ClassName: HttpsPostClientUtil Description:
 *
 * @author Administrator
 */
@Slf4j
public class HttpsPostClientUtil {

  private HttpsPostClientUtil() {
  }

  /**
   * post请求.
   *
   * @param url     请求地址
   * @param map     参数
   * @param charset 编码格式
   * @return 结果
   */
  public static String doPost(String url, Map<String, String> map, String charset) {
    HttpClient httpClient = null;
    HttpPost httpPost = null;
    String result = null;
    try {
      httpClient = new SslClient();
      httpPost = new HttpPost(url);
      // 设置参数
      List<NameValuePair> list = new ArrayList<>();
      Iterator iterator = map.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, String> elem = (Entry<String, String>) iterator.next();
        list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
      }

      if (!list.isEmpty()) {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
        httpPost.setEntity(entity);
      }
      HttpResponse response = httpClient.execute(httpPost);
      if (response != null) {
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
          result = EntityUtils.toString(resEntity, charset);
        }
      }
    } catch (Exception ex) {
      log.info("ex", ex);
    }
    return result;
  }
}
