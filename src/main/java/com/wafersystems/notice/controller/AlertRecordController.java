package com.wafersystems.notice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.notice.entity.AlertRecord;
import com.wafersystems.notice.service.IAlertRecordService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;

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

  @PostMapping("/add")
  public R add(@RequestBody AlertRecord alertRecord) {
    return alertRecordService.save(alertRecord) ? R.ok() : R.fail();
  }

  @PostMapping("/update")
  public R update(@RequestBody AlertRecord alertRecord) {
    return alertRecordService.updateById(alertRecord) ? R.ok() : R.fail();
  }

  @PostMapping("/delete/{id}")
  public R delete(@PathVariable Integer id) {
    return alertRecordService.removeById(id) ? R.ok() : R.fail();
  }

  @GetMapping("/{id}")
  public R<AlertRecord> get(@PathVariable Integer id) {
    return R.ok(alertRecordService.getById(id));
  }

  @GetMapping("/list")
  public R
    <List<AlertRecord>> list(AlertRecord alertRecord) {
    return R.ok(alertRecordService.list(Wrappers.query(alertRecord)));
  }

  @GetMapping("/page")
  public R
    <IPage<AlertRecord>> page(Page page, AlertRecord alertRecord) {
    return R.ok(alertRecordService.page(page, Wrappers.query(alertRecord)));
  }

}
