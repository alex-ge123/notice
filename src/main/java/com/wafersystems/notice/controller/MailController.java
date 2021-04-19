package com.wafersystems.notice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.manager.email.AbstractEmailManager;
import com.wafersystems.notice.model.*;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.notice.service.MailService;
import com.wafersystems.notice.util.DateUtil;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.core.util.R;
import com.wafersystems.virsical.common.security.annotation.Inner;
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
public class MailController {

  @Autowired
  private MailService mailService;
  @Autowired
  private GlobalParamService globalParamService;
  @Autowired
  private TaskExecutor taskExecutor;
  @Autowired
  private ApplicationContext resource;
  @Autowired
  private AbstractEmailManager mailUtil;

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
    PaginationDTO<MailTemplateSearchListDTO> list = mailService.getTemp(id, category, name, pageSize, startIndex);
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
      MailTemplateDTO mailTemplateDto = new MailTemplateDTO();
      mailTemplateDto.setName(fileName);
      mailTemplateDto.setContent(content);
      mailTemplateDto.setCategory(new String(category.getBytes(), StandardCharsets.UTF_8));
      mailTemplateDto.setDescription(new String(description.getBytes(), StandardCharsets.UTF_8));
      mailService.saveTemp(mailTemplateDto);
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
      MailTemplateDTO mailTemplateDto = new MailTemplateDTO();
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
        mailTemplateDto.setCategory(new String(category.getBytes(), StandardCharsets.UTF_8));
      }
      if (!cn.hutool.core.util.StrUtil.isEmpty(description)) {
        mailTemplateDto.setDescription(new String(description.getBytes(), StandardCharsets.UTF_8));
      }
      mailService.updateTemp(mailTemplateDto);
    } catch (Exception e) {
      log.error("更新邮件模板失败", e);
      return R.fail(e.getMessage());
    }
    return R.ok();
  }

  @PostMapping("/template/update/state")
  @PreAuthorize("@pms.hasPermission('')")
  public R templateUpdateState(@RequestBody TemplateStateUpdateDTO dto) {
    return mailService.updateTempState(dto) ? R.ok() : R.fail();
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
    mailDto.setLogo(ParamConstant.getLogoDefault());
    mailDto.setPhone(ParamConstant.getPhone());
    mailDto.setSystemName(ParamConstant.getSystemName());
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
   * @param mailDto  内容
   * @param lang     语言
   * @param tenantId 租户id
   * @return -
   */
  @Inner
  @PostMapping(value = "/sendMail")
  public R sendMail(@RequestParam String subject, @RequestParam String toMail, String copyTo,
                    @RequestParam String tempName,
                    @RequestBody MailDTO mailDto, String lang, Integer tenantId) {
    Locale locale = ParamConstant.getLocaleByStr(lang);
    if (!ParamConstant.isEmailSwitch()) {
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
    try {
      MailDTO finalMailDto = mailDto;
      taskExecutor.execute(() -> {
        try {
          final MailBean mailBean = MailBean.builder()
            .subject(subject)
            .toEmails(toMail)
            .copyTo(copyTo)
            .type(ConfConstant.TypeEnum.FM)
            .template(tempName)
            .mailDTO(finalMailDto)
            .build();
          mailService.send(mailBean, 0, globalParamService.getMailServerConf(tenantId));
        } catch (Exception ex) {
          throw new RuntimeException();
        }
      });
      return R.ok();
    } catch (Exception ex) {
      log.error("发送邮件失败：", ex);
      return R.fail(resource.getMessage("msg.email.sendError", null, locale));
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
      getTemContentVal(getTestParams(testSendMailDTO.getTempName(), null)), testSendMailDTO.getLang(),
      TenantContextHolder.getTenantId());
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

  /**
   * 测试发送邮箱配置
   *
   * @param dto dto
   * @return R
   */
  @PostMapping("/check")
  @PreAuthorize("@pms.hasPermission('')")
  public R check(@RequestBody(required = false) BaseCheckDTO dto) throws Exception {
    return mailService.check(dto);
  }
}
