package com.wafersystems.notice.weather.service;

import com.wafersystems.notice.weather.model.Weather;

/**
 * Description 天气预报.
 * @author zhuyi
 */
public interface WeatherInfoService {

  /**
   * Description 根据城市获取周期内该城市天气数据.
   * @param city 城市名称
   * @param type 信息类别
   */
  Weather getWeatherInfo(String city, String type);

  /**
   *保存. 
   */
  void save(Weather bean);
}
