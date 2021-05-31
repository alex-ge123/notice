package com.wafersystems.notice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wafersystems.notice.entity.AlertConf;

import java.util.List;

/**
 * <p>
 * 提醒配置表 服务类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
public interface IAlertConfService extends IService<AlertConf> {

  /**
   * 初始化租户告警通知配置
   */
  void initTenantConf();

  /**
   * 初始化租户告警通知配置
   *
   * @param tenantId 租户ID
   */
  void initTenantConf(Integer tenantId);

  /**
   * 获取当前租户告警通知配置
   *
   * @return list
   */
  List<AlertConf> getConf();

  /**
   * 修改当前租户告警通知配置
   */
  void updateConf(List<AlertConf> list);

}
