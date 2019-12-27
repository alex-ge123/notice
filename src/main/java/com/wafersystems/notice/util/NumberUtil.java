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
    if (null == number || "".equals(number.trim())) {
      return false;
    } else {
      for (int i = 0; i < number.length(); i++) {
        if (i == 0 && (number.charAt(i) == '-' || number.charAt(i) == '+')) {
          continue;
        }
        if (number.charAt(i) < '0' || number.charAt(i) > '9') {
          return false;
        }
      }
      return true;
    }
  }
}
