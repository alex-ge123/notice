package com.wafersystems.notice.mail.service;

import com.wafersystems.notice.base.model.PaginationDto;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.MailTemplateDto;
import com.wafersystems.notice.mail.model.MailTemplateSearchListDto;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.util.ConfConstant;

/**
 * 邮件接口
 *
 * @author wafer
 */
public interface MailNoticeService {

  /**
   * 邮件发送 author waferzy DateTime 2016-3-10 下午2:37:55.
   *
   * @param subject 邮件主题
   * @param to      邮件接收方(多个之间用逗号隔开)
   * @param copyTo  邮件抄送(多个之间用逗号隔开)
   * @param type    邮件模版类型(使用VM模版或html格式邮件)
   * @param temple  邮件模版
   * @param con     邮件填充内容
   * @param count   邮件重发次数
   * @throws Exception
   */
  void sendMail(String subject, String to, String copyTo, ConfConstant.TypeEnum type, String temple,
                TemContentVal con, Integer count) throws Exception;


  /**
   * 新增/修改邮件模板
   *
   * @param mailTemplateDto
   */
  void saveTemp(MailTemplateDto mailTemplateDto);

  /**
   * 修改邮件模板
   *
   * @param mailTemplateDto
   */
  void updateTemp(MailTemplateDto mailTemplateDto);

  /**
   * 查询邮件模板列表(不包含content)
   *
   * @param id
   * @param category
   * @param name
   * @param page
   * @param row
   * @return
   */
  PaginationDto<MailTemplateSearchListDto> getTemp(Long id, String category, String name, Integer page, Integer row);

  /**
   * 通过id查询邮件模板详细信息
   *
   * @param id
   * @return
   */
  MailTemplateDto getTempById(Long id);

  /**
   * 查询name邮件模板详细信息
   *
   * @param name
   * @return
   */
  MailTemplateDto getTempByName(String name);

  /**
   * 填充租户信息
   *
   * @param mailBean
   * @return
   */
  MailBean fillTenantInfo(MailBean mailBean);
}
