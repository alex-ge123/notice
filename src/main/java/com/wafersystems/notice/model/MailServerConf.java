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
  private String serverType;
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
   * 邮箱账号
   */
  private String from;
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
   * 邮件配置额外参数
   */
  private Map<String, Object> props;

  //**************************************Microsoft配置***************************


  ////**************************************ews配置***************************
}
