package com.wafersystems.notice.weather.service.impl;

import com.wafersystems.notice.base.dao.BaseDao;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.notice.weather.model.Weather;
import com.wafersystems.notice.weather.service.WeatherInfoService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherInfoServiceImpl implements WeatherInfoService {

  @Autowired
  private BaseDao<Weather> baseDao;

  /**
   * Description 根据城市获取周期内该城市天气数据.
   * @param city 城市名称
   * @param type 信息类别
   */
  @Override
  public Weather getWeatherInfo(String city, String type) {
    Weather bean = null;
    DetachedCriteria criteria = DetachedCriteria.forClass(Weather.class);
    criteria.add(Restrictions.eq("city", city));
    criteria.add(Restrictions.eq("type", type));
    criteria.addOrder(Order.desc("createTime"));
    List<Weather> list = baseDao.findByCriteria(criteria);
    if (!StrUtil.isEmptyList(list)) {
      bean = list.get(0);
    }
    return bean;
  }

  /**
   *保存. 
   */
  @Override
  public void save(Weather bean) {
    baseDao.save(bean);
  }

}
