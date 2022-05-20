package com.wafersystems.notice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wafersystems.notice.entity.AlertRecord;
import com.wafersystems.virsical.common.core.dto.AlertDTO;
import com.wafersystems.virsical.common.core.dto.InMailDTO;

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

  /**
   * 处理告警消息
   *
   * @param alertDTO dto
   */
  void processAlertMessage(AlertDTO alertDTO);

  /**
   * 处理站内信消息
   *
   * @param inMailDTO dto
   */
  void processInMailMessage(InMailDTO inMailDTO);
}
