package com.wafersystems.notice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.notice.entity.AlertRecipient;
import com.wafersystems.notice.service.IAlertRecipientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 提醒接收人 前端控制器
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@AllArgsConstructor
@RestController
@RequestMapping("/alert-recipient")
public class AlertRecipientController {

  private final IAlertRecipientService alertRecipientService;

  @PostMapping("/add")
  public R add(@RequestBody AlertRecipient alertRecipient) {
    return alertRecipientService.save(alertRecipient) ? R.ok() : R.fail();
  }

  @PostMapping("/update")
  public R update(@RequestBody AlertRecipient alertRecipient) {
    return alertRecipientService.updateById(alertRecipient) ? R.ok() : R.fail();
  }

  @PostMapping("/delete/{id}")
  public R delete(@PathVariable Integer id) {
    return alertRecipientService.removeById(id) ? R.ok() : R.fail();
  }

  @GetMapping("/{id}")
  public R<AlertRecipient> get(@PathVariable Integer id) {
    return R.ok(alertRecipientService.getById(id));
  }

  @GetMapping("/list")
  public R
    <List<AlertRecipient>> list(AlertRecipient alertRecipient) {
    return R.ok(alertRecipientService.list(Wrappers.query(alertRecipient)));
  }

  @GetMapping("/page")
  public R
    <IPage<AlertRecipient>> page(Page page, AlertRecipient alertRecipient) {
    return R.ok(alertRecipientService.page(page, Wrappers.query(alertRecipient)));
  }

}
