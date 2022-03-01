package com.wafersystems.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wafersystems.notice.config.AsyncTaskManager;
import com.wafersystems.notice.entity.SmsTemplate;
import com.wafersystems.notice.mapper.SmsTemplateMapper;
import com.wafersystems.notice.model.PaginationDTO;
import com.wafersystems.notice.model.TemplateStateUpdateDTO;
import com.wafersystems.notice.service.SmsService;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service
public class SmsServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements SmsService {
  @Autowired
  private AsyncTaskManager asyncTaskManager;

  @Override
  public void saveTemp(SmsTemplate dto) {
    final SmsTemplate temp = getTempById(dto.getId());
    if (ObjectUtil.isNull(temp)) {
      this.save(dto);
      sendLog(dto.getId(), "模板:[" + dto.getName() + "]新增。");
    } else {
      temp.setDescription(dto.getDescription());
      temp.setCategory(dto.getCategory());
      temp.setContent(dto.getContent());
      temp.setName(dto.getName());
      temp.setModtime(null);
      this.updateById(temp);
      sendLog(temp.getId(), "模板:[" + dto.getName() + "]更新。");
    }
    log.debug("新增/修改{}短信模板成功！", dto.getName());
  }

  @Override
  public void delTemp(String id) {
    baseMapper.deleteById(id);
    sendLog(id, "模板:[" + id + "]删除。");
  }


  @Override
  public PaginationDTO<SmsTemplate> getTemp(String id, String category, String name, Integer pageSize, Integer row) {
    final LambdaQueryWrapper<SmsTemplate> query = new LambdaQueryWrapper<>();
    if (null != id) {
      query.eq(SmsTemplate::getId, id);
    }
    if (null != name) {
      query.like(SmsTemplate::getName, "%" + name.trim() + "%");
    }
    if (null != category) {
      query.eq(SmsTemplate::getCategory, category.trim());
    }
    query.orderByDesc(SmsTemplate::getModtime);

    final Page<SmsTemplate> page = new Page<>();
    page.setCurrent(row);
    page.setSize(pageSize);
    final Page<SmsTemplate> result = this.page(page, query);
    final PaginationDTO<SmsTemplate> resultDto = new PaginationDTO<>();
    resultDto.setRows(result.getRecords());
    resultDto.setLimit((int)result.getSize());
    resultDto.setPage((int)result.getCurrent());
    resultDto.setRecords((int)result.getTotal());
    resultDto.setTotal((int)result.getPages());
    return resultDto;
  }

  @Override
  public SmsTemplate getTempById(String id) {
    final LambdaQueryWrapper<SmsTemplate> query = new LambdaQueryWrapper<>();
    query.eq(SmsTemplate::getId, id);
    final List<SmsTemplate> list = this.list(query);
    return CollUtil.getFirst(list);
  }

  @Override
  public boolean updateTempState(TemplateStateUpdateDTO dto) {
    final LambdaUpdateWrapper<SmsTemplate> update = new LambdaUpdateWrapper<>();
    update.set(SmsTemplate::getState, dto.getState());
    if (dto.isUpdateAll()) {
      //全部修改
      this.update(update);
      sendLog("all", "修改全部短信模板状态为：" + dto.getState());
      log.debug("修改全部短信模板状态：{}", dto.getState());
    } else {
      //通过id修改
      update.eq(SmsTemplate::getId, dto.getId());
      this.update(update);
      sendLog(dto.getId(), "修改短信模板状态为：" + dto.getState());
      log.debug("修改短信模板:{}状态：{}", dto.getId(), dto.getState());
    }
    return true;
  }

  /**
   * 记录日志
   *
   * @param name
   */
  private void sendLog(String name, String content) {
    LogDTO logDTO = new LogDTO();
    logDTO.setProductCode(ProductCodeEnum.COMMON.getCode());
    logDTO.setTitle("短信模板更新");
    logDTO.setContent(content);
    logDTO.setType("sms-template-update");
    logDTO.setResult(CommonConstants.SUCCESS);
    logDTO.setUserId(TenantContextHolder.getUserId());
    logDTO.setObjectId(name);
    logDTO.setTenantId(TenantContextHolder.getTenantId());
    logDTO.setUsername(TenantContextHolder.getUsername());
    asyncTaskManager.asyncSendLogMessage(logDTO);
  }
}
