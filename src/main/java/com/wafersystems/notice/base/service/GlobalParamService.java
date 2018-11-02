package com.wafersystems.notice.base.service;

import com.wafersystems.notice.base.model.GlobalParameter;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:27 Company:
 * wafersystems
 */
public interface GlobalParamService {

  /**
   * 初始化系统参数.
   */
  void initSystemParam();

  /**
   * 根据paramKey获取SystemParam
   * 
   * @param paramKey
   * @return
   */
  public GlobalParameter getSystemParamByParamKey(String paramKey);

  /**
   * 保存SystemParam
   * 
   * @param gp
   */
  void save(GlobalParameter gp);
}
