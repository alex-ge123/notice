package com.wafersystems.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wafersystems.notice.entity.MailMicrosoftRecord;
import com.wafersystems.notice.mapper.MailMicrosoftRecordMapper;
import com.wafersystems.notice.service.MicrosoftRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service
public class MicrosoftRecordServiceImpl extends ServiceImpl<MailMicrosoftRecordMapper, MailMicrosoftRecord> implements MicrosoftRecordService {

  @Override
  public void saveTemp(MailMicrosoftRecord dto) {
    this.saveOrUpdate(dto);
  }

  @Override
  public MailMicrosoftRecord getById(String uuid) {
    final LambdaQueryWrapper<MailMicrosoftRecord> query = new LambdaQueryWrapper<>();
    query.eq(MailMicrosoftRecord::getUuid, uuid);
    List<MailMicrosoftRecord> list = this.list(query);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public void delById(Integer id) {
    this.removeById(id);
  }
}
