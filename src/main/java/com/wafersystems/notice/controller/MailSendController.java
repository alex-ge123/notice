package com.wafersystems.notice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.model.PaginationDto;
import com.wafersystems.notice.model.TestSendMailDTO;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailTemplateDto;
import com.wafersystems.notice.model.MailTemplateSearchListDto;
import com.wafersystems.notice.service.MailNoticeService;
import com.wafersystems.notice.util.*;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.security.annotation.Inner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
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
public class MailSendController {

  @Autowired
  private MailNoticeService mailNoticeService;
  @Autowired
  private TaskExecutor taskExecutor;
  @Autowired
  private ApplicationContext resource;
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
  @PreAuthorize("@pms.hasPermission('')")
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
  @PreAuthorize("@pms.hasPermission('')")
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
      mailTemplateDto.setCategory(new String(category.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
      mailTemplateDto.setDescription(new String(description.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
      mailNoticeService.saveTemp(mailTemplateDto);
    } catch (Exception e) {
      log.error("上传邮件模板失败", e);
      return R.fail(e.getMessage());
    }
    return R.ok();
  }

  /**
   * 更新邮件模板
   *
   * @param file
   * @param id
   * @param category
   * @param description
   * @return
   */
  @PostMapping("/template/update")
  @PreAuthorize("@pms.hasPermission('')")
  public R templateUpdate(MultipartFile file, @RequestParam Integer id, String category, String description) {
    try {
      MailTemplateDto mailTemplateDto = new MailTemplateDto();
      mailTemplateDto.setId(id.longValue());
      if (ObjectUtil.isNotNull(file)) {
        String fileName = StrUtil.getFileNameNoEx(file.getOriginalFilename());
        log.info("上传的文件名为：" + fileName);
        byte[] bytes = file.getBytes();
        String content = new String(bytes);
        mailTemplateDto.setContent(content);
        mailTemplateDto.setName(fileName);
      }
      if (!cn.hutool.core.util.StrUtil.isEmpty(category)) {
        mailTemplateDto.setCategory(new String(category.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
      }
      if (!cn.hutool.core.util.StrUtil.isEmpty(description)) {
        mailTemplateDto.setDescription(new String(description.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
      }
      mailNoticeService.updateTemp(mailTemplateDto);
    } catch (Exception e) {
      log.error("更新邮件模板失败", e);
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
  @GetMapping("/template/preview")
  @PreAuthorize("@pms.hasPermission('')")
  public void templatePreview(String title, String toMail, String copyTo,
                              @RequestParam String tempName, String[] params,
                              String lang, HttpServletResponse response) throws Exception {
    Locale locale = ParamConstant.getLocaleByStr(lang != null ? lang : "zh_CN");
    // 创建邮件对象
    params = getTestParams(tempName, params);
    MailDTO mailDto = getTemContentVal(params);
    mailDto.setLocale(locale);
    mailDto.setResource(resource);
    mailDto.setLogo(ParamConstant.getLOGO_DEFALUT());
    mailDto.setPhone(ParamConstant.getPHONE());
    mailDto.setSystemName(ParamConstant.getSYSTEM_NAME());
    mailDto.setImageDirectory(ParamConstant.getIMAGE_DIRECTORY());
    mailDto.setImgPathBanner(ParamConstant.getIMAGE_DIRECTORY() + "/top_banner.jpg");
    mailDto.setImgPathDimcode(ParamConstant.getIMAGE_DIRECTORY() + "/virsical_dimcode.jpg");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
      out.append(mailUtil.getMessage(MailBean.builder()
        .subject("这是一个邮件标题")
        .toEmails("收件人")
        .copyTo("抄送人")
        .type(ConfConstant.TypeEnum.FM)
        .template(tempName)
        .mailDTO(mailDto).build()));
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
   * @param mailDto      内容
   * @param lang     语言
   * @return -
   */
  @Inner
  @PostMapping(value = "/sendMail")
  public R sendMail(@RequestParam String subject, @RequestParam String toMail, String copyTo,
                    @RequestParam String tempName,
                    @RequestBody MailDTO mailDto, String lang) {
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
    if (StrUtil.isNullObject(mailDto)) {
      mailDto = new MailDTO();
    }
    mailDto.setLocale(cn.hutool.core.util.StrUtil.isBlank(lang) ? null : locale);
    mailDto.setResource(resource);
    mailDto.setImageDirectory(ParamConstant.getIMAGE_DIRECTORY());
    mailDto.setImgPathBanner(ParamConstant.getIMAGE_DIRECTORY() + "/top_banner.jpg");
    mailDto.setImgPathDimcode(ParamConstant.getIMAGE_DIRECTORY() + "/virsical_dimcode.jpg");
    try {
      taskExecutor.execute(new MailTask(mailNoticeService, StrUtil.regStr(subject), toMail, copyTo,
        tempName, mailDto, lang));
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
    private MailDTO con;
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
        mailNoticeService.sendMail(MailBean.builder()
          .subject(subject)
          .toEmails(toMail)
          .copyTo(copyTo)
          .type(ConfConstant.TypeEnum.FM)
          .template(tempName)
          .mailDTO(con)
          .build(), 0);
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
  @PostMapping("/testSend")
  @PreAuthorize("@pms.hasPermission('')")
  public R testMailSend(@RequestBody TestSendMailDTO testSendMailDTO) throws Exception {
    log.info("发送测试邮件【{}】", testSendMailDTO.getTitle());
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
    String meetingStr = "meeting";
    String virsicalStr = "virsical";
    String time1 = "2018-11-02 20:30";
    String time2 = "2018-11-02 21:30";
    int i1 = 4;
    int i2 = 30;
    int i3 = 35;
    // 如果没传模板参数，自动创建参数
    if (params == null) {
      List<String> list = new ArrayList<>();
      if (meetingStr.equals(tempName)
        || virsicalStr.equals(tempName)) {
        list.add(DateUtil.formatDateTime(time1).getTime() + "");
        list.add(DateUtil.formatDateTime(time2).getTime() + "");
        list.add("-1");
        for (int i = i1; i < i2; i++) {
          list.add("参数" + i);
        }
      } else {
        for (int i = 1; i < i3; i++) {
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
  private MailDTO getTemContentVal(String[] params) throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> clazz = Class.forName("com.wafersystems.virsical.common.core.dto.MailDTO");
    MailDTO con = (MailDTO) clazz.newInstance();
    for (int i = 1; i <= params.length; i++) {
      clazz.getDeclaredMethod("setValue" + i, String.class).invoke(con, params[i - 1]);
    }
    return con;
  }
}
