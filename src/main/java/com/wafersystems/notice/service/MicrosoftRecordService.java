package com.wafersystems.notice.service;

import com.wafersystems.notice.entity.MailMicrosoftRecord;

/**
 * MicrosoftRecord记录接口
 *
 * @author wafer
 */
public interface MicrosoftRecordService {
  /**
   * 新增/修改
   *
   * @param dto dto
   */
  void saveTemp(MailMicrosoftRecord dto);

  /**
   * 通过id查询
   *
   * @param uuid uuid
   * @return
   */
  MailMicrosoftRecord getById(String uuid);

  /**
   * 通过id删除
   *
   * @param id id
   */
  void delById(Integer id);
}
