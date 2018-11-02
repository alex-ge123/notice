package com.wafersystems.notice.util;

import lombok.extern.log4j.Log4j;

/**
 * ClassName: AccesstokenUtil Description: 访问令牌工具类.
 * 
 * @author gelin
 */
@Log4j
public final class AccesstokenUtil {

  /**
   * Title: generateToken Description: 对"用户id+识别字符串"组合而成的字符串使用AES加密生成的字符串生成accessToken.
   * 
   * @param uid 用户id作为参数
   * @return String
   */
  public static String generateTokenWithUid(String uid) {
    try {
      Accesstoken tokenInfo = Accesstoken.createInstance(uid);
      String tokenStr = tokenInfo.toTokenStr();
      return AesUtil.encrypt(tokenStr.toString(), ConfConstant.AES_KEY);
    } catch (Exception exception) {
      log.error("生成令牌失败", exception);
      return null;
    }
  }

  /**
   * Title: generateToken Description: 对"用户id+登录密码+识别字符串"组合而成的字符串使用AES加密生成的字符串生成accessToken.
   * 
   * @param uid 用户id作为参数
   * @param password 登录密码
   * @return String
   */
  public static String generateTokenWithUidPwd(String uid, String password) {
    try {
      Accesstoken tokenInfo = Accesstoken.createInstance(uid, password);
      String tokenStr = tokenInfo.toTokenStr();
      return AesUtil.encrypt(tokenStr, ConfConstant.AES_KEY);
    } catch (Exception exception) {
      log.error("生成令牌失败", exception);
      return null;
    }
  }

  /**
   * 扩展token，增加客户端类型以及版本信息.
   * 
   * @param uid 用户ID
   * @param clientType 客户端类型
   * @param version 客户端版本
   * @return String
   */
  public static String generateTokenWithFull(String uid, String clientType, String version) {
    try {
      Accesstoken tokenInfo = Accesstoken.createInstance(uid, clientType, version);
      String tokenStr = tokenInfo.toTokenStr();
      return AesUtil.encrypt(tokenStr, ConfConstant.AES_KEY);
    } catch (Exception exception) {
      log.error("生成令牌失败", exception);
      return null;
    }
  }

  /**
   * Description 解析用户登录令牌获取用户信息.
   * 
   * @param token 用户登录令牌
   * @return 用户登录令牌信息对象 author fengxiang Date 2015年7月1日 上午11:17:27
   */
  public static Accesstoken parseToken(String token, Accesstoken.TokenType tokenType) {
    try {
      Accesstoken tokenInfo = null;
      if (!"null".equals(token) && token != null && token.length() > 0
          && !"undefined".equals(token)) {
        String tokenStr = AesUtil.decrypt(token.trim(), ConfConstant.AES_KEY);
        tokenInfo = Accesstoken.createFromTokenStr(tokenStr, tokenType);
      }
      if (tokenInfo == null) {
        tokenInfo = new Accesstoken();
      }
      return tokenInfo;
    } catch (Exception exception) {
      log.error("生成令牌失败", exception);
      return null;
    }
  }

  /**
   * .
   * <p>
   * Title:
   * </p>
   * <p>
   * Description:
   * </p>
   */
  private AccesstokenUtil() {}

  /**
   * 扩展token，增加客户端类型以及版本信息.
   * 
   * @param uid 用户ID
   * @param clientType 客户端类型
   * @param version 客户端版本
   * @return String
   */
  public static String generateTokenWithOutTimeStamp(String uid, String clientType,
      String version) {
    String accessToken = "";
    StringBuilder tokenStr = new StringBuilder();
    String randomValue = RandomNumberUtil.getCharAndNumr(6);
    tokenStr.append(uid).append(ConfConstant.DIVIDER).append(clientType)
        .append(ConfConstant.DIVIDER).append(version).append(ConfConstant.DIVIDER)
        .append(randomValue);
    try {
      accessToken = AesUtil.encrypt(tokenStr.toString(), ConfConstant.AES_KEY);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return accessToken;
  }

  /**
   * 扩展token，增加客户端类型以及版本信息.2.2.
   * 
   * @param uid 用户ID
   * @param clientType 客户端类型
   * @param version 客户端版本
   * @return String
   */
  public static String generateTokenWithOutTimeStamp2(String uid, String clientType,
      String version) {
    String accessToken = "";
    StringBuilder tokenStr = new StringBuilder();
    tokenStr.append(uid).append(ConfConstant.DIVIDER).append(clientType)
        .append(ConfConstant.DIVIDER).append(version).append(ConfConstant.DIVIDER).append("woc");
    try {
      accessToken = AesUtil.encrypt4Old(tokenStr.toString(), "wafer");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return accessToken;
  }
}
