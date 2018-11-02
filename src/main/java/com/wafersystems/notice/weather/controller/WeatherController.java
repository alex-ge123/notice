package com.wafersystems.notice.weather.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.util.*;
import com.wafersystems.notice.weather.model.Weather;
import com.wafersystems.notice.weather.service.WeatherInfoService;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 天气预报.
 * 
 * @author zhuyi
 */
@Log4j
@RestController
@RequestMapping("/weather")
public class WeatherController extends BaseController {

  private static final String WEATHER_PM = "PM";
  private static final String WEATHER_INFO = "INFO";
  private static final String WEATHER_PM_URL = "https://ali-pm25.showapi.com";
  private static final String WEATHER_INFO_URL = "https://ali-weather.showapi.com";

  @Autowired
  private WeatherInfoService weatherInfoService;

  /**
   * Description: 天气信息.
   *
   * @param city 城市
   */
  private Weather getWeather(String city, String type) {
    Weather bean = weatherInfoService.getWeatherInfo(city, type);
    if (!StrUtil.isNullObject(bean)) {
      if (((DateUtil.getCurrentTimeInMillis() - bean.getCreateTime()) / 1000 / 60) >= Long
          .parseLong(ParamConstant.getWEATHER_UPDATE_INTERVAL())) {
        bean = this.getInfo(type, city);
      }
    } else {
      bean = this.getInfo(type, city);
    }
    return bean;
  }

  /**
   * Description: 获取天气信息.
   * 
   * @param city 城市
   * @return -
   */
  @RequestMapping(method = RequestMethod.GET)
  public Object getWeatherInfo(String city) {
    log.debug("开始查城市【" + city + "】天气信息");
    try {
      return returnBackMap(this.getData(city), ConfConstant.RESULT_SUCCESS);
    } catch (Exception ex) {
      log.error("获取天气信息异常！", ex);
      return returnBackMap(null, ConfConstant.RESULT_FAIL);
    }
  }

  /**
   * 天气信息组装(天气预报+空气质量).
   */
  private Weather getData(String city) {
    Weather info = this.getWeather(city, WEATHER_INFO);
    if (!StrUtil.isNullObject(info)) {
      info.setPm(this.getWeather(city, WEATHER_PM).getInfo());
    } else {
      info = this.getWeather(city, WEATHER_PM);
      info.setPm(info.getInfo());
      info.setInfo(null);
    }
    return info;
  }

  /**
   * 在线获取天气信息.
   */
  private Weather getInfo(String type, String city) {
    Weather bean = null;
    try {
      String host;
      String path;
      Map<String, String> querys = new HashMap<>();
      if (WEATHER_PM.equals(type)) {
        host = WEATHER_PM_URL;
        path = "/pm25-detail";
        querys.put("city", city);
      } else {
        host = WEATHER_INFO_URL;
        path = "/hour24";
        querys.put("area", city);
      }
      Map<String, String> headers = new HashMap<>();
      headers.put("Authorization", "APPCODE " + ParamConstant.getALI_APP_CODE());
      HttpResponse response = HttpUtils.doGet(host, path, headers, querys);
      JSONObject json = JSON.parseObject(EntityUtils.toString(response.getEntity()));
      String info = json.get("showapi_res_body").toString();
      if (!StrUtil.isEmptyStr(info)) {
        if (WEATHER_PM.equals(type)) {
          bean = new Weather(city, info, DateUtil.getCurrentTimeInMillis(), WEATHER_PM);
          weatherInfoService.save(bean);
        } else if (WEATHER_INFO.equals(type)) {
          bean = new Weather(city, info, DateUtil.getCurrentTimeInMillis(), WEATHER_INFO);
          weatherInfoService.save(bean);
        }
      }
    } catch (Exception ex) {
      log.error("在线获取天气信息异常！", ex);
    }
    return bean;
  }
}
