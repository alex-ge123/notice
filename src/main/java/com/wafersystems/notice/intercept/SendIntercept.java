package com.wafersystems.notice.intercept;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.SendInterceptProperties;
import com.wafersystems.notice.constants.RedisKeyConstants;
import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.virsical.common.core.dto.SmsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 发送拦截
 *
 * @author shennan
 * @date 2020/2/4
 */
@Component
@Slf4j
public class SendIntercept {

  @Autowired
  private SendInterceptProperties properties;

  @Autowired
  private StringRedisTemplate redisTemplate;

  /**
   * 重复发送短信拦截
   *
   * @param smsDTO
   * @return 是否拦截 false 不拦截，true 拦截
   */
  public boolean SmsBoolIntercept(SmsDTO smsDTO, String redisKey) {
    if (!properties.isSmsEnabled()) {
      return false;
    }
    if (redisTemplate.hasKey(redisKey)) {
      String redisDtoStr = redisTemplate.opsForValue().get(redisKey);
      SmsDTO redisDto = JSON.parseObject(redisDtoStr, SmsDTO.class);
      if (ObjectUtil.equal(smsDTO, redisDto)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 重复发送邮件拦截
   *
   * @param mailBean
   * @return 是否拦截 false 不拦截，true 拦截
   */
  public boolean MailBoolIntercept(MailBean mailBean) {
    if (!properties.isMailEnabled()) {
      return false;
    }
    String redisKey = String.format(RedisKeyConstants.MAIL_KEY,
      mailBean.getToEmails(), mailBean.getTemplate(), mailBean.getSubject(), mailBean.hashCode());
    if (redisTemplate.hasKey(redisKey)) {
      String redisDtoStr = redisTemplate.opsForValue().get(redisKey);
      String beanStr = JSON.toJSONString(mailBean);
      if (StrUtil.equals(redisDtoStr, beanStr)) {
        return true;
      }
    }
    return false;
  }
}
