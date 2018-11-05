package com.wafersystems.notice.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.base.dao.BaseDao;
import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.message.controller.WebSocketEndPoint;
import com.wafersystems.notice.message.model.MessageDto;
import com.wafersystems.notice.message.model.MessageToUserDto;
import com.wafersystems.notice.message.model.MessageType;
import com.wafersystems.notice.message.service.MessagesService;
import com.wafersystems.notice.util.*;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/25 13:45 Company: wafersystems
 */
@Slf4j
@Service
public class MessagesServiceImpl implements MessagesService {

  @Autowired
  private BaseDao baseDao;
  @Autowired
  private WebSocketEndPoint webSocketEndPoint;

  /**
   * 消息发送.
   * 
   * @param message 消息体
   * @param recipientId 消息接收者ID
   * @param domain 域名
   * @param clientId 同一用户的android和ios cid使用分号隔开，多个用户之间使用逗号隔开
   */
  @Override
  public void sendMsg(MessageDto message, String recipientId, String domain, String clientId) {
    if (StringUtils.isNotBlank(recipientId) || StringUtils.isNotBlank(clientId)) {
      message.setTimeStamp(DateUtil.getCurrentTimeInMillis());
      try {
        message.setContent(AesUtil.encryptBase64(message.getContent()));
        message.setContentType(MessageType.ContentType.TEXT_CYBE.toString());
      } catch (Exception ex) {
        message.setContentType(MessageType.ContentType.TEXT_PLAIN.toString());
      }
      baseDao.save(message);
      this.saveMsgToId(recipientId, clientId, message, domain);
    } else {
      log.warn("未传入消息接收对象，消息【" + message.toString() + "】将被忽略");
    }
  }

  /**
   * 消息同步推送.
   * 
   * @param userId -
   * @param domain -
   */
  @Override
  public void mergeMsg(String userId, String domain) {
    webSocketEndPoint.sendMessageToUser(userId, domain, null);
  }

  /**
   * 更新消息状态.
   * 
   * @param msg -
   * @param userId -
   * @param domain -
   */
  @Override
  public Object updateMsg(MessageToUserDto msg, String userId, String domain) {
    if (0 == msg.getAction()) { // 置为已读
      log.debug("消息ID=" + msg.getId() + "执行已读操作！");
      updateState(msg.getId(), ConfConstant.MESSAGE_RADE);
      webSocketEndPoint.sendMessageToUser(userId, domain, null);
    } else if (1 == msg.getAction()) { // 执行链接
      log.debug("消息ID=" + msg.getId() + "执行连接：" + msg.getUrl());
      return this.sendMsgToMeeting(msg, userId, domain);
    }
    return null;
  }

  /**
   * 消息中处理业务数据(如回执中的接受|拒绝).
   * 
   * @param msg -
   * @param userId -
   * @param domain -
   * @return -
   */
  private Object sendMsgToMeeting(MessageToUserDto msg, String userId, String domain) {
    Object response = null;
    String result = null;
    msg.setUrl(StrUtil.regStr(msg.getUrl()));
    String[] url = msg.getUrl().split("\\?");
    String[] params = url[1].split("\\&");
    if (msg.getUrl().toLowerCase().startsWith(ConfConstant.HTTP_URL)) {
      NameValuePair[] nameValuePair = new NameValuePair[params.length];
      for (int i = 0; i < params.length; i++) {
        String[] temp = params[i].split("\\=");
        nameValuePair[i] = new NameValuePair(temp[0], temp[1]);
      }
      result = HttpClientUtil.getPostResponseWithHttpClient(url[0], "utf-8", nameValuePair);
    } else if (url[0].toLowerCase().startsWith(ConfConstant.HTTPS_URL)) {
      Map<String, String> createMap = new HashMap<>();
      for (String param : params) {
        createMap.put(param.split("\\=")[0], param.split("\\=")[1]);
      }
      result = HttpsPostClientUtil.doPost(url[0], createMap, "UTF-8");
    }
    log.debug("会议系统处理的结果为：" + result);
    if (!StrUtil.isEmptyStr(result)) {
      JSONObject node = JSON.parseObject(result);
      if (ConfConstant.RESULT_SUCCESS.equals(node.get("status"))) {
        updateState(msg.getId(), ConfConstant.MESSAGE_HANDLED);
        webSocketEndPoint.sendMessageToUser(userId, domain, null);
      } else if (ConfConstant.RESULT_WARN.equals(node.get("status"))) {
        updateState(msg.getId(), ConfConstant.MESSAGE_HANDLED);
        response = node;
        webSocketEndPoint.sendMessageToUser(userId, domain, null);
      } else {
        updateState(msg.getId(), ConfConstant.MESSAGE_RADE);
        response = node;
        webSocketEndPoint.sendMessageToUser(userId, domain, null);
      }
    }
    return response;
  }

  /**
   * 保存用户与消息的关联.
   * 
   * @param recipientId -
   * @param cids -
   * @param message -
   * @param domain -
   */
  private void saveMsgToId(String recipientId, String cids, MessageDto message, String domain) {
    if (StringUtils.isNotBlank(recipientId)) {
      for (String userId : recipientId.split(ConfConstant.SEMICOLON)) {
        userId = ParamConstant.sepDomainUser(userId)[0];
        this.saveMessageToUser(userId, message, domain);
      }
    }
    if (StringUtils.isNotBlank(cids)) {
      for (String ids : cids.split(ConfConstant.COMMA)) {
        for (String cid : ids.split(ConfConstant.SEMICOLON)) {
          this.saveMessageToUser(cid, message, domain);
        }
      }
    }
  }

  /**
   * 保存用户消息.
   * 
   * @param userId -
   * @param message -
   * @param domain -
   */
  private void saveMessageToUser(String userId, MessageDto message, String domain) {
    MessageToUserDto messageToUserDto = new MessageToUserDto();
    messageToUserDto.setUserId(userId);
    messageToUserDto.setMessage(message);
    messageToUserDto.setDomain(domain);
    messageToUserDto.setState(ConfConstant.MESSAGE_NORMAL);
    baseDao.save(messageToUserDto);
  }



  /**
   * 获取用户的消息数.
   * 
   * @param userId -
   * @param domain -
   * @param state -
   * @param type -
   * @return -
   */
  @Override
  public int getCount(String userId, String domain, Integer state, Integer[] type) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MessageToUserDto.class);
    criteria.add(Restrictions.eq("userId", userId));
    criteria.add(Restrictions.eq("domain", domain));
    if (!StrUtil.isNullObject(state)) {
      criteria.add(Restrictions.eq("state", state));
    }
    criteria.add(Restrictions.ne("state", ConfConstant.MESSAGE_DELETE));
    criteria.createAlias("message", "m");
    if (!StrUtil.isNullObject(type) && type.length > 0) {
      criteria.add(Restrictions.in("m.type", type));
    } else {
      Object[] stateType = { MessageType.handel, MessageType.text };// 排除审核
      criteria.add(Restrictions.in("m.type", stateType));
    }
    return Integer.parseInt(baseDao.getCount(criteria).toString());
  }

  /**
   * 修改消息状态.
   * 
   * @param id -
   * @param state -
   */
  @Override
  public void updateState(Long id, Integer state) {
    baseDao.updateBySql("UPDATE MessageToUserDto SET state='" + state + "' WHERE id='" + id + "'");
  }

  /**
   * 分页获取用户消息.
   *
   * @param userId 用户名
   * @param domain 域名
   * @param state 消息状态
   * @param type 消息类型
   * @param page 当前页数
   * @param row 每页显示行数
   * @return -
   */
  @Override
  public PaginationDto<MessageToUserDto> getUserMessages(String userId, String domain, Integer[] state, Integer[] type,
      Integer page, Integer row, long timeStamp) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MessageToUserDto.class);
    criteria.add(Restrictions.eq("userId", userId));
    criteria.add(Restrictions.eq("domain", domain));
    if (!StrUtil.isNullObject(state) && state.length != 0) {
      criteria.add(Restrictions.in("state", state));
    }
    criteria.add(Restrictions.ne("state", ConfConstant.MESSAGE_DELETE));
    criteria.createAlias("message", "m");
    if (StrUtil.isNullObject(type) || type.length == 0) {
      criteria.add(Restrictions.in("m.type", new Object[] { 1, 2 }));
    } else {
      criteria.add(Restrictions.in("m.type", type));
    }
    // if (!StrUtil.isNullObject(type)) {
    // criteria.add(Restrictions.eq("m.type", type));
    // }
    if (timeStamp > 0) {
      criteria.add(Restrictions.lt("timeStamp", timeStamp));
    }
    criteria.addOrder(Order.desc("m.timeStamp"));
    return baseDao.selectPage(criteria, row, page);
  }
}
