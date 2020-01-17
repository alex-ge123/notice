package com.wafersystems.notice.util;

/**
 * @author superwing
 * @ClassName: NumberUtil
 * @Description: 数字工具类
 */
public class NumberUtil {

  private NumberUtil() {
  }

  /**
   * 判断传入的字符是不是数字.
   *
   * @param number 字符串
   * @return true/false
   */
  public static boolean isNumber(String number) {
    return cn.hutool.core.util.NumberUtil.isNumber(number);
  }
}
