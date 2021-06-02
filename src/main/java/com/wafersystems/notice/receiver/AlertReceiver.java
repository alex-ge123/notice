package com.wafersystems.notice.receiver;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.RabbitMqConfig;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.NoticeMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.dto.AlertDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 提醒通知消费者
 *
 * @author shennan
 * @date 2021年6月1日
 */
@Slf4j
@Component
public class AlertReceiver {
  @Autowired
  private IAlertRecordService recordService;

  /**
   * 监听告警通知队列
   *
   * @param message 消息
   */
  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(value = RabbitMqConfig.QUEUE_NOTICE_ALERT, durable = "true"),
    exchange = @Exchange(value = NoticeMqConstants.EXCHANGE_FANOUT_ALERT, type = ExchangeTypes.FANOUT)
  ))
  public void mail(@Payload String message) {
    try {
      log.info("【{}监听到告警通知消息】{}", RabbitMqConfig.QUEUE_NOTICE_ALERT, message);
      MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);
      // 业务追踪id
      MDC.put(CommonConstants.LOG_BIZ_ID, messageDTO.getBizId());
      if (MsgTypeEnum.ONE.name().equals(messageDTO.getMsgType())) {
        AlertDTO alertDTO = JSON.parseObject(messageDTO.getData().toString(), AlertDTO.class);
        recordService.processAlertMessage(alertDTO);
      } else {
        log.info("消息类型未识别，无法处理告警通知消息");
      }
    } catch (Exception e) {
      log.info("消息监听处理异常", e);
    } finally {
      MDC.clear();
    }
  }
}