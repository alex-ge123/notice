package com.wafersystems.notice.message.controller;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.base.dao.BaseDao;
import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.message.model.MessageDto;
import com.wafersystems.notice.message.model.MessageToUserDto;
import com.wafersystems.notice.message.service.MessagesService;
import com.wafersystems.notice.message.service.impl.GeTuiServiceImpl;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import java.util.Locale;
import java.util.Map;

/**
 * Created with Intellij IDEA. Description: 消息处理类 Author: waferzy DateTime:
 * 2016/7/18 14:37 Company: wafersystems
 */
@Log4j
@RestController
@RequestMapping("/msg")
public class MessagesController extends BaseController {

  @Autowired
  private MessagesService messagesService;
  @Autowired
  private WebSocketEndPoint webSocketEndPoint;
  @Autowired
  private GeTuiServiceImpl geTuiService;
  @Autowired
  private TaskExecutor taskExecutor;
  @Autowired
  private ApplicationContext resource;

  @Autowired
  private BaseDao baseDao;

  @RequestMapping("/testSave")
  @ResponseBody
  public Object testSave(@RequestParam String lang){
    Locale locale = ParamConstant.getLocaleByStr(lang);
    MessageDto messageDto = new MessageDto();
    messageDto.setUrls("http***"+System.currentTimeMillis());
    baseDao.save(messageDto);
//    int a=2/0;
    return returnBackMap(resource.getMessage("msg.email.subjectNull", null, locale),
            ConfConstant.RESULT_FAIL);
  }

  /**
   * 消息发送.
   * 
   * @param data 消息体
   * @param recipientId 接收者(多个接收者之间用分号隔开)
   * @param clientId 设备唯一标识(多个标识之间用分号隔开)
   * @param lang -
   * @return -
   */
  @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
  public Object sendMsg(String data, String recipientId, @RequestHeader String token, String clientId, String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    try {
      if (!StrUtil.isEmptyStr(data)) {
        if (!StrUtil.isEmptyStr(recipientId)) {
          MessageDto message = JSON.parseObject(data, MessageDto.class);
          if (!StrUtil.isNullObject(message) && !StrUtil.isEmptyStr(message.getContent())) {
            message.setContent(StrUtil.regStr(message.getContent()));
            message.setTitle(StrUtil.regStr(message.getTitle()));
            String domain = ParamConstant.sepDomainUser(getUserIdFromToken(token))[1];
            messagesService.sendMsg(message, recipientId, domain, clientId);
            taskExecutor.execute(new SendMsgTask(recipientId, domain, message, clientId));
            return returnBackMap(null, ConfConstant.RESULT_SUCCESS);
          } else {
            return returnBackMap(resource.getMessage("msg.msg.contentNull", null, locale),
                ConfConstant.RESULT_FAIL);
          }
        } else {
          return returnBackMap(resource.getMessage("msg.msg.recipientIdNull", null, locale),
              ConfConstant.RESULT_FAIL);
        }
      } else {
        return returnBackMap(resource.getMessage("msg.msg.contentNull", null, locale),
            ConfConstant.RESULT_FAIL);
      }
    } catch (Exception ex) {
      log.error("消息发送失败,消息体[" + data + "],接收者[" + recipientId + "]", ex);
      return returnBackMap(resource.getMessage("msg.msg.sendError", null, locale),
          ConfConstant.RESULT_FAIL);
    }
  }

  /**
   * 获取用户消息列表.
   * 
   * @param token -
   * @param state -
   * @param type -
   * @param page -
   * @param row -
   * @param lang -
   * @return -
   */
  @RequestMapping(value = "/getUserMessages", method = RequestMethod.GET)
  public Object getUserMessages(@RequestHeader String token, String state, Integer type, @RequestParam(
      defaultValue = ConfConstant.PAGE_DEFAULT_LENGTH) Integer page, @RequestParam(
      defaultValue = ConfConstant.DATA_DEFAULT_LENGTH) Integer row, @RequestParam(
      defaultValue = "0") long timeStamp, String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    try {
      String[] userIdDomain = ParamConstant.sepDomainUser(getUserIdFromToken(token));
      Integer[] stateInt;
      if (StrUtil.isEmptyStr(state)) {
        stateInt = null;
      } else {
        String[] stateArr = state.split(";");
        stateInt = new Integer[stateArr.length];
        for (int i = 0; i < stateArr.length; i++) {
          stateInt[i] = Integer.parseInt(stateArr[i]);
        }
      }

      PaginationDto<MessageToUserDto> pagination =
          messagesService.getUserMessages(userIdDomain[0], userIdDomain[1], stateInt,
              StrUtil.isNullObject(type) ? new Integer[] {} : new Integer[] { type }, page, row,
              timeStamp);
      if (!StrUtil.isNullObject(pagination) && StrUtil.isEmptyList(pagination.getRows())
          && page > Integer.parseInt(ConfConstant.PAGE_DEFAULT_LENGTH)
          && pagination.getRecords() > Integer.parseInt(ConfConstant.PAGE_DEFAULT_LENGTH)) {
        pagination =
            messagesService.getUserMessages(userIdDomain[0], userIdDomain[1], stateInt,
                StrUtil.isNullObject(type) ? new Integer[] {} : new Integer[] { type }, page - 1,
                row, timeStamp);
      }
      return returnBackMap(pagination, ConfConstant.RESULT_SUCCESS);
    } catch (Exception exception) {
      log.error("获取用户[" + getUserIdFromToken(token) + "]消息列表异常！", exception);
      return returnBackMap(resource.getMessage("msg.action.fail", null, locale),
          ConfConstant.RESULT_FAIL);
    }
  }

  /**
   * 获取用户消息数量.
   * 
   * @param token -
   * @param state -
   * @param type -
   * @param lang -
   * @return -
   */
  @RequestMapping(value = "/getMessageCount", method = RequestMethod.GET)
  public Object getMessageCount(@RequestHeader String token, Integer state, Integer type, String lang) {
    try {
      String[] userIdDomain = ParamConstant.sepDomainUser(getUserIdFromToken(token));
      int count =
          messagesService.getCount(userIdDomain[0], userIdDomain[1], state,
              StrUtil.isNullObject(type) ? new Integer[] {} : new Integer[] { type });
      return returnBackMap(count, ConfConstant.RESULT_SUCCESS);
    } catch (Exception ex) {
      log.error("获取用户消息数量异常！", ex);
      return returnBackMap(
          resource.getMessage("msg.action.fail", null, ParamConstant.getLocaleByStr(lang)),
          ConfConstant.RESULT_FAIL);
    }
  }

  /**
   * 修改消息状态.
   * 
   * @param token -
   * @param msg -
   * @param lang -
   * @return -
   */
  @RequestMapping(method = RequestMethod.PUT)
  public Object updateMsg(@RequestHeader String token, @RequestBody Map<String, Object> msg, String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    try {
      String[] userIdDomain = ParamConstant.sepDomainUser(getUserIdFromToken(token));
      Object result =
          messagesService.updateMsg(
              JSON.parseObject(String.valueOf(msg.get("msg")), MessageToUserDto.class),
              userIdDomain[0], userIdDomain[1]);
      if (!StrUtil.isNullObject(result)) {
        return result;
      } else {
        return returnBackMap(null, ConfConstant.RESULT_SUCCESS);
      }
    } catch (Exception ex) {
      log.error("修改消息状态失败！", ex);
      return returnBackMap(resource.getMessage("msg.action.fail", null, locale),
          ConfConstant.RESULT_FAIL);
    }
  }

  /**
   * 删除接口.
   * 
   * @param token -
   * @param ids -
   * @param lang -
   * @return -
   */
  @RequestMapping(method = RequestMethod.DELETE)
  public Object deleteMsg(@RequestHeader String token, @RequestBody Map<String, Object> ids, String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    try {
      if (!StrUtil.isNullObject(ids)) {
        String[] userIdDomain = ParamConstant.sepDomainUser(getUserIdFromToken(token));
        String temp = String.valueOf(ids.get("ids"));
        if (temp.contains(ConfConstant.COMMA)) {
          for (String id : temp.split(ConfConstant.COMMA)) {
            messagesService.updateState(Long.parseLong(id), ConfConstant.MESSAGE_DELETE);
          }
        } else {
          messagesService.updateState(Long.parseLong(temp), ConfConstant.MESSAGE_DELETE);
        }
        messagesService.mergeMsg(userIdDomain[0], userIdDomain[1]);
        return returnBackMap(null, ConfConstant.RESULT_SUCCESS);
      } else {
        return returnBackMap(resource.getMessage("msg.msg.delError", null, locale),
            ConfConstant.RESULT_FAIL);
      }
    } catch (Exception ex) {
      log.error("删除消息失败！", ex);
      return returnBackMap(resource.getMessage("msg.action.fail", null, locale),
          ConfConstant.RESULT_FAIL);
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private class SendMsgTask implements Runnable {
    private String recipientId;
    private String domain;
    private MessageDto message;
    private String clientId;

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      // websocket消息发送
      if (StringUtils.isNotBlank(recipientId)) {
        for (String userId : recipientId.split(ConfConstant.SEMICOLON)) {
          log.debug("推送userId为【" + userId + "】.域【" + domain + "】");
          userId = ParamConstant.sepDomainUser(userId)[0];
          webSocketEndPoint.sendMessageToUser(userId, domain,
              new TextMessage(JSON.toJSONString(message, true)));
        }
      }
      // 个推消息发送
      if (message.getType() != 3 && ParamConstant.isGETUI_SWITCH() && StringUtils.isNotBlank(clientId)) { // 审核消息不发送个推消息
        for (String cids : clientId.split(ConfConstant.COMMA)) {
          for (String cid : cids.split(ConfConstant.SEMICOLON)) {
            log.debug("推送clientId为：" + clientId);
            geTuiService.pushMsg(message, recipientId, domain, cid);
          }
        }
      }
    }
  }
}
