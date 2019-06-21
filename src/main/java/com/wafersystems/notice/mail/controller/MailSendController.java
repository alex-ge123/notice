package com.wafersystems.notice.mail.controller;

import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.EmailUtil;
import com.wafersystems.notice.util.ParamConstant;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.security.annotation.Inner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

  @Value("${mail.template.path}")
  String mailTemplatePath;

  @Autowired
  private EmailUtil mailUtil;

  @PostMapping("/aa")
  public R aa(@RequestParam String aa) {
    return R.ok(aa);
  }

  /**
   * 获取所有邮件模板
   *
   * @return R
   */
  @GetMapping("/template/list")
  public R templateList() {
    ArrayList<String> files;
    try {
      files = getFiles(mailTemplatePath);
    } catch (Exception e) {
      return R.fail(e.getMessage());
    }
    return R.ok(files);
  }

  /**
   * 上传邮件模板
   *
   * @param file 邮件模板
   * @return R
   */
  @PostMapping("/template/upload")
  public R templateUpload(@RequestParam MultipartFile file) {
    // 获取文件名
    String fileName = file.getOriginalFilename();
    log.info("上传的文件名为：" + fileName);
    File dir = new File(mailTemplatePath + "/" + fileName);
    // 检测是否存在目录
    if (!dir.getParentFile().exists()) {
      dir.getParentFile().mkdirs();
    }
    try {
      file.transferTo(dir);
    } catch (IOException e) {
      log.error("上传邮件模板失败", e);
      return R.fail(e.getMessage());
    }
    return R.ok();
  }

  /**
   * 模板预览
   *
   * @param title    标题
   * @param toMail   接收人
   * @param copyTo   抄送人
   * @param tempName 邮件模板名称
   * @param params   参数
   * @throws Exception Exception
   */
  @RequestMapping("/template/preview")
  public void templatePreview(String title, String toMail, String copyTo,
                              @RequestParam String tempName, String[] params,
                              String lang, HttpServletResponse response) throws Exception {
    Locale locale = ParamConstant.getLocaleByStr(lang != null ? lang : "zh_CN");
    // 创建邮件对象
    MailBean mailBean = new MailBean();
    mailBean.setSubject(title);
    mailBean.setToEmails(toMail);
    mailBean.setCopyTo(copyTo);
    mailBean.setType(ConfConstant.TypeEnum.VM);
    mailBean.setTemplate(tempName.contains(".vm") ? tempName : tempName + ".vm");
    TemContentVal con = getTemContentVal(params);
    con.setLocale(locale);
    con.setResource(resource);
    con.setImageDirectory(ParamConstant.getIMAGE_DIRECTORY());
    mailBean.setTemVal(con);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
      out.append(mailUtil.getMessage(mailBean));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

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
  @Inner
  @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
  public Object sendMail(@RequestParam String subject, @RequestParam String toMail, String copyTo,
                         @RequestParam String tempName,
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


  /**
   * 测试发送邮件
   *
   * @param title 标题
   * @return Object
   */
  @RequestMapping("/testSend")
  public Object testMailSend(@RequestParam String title, @RequestParam String toMail, String copyTo,
                             @RequestParam String tempName, @RequestParam String[] params,
                             @RequestParam String lang) throws Exception {
    log.info("发送测试邮件【{}】", title);
//    con.setValue1(DateUtil.formatDateTime("2018-11-02 20:30").getTime() + "");// 开始时间
//    con.setValue2(DateUtil.formatDateTime("2018-11-02 21:30").getTime() + "");// 结束时间
//    con.setValue3("-1");// 状态(-1.纯文本信息,0.邮件事件[带邀请按钮],1.邮件事件[无按钮])
//    con.setValue4("测试");// 邮件title称呼
//    con.setValue5("7");// 邮件类型(1-邀请|2-被创建|3-主持人邀请|4-删除参会人|5-创建人取消会议邮件|6-参会人取消邮件|7-提醒|8-修改|9-同意邀请|10-拒绝邀请)
//    con.setValue6("chenlei");// 创建人或被邀请者
//    con.setValue7("华山");// 会议室名称
//    con.setValue8("开发会议");// 会议主题
//    con.setValue9("李四");// 主持人
//    con.setValue10("张三;王五");// 参会人姓名
//    con.setValue11("无");// 会议备注
//    con.setValue12("每天6:00——7:00");// 会议周期
//    con.setValue13("");// (value5=9和value5=10时 回执邮件中 **接受或**拒绝了会议邀请中的 **）
//    con.setValue14("https://bkdev.virsical.cn:8499/smartmeeting/smart/third/jumpToWebEx?meetingId=32&type=1");//
//    // WebEx会议URL
//    con.setValue15("https://bkdev.virsical.cn:8499/smartmeeting/smart/third/jumpToReceipt?meetingId=32&userId" +
//      "=zhangyi&type=0");// 回执URL
    sendMail(title, toMail, copyTo, tempName, getTemContentVal(params), lang);
    return R.ok();
  }

  /**
   * 通过反射组装参数
   *
   * @param params 参数
   * @return TemContentVal
   * @throws ClassNotFoundException    ClassNotFoundException
   * @throws InstantiationException    InstantiationException
   * @throws IllegalAccessException    IllegalAccessException
   * @throws InvocationTargetException InvocationTargetException
   * @throws NoSuchMethodException     NoSuchMethodException
   */
  private TemContentVal getTemContentVal(String[] params) throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> clazz = Class.forName("com.wafersystems.notice.mail.model.TemContentVal");
    TemContentVal con = (TemContentVal) clazz.newInstance();
    for (int i = 1; i <= params.length; i++) {
      clazz.getDeclaredMethod("setValue" + i, String.class).invoke(con, params[i - 1]);
    }
    return con;
  }

  /**
   * 获取指定路径下所有文件地址
   *
   * @param path 路径
   * @return 文件地址
   */
  private ArrayList<String> getFiles(String path) {
    ArrayList<String> fileList = new ArrayList<>();
    File file = new File(path);
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File fileIndex : files) {
          if (!fileIndex.isDirectory()) {
            //如果文件是普通文件，则将文件地址放入集合中
            fileList.add(fileIndex.getPath());
          }
        }
      }
    }
    return fileList;
  }
}
