package com.wafersystems.notice.util;

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
public class HttpsPostClientUtil {

  /**
   * post请求.
   * 
   * @param url 请求地址
   * @param map 参数
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
      List<NameValuePair> list = new ArrayList<NameValuePair>();
      Iterator iterator = map.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, String> elem = (Entry<String, String>) iterator.next();
        list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
      }
      if (list.size() > 0) {
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
      ex.printStackTrace();
    }
    return result;
  }

  /**
   * 测试主方法.
   * 
   * @param args -
   */
  public static void main(String[] args) {
    String url = "https://mtest.avic-intl.cn/";
    String charset = "utf-8";

    String httpOrgCreateTest = url + "/inter/message/sendPlatfromServiceMsg";
    Map<String, String> createMap = new HashMap<String, String>();
    createMap.put("domain", "avic.cn");
    createMap.put("content", "201511122157");
    createMap.put("type", "81");
    createMap.put("serviceId", "localService");
    createMap.put("serviceName", "内部服务");
    createMap.put("icon", "http://dfsf");
    createMap.put("userId", "waferhanwu@avic.cn");
    String httpOrgCreateTestRtn = doPost(httpOrgCreateTest, createMap, charset);
    System.out.println("result:" + httpOrgCreateTestRtn);
  }

}
