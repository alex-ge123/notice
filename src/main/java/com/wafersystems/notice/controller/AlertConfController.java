package com.wafersystems.notice.controller;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.entity.AlertConf;
import com.wafersystems.notice.service.IAlertConfService;
import com.wafersystems.virsical.common.core.constant.NoticeMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.InMailTemplateEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgActionEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.InMailDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
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
  @PreAuthorize("@pms.hasPermission('admin@common@alert_setting,admin@platform@notice_parameter')")
  public R add() {
    return R.ok(alertConfService.getConf());
  }

  @PostMapping("/update")
  @PreAuthorize("@pms.hasPermission('admin@common@alert_setting,admin@platform@notice_parameter')")
  public R update(@RequestBody List<AlertConf> list) {
    alertConfService.updateConf(list);
    return R.ok();
  }

  private final AmqpTemplate rabbitTemplate;
  @RequestMapping("test")
  public Object test() {
    final InMailDTO inMailDTO = new InMailDTO();
    inMailDTO.setRecipients(Collections.singletonList(TenantContextHolder.getUserId()));
    inMailDTO.setTenantId(TenantContextHolder.getTenantId());
    inMailDTO.setAlertId("test");
    inMailDTO.setProduct(ProductCodeEnum.MEETING);
    // 前端国际化
    inMailDTO.setTemplate(InMailTemplateEnum.MEETING_NOTICE);
    inMailDTO.setParamList(Arrays.asList("2022-04-19 16:00 ~ 17:00(中国时间)", "华清池", "开发规范培训",
      "https://cloud.virsical.cn/vsk/fb/f?p=1-14176-1765-0"));
    rabbitTemplate.convertAndSend(NoticeMqConstants.EXCHANGE_FANOUT_INMAIL, "",
      JSON.toJSONString(new MessageDTO(MsgTypeEnum.ONE.name(), MsgActionEnum.ADD.name(), inMailDTO)));
    return R.ok();
  }
}
