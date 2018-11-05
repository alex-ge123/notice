package com.wafersystems.notice.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;

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
 */
@Log4j
public class DateUtil {

  /**
   * 默认日期时间格式[时间戳(yyyy-MM-dd HH:mm:ss)].
   */
  public static final String DEFAULT_FORMATE_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  /**
   * 默认日期时间格式[日期型(yyyy-MM-dd)].
   */
  public static final String DEFAULT_FORMATE_DATE = "yyyy-MM-dd";
  /**
   * 默认日期时间格式[日期型(yyyy/MM/dd)].
   */
  public static final String DEFAULT_FORMATE_DATE_NEW = "yyyy/MM/dd";
  /**
   * 默认日期时间格式[时刻型(HH:mm:ss)].
   */
  public static final String DEFAULT_FORMATE_TIME = "HH:mm:ss";
  /**
   * 日期时间格式[时间戳(yyyy-MM-dd HH:mm)].
   */
  public static final String FORMATE_DATE_TIME = "yyyy-MM-dd HH:mm";
  /**
   * 日期时间格式[时间戳(dd HH:mm)].
   */
  public static final String FORMATE_PATTERN_DATE_TIME = "MM-dd HH:mm";
  /**
   * 日期时间格式[日期型(MM-dd)].
   */
  public static final String FORMATE_PATTERN_DATE = "MM-dd";
  /**
   * 日期时间格式[时刻型(HH:mm)].
   */
  public static final String FORMATE_PATTERN_TIME = "HH:mm";
  /**
   * 日期时间格式[时间戳(MM/dd/yyyy)].
   */
  public static final String ISO_FORMATE_DATE = "MM/dd/yyyy";
  /**
   * 日期时间格式[时间戳(MM/dd/yyyy HH:mm:ss)].
   */
  public static final String ISO_FORMATE_DATE_TIME = "MM/dd/yyyy HH:mm:ss";
  /**
   * 日期时间格式[时间戳(yyyy-MM-dd'T'HH:mm:ss.SSSZ)].
   */
  public static final String UTC_FORMATE_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  public static final String UTC_FORMATE_DATE_TIME2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  /**
   * 一天毫秒数.
   */
  public static final long ONE_DAY = 1000 * 60 * 60 * 24;

  public static final String DEFAULT_TIME_HMS = " 00:00:00";

  public static final String DEFAULT_TIME_S = ":" + "00";

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

  /**
   * <pre>
   * 描述:[日期型]字符串转化指定格式日期
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午3:25:25.
   * </pre>
   *
   * @param dateTime 日期时间
   * @param format 日期格式
   * @return 日期
   */
  public static Date formatDateTime(String dateTime, String format) {
    try {
      if (StringUtil.isEmptyStr(format)) {
        format = DEFAULT_FORMATE_DATE_TIME;
      }
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      if (dateTime != null) {
        return dateFormat.parse(dateTime);
      }
    } catch (Exception exception) {
      log.error("--日期转化指定格式[" + format + "]字符串失败!", exception);
    }
    return null;
  }

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
    if (StringUtil.isEmptyStr(dateTime)) {
      return null;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_TIME);
      if (NumberUtil.isNumber(dateTime)) {
        Date date = new Date(Long.parseLong(dateTime));
        dateTime = sdf.format(date);
      } else {
        if (!dateTime.contains(":")) {
          dateTime = dateTime + DEFAULT_TIME_HMS;
        } else {
          if (dateTime.length() < 19) {
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

  /**
   * <pre>
   * 描述:[字符串型]日期转化指定格式字符串
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午3:25:25.
   * </pre>
   *
   * @param dateTime 日期时间
   * @param format 日期格式
   * @return 字符串
   */
  public static String formatDateTimeStr(Date dateTime, String format) {
    try {
      if (StringUtil.isEmptyStr(format)) {
        format = DEFAULT_FORMATE_DATE_TIME;
      }
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      if (dateTime != null) {
        return dateFormat.format(dateTime);
      }
    } catch (Exception exception) {
      log.error("--日期转化指定格式[" + format + "]字符串失败!", exception);
    }
    return null;
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[yyyy-MM-dd HH:mm:ss])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   * @return 字符串
   */
  public static String formatDateTimeStr(Date dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_TIME);
      return sdf.format(dateTime);
    } catch (Exception exception) {
      log.error("转化日期格式错误：", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:字符串转日期型(日期格式[yyyy-MM-dd])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期字符串(格式:'yyyy-MM-dd HH:mm:ss'或毫秒时间戳值或'yyyy-MM-dd')
   * @return 日期
   */
  public static Date formatDate(String dateTime) {
    if (StringUtil.isEmptyStr(dateTime)) {
      return null;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE);
      if (NumberUtil.isNumber(dateTime)) {
        Date date = new Date(Long.parseLong(dateTime));
        dateTime = sdf.format(date);
      }
      return sdf.parse(dateTime);
    } catch (Exception exception) {
      log.error("日期时间格式转换错误：", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:转化指定日期(时分秒置为0)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:14:15.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static Date formatDate(Date dateTime) {
    String startTime = formatDateStr(dateTime);
    dateTime = formatDate(startTime);
    return dateTime;
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[yyyy-MM-dd HH:mm])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static String formatDateHmTimeStr(Date dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(FORMATE_DATE_TIME);
      return sdf.format(dateTime);
    } catch (Exception exception) {
      log.error("--日期型转字符串(日期格式[yyyy-MM-dd HH:mm])失败!", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[yyyy-MM-dd])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static String formatDateStr(Date dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE);
      return sdf.format(dateTime);
    } catch (Exception exception) {
      log.error("--日期型转字符串(日期格式[yyyy-MM-dd])失败!", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[HH:mm:ss])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static String formatTimeStr(Date dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_TIME);
      return sdf.format(dateTime);
    } catch (Exception exception) {
      log.error("--日期型转字符串(日期格式[HH:mm:ss])失败!", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[HH:mm])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static String formatHmStr(Date dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(FORMATE_PATTERN_TIME);
      return sdf.format(dateTime);
    } catch (Exception exception) {
      log.error("--日期型转字符串(日期格式[HH:mm])失败!", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:日期转XMLGregorianCalendar
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:13:36.
   * </pre>
   *
   * @param date -
   */
  public static XMLGregorianCalendar getXMLCalendar(Date date) {
    DatatypeFactory datatypeFactory = null;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException exception) {
      log.error("获取时间转换工厂时发生错误", exception);
    }
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    XMLGregorianCalendar xmlDatetime = datatypeFactory.newXMLGregorianCalendar(calendar);
    return xmlDatetime;
  }

  /**
   * <pre>
   * 描述:日期格式校验(日期格式[yyyy-MM-dd HH:mm:ss])
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:27:42.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static boolean isDateTime(String dateTime) {
    if (dateTime == null) {
      return false;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_TIME);
      sdf.parse(dateTime);
      return true;
    } catch (Exception exception) {
      log.error("日期时间格式校验错误：", exception);
      return false;
    }
  }

  /**
   * <pre>
   * 描述:间隔指定秒数后日期(例如:每30秒)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔分钟
   */
  public static Date handleDateTimeBySecond(Date dateTime, int interval) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.SECOND, interval);
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定秒数后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定分钟后日期(例如:每30分钟)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔分钟
   */
  public static Date handleDateTimeByMinute(Date dateTime, int interval) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.MINUTE, interval);
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定分钟后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定小时后日期(例如:每3小时).
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔小时
   */
  public static Date handleDateTimeByHour(Date dateTime, int interval) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.HOUR, interval);
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定小时后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定天数后日期(例如:每3天)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔天数
   */
  public static Date handleDateTimeByDay(Date dateTime, int interval) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.DAY_OF_MONTH, interval);
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定天数后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定月数的指定天数后日期(例如:每月1日)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔月数(间隔几个月)
   * @param day 指定天数
   */
  public static Date handleDateTimeByMonth(Date dateTime, int interval, int day) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.MONTH, interval);
      calendar.set(Calendar.DAY_OF_MONTH, day);
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定月数的指定天数后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定月数的指定周数指定星期数后日期(例如:每3个月第一个星期一)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔月数(间隔几个月)
   * @param num 指定周数(1-4:第几个星期)
   * @param week 指定周几(1-7:周一至周日,-1:不指定周几(JDK默认星期一))
   */
  public static Date handleDateTimeByMonth(Date dateTime, int interval, int num, int week) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.MONTH, interval);
      if (num < 0) { // 最后一个星期
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
      } else {
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, num);
      }
      if (week < 0) { // [默认星期一]
        calendar.set(Calendar.DAY_OF_WEEK, 1 % 7 + 1);
      } else {
        calendar.set(Calendar.DAY_OF_WEEK, week % 7 + 1);
      }
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定月数的指定周数指定星期数后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定年数的指定月份指定天数后日期(例如:每年1月1日)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔年(间隔几年)
   * @param month 指定月份(1-12:月份)
   * @param day 指定天数
   */
  public static Date handleDateTimeByYear(Date dateTime, int interval, int month, int day) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.YEAR, interval);
      calendar.set(Calendar.MONTH, month - 1);
      calendar.set(Calendar.DAY_OF_MONTH, day);
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定年数的指定月份指定天数后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:间隔指定年数的指定月份指定周数指定星期数后日期(例如:每年1月份第一个星期一)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:29:07.
   * </pre>
   *
   * @param dateTime 指定日期
   * @param interval 间隔年(间隔几年)
   * @param month 指定月份(1-12:月份)
   * @param num 指定周数(1-4:第几个星期)
   * @param week 指定周几(1-7:周一至周日,-1:不指定周几[默认星期一])
   */
  public static Date handleDateTimeByYear(Date dateTime, int interval, int month, int num,
                                          int week) {
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(dateTime);
      calendar.add(Calendar.YEAR, interval);
      calendar.set(Calendar.MONTH, month - 1);
      if (num < 0) { // 最后一个星期
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
      } else {
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, num);
      }
      if (week < 0) { // [默认星期一]
        calendar.set(Calendar.DAY_OF_WEEK, 1 % 7 + 1);
      } else {
        calendar.set(Calendar.DAY_OF_WEEK, week % 7 + 1);
      }
      dateTime = calendar.getTime();
    } catch (Exception exception) {
      log.error("--间隔指定年数的指定月份指定周数指定星期数后日期异常!", exception);
    }
    return dateTime;
  }

  /**
   * <pre>
   * 描述:获取当前时间的星期数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:31:06.
   * </pre>
   *
   * @param date 指定日期
   */
  public static String formatWeek(Date date) {
    String[] weeks = {"7", "1", "2", "3", "4", "5", "6"};
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (week < 0) {
      week = 0;
    }
    return weeks[week];
  }

  /**
   * <pre>
   * 描述:获取中英文星期数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:32:32.
   * </pre>
   *
   * @param date 指定日期
   * @param lang 语言(中文:zh/zh_CN,英文:en/en_US)
   */
  public static String formatWeek(Date date, String lang) {
    String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    if (!StringUtil.isEmptyStr(lang) && (lang.contains("en") || lang.contains("EN"))) {
      weeks = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
              "Saturday"};
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (week < 0) {
      week = 0;
    }
    return weeks[week];
  }

  /**
   * <pre>
   * 描述:获取中英文星期数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:32:32.
   * </pre>
   *
   * @param week 指定日期 -
   * @param lang 语言(中文:zh/zh_CN,英文:en/en_US)
   */
  public static String formatWeek(int week, String lang) {
    String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    if (!StringUtil.isEmptyStr(lang) && (lang.contains("en") || lang.contains("EN"))) {
      weeks = new String[] {"Sun.", "Mon.", "Tues.", "Wed.", "Thur.", "Fri.", "Sat."};
    }
    if (week < 0) {
      week = 0;
    }
    return weeks[week];
  }

  /**
   * <pre>
   * 描述:获取日期间隔分钟数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:39:08.
   * </pre>
   *
   * @param from 起始时间
   * @param to 结束时间
   */
  public static int intervalMinutes(String from, String to) {
    try {
      Date startTime = formatDateTime(from);
      Date endTime = formatDateTime(to);
      double interval = (endTime.getTime() - startTime.getTime()) / (double) (1000 * 60);
      return (int) Math.floor(interval);
    } catch (Exception exception) {
      log.error("--获取日期间隔分钟数失败!", exception);
    }
    return 0;
  }

  /**
   * <pre>
   * 描述:获取日期间隔分钟数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:39:08.
   * </pre>
   *
   * @param startTime 起始时间
   * @param endTime 结束时间
   */
  public static int intervalMinutes(Date startTime, Date endTime) {
    try {
      double interval = (endTime.getTime() - startTime.getTime()) / (double) (1000 * 60);
      return (int) Math.floor(interval);
    } catch (Exception exception) {
      log.error("--获取日期间隔分钟数失败!", exception);
    }
    return 0;
  }

  /**
   * <pre>
   * 描述:获取日期间隔小时数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:39:08.
   * </pre>
   *
   * @param from 起始时间
   * @param to 结束时间
   */
  public static int intervalHours(String from, String to) {
    try {
      Date startTime = formatDateTime(from);
      Date endTime = formatDateTime(to);
      double interval = (endTime.getTime() - startTime.getTime()) / (double) (1000 * 60 * 60);
      return (int) Math.floor(interval);
    } catch (Exception exception) {
      log.error("--获取日期间隔小时数失败!", exception);
    }
    return 0;
  }

  /**
   * <pre>
   * 描述:获取日期间隔小时数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:39:08.
   * </pre>
   *
   * @param start 起始时间
   * @param end 结束时间
   */
  public static int intervalHours(Date start, Date end) {
    try {
      double interval = (end.getTime() - start.getTime()) / (double) (1000 * 60 * 60);
      return (int) Math.floor(interval);
    } catch (Exception exception) {
      log.error("--获取日期间隔小时数失败!", exception);
    }
    return 0;
  }

  /**
   * <pre>
   * 描述:获取日期间隔小时数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:39:08.
   * </pre>
   *
   * @param from 起始时间
   * @param to 结束时间
   */
  public static int intervalDays(String from, String to) {
    try {
      Date startTime = formatDateTime(from);
      Date endTime = formatDateTime(to);
      double interval = (endTime.getTime() - startTime.getTime()) / (double) (1000 * 60 * 60 * 24);
      return (int) Math.ceil(interval);
    } catch (Exception exception) {
      log.error("--获取日期间隔小时数失败!", exception);
    }
    return 0;
  }

  /**
   * <pre>
   * 描述:获取日期间隔天数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:39:08.
   * </pre>
   *
   * @param start 起始时间
   * @param end 结束时间
   */
  public static int intervalDays(Date start, Date end) {
    try {
      double interval = (end.getTime() - start.getTime()) / (double) (1000 * 60 * 60 * 24);
      return (int) Math.ceil(interval);
    } catch (Exception exception) {
      log.error("--获取日期间隔天数失败!", exception);
    }
    return 0;
  }

  /**
   * <pre>
   * 描述:获取月初第一天
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:40:45.
   * </pre>
   *
   * @param date 指定日期(日期格式[yyyy-MM-dd])
   */
  public static String getMonthFirstDay(String date) {
    String[] mydate = date.split("-");
    return mydate[0] + "-" + mydate[1] + "-01";
  }

  /**
   * <pre>
   * 描述:获取日期的日数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:42:19.
   * </pre>
   *
   * @param date 指定日期
   */
  public static String formatDay(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("dd");
    String ctime = formatter.format(date);
    return ctime;
  }

  /**
   * <pre>
   * 描述:获取日期的月数
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:42:19.
   * </pre>
   *
   * @param date 指定日期
   */
  public static String formatMonth(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("MM");
    String ctime = formatter.format(date);
    return ctime;
  }

  /**
   * <pre>
   * 描述:获取日期的年
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:42:19.
   * </pre>
   *
   * @param date 指定日期
   */
  public static String formatYear(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
    String ctime = formatter.format(date);
    return ctime;
  }

  /**
   * <pre>
   * 描述:获取指定日期最后时间(格式:yyyy-MM-dd 23:59:59)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:49:04.
   * </pre>
   *
   * @param date 指定日期
   */
  public static Date getLastTime(Date date) {
    String dateTime = formatDateStr(date) + " 23:59:59";
    return formatDateTime(dateTime);
  }

  /**
   * <pre>
   * 描述:获取指定日期开始时间(格式:yyyy-MM-dd 00:00:00)
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:49:04.
   * </pre>
   *
   * @param date 指定日期
   */
  public static Date getFirstTime(Date date) {
    String dateTime = formatDateStr(date) + " 00:00:00";
    date = formatDateTime(dateTime);
    return date;
  }

  /**
   *
   * <pre>
   * 描述:根据传入日期获取所在周开始日期
   * 作者:wafer
   * 时间:2017年1月19日 下午3:52:04.
   * </pre>
   *
   * @param date -
   */
  public static Date getMinWeekDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
    return calendar.getTime();
  }

  /**
   *
   * <pre>
   * 描述: 根据传入日期获取所在周结束日期
   * 作者:wafer
   * 时间:2017年1月19日 下午3:52:10.
   * </pre>
   *
   * @param date -
   */
  public static Date getMaxWeekDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
    return calendar.getTime();
  }

  /**
   *
   * <pre>
   * 描述:根据传入日期获取月初日期
   * 作者:wafer
   * 时间:2017年1月19日 下午3:52:04.
   * </pre>
   *
   * @param date -
   */
  public static Date getMinMonthDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    return calendar.getTime();
  }

  /**
   *
   * <pre>
   * 描述: 根据传入日期获取月末日期
   * 作者:wafer
   * 时间:2017年1月19日 下午3:52:10.
   * </pre>
   *
   * @param date -
   */
  public static Date getMaxMonthDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    return calendar.getTime();
  }

  /**
   * <pre>
   * 描述:获取中文日期字符串
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:50:26.
   * </pre>
   *
   * @param date 指定日期
   */
  public static String formatChinaDate(Date date) {
    String format = "yyyy年MM月dd日";
    return formatDateTimeStr(date, format);
  }

  /**
   * <pre>
   * 描述: 本地时区时间转换为UTC时间.
   * </pre>
   *
   * @param date 本地时区时间
   * @param format 转换格式例如："yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"或者"yyyyMMdd'T'HHmmss'Z'"
   */
  public static String localToUtc(Date date, String format) {
    // 1、取得本地时间：
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    // 2、取得时间偏移量：
    int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
    // 3、取得夏令时差：
    int dstOffset = cal.get(Calendar.DST_OFFSET);
    // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
    cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

    return formatDateTimeStr(cal.getTime(), format);
  }

  /**
   * <pre>
   * 描述:格式化合并日期时间
   * 作者:ZhangYi
   * 时间:2016年4月27日 下午2:52:41.
   * </pre>
   *
   * @param start 开始时间
   * @param end 结束时间
   */
  public static String formatRange(Date start, Date end) {
    String startDate = formatDateStr(start);
    String endDate = formatDateStr(end);
    if (!startDate.equals(endDate)) {
      return formatDateHmTimeStr(start) + " ~ " + formatDateHmTimeStr(end);
    } else {
      return formatDateHmTimeStr(start) + " ~ " + formatHmStr(end);
    }
  }

  /**
   * <pre>
   * 描述:格式化合并日期时间
   * 作者:tianhaoyuan
   * 时间:2017年4月25日 下午2:52:41.
   * </pre>
   *
   * @param start 开始时间
   * @param end 结束时间
   * @return eg:2017年04月25日11:00-12:00
   */
  public static String formatRangeCn(Date start, Date end) {
    String cnDate = formatChinaDate(start);
    String startHm = formatHmStr(start);
    String endHm = formatHmStr(end);
    return cnDate + startHm + "-" + endHm;
  }

  /**
   * .
   *
   * <pre>
   * 描述:日期型转字符串(日期格式[yyyy-MM-dd HH:mm:ss])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57
   * 参数：(参数列表)
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static String getDateTimeStr(Object dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_TIME);

      return sdf.format(dateTime);
    } catch (Exception exception) {
      return null;
    }
  }

  /**
   * 描述:获取中文星期数
   */
  public static String getWeekOfDateUp(Date date) {
    String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    int num = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (num < 0) {
      num = 0;
    }
    return weekDays[num];
  }


  /**
   * 描述:[字符串型]日期转化指定格式字符串
   */
  public static String getDateStr(Object dateTime, String format) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      return sdf.format(dateTime);
    } catch (Exception error) {
      return null;
    }
  }



  /**
   * 描述:字符串转日期型(日期格式[yyyy-MM-dd HH:mm:ss])
   */
  public static Date getDateTime(String dateTime) {
    if (StringUtil.isEmptyStr(dateTime)) {
      return null;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_TIME);
      if (NumberUtil.isNumber(dateTime)) {
        Date date = new Date(Long.parseLong(dateTime));
        dateTime = sdf.format(date);
      } else {
        if (!dateTime.contains(":")) {
          dateTime = dateTime + " 00:00:00";
        } else {
          if (dateTime.length() < 19) {
            dateTime = dateTime + ":" + "00";
          }
        }
      }
      return sdf.parse(dateTime);
    } catch (Exception exception) {
      return null;
    }
  }


  /**
   * 描述:获取指定日期最后时间
   */
  public static Date getTodayLastTime(Date date) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    String dateStr = calendar.get(Calendar.YEAR) + "-";
    int month = calendar.get(Calendar.MONTH) + 1;
    if (month < 10) {
      dateStr += "0" + month + "-";

    } else {

      dateStr += month + "-";
    }

    int day = calendar.get(Calendar.DAY_OF_MONTH);

    if (day < 10) {

      dateStr += "0" + day + " 23:59:59";

    } else {

      dateStr += day + " 23:59:59";
    }

    return getDateTime(dateStr);

  }


  /**
   * 描述:转化指定日期(时分秒置为0)
   */
  public static Date changeTimeToDate(Date date) {

    Calendar currentCalendar = Calendar.getInstance();
    currentCalendar.setTime(date);
    currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
    currentCalendar.set(Calendar.MINUTE, 0);
    currentCalendar.set(Calendar.SECOND, 0);
    currentCalendar.set(Calendar.MILLISECOND, 0);

    return currentCalendar.getTime();
  }



  /**
   * 描述:判断当前时间是否是今天
   */
  public static boolean isToday(Date time) {
    Date now = new Date();
    String nowStr = formatDate(now, DEFAULT_FORMATE_DATE);
    String timeStr = formatDate(time, DEFAULT_FORMATE_DATE);
    return nowStr.equals(timeStr);
  }

  public static String formatDate(Date date, String format) {
    if (date == null) {
      return "";
    }
    SimpleDateFormat sf = new SimpleDateFormat(format);
    return sf.format(date);
  }

  /**
   * 描述:获取指定日期最后时间(格式:yyyy-MM-dd 18:00:00)
   */
  public static Date getHyLastTime(Date date) {
    String dateTime = formatDateStr(date) + " 18:00:00";
    return formatDateTime(dateTime);
  }

  /**
   * 描述:获取指定日期开始时间(格式:yyyy-MM-dd)
   */

  public static Date getDateNewTime(String dateTime) {
    if (StringUtil.isEmptyStr(dateTime)) {
      return null;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_NEW);
      if (NumberUtil.isNumber(dateTime)) {
        Date date = new Date(Long.parseLong(dateTime));
        dateTime = sdf.format(date);
      }
      return sdf.parse(dateTime);
    } catch (Exception exception) {
      log.error("日期时间格式转换错误：", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[yyyy/MM/dd])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static String formatDateNewTimeStr(Date dateTime) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE_NEW);
      return sdf.format(dateTime);
    } catch (Exception exception) {
      log.error("--日期型转字符串(日期格式[yyyy-MM-dd HH:mm])失败!", exception);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:日期型转字符串(日期格式[yyyy/MM/dd])
   * 作者:ZhangYi
   * 时间:2016年4月15日 上午10:34:57.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static Date formatNewDate(Date dateTime) {
    String startTime = formatDateNewTimeStr(dateTime);
    dateTime = getDateNewTime(startTime);
    return dateTime;
  }

  /**
   * <pre>
   * 描述:日期格式校验(日期格式[yyyy-MM-dd])
   * 作者:ZhangYi
   * 时间:2016年5月5日 下午4:27:42.
   * </pre>
   *
   * @param dateTime 日期时间
   */
  public static boolean isValidDate(String dateTime) {
    if (dateTime == null) {
      return false;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATE_DATE);
      sdf.parse(dateTime);
      return true;
    } catch (Exception exception) {
      //log.error("日期时间格式校验错误：", exception);
      return false;
    }
  }

  /**
   * 描述:获取指定日期开始时间(格式:yyyy-MM-dd 00:00:00)
   *
   * @param date 指定日期
   */
  public static Date getStartTime(Date date, String startTime) {
    String dateTime = formatDateStr(date) + " " + startTime;
    date = formatDateTime(dateTime);
    return date;
  }

  /**
   * 描述:获取指定日期最后时间(格式:yyyy-MM-dd 18:00:00)
   *
   * @param date 指定日期
   */
  public static Date getEndTime(Date date, String endTime) {
    String dateTime = formatDateStr(date) + " " + endTime;
    return formatDateTime(dateTime);
  }


  /**
   * 测试主方法。
   */
  public static void main(String[] args) {


    Date dateTime = DateUtil.formatDate("2017-02-03 17:30:20");
    String startTime = formatDateNewTimeStr(dateTime);
    dateTime = getDateNewTime(startTime);

    System.out.println(startTime);
    System.out.println(dateTime);

    String dateString = formatRangeCn(new Date(), new Date());
    System.out.println(dateString);
    // String date = "1970-01-01 00:00:00";
    // String time = "1461032462000";
    // String to = "1462032000000";
    // long t1 = Long.valueOf(time) / (24 * 60 * 60 * 1000);
    // System.out.println(t1);
    // System.out.println(DateUtil.getDate(date).getTime());
    // System.out.println(DateUtil.formatDateTime(time));
    // System.out.println(DateUtil.getDate("2016-05-01"));
    // System.out.println(DateUtil.getChinaDateYMD(new Date()));
    // System.out.println(DateUtil.formatDateTimeStr(getLastTime(new Date())));
    // Calendar calender1 = Calendar.getInstance();
    // calender1.setTime(new Date());
    // calender1.add(Calendar.DATE, 30);
    // String dateTime1 = formatDateTimeStr(calender1.getTime());
    // System.out.println(dateTime1);
    // GregorianCalendar calendar = new GregorianCalendar();
    // calendar.setTime(new Date());
    // calendar.add(Calendar.MONTH, 2);
    // calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
    // calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);
    // String chinaFormateDateTime =
    // "G yyyy年MM月dd日 a HH时mm分ss秒SSS毫秒 zZ(本年第w周,本月第W周,本年第D天,本月第F星期的E)";
    // String dateTime =
    // formatDateTimeStr(formatDateTime("2016-07-31 13:26:50"),
    // chinaFormateDateTime);
    // System.out.println(dateTime);
    // Calendar calender3 = Calendar.getInstance();
    // calender3.setTime(new Date());
    // calender3.add(Calendar.DAY_OF_YEAR, 30);
    // String dateTime3 = formatDateTimeStr(calender3.getTime());
    // System.out.println(dateTime3);
    // Date date = new Date("2017/01/14");
    // System.out.println("月初：" + getMinMonthDate(date));
    // System.out.println("月末：" + getMaxMonthDate(date));
    // System.out.println("所在周开始日期：" + getMinWeekDate(date));
    // System.out.println("所在周结束日期：" + getMaxWeekDate(date));
    System.out.println(formatWeek(new Date(), "en"));
    System.out.println(handCorn(new Date()));
  }

  public static String handCorn(Date startTaskTime) {
    String format = "ss mm HH * * ?";
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(startTaskTime);
  }
}
