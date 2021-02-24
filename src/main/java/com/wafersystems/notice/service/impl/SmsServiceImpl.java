package com.wafersystems.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wafersystems.notice.config.AsyncTaskManager;
import com.wafersystems.notice.dao.impl.BaseDaoImpl;
import com.wafersystems.notice.model.PaginationDTO;
import com.wafersystems.notice.model.SmsTemplateDTO;
import com.wafersystems.notice.model.TemplateStateUpdateDTO;
import com.wafersystems.notice.service.SmsService;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.enums.ProductCodeEnum;
import com.wafersystems.virsical.common.core.dto.LogDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
public class SmsServiceImpl implements SmsService {

  @Autowired
  private BaseDaoImpl baseDao;

  @Autowired
  private AsyncTaskManager asyncTaskManager;

  @Override
  public void saveTemp(SmsTemplateDTO dto) {
    final SmsTemplateDTO temp = getTempById(dto.getId());
    if (ObjectUtil.isNull(temp)) {
      baseDao.save(dto);
      sendLog(dto.getId(), "模板:[" + dto.getName() + "]新增。");
    } else {
      temp.setDescription(dto.getDescription());
      temp.setCategory(dto.getCategory());
      temp.setContent(dto.getContent());
      temp.setName(dto.getName());
      temp.setModtime(null);
      baseDao.update(temp);
      sendLog(temp.getId(), "模板:[" + dto.getName() + "]更新。");
    }
    log.debug("新增/修改{}短信模板成功！", dto.getName());
  }

  @Override
  public void delTemp(String id) {
    baseDao.delete(SmsTemplateDTO.class, id);
    sendLog(id, "模板:[" + id + "]删除。");
  }



  @Override
  public PaginationDTO<SmsTemplateDTO> getTemp(String id, String category, String name, Integer page, Integer row) {
    DetachedCriteria criteria = DetachedCriteria.forClass(SmsTemplateDTO.class);
    if (null != id) {
      criteria.add(Restrictions.eq("id", id));
    }
    if (null != name) {
      criteria.add(Restrictions.ilike("name", name.trim(), MatchMode.ANYWHERE));
    }
    if (null != category) {
      criteria.add(Restrictions.eq("category", category.trim()));
    }
    criteria.addOrder(Order.desc("modtime"));
    return baseDao.selectPage(criteria, page, row);
  }

  @Override
  public SmsTemplateDTO getTempById(String id) {
    DetachedCriteria criteria = DetachedCriteria.forClass(SmsTemplateDTO.class);
    criteria.add(Restrictions.eq("id", id));
    List<SmsTemplateDTO> list = baseDao.findByCriteria(criteria);
    return CollUtil.getFirst(list);
  }

  @Override
  public boolean updateTempState(TemplateStateUpdateDTO dto) {
    if (dto.isUpdateAll()) {
      //全部修改
      baseDao.updateBySql("update SmsTemplateDTO as m set m.state = " + dto.getState() + " where 1 = 1");
      sendLog("all", "修改全部短信模板状态为：" + dto.getState());
      log.debug("修改全部短信模板状态：{}", dto.getState());
    } else {
      //通过id修改
      baseDao.updateBySql("update SmsTemplateDTO as m set m.state = " + dto.getState() + " where m.id = '" + dto.getId() + "'");
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
