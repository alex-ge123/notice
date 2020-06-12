package com.wafersystems.notice.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * 项目:ReserveMeeting
 * 描述:日期工具类
 * 说明:[例如:公元2016年07月31日 上午 10时15分15秒361毫秒 CST+0800(当前日期为:本年第32周,本月第6周,本年第213天,本月第5星期的星期日)]
 * (yyyy:年,MM:月,dd:日,a:上午/下午[am/pm],HH:时,mm:分,ss:秒,SSS:毫秒,
 * w:年中周数[例如:本年第17周],W:月周数[例如:本月第2周],D:年中天数[例如:本年第168天],F:星期数[例如:2],E:星期文本[例如:星期二],
 * zzz:时区标识[CST/GMT],Z:时区[例如:+0800],G:年限标识(公元))
 * 作者:ZhangYi
 * 时间:2016年6月23日 上午11:18:01
 * 版本:wrm_v4.0
 * JDK:1.7.80.
 * </pre>
 * @author wafer
 */
@Slf4j
public class DateUtil {

  private DateUtil() {
  }

  /**
   * 默认日期时间格式[时间戳(yyyy-MM-dd HH:mm:ss)].
   */
  public static final String DEFAULT_FORMATE_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

  public static final String DEFAULT_TIME_HMS = " 00:00:00";

  public static final String DEFAULT_TIME_S = ":" + "00";

  /**
   * <pre>
   * 描述:字符串转日期型(日期格式[yyyy-MM-dd HH:mm:ss])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期字符串(格式:'yyyy-MM-dd HH:mm:ss'或毫秒时间戳值或'yyyy-MM-dd HH:mm')
   * @return 日期
   */
  public static Date formatDateTime(String dateTime) {
    if (StrUtil.isBlank(dateTime)) {
      return null;
    }
    String colon = ":";
    int timeLength = 19;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_TIME);
      if (NumberUtil.isNumber(dateTime)) {
        Date date = new Date(Long.parseLong(dateTime));
        dateTime = sdf.format(date);
      } else {
        if (!dateTime.contains(colon)) {
          dateTime = dateTime + DEFAULT_TIME_HMS;
        } else {
          if (dateTime.length() < timeLength) {
            dateTime = dateTime + DEFAULT_TIME_S;
          }
        }
      }
      return sdf.parse(dateTime);
    } catch (Exception exception) {
      log.error("日期时间格式转换错误：", exception);
      return null;
    }
  }

}
