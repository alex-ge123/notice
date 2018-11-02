package com.wafersystems.notice.base.controller;

import com.wafersystems.notice.util.Accesstoken;
import com.wafersystems.notice.util.AccesstokenUtil;
import com.wafersystems.notice.util.ConfConstant;

import java.util.HashMap;
import java.util.Map;


/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/5/13 10:20 Company:
 * wafersystems
 */
public class BaseController {

  /**
   * Description:
   *
   * @param obj 返回的数据,异常时表示异常信息
   * @param status 是否出现异常
   * @return 返回给前端
   */
  protected Map<String, Object> returnBackMap(Object obj, int status) {
    Map<String, Object> map = new HashMap<String, Object>();
    if (ConfConstant.RESULT_SUCCESS == status) {
      map.put("status", status);
      map.put("data", obj);
    } else {
      map.put("status", status);
      map.put("msg", obj);
    }
    return map;
  }

  /**
   * 根据token获取用户Id.
   *
   * @param token -
   * @return -
   */
  public String getUserIdFromToken(String token) {
    Accesstoken tokenInfo = AccesstokenUtil.parseToken(token, Accesstoken.TokenType.FULL);
    if (null == tokenInfo) {
      return null;
    } else {
      return tokenInfo.getUid();
    }
  }
}
