package com.wafersystems.notice.message.service.impl;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.em.EPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.wafersystems.notice.message.model.MessageDto;
import com.wafersystems.notice.message.model.MessageType;
import com.wafersystems.notice.message.service.MessagesService;
import com.wafersystems.notice.util.AesUtil;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.StrUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/9/27 15:36 Company:
 * wafersystems
 */
@Log4j
@Component
public class GeTuiServiceImpl {

  /**
   * 个推推送器.
   */
  private IGtPush push;
  @Autowired
  private MessagesService messagesService;

  /**
   * 初始化个推服务.
   */
  public void init() {
    if (!StrUtil.isNullObject(push)) {
      log.debug("个推服务已经初始化ok 无需重复初始化！");
    }
    push = new IGtPush(ParamConstant.getGETUI_URL(), ParamConstant.getGETUI_APPKEY(),
        ParamConstant.getGETUI_MASTRE_SECRET());
    // 配置返回每个用户返回用户状态，可选
    System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
  }

  /**
   * 使用个推推送消息.
   * 
   * @param message -
   */
  public void pushMsg(MessageDto message, String recipientId, String domain, String clientId) {
    IPushResult ipushResult;
    SingleMessage singleMessage = null;
    Target target = null;
    try {
      singleMessage = createListMessage(message,
          messagesService.getCount(recipientId, domain, ConfConstant.MESSAGE_NORMAL, null));
      target = createTarget(clientId);
      ipushResult = push.pushMessageToSingle(singleMessage, target);
      log.debug("ClientId为" + clientId + "调用第三方(个推)返回状态 ResultCode: " + ipushResult.getResultCode()
          + ",response:" + ipushResult.getResponse());
    } catch (RequestException ex) {
      log.error("个推推送失败！", ex);
      int count = 0;
      while (count < ParamConstant.getDEFAULT_REPEAT_COUNT() && !StrUtil.isNullObject(singleMessage)
          && !StrUtil.isNullObject(target)) {
        ++count;
        log.warn("ClientId为" + target.getClientId() + "的移动设备第" + count + "次重新推送！");
        ipushResult = push.pushMessageToSingle(singleMessage, target, ex.getRequestId());
        if (!StrUtil.isNullObject(ipushResult)
            && EPushResult.RESULT_OK.equals(ipushResult.getResultCode())) {
          log.debug("ClientId为" + clientId + "调用第三方(个推)返回状态 ResultCode: "
              + ipushResult.getResultCode() + ",response:" + ipushResult.getResponse());
          break;
        }
      }
    }
  }

  private SingleMessage createListMessage(MessageDto message, int unRead) {
    SingleMessage singleMsg = new SingleMessage();
    // 消息离线是否存储
    singleMsg.setOffline(true);
    // 消息离线存储多久
    singleMsg
        .setOfflineExpireTime(Long.parseLong(ParamConstant.getGETUI_OFFLINE_TIME()) * 3600 * 1000);
    // 不限制推送方式(0)，wifi推送(1)
    singleMsg.setPushNetWorkType(0);
    // singleMsg.setData(notificationTemplateDemo(message, unRead));
    singleMsg.setData(notificationTemplateTouChuan(message, unRead));
    return singleMsg;
  }

  private Target createTarget(String clientId) {
    Target target = new Target();
    target.setAppId(ParamConstant.getGETUI_APPID());
    target.setClientId(clientId);
    return target;
  }

  /**
   * 点击通知打开应用模板
   * 
   * @param message -
   * @param unRead -
   * @return -
   */
  /*private static NotificationTemplate notificationTemplateDemo(MessageDto message, int unRead) {
    NotificationTemplate template = new NotificationTemplate();
    template.setAppId(ParamConstant.getGETUI_APPID());
    template.setAppkey(ParamConstant.getGETUI_APPKEY());
    template.setTitle(message.getTitle());
    String content = MessageType.ContentType.TEXT_PLAIN.toString().equals(message.getContentType())
        ? message.getContent() : AesUtil.decryptBase64(message.getContent());
    template.setTransmissionContent(content);
    template.setText(MessageType.ContentType.TEXT_PLAIN.toString().equals(message.getContentType())
        ? message.getContent() : AesUtil.decryptBase64(message.getContent()));
    template.setLogoUrl(message.getLogo());
    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
    template.setTransmissionType(2);
    // iOS推送使用该字段
    template.setAPNInfo(createIosTemplate(content, content, unRead));
    return template;
  }*/

  /**
   * 透传消息.
   * 
   * @param message -
   * @param unRead -
   * @return -
   */
  private static TransmissionTemplate notificationTemplateTouChuan(MessageDto message, int unRead) {
    TransmissionTemplate template = new TransmissionTemplate();
    template.setAppId(ParamConstant.getGETUI_APPID());
    template.setAppkey(ParamConstant.getGETUI_APPKEY());
    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
    template.setTransmissionType(2);
    String content = MessageType.ContentType.TEXT_PLAIN.toString().equals(message.getContentType())
        ? message.getContent() : AesUtil.decryptBase64(message.getContent());
    template.setTransmissionContent(content);
    // iOS推送使用该字段
    template.setAPNInfo(createIosTemplate(content, content, unRead));
    return template;
  }

  /**
   * IOS模版推送
   * 
   * @param alertMsg 消息体
   * @param unRead 未读数
   * @return -
   */
  private static APNPayload createIosTemplate(String alertMsg, String msg, int unRead) {
    log.debug(msg);
    APNPayload payload = new APNPayload();
    payload.setBadge(unRead); // 将应用icon上显示的数字设为1
    payload.setContentAvailable(1);
    payload.setSound("default");
    payload.setCategory("$由客户端定义");
    // 简单模式APNPayload.SimpleMsg
    payload.setAlertMsg(new APNPayload.SimpleAlertMsg(alertMsg));
    return payload;
  }
}
