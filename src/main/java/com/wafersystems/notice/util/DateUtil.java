package com.wafersystems.notice.util;


import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * ClassName: DateUtil Description: 日期处理工具类.
 * 
 * @author gelin
 */
@Log4j
public final class DateUtil {

  /**
   * 获取当前时间戳.
   * 
   * @return Long
   */
  public static Long getCurrentTimeInMillis() {
    Calendar cal;
    if (StringUtils.isNotBlank(ParamConstant.getDEFAULT_TIMEZONE())) {
      cal = Calendar.getInstance(TimeZone.getTimeZone(ParamConstant.getDEFAULT_TIMEZONE()));
    } else {
      cal = Calendar.getInstance();
    }
    cal.setTime(new Date());
    return cal.getTimeInMillis();
  }

  /**
   * 将日期转化为 要求 类型字符串.
   * 
   * @param dateTime - 日期
   * @return String
   */
  public static String getDateTimeStr(Object dateTime, String format) {
    try {
      SimpleDateFormat sdf;
      if (StrUtil.isEmptyStr(format)) {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      } else {
        sdf = new SimpleDateFormat(format);
      }
      return sdf.format(Long.parseLong(dateTime.toString()));
    } catch (Exception exception) {
      log.error("转化日期格式错误：", exception);
      return null;
    }
  }
}
