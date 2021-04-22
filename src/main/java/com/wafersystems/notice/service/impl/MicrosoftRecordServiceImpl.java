package com.wafersystems.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.wafersystems.notice.dao.impl.BaseDaoImpl;
import com.wafersystems.notice.model.MicrosoftRecordDTO;
import com.wafersystems.notice.service.MicrosoftRecordService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
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
public class MicrosoftRecordServiceImpl implements MicrosoftRecordService {

  @Autowired
  private BaseDaoImpl baseDao;

  @Override
  public void saveTemp(MicrosoftRecordDTO dto) {
    baseDao.save(dto);
  }

  @Override
  public MicrosoftRecordDTO getById(String uuid) {
    DetachedCriteria criteria = DetachedCriteria.forClass(MicrosoftRecordDTO.class);
    criteria.add(Restrictions.eq("uuid", uuid));
    List<MicrosoftRecordDTO> list = baseDao.findByCriteria(criteria);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public void delById(Long id) {
    baseDao.delete(MicrosoftRecordDTO.class, id);
  }
}
