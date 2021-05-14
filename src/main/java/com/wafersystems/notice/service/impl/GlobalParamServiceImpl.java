package com.wafersystems.notice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wafersystems.notice.config.MailProperties;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.constants.ParamConstant;
import com.wafersystems.notice.constants.RedisKeyConstants;
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
import org.springframework.data.redis.core.StringRedisTemplate;
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

  @Autowired
  private StringRedisTemplate redisTemplate;

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
    baseDao.delete(GlobalParameter.class, id.longValue());
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
  public List<GlobalParameter> getSystemParamList(Integer tenantId, String type) {
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
    log.info("开始加载系统数据库配置相关参数");
    DetachedCriteria criteria = DetachedCriteria.forClass(GlobalParameter.class);
    // 查询全局配置
    criteria.add(Restrictions.eq("tenantId", CommonConstants.PLATFORM_ADMIN_TENANT_ID));
    List<GlobalParameter> list = baseDao.findByCriteria(criteria);
    Map<String, String> map = new HashMap<>(30);
    // 清空系统全局参数缓存
    Optional.ofNullable(list).orElse(Arrays.asList()).forEach(globalParameter -> {
      if (StrUtil.isBlank(globalParameter.getParamValue())) {
        log.info("Param [" + globalParameter.getParamKey() + "] is null, ignore!");
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
    if (StrUtil.isNotBlank(ParamConstant.getUrlSmsServer())
      && StrUtil.isNotBlank(ParamConstant.getUrlSmsClientId())
      && StrUtil.isNotBlank(ParamConstant.getUrlSmsSecret())) {
      ParamConstant.setSmsSwitch(true);
    } else {
      log.warn("短信服务地址未配置，将不能使用短信服务！");
      ParamConstant.setSmsSwitch(false);
    }
    ParamConstant.setEmailSwitch(true);
  }

  private void setValue(Map<String, String> map) {
    if (!map.isEmpty()) {
      //redis缓存
      String cacheKey = RedisKeyConstants.CACHE_KEY + RedisKeyConstants.CACHE_HASH_KEY;
      redisTemplate.opsForHash().putAll(cacheKey, map);
      log.info("通用参数存放至redis缓存！{}", map.keySet());

      //本地缓存
      ParamConstant.setDefaultDomain(
        StrUtil.isNotBlank(map.get("DEFAULT_DOMAIN")) ? map.get("DEFAULT_DOMAIN") : "wafersystems.com");
      ParamConstant.setDefaultMailAuth(map.get("DEFAULT_MAIL_AUTH"));
      ParamConstant.setDefaultMailCharset(
        StrUtil.isNotBlank(map.get("DEFAULT_MAIL_CHARSET")) ? map.get("DEFAULT_MAIL_CHARSET") : "GBK");
      ParamConstant.setDefaultMailFrom(map.get("DEFAULT_MAIL_FROM"));
      ParamConstant.setDefaultMailHost(map.get("DEFAULT_MAIL_HOST"));
      ParamConstant.setDefaultMailPort(
        map.get("DEFAULT_MAIL_PORT") != null ? Integer.parseInt(map.get("DEFAULT_MAIL_PORT")) : 25);
      ParamConstant.setDefaultMailMailName(
        StrUtil.isNotBlank(map.get("DEFAULT_MAIL_MAILNAME")) ? map.get("DEFAULT_MAIL_MAILNAME") : "威思客预约服务");
      ParamConstant.setDefaultMailPassword(map.get("DEFAULT_MAIL_PASSWORD"));
      ParamConstant.setDefaultRepeatCount(StrUtil.isNotBlank(
        map.get("DEFAULT_REPEAT_COUNT")) ? Integer.parseInt(map.get("DEFAULT_REPEAT_COUNT")) : 0);
      ParamConstant.setLogoDefault(map.get("LOGO_DEFALUT"));
      ParamConstant.setUrlSmsServer(map.get("URL_SMS_SERVER"));
      ParamConstant.setUrlSmsClientId(map.get("URL_SMS_CLIENTID"));
      ParamConstant.setUrlSmsSecret(map.get("URL_SMS_SECRET"));
      ParamConstant.setSmsSignName(map.get("SMS_SIGN_NAME"));
      ParamConstant.setSystemName(StrUtil.equals(map.get("SYSTEM_NAME"), null) ? "威发系统有限公司" : map.get("SYSTEM_NAME"));
      ParamConstant.setPhone(StrUtil.equals(map.get("PHONE"), null) ? "" : map.get("PHONE"));
      ParamConstant.setSmsRepeatCount(StrUtil.isNotBlank(
        map.get("SMS_REPEAT_COUNT")) ? Integer.parseInt(map.get("SMS_REPEAT_COUNT")) : 0);
      ParamConstant.setDefaultMailUsername(map.get("DEFAULT_MAIL_USERNAME"));
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

    // 租户配置不为空，走租户配置
    if (systemParamList != null && !systemParamList.isEmpty()) {
      String serverType = MailConstants.MAIL_SERVER_TYPE_SMTP;
      //判断邮件服务器类型
      for (GlobalParameter p : systemParamList) {
        if (MailConstants.MAIL_SERVER_TYPE.equals(p.getParamKey())) {
          serverType = stringEncryptor.decrypt(p.getParamValue());
          break;
        }
      }
      return getServerConf(systemParamList, serverType);
    }

    // 租户配置为空，走默认配置
    return smtpDefaultConf();
  }

  private MailServerConf getServerConf(List<GlobalParameter> systemParamList, String serverType) {
    if (MailConstants.MAIL_SERVER_TYPE_MICROSOFT.equals(serverType)) {
      return getMicrosoftConf(systemParamList);
    } else if (MailConstants.MAIL_SERVER_TYPE_EWS.equals(serverType)) {
      return getEwsConf(systemParamList);
    } else {
      return getSmtpConf(systemParamList);
    }
  }

  private MailServerConf smtpDefaultConf() {
    log.info("使用系统默认邮件配置发送邮件 >>>");
    MailServerConf conf = new MailServerConf();
    final Map<String, Object> props = mailProperties.getProps();
    conf.setServerType(MailConstants.MAIL_SERVER_TYPE_SMTP);
    conf.setHost(ParamConstant.getDefaultMailHost());
    conf.setPort(ParamConstant.getDefaultMailPort());
    conf.setFrom(ParamConstant.getDefaultMailFrom());
    conf.setUsername(ParamConstant.getDefaultMailUsername());
    conf.setPassword(ParamConstant.getDefaultMailPassword());
    conf.setAuth(ParamConstant.getDefaultMailAuth());
    conf.setName(ParamConstant.getDefaultMailMailName());
    conf.setProps(props);
    return conf;
  }

  /**
   * 获取smtp配置，如果为空，则取默认配置
   *
   * @param systemParamList systemParamList
   * @return MailServerConf
   */
  private MailServerConf getSmtpConf(List<GlobalParameter> systemParamList) {
    MailServerConf conf = new MailServerConf();
    Map<String, Object> props = new HashMap<>(10);
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
      } else if (MailConstants.MAIL_USERNAME.equals(p.getParamKey())) {
        conf.setUsername(value);
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

    if (conf.getPort() == 0) {
      return smtpDefaultConf();
    } else {
      return conf;
    }
  }

  /**
   * 获取ews配置
   *
   * @param systemParamList systemParamList
   * @return MailServerConf
   */
  private MailServerConf getEwsConf(List<GlobalParameter> systemParamList) {
    MailServerConf conf = new MailServerConf();
    conf.setServerType(MailConstants.MAIL_SERVER_TYPE_EWS);
    for (GlobalParameter p : systemParamList) {
      // 解密参数
      String value = stringEncryptor.decrypt(p.getParamValue());
      if (MailConstants.MAIL_EWS_URL.equals(p.getParamKey())) {
        conf.setEwsUrl(value);
      } else if (MailConstants.MAIL_EWS_ACCOUNT.equals(p.getParamKey())) {
        conf.setEwsAccount(value);
      } else if (MailConstants.MAIL_EWS_PASSWORD.equals(p.getParamKey())) {
        conf.setEwsPassword(value);
      }
    }
    return conf;
  }

  /**
   * 获取Microsoft配置
   *
   * @param systemParamList systemParamList
   * @return MailServerConf
   */
  private MailServerConf getMicrosoftConf(List<GlobalParameter> systemParamList) {
    MailServerConf conf = new MailServerConf();
    conf.setServerType(MailConstants.MAIL_SERVER_TYPE_MICROSOFT);
    for (GlobalParameter p : systemParamList) {
      // 解密参数
      String value = stringEncryptor.decrypt(p.getParamValue());
      if (MailConstants.MAIL_MICROSOFT_CLIENTID.equals(p.getParamKey())) {
        conf.setClientId(value);
      } else if (MailConstants.MAIL_MICROSOFT_CLIENTSECRET.equals(p.getParamKey())) {
        conf.setClientSecret(value);
      } else if (MailConstants.MAIL_MICROSOFT_FROM.equals(p.getParamKey())) {
        conf.setMicrosoftFrom(value);
      } else if (MailConstants.MAIL_MICROSOFT_SCOPE.equals(p.getParamKey())) {
        conf.setScope(value);
      } else if (MailConstants.MAIL_MICROSOFT_TENANTID.equals(p.getParamKey())) {
        conf.setOfficeTenantId(value);
      }
    }
    return conf;
  }


}
