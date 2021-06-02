package com.wafersystems.notice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wafersystems.notice.entity.AlertRecord;

/**
 * <p>
 * 提醒记录表 服务类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
public interface IAlertRecordService extends IService<AlertRecord> {

  /**
   * 分页查询告警信息
   *
   * @param page  page
   * @param query query
   * @return IPage
   */
  IPage<AlertRecord> alertPage(Page<AlertRecord> page, QueryWrapper<AlertRecord> query);
}
