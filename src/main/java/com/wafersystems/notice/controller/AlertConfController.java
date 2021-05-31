package com.wafersystems.notice.controller;

import com.wafersystems.notice.entity.AlertConf;
import com.wafersystems.notice.service.IAlertConfService;
import com.wafersystems.virsical.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/list")
  public R add() {
    return R.ok(alertConfService.getConf());
  }

  @PostMapping("/update")
  public R update(@RequestBody List<AlertConf> list) {
    alertConfService.updateConf(list);
    return R.ok();
  }
}
