package com.wafersystems.notice.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * ClassName Accesstoken Description 访问令牌实体类.
 *
 * @author fengxiang Date 2015年7月1日 下午3:28:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Accesstoken implements Serializable {

  /**
   * ClassName TokenType Description 令牌类别.
   *
   * @author fengxiang Date 2015年7月1日 下午3:30:51
   */
  public enum TokenType {
    /**
     * 基于用户ID的令牌.
     */
    UID,

    /**
     * 基于用户ID和密码的令牌.
     */
    UIDP8D,

    /**
     * 基于完整参数的令牌.
     */
    FULL
  }

  /**
   * 用户ID.
   */
  private String uid;

  /**
   * 密码.
   */
  private String password;

  /**
   * 客户端类型.
   */
  private String clientType;

  /**
   * 客户端版本.
   */
  private String version;

  /**
   * 令牌类别.
   */
  private TokenType tokenType;

  /**
   * Description 工厂方法：使用基本属性创建对象.
   *
   * @param uid 用户ID
   * @return 用户登录令牌的数据传输对象 author fengxiang Date 2015年7月1日 上午10:59:13
   */
  public static Accesstoken createInstance(String uid) {
    Accesstoken tokenInfo = new Accesstoken();
    tokenInfo.tokenType = TokenType.UID;
    tokenInfo.uid = uid;
    return tokenInfo;
  }

  /**
   * Description 工厂方法：使用基本属性创建对象.
   *
   * @param uid      用户ID
   * @param password 密码
   * @return 用户登录令牌的数据传输对象 author fengxiang Date 2015年7月1日 上午10:59:13
   */
  public static Accesstoken createInstance(String uid, String password) {
    Accesstoken tokenInfo = new Accesstoken();
    tokenInfo.tokenType = TokenType.UIDP8D;
    tokenInfo.uid = uid;
    tokenInfo.password = password;
    return tokenInfo;
  }

  /**
   * Description 工厂方法：使用基本属性创建对象.
   *
   * @param uid        用户ID
   * @param clientType 客户端类型
   * @param version    客户端版本
   * @return 用户登录令牌的数据传输对象 author fengxiang Date 2015年7月1日 上午10:59:13
   */
  public static Accesstoken createInstance(String uid, String clientType, String version) {
    Accesstoken tokenInfo = new Accesstoken();
    tokenInfo.tokenType = TokenType.FULL;
    tokenInfo.uid = uid;
    tokenInfo.clientType = clientType;
    tokenInfo.version = version;
    return tokenInfo;
  }

  /**
   * Description 根据待加密字符串创建.
   *
   * @param tokenStr  - author fengxiang
   * @param tokenType - Date 2015年7月1日 上午11:21:58
   */
  public static Accesstoken createFromTokenStr(String tokenStr, TokenType tokenType) {
    Accesstoken tokenInfo = null;
    String[] str = tokenStr.split(ConfConstant.DIVIDER);
    switch (tokenType) {
      case UID:
        if (str.length >= 1) {
          String uid = str[0];
          tokenInfo = createInstance(uid);
        }
        break;
      case UIDP8D:
        if (str.length >= 2) {
          String uid = str[0];
          String password = str[1];
          tokenInfo = createInstance(uid, password);
        }
        break;
      case FULL:
        if (str.length >= 3) {
          String uid = str[0];
          String clientType = str[1];
          String version = str[2];
          tokenInfo = createInstance(uid, clientType, version);
          return tokenInfo;
        }
        break;
      default:
        ;
    }
    return tokenInfo;
  }

  /**
   * Description 生成带用户名的待加密字符串.
   *
   * @return 带用户名和密码的待加密字符串 author fengxiang Date 2015年7月1日 上午11:45:05
   */
  public String toTokenStr() {
    StringBuilder tokenStr = new StringBuilder();
    String randomValue = RandomNumberUtil.getCharAndNumr(6);
    switch (tokenType) {
      case UID:
        tokenStr.append(uid).append(ConfConstant.DIVIDER).append(randomValue);
        break;
      case UIDP8D:
        tokenStr.append(uid).append(ConfConstant.DIVIDER).append(password)
          .append(ConfConstant.DIVIDER).append(randomValue);
        break;
      case FULL:
        tokenStr.append(uid).append(ConfConstant.DIVIDER).append(clientType)
          .append(ConfConstant.DIVIDER).append(version).append(ConfConstant.DIVIDER)
          .append(randomValue).append(ConfConstant.DIVIDER);
        break;
      default:
        ;
    }
    return ObjectUtil.isNull(tokenStr) ? null : tokenStr.toString();
  }
}
