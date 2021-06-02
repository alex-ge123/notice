package com.wafersystems.notice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wafersystems.notice.constants.AlertConstants;
import com.wafersystems.notice.entity.AlertConf;
import com.wafersystems.notice.entity.AlertRecipient;
import com.wafersystems.notice.mapper.AlertConfMapper;
import com.wafersystems.notice.service.IAlertConfService;
import com.wafersystems.notice.service.IAlertRecipientService;
import com.wafersystems.notice.util.RedisHelper;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 提醒配置表 服务实现类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Service
@Slf4j
public class AlertConfServiceImpl extends ServiceImpl<AlertConfMapper, AlertConf> implements IAlertConfService {
  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private IAlertRecipientService recipientService;

  @Override
  @EventListener({WebServerInitializedEvent.class})
  public void initTenantConf() {
    final Set<Object> tenantIds = redisTemplate.opsForHash().keys(CommonConstants.TENANT_ID_DOMAIN_KEY);
    Optional.ofNullable(tenantIds).orElse(Collections.emptySet())
      .forEach(id -> initTenantConf(Integer.valueOf(String.valueOf(id))));
  }

  /**
   * 初始化租户告警通知配置
   *
   * @param tenantId 租户ID
   */
  @Override
  public void initTenantConf(Integer tenantId) {
    final LambdaQueryWrapper<AlertConf> query = new LambdaQueryWrapper<>();
    query.eq(AlertConf::getTenantId, tenantId);
    final List<AlertConf> list = this.list(query);
    if (CollUtil.isEmpty(list)) {
      final ArrayList<AlertConf> insertConf = new ArrayList<>(3);
      final AlertConf local = new AlertConf();
      local.setTenantId(tenantId);
      local.setType(AlertConstants.LOCAL.getType());
      insertConf.add(local);
      final AlertConf sms = new AlertConf();
      sms.setTenantId(tenantId);
      sms.setType(AlertConstants.SMS.getType());
      insertConf.add(sms);
      final AlertConf mail = new AlertConf();
      mail.setTenantId(tenantId);
      mail.setType(AlertConstants.MAIL.getType());
      insertConf.add(mail);
      this.saveBatch(insertConf);
      log.info("创建租户{}告警配置", tenantId);
    }
  }

  @Override
  public List<AlertConf> getConf() {
    List<AlertConf> conf = baseMapper.getConfByTenant(TenantContextHolder.getTenantId());
    Optional.ofNullable(conf).orElse(Collections.emptyList())
      .forEach(alertConf -> {
        if (ObjectUtil.equal(AlertConstants.LOCAL.getType(), alertConf.getType())
          && CollUtil.isNotEmpty(alertConf.getRecipients())) {
          // 站内消息填充用户详细信息
          final List<String> recipients = alertConf.getRecipients();
          final List users = RedisHelper.hMultiGet(redisTemplate, CommonConstants.USER_KEY + TenantContextHolder.getTenantId(),
            recipients, SysUser.class);
          alertConf.setUsers(users);
        }
      });
    return conf;
  }

  @Override
  public AlertConf getConf(Integer tenantId, Integer type) {
    return baseMapper.getConfByTenantAndType(tenantId, type);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateConf(List<AlertConf> list) {
    if (CollUtil.isNotEmpty(list)) {
      final boolean b = this.updateBatchById(list);
      if (b) {
        list.forEach(conf -> {
          // 删除旧接收人信息
          final LambdaQueryWrapper<AlertRecipient> delete = new LambdaQueryWrapper<>();
          delete.eq(AlertRecipient::getAlertConfId, conf.getId());
          recipientService.remove(delete);

          // 插入新接收人信息
          final List<String> recipients = conf.getRecipients();
          final ArrayList<AlertRecipient> alertRecipients = new ArrayList<>();
          Optional.ofNullable(recipients).orElse(Collections.emptyList())
            .forEach(recipient -> {
              final AlertRecipient alertRecipient = new AlertRecipient();
              alertRecipient.setAlertConfId(conf.getId());
              alertRecipient.setRecipient(recipient);
              alertRecipients.add(alertRecipient);
            });
          recipientService.saveBatch(alertRecipients);
        });
      }
      log.debug("租户{}告警配置更新完成,详细信息={}", TenantContextHolder.getTenantId(), JSON.toJSONString(list));
    }
  }
}
