package com.wafersystems.notice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.notice.entity.AlertConf;
import com.wafersystems.notice.service.IAlertConfService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 提醒配置表 前端控制器
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@AllArgsConstructor
@RestController
@RequestMapping("/alert-conf")
public class AlertConfController {

  private final IAlertConfService alertConfService;

  @PostMapping("/add")
  public R add(@RequestBody AlertConf alertConf) {
    return alertConfService.save(alertConf) ? R.ok() : R.fail();
  }

  @PostMapping("/update")
  public R update(@RequestBody AlertConf alertConf) {
    return alertConfService.updateById(alertConf) ? R.ok() : R.fail();
  }

  @PostMapping("/delete/{id}")
  public R delete(@PathVariable Integer id) {
    return alertConfService.removeById(id) ? R.ok() : R.fail();
  }

  @GetMapping("/{id}")
  public R<AlertConf> get(@PathVariable Integer id) {
    return R.ok(alertConfService.getById(id));
  }

  @GetMapping("/list")
  public R
    <List<AlertConf>> list(AlertConf alertConf) {
    return R.ok(alertConfService.list(Wrappers.query(alertConf)));
  }

  @GetMapping("/page")
  public R
    <IPage<AlertConf>> page(Page page, AlertConf alertConf) {
    return R.ok(alertConfService.page(page, Wrappers.query(alertConf)));
  }

}
