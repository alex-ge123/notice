package com.wafersystems.notice.service.impl;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wafersystems.notice.constants.AlertConstants;
import com.wafersystems.notice.entity.AlertRecord;
import com.wafersystems.notice.mapper.AlertRecordMapper;
import com.wafersystems.notice.service.IAlertRecordService;
import com.wafersystems.notice.util.RedisHelper;
import com.wafersystems.notice.util.StrUtil;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.constant.SysDictConstants;
import com.wafersystems.virsical.common.core.tenant.TenantContextHolder;
import com.wafersystems.virsical.common.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 提醒记录表 服务实现类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Service
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements IAlertRecordService {
  @Autowired
  private StringRedisTemplate redisTemplate;

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
    if (StrUtil.isEmptyStr(product)) {
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


}
