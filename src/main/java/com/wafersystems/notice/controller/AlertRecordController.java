package com.wafersystems.notice.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wafersystems.notice.constants.AlertConstants;
import com.wafersystems.notice.entity.AlertRecord;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 提醒记录表 前端控制器
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@AllArgsConstructor
@RestController
@RequestMapping("/alert-record")
public class AlertRecordController {

  private final IAlertRecordService alertRecordService;

  @PostMapping("/read")
  public R add(@RequestBody List<Integer> readList) {
    final AlertRecord alertRecord = new AlertRecord();
    alertRecord.setStatus(AlertConstants.ALERT_RECORD_STATUS_READ);
    final LambdaUpdateWrapper<AlertRecord> update = new LambdaUpdateWrapper<>();
    if (CollUtil.isNotEmpty(readList)) {
      update.in(AlertRecord::getId, readList);
    }
    update.eq(AlertRecord::getAlertType, AlertConstants.LOCAL.getType());
    update.eq(AlertRecord::getRecipient, String.valueOf(TenantContextHolder.getUserId()));
    update.set(AlertRecord::getStatus, AlertConstants.ALERT_RECORD_STATUS_READ);
    return alertRecordService.update(update) ? R.ok() : R.fail();
  }

  /**
   * 分页查询当前租户下所有消息
   *
   * @param page        page
   * @param alertRecord alertRecord
   * @return page
   */
  @GetMapping("/page")
  public R<IPage<AlertRecord>> page(Page page, AlertRecord alertRecord) {
    alertRecord.setTenantId(TenantContextHolder.getTenantId());
    final QueryWrapper<AlertRecord> query = Wrappers.query(alertRecord);
    query.orderByDesc("create_time");
    return R.ok(alertRecordService.alertPage(page, query));
  }

  /**
   * 分页查询当前用户下所有站内消息
   *
   * @param page        page
   * @param alertRecord alertRecord
   * @return page
   */
  @GetMapping("/current/page")
  public R<IPage<AlertRecord>> currentPage(Page page, AlertRecord alertRecord) {
    final QueryWrapper<AlertRecord> query = getQuery(alertRecord);
    query.ne("delivery_status", AlertConstants.ALERT_RECORD_DELIVERY_STATUS_ERROR);
    return R.ok(alertRecordService.alertPage(page, query));
  }

  /**
   * 分页查询当前用户下所有站内未读消息
   *
   * @param page        page
   * @param alertRecord alertRecord
   * @return page
   */
  @GetMapping("/current/unread/page")
  public R<List<AlertRecord>> currentUnread(Page page, AlertRecord alertRecord) {
    alertRecord.setStatus(AlertConstants.ALERT_RECORD_STATUS_UNREAD);
    final QueryWrapper<AlertRecord> query = getQuery(alertRecord);
    return R.ok(alertRecordService.alertPage(page, query));
  }

  private QueryWrapper<AlertRecord> getQuery(AlertRecord alertRecord) {
    alertRecord.setTenantId(TenantContextHolder.getTenantId());
    alertRecord.setAlertType(AlertConstants.LOCAL.getType());
    alertRecord.setRecipient(String.valueOf(TenantContextHolder.getUserId()));
    final QueryWrapper<AlertRecord> query = Wrappers.query(alertRecord);
    query.orderByDesc("create_time");
    return query;
  }
}
