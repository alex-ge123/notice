package com.wafersystems.notice.service;

import com.wafersystems.notice.model.MicrosoftRecordDTO;

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
  void saveTemp(MicrosoftRecordDTO dto);

  /**
   * 通过id查询
   *
   * @param uuid uuid
   * @return
   */
  MicrosoftRecordDTO getById(String uuid);

  /**
   * 通过id删除
   *
   * @param id id
   */
  void delById(Long id);
}
