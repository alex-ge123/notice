/**
 * @Title: StrUtil.java
 * @Package com.wafersystems.util
 * @Description:
 * @author Administrator
 * @date 2015-11-30 下午1:08:37
 */

package com.wafersystems.notice.util;

/**
 * ClassName: StrUtil Description: .
 *
 * @author Administrator
 */
public class StrUtil {

  private StrUtil() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Title: isEmptyStr. Description: 检查字符串是否空.
   *
   * @param str - 检查字串
   * @return boolean
   */
  public static boolean isEmptyStr(String str) {
    return null == str || "".equals(str.trim()) || "null".equalsIgnoreCase(str.trim());
  }

  /**
   * Title: isNullObject. Description: 检查对象是否空.
   *
   * @param obj - 检查对象
   * @return boolean
   */
  public static boolean isNullObject(Object obj) {
    return null == obj || "".equals(obj) || "null".equals(obj);
  }

  /**
   * 特殊字符回归.
   *
   * @param str -
   * @return -
   */
  public static String regStr(String str) {
    if (isEmptyStr(str)) {
      return str;
    } else {
      return str.replace("%2B", "+").replace("%3F", "?").replace("%25", "%").replace("%23", "#")
        .replace("%26", "&").replace("%3D", "=");
    }
  }

  /**
   * 去掉文件后缀
   *
   * @param filename
   * @return
   */
  public static String getFileNameNoEx(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf('.');
      if ((dot > -1) && (dot < (filename.length()))) {
        return filename.substring(0, dot);
      }
    }
    return filename;
  }
}
