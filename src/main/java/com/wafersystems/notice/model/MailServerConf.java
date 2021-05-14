package com.wafersystems.notice.model;

import lombok.Data;

import java.util.Map;

/**
 * 邮件发送服务器配置
 *
 * @author tandk
 * @date 2021/1/12 17:59
 */
@Data
public class MailServerConf {

  /**
   * 服务类型
   * - smtp
   * - microsoft
   */
  private String serverType = "smtp";

  //**************************************smtp配置***************************
  /**
   * 邮件服务器
   */
  private String host;
  /**
   * 邮件服务器端口
   */
  private int port;
  /**
   * 邮箱用户名
   */
  private String username;

  /**
   * 邮箱授权码
   */
  private String password;
  /**
   * 开启认证
   */
  private String auth;
  /**
   * 邮件显示名称
   */
  private String name;

  /**
   * 邮箱账号
   */
  private String from;

  /**
   * 邮件配置额外参数
   */
  private Map<String, Object> props;

  //**************************************Microsoft配置***************************
  /**
   * clientId
   */
  private String clientId;

  /**
   * clientSecret
   */
  private String clientSecret;

  /**
   * officeTenantId
   */
  private String officeTenantId;

  /**
   * 邮箱账号
   */
  private String microsoftFrom;

  /**
   * scope
   */
  private String scope;
  ////**************************************ews配置***************************
  /**
   * ewsUrl
   */
  private String ewsUrl;

  /**
   * 邮箱账号
   */
  private String ewsAccount;

  /**
   * 密码
   */
  private String ewsPassword;
}
