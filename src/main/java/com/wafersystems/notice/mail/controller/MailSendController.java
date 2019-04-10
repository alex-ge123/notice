package com.wafersystems.notice.mail.controller;

import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * 邮件控制器
 *
 * @author wafer
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
   * @param subject  邮件主题
   * @param toMail   接收人
   * @param copyTo   抄送人
   * @param tempName 模版名称
   * @param con      内容
   * @param lang     语言
   * @return -
   */
  @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
  public Object sendMail(@RequestParam String subject, @RequestParam String toMail, String copyTo, @RequestParam String tempName,
                         @RequestBody TemContentVal con, String lang) {
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
    @Override
    public void run() {
      try {
        mailNoticeService.sendMail(subject, toMail, copyTo, ConfConstant.TypeEnum.VM,
          tempName.contains(".vm") ? tempName : tempName + ".vm", con, 0);
      } catch (Exception ex) {
        throw new RuntimeException();
      }
    }
  }
}
