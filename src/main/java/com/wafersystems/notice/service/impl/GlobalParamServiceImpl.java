package com.wafersystems.notice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wafersystems.notice.dao.BaseDao;
import com.wafersystems.notice.model.GlobalParameter;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.notice.constants.ParamConstant;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/7/14 11:27 Company:
 * wafersystems
 *
 * @author wafer
 */
@Slf4j
@Service
public class GlobalParamServiceImpl implements GlobalParamService {

  @Autowired
  private BaseDao<GlobalParameter> baseDao;

  @Autowired
  private StringEncryptor stringEncryptor;

  /**
   * 保存SystemParam
   */
  @Override
  public void save(GlobalParameter gp) {
    baseDao.update(gp);
  }

  /**
   * 根据paramKey获取SystemParam
   *
   * @param paramKey
   * @return
   */
  @Override
  public GlobalParameter getSystemParamByParamKey(String paramKey) {
    DetachedCriteria criteria = DetachedCriteria.forClass(GlobalParameter.class);
    criteria.add(Restrictions.eq("paramKey", paramKey));
    List<GlobalParameter> list = baseDao.findByCriteria(criteria);
    if (!list.isEmpty()) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 获取所有配置参数
   *
   * @return list
   */
  @Override
  public List<GlobalParameter> getSystemParamList() {
    DetachedCriteria criteria = DetachedCriteria.forClass(GlobalParameter.class);
    return baseDao.findByCriteria(criteria);
  }

  /**
   * 初始化系统参数.
   */
  @Override
  @EventListener({WebServerInitializedEvent.class})
  public void initSystemParam() {
    log.debug("开始加载系统数据库配置相关参数");
    DetachedCriteria criteria = DetachedCriteria.forClass(GlobalParameter.class);
    List<GlobalParameter> list = baseDao.findByCriteria(criteria);
    Map<String, String> map = new HashMap<>(30);
    // 清空系统全局参数缓存
    Optional.ofNullable(list).orElse(Arrays.asList()).forEach(globalParameter -> {
      if (StrUtil.isBlank(globalParameter.getParamValue())) {
        log.debug("DBParam [" + globalParameter.getParamKey() + "] is null, ignore!");
      } else {
        // 参数值解密，解密失败按原文处理
        String value = globalParameter.getParamValue();
        try {
          value = stringEncryptor.decrypt(value);
        } catch (Exception e) {
          log.debug("参数值解密异常：key[{}]，value[{}]", globalParameter.getParamKey(), globalParameter.getParamValue());
        }
        map.put(globalParameter.getParamKey(), value);
      }
    });
    this.setValue(map);
    this.checkValue();
    log.debug("系统数据库配置相关参数加载完毕");
  }

  private void checkValue() {
    if (StrUtil.isNotBlank(ParamConstant.getURL_SMS_SERVER())
      && StrUtil.isNotBlank(ParamConstant.getURL_SMS_CLIENTID())
      && StrUtil.isNotBlank(ParamConstant.getURL_SMS_SECRET())) {
      ParamConstant.setSMS_SWITCH(true);
    } else {
      log.warn("短信服务地址未配置，将不能使用短信服务！");
      ParamConstant.setSMS_SWITCH(false);
    }
    if (StrUtil.isNotBlank(ParamConstant.getDEFAULT_MAIL_AUTH())
      && StrUtil.isNotBlank(ParamConstant.getDEFAULT_MAIL_FROM())
      && StrUtil.isNotBlank(ParamConstant.getDEFAULT_MAIL_HOST())
      && ParamConstant.getDEFAULT_MAIL_PORT() != null
      && StrUtil.isNotBlank(ParamConstant.getDEFAULT_MAIL_PASSWORD())) {
      ParamConstant.setEMAIL_SWITCH(true);
    } else {
      log.warn("邮件服务参数配置不完整，将不能使用邮件服务！");
      ParamConstant.setEMAIL_SWITCH(false);
    }
  }

  private void setValue(Map<String, String> map) {
    if (!map.isEmpty()) {
      ParamConstant.setDEFAULT_DOMAIN(
        StrUtil.isNotBlank(map.get("DEFAULT_DOMAIN")) ? map.get("DEFAULT_DOMAIN") : "wafersystems.com");
      ParamConstant.setDEFAULT_MAIL_AUTH(map.get("DEFAULT_MAIL_AUTH"));
      ParamConstant.setDEFAULT_MAIL_CHARSET(
        StrUtil.isNotBlank(map.get("DEFAULT_MAIL_CHARSET")) ? map.get("DEFAULT_MAIL_CHARSET") : "GBK");
      ParamConstant.setDEFAULT_MAIL_FROM(map.get("DEFAULT_MAIL_FROM"));
      ParamConstant.setDEFAULT_MAIL_HOST(map.get("DEFAULT_MAIL_HOST"));
      ParamConstant.setDEFAULT_MAIL_PORT(
        map.get("DEFAULT_MAIL_PORT") != null ? Integer.parseInt(map.get("DEFAULT_MAIL_PORT")) : 25);
      ParamConstant.setDEFAULT_MAIL_MAILNAME(
        StrUtil.isNotBlank(map.get("DEFAULT_MAIL_MAILNAME")) ? map.get("DEFAULT_MAIL_MAILNAME") : "威思客预约服务");
      ParamConstant.setDEFAULT_MAIL_PASSWORD(map.get("DEFAULT_MAIL_PASSWORD"));
      ParamConstant.setDEFAULT_MAIL_TIMEOUT(
        StrUtil.isNotBlank(map.get("DEFAULT_MAIL_TIMEOUT")) ? map.get("DEFAULT_MAIL_TIMEOUT") : "25000");
      ParamConstant.setDEFAULT_REPEAT_COUNT(StrUtil.isNotBlank(
        map.get("DEFAULT_REPEAT_COUNT")) ? Integer.parseInt(map.get("DEFAULT_REPEAT_COUNT")) : 0);
      ParamConstant.setDEFAULT_TIMEZONE(map.get("DEFAULT_TIMEZONE"));
      ParamConstant.setLOGO_DEFALUT(map.get("LOGO_DEFALUT"));
      ParamConstant.setIMAGE_DIRECTORY(map.get("IMAGE_DIRECTORY"));
      ParamConstant.setURL_SMS_SERVER(map.get("URL_SMS_SERVER"));
      ParamConstant.setURL_SMS_CLIENTID(map.get("URL_SMS_CLIENTID"));
      ParamConstant.setURL_SMS_SECRET(map.get("URL_SMS_SECRET"));
      ParamConstant.setSMS_SIGN_NAME(map.get("SMS_SIGN_NAME"));
      ParamConstant.setSYSTEM_NAME(StrUtil.equals(map.get("SYSTEM_NAME"), null) ? "威发系统有限公司" : map.get("SYSTEM_NAME"));
      ParamConstant.setPHONE(StrUtil.equals(map.get("PHONE"), null) ? "" : map.get("PHONE"));
    }
  }

}
