package com.wafersystems.notice.mapper;

import com.wafersystems.notice.entity.AlertConf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 提醒配置表 Mapper 接口
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
public interface AlertConfMapper extends BaseMapper<AlertConf> {

  /**
   * 通过租户Id查询告警配置
   *
   * @param tenantId 租户ID
   * @return 告警配置
   */
  List<AlertConf> getConfByTenant(@Param("tenantId") Integer tenantId);


  /**
   * 查询配置
   *
   * @param tenantId 租户ID
   * @param type     类型
   * @return alertType
   */
  AlertConf getConfByTenantAndType(@Param("tenantId") Integer tenantId, @Param("type") Integer type);
}
