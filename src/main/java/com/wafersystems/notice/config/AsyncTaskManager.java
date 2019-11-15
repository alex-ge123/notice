package com.wafersystems.notice.config;

import com.alibaba.fastjson.JSON;
import com.wafersystems.virsical.common.core.constant.UpmsMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgActionEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步任务
 *
 * @author tandk
 * @date 2019/8/15 18:40
 */
@Slf4j
@Service
@AllArgsConstructor
public class AsyncTaskManager {
  private RabbitTemplate rabbitTemplate;

  /**
   * 异步操作日志消息
   *
   * @param logDTO 日志对象
   */
  @Async("mqAsync")
  public void asyncSendLogMessage(LogDTO logDTO) {
    rabbitTemplate.convertAndSend(UpmsMqConstants.EXCHANGE_DIRECT_UPMS_LOG, UpmsMqConstants.ROUTINT_KEY_LOG,
      JSON.toJSONString(new MessageDTO(MsgTypeEnum.ONE.name(), MsgActionEnum.ADD.name(), logDTO)));
  }

}
