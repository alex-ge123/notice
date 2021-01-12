package com.wafersystems.notice.service;

import com.wafersystems.notice.model.GlobalParameter;
import com.wafersystems.notice.model.ParameterDTO;

import java.util.List;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:27 Company:
 * wafersystems
 *
 * @author wafer
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
  GlobalParameter getSystemParamByParamKey(String paramKey);

  /**
   * 查询租户配置参数
   *
   * @return list
   */
  List<GlobalParameter> getSystemParamList();

  /**
   * 保存SystemParam
   *
   * @param gp
   */
  void save(GlobalParameter gp);

  /**
   * 批量保存SystemParam
   *
   * @param list 参数集合
   */
  void saveBatch(List<ParameterDTO> list);
}
