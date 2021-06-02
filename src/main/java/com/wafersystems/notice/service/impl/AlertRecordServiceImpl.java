package com.wafersystems.notice.service.impl;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wafersystems.notice.constants.AlertConstants;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.entity.AlertConf;
import com.wafersystems.notice.entity.AlertRecord;
import com.wafersystems.notice.mapper.AlertRecordMapper;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.service.GlobalParamService;
import com.wafersystems.notice.service.IAlertConfService;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.notice.service.MailService;
import com.wafersystems.notice.util.RedisHelper;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.SysDictConstants;
import com.wafersystems.virsical.common.core.dto.AlertDTO;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 提醒记录表 服务实现类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Service
@Slf4j
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements IAlertRecordService {
  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private IAlertConfService alertConfService;

  @Autowired
  private MailService mailService;

  @Autowired
  private GlobalParamService globalParamService;

  /**
   * 产品标识本地缓存 一小时
   */
  private static TimedCache<String, String> productLocalCache = new TimedCache<>(1000 * 60 * 60);

  @Override
  public IPage<AlertRecord> alertPage(Page<AlertRecord> page, QueryWrapper<AlertRecord> query) {
    final Page<AlertRecord> result = this.page(page, query);
    final List<AlertRecord> records = result.getRecords();
    if (CollUtil.isNotEmpty(records)) {
      records.forEach(this::fillRecord);
    }
    return result;
  }

  /**
   * 填充产品标识及用户名
   *
   * @param alertRecord record
   */
  private void fillRecord(AlertRecord alertRecord) {
    // 填充产品名称
    alertRecord.setProductName(getProductName(alertRecord.getProduct()));
    if (ObjectUtil.equal(AlertConstants.LOCAL.getType(), alertRecord.getAlertType())) {
      // 填充用户名称
      final SysUser user = RedisHelper.hGet(redisTemplate,
        CommonConstants.USER_KEY + TenantContextHolder.getTenantId(),
        alertRecord.getRecipient(), SysUser.class);
      if (ObjectUtil.isNotNull(user)) {
        alertRecord.setUserName(user.getRealName());
      }
    }
  }

  private String getProductName(String product) {
    if (StrUtil.isBlank(product)) {
      return "";
    }
    if (!productLocalCache.containsKey(product)) {
      final Map<Object, Object> map = redisTemplate.opsForHash().entries(CommonConstants.SYS_DICT + SysDictConstants.PRODUCT_TYPE);
      if (CollUtil.isNotEmpty(map)) {
        map.forEach((key, value) -> productLocalCache.put(String.valueOf(value), String.valueOf(key)));
      } else {
        return product;
      }
    }
    return productLocalCache.get(product);
  }

  @Override
  public void processAlertMessage(AlertDTO alertDTO) {
    final ArrayList<AlertRecord> alertRecords = new ArrayList<>();
    final boolean b = saveRecords(alertDTO, alertRecords);
    if (b) {
      alertRecords.forEach(alertRecord -> {
        try {
          if (ObjectUtil.equal(AlertConstants.LOCAL.getType(), alertDTO.getAlertType())) {
            // 站内消息
            sendMqtt(alertDTO, alertRecord);
          } else if (ObjectUtil.equal(AlertConstants.LOCAL.getType(), alertDTO.getAlertType())) {
            // 邮件
            sendMail(alertDTO, alertRecord);
          } else if (ObjectUtil.equal(AlertConstants.LOCAL.getType(), alertDTO.getAlertType())) {
            // 短信
            sendSms(alertDTO, alertRecord);
          }
          // 发送成功，修改投递状态
          alertRecord.setDeliveryStatus(AlertConstants.ALERT_RECORD_DELIVERY_STATUS_SEND);
          this.updateById(alertRecord);
        } catch (Exception e) {
          log.error("告警消息{}发送失败", alertRecord.getId().toString(), e);
          // 发送失败，修改投递状态，记录失败原因
          alertRecord.setDeliveryStatus(AlertConstants.ALERT_RECORD_DELIVERY_STATUS_ERROR);
          alertRecord.setFailedInfo(StrUtil.subPre(e.getMessage(), 255));
          this.updateById(alertRecord);
        }
      });
    }
  }

  private void sendMqtt(AlertDTO alertDTO, AlertRecord alertRecord) {

  }

  private void sendSms(AlertDTO alertDTO, AlertRecord alertRecord) {

  }

  private void sendMail(AlertDTO alertDTO, AlertRecord alertRecord) throws Exception {
    // 构造邮件信息
    final MailDTO mailDTO = new MailDTO();
    mailDTO.setSubject(alertDTO.getTitle());
    mailDTO.setToMail(alertRecord.getRecipient());
    mailDTO.setTempName(alertDTO.getTemplateId());
    mailDTO.setLang(alertDTO.getLang());
    mailDTO.setTenantId(alertDTO.getTenantId());
    mailDTO.setUseBaseTemplate(true);
    final HashMap<String, Object> paramMap = new HashMap<>(2);
    paramMap.put("title", alertDTO.getTitle());
    paramMap.put("content", alertDTO.getContent());
    mailDTO.setData(paramMap);

    final MailBean mailBean = MailBean.builder()
      .subject(alertDTO.getTitle())
      .toEmails(alertRecord.getRecipient())
      .type(ConfConstant.TypeEnum.FM)
      .template(alertDTO.getTemplateId())
      .mailDTO(mailDTO)
      .build();

    // 发送
    mailService.send(mailBean, 0, globalParamService.getMailServerConf(alertDTO.getTenantId()));
  }

  private boolean saveRecords(AlertDTO alertDTO, ArrayList<AlertRecord> alertRecords) {
    AtomicBoolean returnFlag = new AtomicBoolean(true);
    if (CollUtil.isNotEmpty(alertDTO.getRecipients())) {
      // 取传递的接收人
      alertDTO.getRecipients().forEach(recipient -> {
        AlertRecord record = createRecords(alertDTO);
        record.setRecipient(recipient);
        alertRecords.add(record);
      });
    } else {
      TenantContextHolder.setTenantId(alertDTO.getTenantId());
      AlertConf conf = alertConfService.getConf(alertDTO.getTenantId(), alertDTO.getAlertType());
      Integer deliveryStatus;
      if (ObjectUtil.isNull(conf)) {
        // 配置为空
        deliveryStatus = AlertConstants.ALERT_RECORD_DELIVERY_STATUS_ERROR;
        final AlertRecord records = createRecords(alertDTO);
        records.setRecipient("");
        records.setFailedInfo("告警配置不存在");
        records.setDeliveryStatus(deliveryStatus);
        this.save(records);
        returnFlag.set(false);
      } else if (CollUtil.isEmpty(conf.getRecipients())) {
        // 接收人为空
        deliveryStatus = AlertConstants.ALERT_RECORD_DELIVERY_STATUS_ERROR;
        final AlertRecord records = createRecords(alertDTO);
        records.setRecipient("");
        records.setFailedInfo("告警接收人为空");
        records.setDeliveryStatus(deliveryStatus);
        this.save(records);
        returnFlag.set(false);
      } else {
        // 取配置的接收人
        conf.getRecipients().forEach(recipient -> {
          AlertRecord record = createRecords(alertDTO);
          record.setRecipient(recipient);
          if (ObjectUtil.equal(AlertConstants.ALERT_CONF_STATUS_CLOSED, conf.getState())) {
            // 配置关闭
            record.setDeliveryStatus(AlertConstants.ALERT_RECORD_DELIVERY_STATUS_ERROR);
            record.setFailedInfo("告警配置关闭");
            returnFlag.set(false);
          }
          alertRecords.add(record);
        });
      }
    }
    if (CollUtil.isNotEmpty(alertRecords)) {
      // 批量保存
      returnFlag.set(this.saveBatch(alertRecords));
    }
    return returnFlag.get();
  }

  private AlertRecord createRecords(AlertDTO alertDTO) {
    AlertRecord record = new AlertRecord();
    record.setAlertId(alertDTO.getAlertId());
    record.setProduct(alertDTO.getProduct());
    record.setTitle(StrUtil.subPre(alertDTO.getTitle(), 50));
    record.setContent(StrUtil.subPre(alertDTO.getContent(), 255));
    record.setAlertType(alertDTO.getAlertType());
    record.setTenantId(alertDTO.getTenantId());
    record.setStatus(AlertConstants.ALERT_RECORD_STATUS_UNREAD);
    record.setDeliveryStatus(AlertConstants.ALERT_RECORD_DELIVERY_STATUS_UNSEND);
    return record;
  }
}
