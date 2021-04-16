package com.wafersystems.notice.service;

import com.wafersystems.notice.model.*;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.util.R;

/**
 * 邮件接口
 *
 * @author wafer
 */
public interface MailService {

  /**
   * 邮件发送
   *
   * @param mailBean       邮件填充内容
   * @param count          邮件重发次数
   * @param mailServerConf 邮件服务器配置参数
   * @throws Exception Exception
   */
  void send(MailBean mailBean, Integer count, MailServerConf mailServerConf) throws Exception;


  /**
   * 新增/修改邮件模板
   *
   * @param mailTemplateDto
   */
  void saveTemp(MailTemplateDTO mailTemplateDto);

  /**
   * 修改邮件模板
   *
   * @param mailTemplateDto
   */
  void updateTemp(MailTemplateDTO mailTemplateDto);

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
  PaginationDTO<MailTemplateSearchListDTO> getTemp(Long id, String category, String name, Integer page, Integer row);

  /**
   * 通过id查询邮件模板详细信息
   *
   * @param id
   * @return
   */
  MailTemplateDTO getTempById(Long id);

  /**
   * 查询name邮件模板详细信息
   *
   * @param name
   * @return
   */
  MailTemplateDTO getTempByName(String name);

  /**
   * 填充租户信息
   *
   * @param mailBean
   * @return
   */
  MailBean fillTenantInfo(MailBean mailBean);

  /**
   * 修改模板状态
   *
   * @param dto dto
   * @return boolean
   */
  boolean updateTempState(TemplateStateUpdateDTO dto);

  /**
   * 校验邮件配置
   * @param dto dto
   * @return boolean
   */
  R check(BaseCheckDTO dto);
}
