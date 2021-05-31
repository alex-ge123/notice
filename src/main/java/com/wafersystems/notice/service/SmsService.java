package com.wafersystems.notice.service;

import com.wafersystems.notice.entity.SmsTemplate;
import com.wafersystems.notice.model.PaginationDTO;
import com.wafersystems.notice.model.TemplateStateUpdateDTO;

/**
 * 短信接口
 *
 * @author wafer
 */
public interface SmsService {

  /**
   * 新增/修改短信模板
   *
   * @param dto dto
   */
  void saveTemp(SmsTemplate dto);

  /**
   * 删除短信模板
   *
   * @param id id
   */
  void delTemp(String id);

  /**
   * 分页查询短信模板
   *
   * @param id
   * @param category
   * @param name
   * @param page
   * @param row
   * @return
   */
  PaginationDTO<SmsTemplate> getTemp(String id, String category, String name, Integer page, Integer row);

  /**
   * 通过id查询邮件模板详细信息
   *
   * @param id
   * @return
   */
  SmsTemplate getTempById(String id);

  /**
   * 修改模板状态
   *
   * @param dto dto
   * @return boolean
   */
  boolean updateTempState(TemplateStateUpdateDTO dto);
}
