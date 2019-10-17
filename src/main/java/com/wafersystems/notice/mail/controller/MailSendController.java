package com.wafersystems.notice.mail.controller;

import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.base.model.TestSendMailDTO;
import com.wafersystems.notice.config.FreemarkerMacroMessage;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.MailTemplateDto;
import com.wafersystems.notice.mail.model.MailTemplateSearchListDto;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.*;
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
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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

  @Autowired
  private FreemarkerMacroMessage fMessage;

  @Value("${mail.template.path}")
  String mailTemplatePath;

  @Autowired
  private EmailUtil mailUtil;

  /**
   * 获取所有邮件模板
   *
   * @param id
   * @param category
   * @param name
   * @param pageSize   分页大小
   * @param startIndex 起始页
   * @return
   */
  @GetMapping("/template/list")
  public R templateList(Long id, String category, String name, @RequestParam(
    defaultValue = ConfConstant.DATA_DEFAULT_LENGTH) Integer pageSize, @RequestParam(
    defaultValue = ConfConstant.PAGE_DEFAULT_LENGTH) Integer startIndex) {
    PaginationDto<MailTemplateSearchListDto> list = mailNoticeService.getTemp(id, category, name, pageSize, startIndex);
    return R.ok(list);
  }

  /**
   * 上传邮件模板
   *
   * @param file 邮件模板
   * @return R
   */
  @PostMapping("/template/upload")
  public R templateUpload(@RequestParam MultipartFile file, @RequestParam String category,
                          @RequestParam String description) {
    try {
      String fileName = StrUtil.getFileNameNoEx(file.getOriginalFilename());
      log.info("上传的文件名为：" + fileName);
      byte[] bytes = file.getBytes();
      String content = new String(bytes);
      MailTemplateDto mailTemplateDto = new MailTemplateDto();
      mailTemplateDto.setName(fileName);
      mailTemplateDto.setContent(content);
      mailTemplateDto.setCategory(category);
      mailTemplateDto.setDescription(description);
      mailNoticeService.saveTemp(mailTemplateDto);
    } catch (Exception e) {
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
    mailBean.setSubject("这是一个邮件标题");
    mailBean.setToEmails("收件人");
    mailBean.setCopyTo("抄送人");
    mailBean.setType(ConfConstant.TypeEnum.FM);
    mailBean.setTemplate(tempName);
    params = getTestParams(tempName, params);
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
  public R sendMail(@RequestParam String subject, @RequestParam String toMail, String copyTo,
                    @RequestParam String tempName,
                    @RequestBody TemContentVal con, String lang) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    if (!ParamConstant.isEMAIL_SWITCH()) {
      log.warn("邮件服务参数未配置，将忽略主题【" + subject + "】的邮件发送");
      return R.fail(resource.getMessage("msg.msg.emailServerNull", null, locale));
    }
    if (StrUtil.isEmptyStr(subject)) {
      return R.fail(resource.getMessage("msg.email.subjectNull", null, locale));
    } else if (StrUtil.isEmptyStr(toMail)) {
      return R.fail(resource.getMessage("msg.email.toNull", null, locale));
    } else if (StrUtil.isEmptyStr(tempName)) {
      return R.fail(resource.getMessage("msg.email.temNull", null, locale));
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
    try {
      taskExecutor.execute(new MailTask(mailNoticeService, StrUtil.regStr(subject), toMail, copyTo,
        tempName, con, lang));
      return R.ok();
    } catch (Exception ex) {
      log.error("发送邮件失败：", ex);
      return R.fail(resource.getMessage("msg.email.sendError", null, locale));
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
//        mailNoticeService.sendMail(subject, toMail, copyTo, ConfConstant.TypeEnum.VM,
//          tempName.contains(".vm") ? tempName : tempName + ".vm", con, 0);

        mailNoticeService.sendMail(subject, toMail, copyTo, ConfConstant.TypeEnum.FM,
          tempName, con, 0);
      } catch (Exception ex) {
        throw new RuntimeException();
      }
    }
  }


  /**
   * 测试发送邮件
   *
   * @param testSendMailDTO 测试发送邮件对象
   * @return Object
   */
  @RequestMapping("/testSend")
  public R testMailSend(@RequestBody TestSendMailDTO testSendMailDTO) throws Exception {
    log.info("发送测试邮件【{}】", testSendMailDTO.getTitle());
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
    sendMail(testSendMailDTO.getTitle(), testSendMailDTO.getToMail(), null, testSendMailDTO.getTempName(),
      getTemContentVal(getTestParams(testSendMailDTO.getTempName(), null)), testSendMailDTO.getLang());
    return R.ok();
  }

  /**
   * 获取邮件模板测试参数
   *
   * @param tempName 模板名称
   * @param params   参数
   * @return 参数
   */
  private String[] getTestParams(String tempName, String[] params) {
    // 如果没传模板参数，自动创建参数
    if (params == null) {
      List<String> list = new ArrayList<>();
      if ("meeting".equals(tempName)
        || "virsical".equals(tempName)) {
        list.add(DateUtil.formatDateTime("2018-11-02 20:30").getTime() + "");
        list.add(DateUtil.formatDateTime("2018-11-02 21:30").getTime() + "");
        list.add("-1");
        for (int i = 4; i < 30; i++) {
          list.add("参数" + i);
        }
      } else {
        for (int i = 1; i < 35; i++) {
          list.add("参数" + i);
        }
      }
      params = new String[list.size()];
      list.toArray(params);
    }
    return params;
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
            String fileIndexPath = fileIndex.getPath();
            int index = fileIndexPath.lastIndexOf(File.separator);
            if (index != -1) {
              fileIndexPath = fileIndexPath.substring(index + 1);
            }
            fileList.add(fileIndexPath);
          }
        }
      }
    }
    return fileList;
  }
}
