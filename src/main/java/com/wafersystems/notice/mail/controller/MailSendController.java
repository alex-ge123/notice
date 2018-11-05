package com.wafersystems.notice.mail.controller;

import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/5/13 9:56 Company:
 * wafersystems
 */
@Slf4j
@RestController
@RequestMapping("/mail")
public class MailSendController extends BaseController {

  @Autowired
  private MailNoticeService mailNoticeService;
  @Autowired
  private TaskExecutor taskExecutor;
  @Autowired
  private ApplicationContext resource;

  /**
   * 邮件发送.
   *
   * @param subject 邮件主题
   * @param toMail 接收人
   * @param copyTo 抄送人
   * @param tempName 模版名称
   * @param con 内容
   * @param lang 语言
   * @return -
   */
  @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
  public Object sendMail(String subject, String toMail, String copyTo, String tempName,
      TemContentVal con, String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    if (!ParamConstant.isEMAIL_SWITCH()) {
      log.warn("邮件服务参数未配置，将忽略主题【" + subject + "】的邮件发送");
      return returnBackMap(resource.getMessage("msg.msg.emailServerNull", null, locale),
          ConfConstant.RESULT_FAIL);
    }
    try {
      if (StrUtil.isEmptyStr(subject)) {
        return returnBackMap(resource.getMessage("msg.email.subjectNull", null, locale),
            ConfConstant.RESULT_FAIL);
      } else if (StrUtil.isEmptyStr(toMail)) {
        return returnBackMap(resource.getMessage("msg.email.toNull", null, locale),
            ConfConstant.RESULT_FAIL);
      } else if (StrUtil.isEmptyStr(tempName)) {
        return returnBackMap(resource.getMessage("msg.email.temNull", null, locale),
            ConfConstant.RESULT_FAIL);
      }
      if (StrUtil.isNullObject(con)) {
        con = new TemContentVal();
      }
      con.setLogo(
          StrUtil.isEmptyStr(con.getLogo()) ? ParamConstant.getLOGO_DEFALUT() : con.getLogo());
      log.debug("logo地址为：" + con.getLogo());
      con.setLocale(locale);
      con.setResource(resource);
      con.setImageDirectory(ParamConstant.getIMAGE_DIRECTORY());
      taskExecutor.execute(new MailTask(mailNoticeService, StrUtil.regStr(subject), toMail, copyTo,
          tempName, con, lang));
      return returnBackMap(null, ConfConstant.RESULT_SUCCESS);
    } catch (Exception ex) {
      log.error("发送邮件失败：", ex);
      return returnBackMap(resource.getMessage("msg.email.sendError", null, locale),
          ConfConstant.RESULT_FAIL);
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class MailTask implements Runnable {
    private MailNoticeService mailNoticeService;
    private String subject;
    private String toMail;
    private String copyTo;
    private String tempName;
    private TemContentVal con;
    private String lang;

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread,
     * starting the thread causes the object's <code>run</code> method to be called in that
     * separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      try {
        mailNoticeService.sendMail(subject, toMail, copyTo, ConfConstant.TypeEnum.VM,
            tempName.contains(".vm") ? tempName : tempName + ".vm", con, 0);
      } catch (Exception ex) {
        throw new RuntimeException();
      }
    }
  }

    /**
     * 测试发送邮件
     * @param title 标题
     * @return
     */
  @RequestMapping("/testSend")
  public Object testMailSend(@RequestParam String title) {
      log.info("发送测试邮件【{}】", title);
    List<String> params = new ArrayList<>();
    params.add(DateUtil.formatDateTime("2018-11-02 20:30").getTime() + "");// 开始时间
    params.add(DateUtil.formatDateTime("2018-11-02 21:30").getTime() + "");// 结束时间
    params.add("-1");// 状态(-1.纯文本信息,0.邮件事件[带邀请按钮],1.邮件事件[无按钮])
    params.add("测试");// 邮件title称呼
    params.add("7");// 邮件类型(1-邀请|2-被创建|3-主持人邀请|4-删除参会人|5-创建人取消会议邮件|6-参会人取消邮件|7-提醒|8-修改|9-同意邀请|10-拒绝邀请)
    params.add("chenlei");// 创建人或被邀请者
    params.add("华山");// 会议室名称
    params.add("开发会议");// 会议主题
    params.add("李四");// 主持人
    params.add("张三;王五");// 参会人姓名
    params.add("无");// 会议备注
    params.add("每天6:00——7:00");// 会议周期
    params.add("");// (value5=9和value5=10时 回执邮件中 **接受或**拒绝了会议邀请中的 **）
    params.add("https://bkdev.virsical.cn:8499/smartmeeting/smart/third/jumpToWebEx?meetingId=32&type=1");// WebEx会议URL
    params.add("https://bkdev.virsical.cn:8499/smartmeeting/smart/third/jumpToReceipt?meetingId=32&userId=zhangyi&type=0");// 回执URL

    NameValuePair[] nameValuePair = new NameValuePair[6 + params.size()];
    nameValuePair[0] = new NameValuePair("tempName", "meeting");
    nameValuePair[1] = new NameValuePair("subject", StringUtil.replaceStr(title));
    nameValuePair[2] = new NameValuePair("toMail", "tandongkui@wafersystems.com");
    nameValuePair[3] = new NameValuePair("copyTo", "286414791@qq.com");
    nameValuePair[4] = new NameValuePair("logo", "2");
    nameValuePair[5] = new NameValuePair("lang", "zh_CN");
    int count = 6;
    for (String temp : params) {
        nameValuePair[count] =
                new NameValuePair("value" + (count - 5), StringUtil.replaceStr(temp));
        count++;
    }
    String url = "http://localhost:20110/mail/sendMail";
    String result = HttpClientUtil.getPostResponseWithHttpClient(url, "utf-8", nameValuePair);//, null);
    return result;

  }
}
