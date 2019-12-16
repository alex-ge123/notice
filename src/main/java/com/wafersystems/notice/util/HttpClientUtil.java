package com.wafersystems.notice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: HttpClientUtil Description: HttpClient工具类.
 *
 * @author Moon
 */
@Slf4j
public class HttpClientUtil {

  private HttpClientUtil() {
  }

  private static final MultiThreadedHttpConnectionManager MANAGER =
    new MultiThreadedHttpConnectionManager();

  private static final int CONNECTION_TIME_OUT = 20000;

  private static final int SOCKET_TIME_OUT = 10000;

  private static final int MAX_CONNECTION_PERHOST = 5;

  private static int MAX_TOTAL_CONNECTIONS = 40;

  private static boolean INITIALED = false;

  /**
   * 否使用代理.
   */
  public static final boolean IS_USER_PROXY = false;
  /**
   * 代理服务器地址. IP:PORT.
   */
  public static final String PROXY = null;

  /**
   * Title: SetPara Description: 设置全局参数.
   */
  public static void setPara() {
    MANAGER.getParams().setConnectionTimeout(CONNECTION_TIME_OUT);
    MANAGER.getParams().setSoTimeout(SOCKET_TIME_OUT);
    MANAGER.getParams().setDefaultMaxConnectionsPerHost(MAX_CONNECTION_PERHOST);
    MANAGER.getParams().setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
    INITIALED = true;
  }

  /**
   * getHttpDefaultHeader.
   *
   * @return String
   */
  public static List<Header> getHttpDefaultHeader() {
    List<Header> headers = new ArrayList<>();
    headers.add(
      new Header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
    headers.add(new Header("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
    headers.add(new Header("Accept-Encoding", "deflate,sdch"));
    headers.add(new Header("Accept-Language", "zh-CN,zh;q=0.8"));
    headers.add(new Header("Cache-Contro", "max-age=0"));
    headers.add(new Header("Connection", "keep-alive"));
    headers.add(new Header("Cookie", ""));
    headers.add(new Header("Referer", "http://www.google.com"));
    headers.add(new Header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.8 "
      + "(KHTML, like Gecko; Google Web Preview) Chrome/19.0.1084.36 Safari/536.8"));
    return headers;
  }

  /**
   * Title: getHttpClient Description: 获得一个HttpClient.
   *
   * @return HttpClient
   */
  public static HttpClient getHttpClient() {
    HttpClient client = new HttpClient(MANAGER);
    client.getHostConfiguration().getParams().setParameter("http.default-headers",
      getHttpDefaultHeader());
    if (INITIALED) {
      HttpClientUtil.setPara();
    }
    return client;
  }

  /**
   * Title: getGetResponseWithHttpClient Description: 获取响应数据.
   *
   * @param url    - 请求的URL
   * @param encode - 请求的编码
   * @return String
   * @throws IOException IOE
   */
  public static String getGetResponseWithHttpClient(String url, String encode) throws IOException {
    return getGetResponseWithHttpClient(url, encode, IS_USER_PROXY);
  }

  /**
   * Title: getGetResponseWithHttpClient Description: 获取响应数据.
   *
   * @param url     - 请求的URL
   * @param encode  - 请求的编码
   * @param byProxy - 是否使用代理
   * @return String
   * @throws IOException IOE
   */
  public static String getGetResponseWithHttpClient(String url, String encode, boolean byProxy)
    throws IOException {
    return getGetResponseWithHttpClient(url, encode, byProxy, null);
  }

  /**
   * Title: getGetResponseWithHttpClient Description: 使用get方法获取响应数据.
   *
   * @param url     - 请求的URL
   * @param encode  - 请求的编码
   * @param byProxy - 是否使用代理
   * @param cookie  - cookie值
   * @return String
   * @throws IOException IOE
   */
  public static String getGetResponseWithHttpClient(String url, String encode, boolean byProxy,
                                                    String cookie) throws IOException {
    // 初始化get方法
    HttpClient client = new HttpClient(MANAGER);
    GetMethod get = new GetMethod(url);
    initGetMethod(client, get, byProxy, cookie);

    String result = null;
    StringBuilder resultBuffer = new StringBuilder();

    try (BufferedReader in = new BufferedReader(
      new InputStreamReader(get.getResponseBodyAsStream(), get.getResponseCharSet()));) {
      client.executeMethod(get);

      String inputLine = null;

      while ((inputLine = in.readLine()) != null) {
        resultBuffer.append(inputLine);
        resultBuffer.append("\n");
      }
      // iso-8859-1 is the default reading encode
      result = HttpClientUtil.converterStringCode(resultBuffer.toString(), get.getResponseCharSet(),
        encode);

    } finally {
      get.releaseConnection();
    }
    return result;
  }

  /**
   * Title: getGetResponseWithHttpClient Description: 使用get方法获取响应的字节数据.
   *
   * @param url     - 请求的URL
   * @param encode  - 请求的编码
   * @param byProxy - 是否使用代理
   * @param cookie  - cookie值
   * @return String
   * @throws IOException IOE
   */
  public static byte[] getGetResponseBytesWithHttpClient(String url, String encode, boolean byProxy,
                                                         String cookie) throws IOException {
    // 初始化get方法
    HttpClient client = new HttpClient(MANAGER);
    GetMethod get = new GetMethod(url);
    initGetMethod(client, get, byProxy, cookie);
    try {
      client.executeMethod(get);
      return get.getResponseBody();
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Title: getGetResponseWithHttpClient Description: 使用get方法获取响应的字节数据.
   *
   * @param url    - 请求的URL
   * @param encode - 请求的编码
   * @return String
   * @throws IOException IOE
   */
  public static byte[] getGetResponseBytesWithHttpClient(String url, String encode)
    throws IOException {
    return getGetResponseBytesWithHttpClient(url, encode, IS_USER_PROXY, null);
  }

  /**
   * Description 初始化get方法.
   *
   * @param byProxy 是否使用代理
   * @param cookie  cookie值 author fengxiang Date 2015年5月20日 下午6:20:02
   */
  private static void initGetMethod(HttpClient client, GetMethod get, boolean byProxy,
                                    String cookie) {
    if (cn.hutool.core.util.StrUtil.isNotEmpty(PROXY) && byProxy) {
      String[] hostArray = PROXY.split(":");
      client.getHostConfiguration().setProxy(hostArray[0], Integer.parseInt(hostArray[1]));
      client.getParams().setAuthenticationPreemptive(true);
    }
    client.getParams().setParameter("http.socket.timeout", CONNECTION_TIME_OUT);
    client.getParams().setParameter("http.connection.timeout", CONNECTION_TIME_OUT);

    List<Header> headers = new ArrayList<Header>();
    headers.add(new Header("User-Agent",
      "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.2 (KHTML, like Gecko) "
        + "Chrome/15.0.854.0 Safari/535.2"));
    headers.add(
      new Header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
    headers.add(new Header("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
    headers.add(new Header("Accept-Language", "zh-CN,zh;q=0.8"));
    headers.add(new Header("Cache-Control", "max-age=3600"));
    headers.add(new Header("Connection", "keep-alive"));
    if (!StrUtil.isEmptyStr(cookie)) {
      headers.add(new Header("Cookie", cookie));
    }
    client.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
    // 加入连接超时处理，2秒 modified by Gel 2014-03-11
    client.getParams().setParameter("http.socket.timeout", CONNECTION_TIME_OUT);
    client.getParams().setParameter("http.connection.timeout", CONNECTION_TIME_OUT);
    if (INITIALED) {
      HttpClientUtil.setPara();
    }
    // 设置get方法参数：自动重定向
    get.setFollowRedirects(true);
  }


  /**
   * Title: getPostResponseWithHttpClient Description: 获取响应数据.
   *
   * @param url    - 请求的URL
   * @param encode - 请求编码
   * @return String
   */
  public static String getPostResponseWithHttpClient(String url, String encode) {
    return getPostResponseWithHttpClient(url, encode, null);
  }

  /**
   * Title: getPostResponseWithHttpClient Description: 获取响应数据.
   *
   * @param url           - 请求的URL
   * @param encode        - 请求的编码
   * @param nameValuePair - post 参数
   * @return String
   */
  public static String getPostResponseWithHttpClient(String url, String encode,
                                                     NameValuePair[] nameValuePair) {
    // 初始化post方法
    HttpClient client = new HttpClient(MANAGER);
    PostMethod post = new PostMethod(url);
    initPostMethod(post, nameValuePair, client);

    String result = null;
    StringBuilder resultBuffer = new StringBuilder();

    try (BufferedReader in = new BufferedReader(
      new InputStreamReader(post.getResponseBodyAsStream(), post.getResponseCharSet()));) {
      client.executeMethod(post);

      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        resultBuffer.append(inputLine);
        resultBuffer.append("\n");
      }

      // iso-8859-1 is the default reading encode
      result = HttpClientUtil.converterStringCode(resultBuffer.toString(),
        post.getResponseCharSet(), encode);
    } catch (Exception exception) {
      log.error("getPostResponseWithHttpClient Error", exception);

      result = "";
    } finally {
      post.releaseConnection();

    }
    return result;
  }

  /**
   * Title: getPostResponseStreamWithHttpClient Description: 使用post方法获取响应数据的原始输入流.
   *
   * @param url           - 请求的URL
   * @param encode        - 请求的编码
   * @param nameValuePair - post 参数
   * @return String
   * @throws IOException 异常
   */
  public static InputStream getPostResponseStreamWithHttpClient(String url, String encode,
                                                                NameValuePair[] nameValuePair) throws IOException {
    // 初始化post方法
    HttpClient client = new HttpClient(MANAGER);
    PostMethod post = new PostMethod(url);
    initPostMethod(post, nameValuePair, client);
    try {
      client.executeMethod(post);
      return post.getResponseBodyAsStream();
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Title: getPostResponseStreamWithHttpClient Description: 使用post方法获取响应数据的原始输入流.
   *
   * @param url    - 请求的URL
   * @param encode - 请求的编码
   * @return String
   * @throws IOException 异常
   */
  public static InputStream getPostResponseStreamWithHttpClient(String url, String encode)
    throws IOException {
    return getPostResponseStreamWithHttpClient(url, encode, null);
  }

  /**
   * Description 初始化post方法.
   *
   * @param nameValuePair author fengxiang
   * @param client        - Date 2015年5月20日 下午6:39:07
   */
  private static PostMethod initPostMethod(PostMethod post, NameValuePair[] nameValuePair,
                                           HttpClient client) {
    if (INITIALED) {
      HttpClientUtil.setPara();
    }
    client.getParams().setParameter("http.socket.timeout", CONNECTION_TIME_OUT);
    client.getParams().setParameter("http.connection.timeout", CONNECTION_TIME_OUT);

    if (nameValuePair != null) {
      post.setRequestBody(nameValuePair);
    }
    post.setFollowRedirects(false);
    post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    return post;
  }

  /**
   * Title: ConverterStringCode Description: 字符编码转换.
   *
   * @param source     - 源
   * @param srcEncode  - 源编码
   * @param destEncode - 目标编码
   * @return String
   * @throws UnsupportedEncodingException UnsupportedEncodingException
   */
  private static String converterStringCode(String source, String srcEncode, String destEncode)
    throws UnsupportedEncodingException {
    return new String(source.getBytes(srcEncode), destEncode);
  }
}
