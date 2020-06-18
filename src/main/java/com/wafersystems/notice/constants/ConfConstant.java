package com.wafersystems.notice.constants;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/6/27 11:13 Company:
 * wafersystems
 *
 * @author wafer
 */
public interface ConfConstant {

  /**
   * Mail模版规则.
   */
  public enum TypeEnum {
    /**
     * DEF,VM,FM
     */
    DEF,
    VM,
    FM
  }

  /**
   * 成功标志位.
   */
  Integer RESULT_SUCCESS = 0;

  /**
   * 失败标志位.
   */
  Integer RESULT_FAIL = 1;

  /**
   * 警告标志位.
   */
  Integer RESULT_WARN = 2;

  /**
   * http问地址.
   */
  String HTTP_URL = "http:";

  /**
   * https证书访问地址.
   */
  String HTTPS_URL = "https:";

  /**
   * 分割符分号.
   */
  String SEMICOLON = ";";

  /**
   * 分割符逗号.
   */
  String COMMA = ",";

  /**
   * Token分割符.
   */
  String DIVIDER = "=";


  /**
   * 加密生成令牌密钥.
   */
  String AES_KEY = "wafer123wafer123";

  /**
   * websocket握手时存放用户ID.
   */
  String WEBSOCKET_USERID = "WEBSOCKET_USER_ID";

  /**
   * 未读消息.
   */
  Integer MESSAGE_NORMAL = 1;

  /**
   * 已读消息.
   */
  Integer MESSAGE_RADE = 2;

  /**
   * 删除消息.
   */
  Integer MESSAGE_DELETE = 3;

  /**
   * 已处理消息(针对待办消息).
   */
  Integer MESSAGE_HANDLED = 4;

  /**
   * 分页默认页码.
   */
  String PAGE_DEFAULT_LENGTH = "1";
  /**
   * 数据默认长度.
   */
  String DATA_DEFAULT_LENGTH = "10";
  /**
   * 服务默认编码格式.
   */
  String DEFAULT_ENCODING = "utf-8";
}
