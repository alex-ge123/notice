package com.wafersystems.notice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wafersystems.notice.config.MailProperties;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.dao.BaseDao;
import com.wafersystems.notice.model.GlobalParameter;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.notice.model.ParameterDTO;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.virsical.common.core.config.AesKeyProperties;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.security.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeanUtils;
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

  @Autowired
  private AesKeyProperties aesKeyProperties;

  @Autowired
  private MailProperties mailProperties;

  /**
   * 保存SystemParam
   */
  @Override
  public void save(GlobalParameter gp) {
    baseDao.update(gp);
  }

  /**
   * 批量保存SystemParam
   *
   * @param list 参数集合
   */
  @Override
  public void saveBatch(List<ParameterDTO> list) {
    list.forEach(dto -> {
      GlobalParameter globalParameter = new GlobalParameter();
      BeanUtils.copyProperties(dto, globalParameter);
      globalParameter.setTenantId(TenantContextHolder.getTenantId());
      String value = AesUtils.decryptAes(dto.getParamValue(),
        aesKeyProperties.getKey());
      globalParameter.setParamValue(stringEncryptor.encrypt(value));
      baseDao.saveOrUpdate(globalParameter);
    });
  }

  /**
   * 删除单个参数
   *
   * @param id id
   */
  @Override
  public void del(Integer id) {
    baseDao.delete(GlobalParameter.class, id);
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
    criteria.add(Restrictions.eq("tenantId", TenantContextHolder.getTenantId()));
    List<GlobalParameter> list = baseDao.findByCriteria(criteria);
    if (!list.isEmpty()) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 查询租户配置参数
   *
   * @param tenantId 租户id
   * @param type     类型
   * @return list
   */
  @Override
  public List<GlobalParameter> getSystemParamList(int tenantId, String type) {
    DetachedCriteria criteria = DetachedCriteria.forClass(GlobalParameter.class);
    // 查询全局配置
    criteria.add(Restrictions.eq("tenantId", tenantId));
    if (StrUtil.isNotBlank(type)) {
      criteria.add(Restrictions.eq("type", type));
    }
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
    // 查询全局配置
    criteria.add(Restrictions.eq("tenantId", CommonConstants.PLATFORM_ADMIN_TENANT_ID));
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
          log.warn("参数值解密异常：key[{}]，value[{}]", globalParameter.getParamKey(), globalParameter.getParamValue());
        }
        map.put(globalParameter.getParamKey(), value);
      }
    });
    this.setValue(map);
    this.checkValue();
    log.info("系统数据库配置相关参数初始化加载完毕");
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


  /**
   * 根据租户获取租户邮件配置，租户未配置，使用系统默认邮件配置
   *
   * @return MailServerConf
   */
  @Override
  public MailServerConf getMailServerConf(Integer tenantId) {
    // 查询租户邮件配置
    List<GlobalParameter> systemParamList = getSystemParamList(tenantId, MailConstants.TYPE);
    MailServerConf conf = new MailServerConf();
    // 其它参数
    Map<String, Object> props = new HashMap<>(10);
    if (systemParamList != null && !systemParamList.isEmpty()) {
      for (GlobalParameter p : systemParamList) {
        // 解密参数
        String value = stringEncryptor.decrypt(p.getParamValue());
        if (MailConstants.MAIL_HOST.equals(p.getParamKey())) {
          conf.setHost(value);
        } else if (MailConstants.MAIL_FROM.equals(p.getParamKey())) {
          conf.setFrom(value);
        } else if (MailConstants.MAIL_PASSWORD.equals(p.getParamKey())) {
          conf.setPassword(value);
        } else if (MailConstants.MAIL_AUTH.equals(p.getParamKey())) {
          conf.setAuth(value);
        } else if (MailConstants.MAIL_MAILNAME.equals(p.getParamKey())) {
          conf.setName(value);
        } else if (MailConstants.MAIL_PORT.equals(p.getParamKey())) {
          try {
            conf.setPort(Integer.parseInt(value));
          } catch (Exception e) {
            conf.setPort(0);
            break;
          }
        } else {
          props.put(p.getParamKey(), value);
        }
      }
      conf.setProps(props);
    }
    if (conf.getPort() == 0) {
      log.info("使用系统默认邮件配置发送邮件 >>>");
      props = mailProperties.getProps();
      conf.setHost(ParamConstant.getDEFAULT_MAIL_HOST());
      conf.setPort(ParamConstant.getDEFAULT_MAIL_PORT());
      conf.setFrom(ParamConstant.getDEFAULT_MAIL_FROM());
      conf.setPassword(ParamConstant.getDEFAULT_MAIL_PASSWORD());
      conf.setAuth(ParamConstant.getDEFAULT_MAIL_AUTH());
      conf.setName(ParamConstant.getDEFAULT_MAIL_MAILNAME());
      conf.setProps(props);
    } else {
      log.info("使用租户自定义邮件配置发送邮件 >>>");
    }
    return conf;
  }
}
