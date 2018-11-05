package com.wafersystems.notice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.oro.text.regex.*;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * .
 * 
 * <pre>
 * 项目:aliyun[smartmeeting3.5]
 * 描述:字符串工具类
 * 作者:ZhangYi
 * 时间:2015年9月11日 下午4:30:32
 * 版本:wsm_v3.5
 * JDK:1.7.80
 * </pre>
 */
@Slf4j
public class StringUtil {
  /**
   * 分隔符:并号(&).
   */
  public static final String DELIMITER_AND = "&";
  /**
   * 分隔符:破折号(-).
   */
  public static final String DELIMITER_DASH = "-";
  /**
   * 分隔符:下划线(_).
   */
  public static final String DELIMITER_UNDERLINE = "_";
  /**
   * 分隔符:逗号(,).
   */
  public static final String DELIMITER_COMMA = ",";
  /**
   * 分隔符:点号(.).
   */
  public static final String DELIMITER_POINT = ".";
  /**
   * 分隔符:冒号(:).
   */
  public static final String DELIMITER_COLON = ":";
  /**
   * 分隔符:分号(;).
   */
  public static final String DELIMITER_SEMICOLON = ";";

  /**
   * 编码方式:UTF-8.
   */
  public static final String ENCODING_UTF8 = "UTF-8";
  /**
   * 编码方式:UTF-16.
   */
  public static final String ENCODING_UTF16 = "UTF-16";
  /**
   * 编码方式:GBK.
   */
  public static final String ENCODING_GBK = "GBK";
  /**
   * 编码方式:GB2312.
   */
  public static final String ENCODING_GB2312 = "GB2312";
  /**
   * 编码方式:ISO8859-1.
   */
  public static final String ENCODING_ISO8859_1 = "ISO-8859-1";

  /**
   * (数字+字母)字符库
   */
  // private static String[] character_library =
  // new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "A",
  // "b", "B", "c",
  // "C", "d", "D", "e", "E", "f", "F", "g", "G", "h", "H", "i", "I", "j", "J",
  // "k", "K", "l",
  // "L", "m", "M", "n", "N", "o", "O", "p", "P", "q", "Q", "r", "R", "s", "S",
  // "t", "T", "u",
  // "U", "v", "V", "w", "W", "x", "X", "y", "Y", "z", "Z" };

  /**
   * Perl5Compiler
   */
  private static final PatternCompiler compiler = new Perl5Compiler();
  /**
   * Perl5Matcher
   */
  private static final PatternMatcher matcher = new Perl5Matcher();
  /**
   * 正则表达式缓冲
   */
  private static Map<String, Pattern> REGEX_PATTERN = new HashMap<>();

  /**
   * <pre>
   * 描述:判断空字符串(空指针、空字符串、"null"字符串).
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午4:39:14
   * 参数：(参数列表)
   * &#64;param str 目标字符串
   * &#64;return (true为空字符串,false不为空字符串)
   * </pre>
   */
  public static boolean isEmptyStr(String str) {
    if (null == str || "".equals(str.trim()) || "null".equals(str.trim().toLowerCase())) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 判断对象是否为空.
   * 
   * @param obj -
   * @return -
   */
  public static boolean isNullObject(Object obj) {
    return null == obj || "".equals(obj);
  }

  /**
   * <pre>
   * 描述:格式化字符串.
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午4:42:55
   * 参数：(参数列表)
   * &#64;param str 目标字符串
   * &#64;return
   * </pre>
   */
  public static String formatStr(String str) {
    if (null == str || str.equals("null")) {
      return "";
    } else {
      return str.trim();
    }
  }

  /**
   * <pre>
   * 描述:字符串校验(特殊字符:下划线/中划线/点/逗号/分号/小括号/空格/&).
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午4:50:15
   * 参数：(参数列表)
   * &#64;param value 目标字符串
   * &#64;param type 校验类型(-1.字母+数字+汉字+特殊字符;0.数字,1.字母;2.字母+数字;3.字母+数字+特殊字符;4.汉字+空格)
   * &#64;return
   * </pre>
   */
  public static boolean checkStr(String value, int type) {
    String reg = "^[-a-zA-Z0-9_\u4e00-\u9fa5" + "" //
        + "\\-\\. &,;()；，（）]+$";
    if (type == 0) { // 数字
      reg = "^[0-9]+$";
    }
    if (type == 1) { // 字母
      reg = "^[a-zA-Z]+$";
    }
    if (type == 2) { // 字母+数字
      reg = "^[a-zA-Z0-9]+$";
    }
    if (type == 3) { // 字母+数字+特殊字符
      reg = "^[a-zA-Z0-9_\\-\\. &,;()；，（）]+$";
    }
    if (type == 4) { // 汉字+空格
      reg = "^[\u4e00-\u9fa5 " //
          + "]+$";
    }
    if (isEmptyStr(value)) {
      return false;
    } else {
      if (value.matches(reg)) {
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * <pre>
   * 描述:电话号码或手机号码匹配[支持大陆|香港|澳门|台湾].
   *      (例如:029-86707016,86707016,13688888888,+8613688888888,+85293505959,+85362954356,+8860932637679)
   * 作者:ZhangYi
   * 时间:2016年3月10日 上午11:03:34
   * 参数：(参数列表)
   * &#64;param tel 座机号码或手机号码
   * &#64;return
   * </pre>
   */
  public static boolean telMatches(String tel) {
    String reg =
        "(^[1-9]{1}([0-9]{5,7})$|^[0]([1-9]{2,3})([-]?)([1-9]{1}[0-9]{5,7})$|(^[1]([34578]{1})"
            + "([0-9]{9})$)|(^[+]([8][6])([1][34578]{1})([0-9]{9})$)|(^[+](([8][5][2])|([8][5][3]))"
            + "([69]{1})([0-9]{7})$)|(^[+]([8][8][6])([0]?[9]{1})([0-9]{8})$))";
    if (isEmptyStr(tel)) {
      return false;
    } else {
      return tel.matches(reg);
    }
  }

  /**
   * <pre>
   * 描述:统计字符串长度.
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午5:31:26
   * 参数：(参数列表)
   * &#64;param value
   * &#64;return
   * </pre>
   */
  public static int countLength(String value) {
    int len = 0;
    String chinese = "[\u0391-" //
        + "\uFFE5" //
        + "]";
    /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
    for (int i = 0; i < value.length(); i++) {
      /* 获取一个字符 */
      String temp = value.substring(i, i + 1);
      /* 判断是否为中文字符 */
      if (temp.matches(chinese)) {
        /* 中文字符长度为2 */
        len += 2;
      } else {
        /* 其他字符长度为1 */
        len += 1;
      }
    }
    return len;
  }

  /**
   * <pre>
   * 描述:截取一段字符(不区分中英文,如果数字不正好，则少取一个字符位).
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午5:36:48
   * 参数：(参数列表)
   * &#64;param value  原始字符串 
   * &#64;param len  截取长度(一个汉字长度按2算的) 
   * &#64;return
   * </pre>
   */
  public static String substring(String value, int len) {
    if (isEmptyStr(value)) {
      return "";
    }
    byte[] strByte = new byte[len];
    if (len > countLength(value)) {
      return value;
    }
    try {
      System.arraycopy(value.getBytes("GBK"), 0, strByte, 0, len);
      int count = 0;
      for (int i = 0; i < len; i++) {
        int num = (int) strByte[i];
        if (num < 0) {
          count++;
        }
      }
      if (count % 2 != 0) {
        len = (len == 1) ? ++len : --len;
      }
      return new String(strByte, 0, len, "GBK");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * <pre>
   * 描述:提取数字并拼接随机数.
   * 作者:administrator
   * 时间:2005年9月11日 下午5:49:32
   * 参数：(参数列表)
   * &#64;param longStr
   * &#64;return
   * </pre>
   */
  public static String extractionAssemble(String str) {
    str = str.trim();
    StringBuffer strNum = new StringBuffer();// 从字符串中提取数字
    if (str != null && !"".equals(str)) {
      for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
          strNum.append(str.charAt(i));
        }
      }
    }
    long num = Long.parseLong(strNum.toString());
    num = num % 100;
    String numStr = String.valueOf(num);
    int random4Num = (int) (Math.random() * 9000 + 1000);
    numStr += random4Num;
    return numStr;
  }

  /**
   * <pre>
   * 描述:过滤重复字符串.
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午6:00:05
   * 参数：(参数列表)
   * &#64;param values 目标数组
   * &#64;return
   * </pre>
   */
  public static String[] filterRepeat(String[] values) {
    TreeSet<String> filter = new TreeSet<String>();
    for (String value : values) {
      if (!isEmptyStr(value)) {
        filter.add(value);
      }
    }
    String[] result = new String[filter.size()];
    filter.toArray(result);
    return result;
  }

  /**
   * <pre>
   * 描述:数组转化为字符串.
   * 作者:ZhangYi
   * 时间:2015年9月11日 下午6:15:36
   * 参数：(参数列表)
   * &#64;param values 目标数组
   * &#64;param flag  转化类型(true:单引号拼接,false:普遍拼接)
   * &#64;return
   * </pre>
   */
  public static String arrayToString(Object[] values, boolean flag) {
    StringBuffer result = new StringBuffer();
    for (Object value : values) {
      if (!isEmptyStr(value.toString())) {
        if (isEmptyStr(result.toString())) {
          if (flag) {
            result.append("'" + value + "'");
          } else {
            result.append(value);
          }
        } else {
          if (flag) {
            result.append(",'" + value + "'");
          } else {
            result.append("," + value);
          }
        }
      }
    }
    return result.toString();
  }

  /**
   * <pre>
   * 作者：ZhangYi.
   * 描述：替换res中多个replacement为1个,并替换头尾replacement
   * 时间：2014年8月4日 下午6:43:29
   * &#64;param target 字符串源
   * &#64;param replacement 替换标示 
   * &#64;return
   * </pre>
   */
  public static String replaceAll(String target, String replacement) {
    if (target.indexOf(replacement + replacement) == -1) {
      if (target.startsWith(replacement)) {
        target = target.substring(1);
      }
      if (target.endsWith(replacement)) {
        target = target.substring(0, target.length() - 1);
      }
      return target;
    } else {
      target = target.replaceAll(replacement + replacement, replacement);
    }
    return replaceAll(target, replacement);
  }

  /**
   * <pre>
   * 描述:UTF-8转码ISO8859-1.
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午2:30:05
   * 参数：(参数列表)
   * &#64;param str
   * &#64;return
   * </pre>
   */
  public static String utf8ToIso(String str) {
    try {
      return str == null ? null : new String(str.getBytes(ENCODING_UTF8), ENCODING_ISO8859_1);
    } catch (UnsupportedEncodingException ex) {
      log.error("UTF8转ISO8859-1错误：" + str, ex);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:字符串转码UTF-8.
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午2:31:14
   * 参数：(参数列表)
   * &#64;param str
   * &#64;return
   * </pre>
   */
  public static String stringToUtf8(String str) {
    try {
      return str == null ? null : new String(str.getBytes(ENCODING_UTF8), ENCODING_UTF8);
    } catch (UnsupportedEncodingException ex) {
      log.error("--字符串转UTF8错误：" + str, ex);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:字符串编码GBK的二进制.
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午2:43:14
   * 参数：(参数列表)
   * &#64;param str
   * &#64;return
   * </pre>
   */
  public static byte[] stringToGBKByte(String str) {
    try {
      return str.getBytes(ENCODING_GBK);
    } catch (UnsupportedEncodingException ex) {
      log.error("--字符串转GBK错误：" + str, ex);
      return null;
    }
  }

  /**
   * <pre>
   * 描述:字符串反转.
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午2:44:59
   * 参数：(参数列表)
   * &#64;param str
   * &#64;return
   * </pre>
   */
  public static String reverseString(String str) {
    StringBuffer info = new StringBuffer(str);
    info = info.reverse();
    return info.toString();
  }

  /**
   * <pre>
   * 描述:字符串反转(字符反转).
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午2:45:36
   * 参数：(参数列表)
   * &#64;param str
   * &#64;return
   * </pre>
   */
  public static String reverseString2(String str) {
    char[] ch = str.toCharArray();
    StringBuffer info = new StringBuffer();
    for (int i = ch.length - 1; i >= 0; i--) {
      info.append(ch[i]);
    }
    return info.toString();
  }

  /**
   * <pre>
   * 描述:n位16进制随机数字.
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午2:51:19
   * 参数：(参数列表)
   * &#64;param n
   * &#64;return
   * </pre>
   */
  public static String randomHexNumber(int num) {
    int jindex = 0;
    StringBuffer number = new StringBuffer();
    while (jindex < (num - 1) / 4 + 1) {
      int index = new Random().nextInt(65536);
      String mn = Integer.toHexString(index).toUpperCase();
      while (mn.length() < 4) {
        mn = "0" + mn;
      }
      number.append(mn);
      jindex++;
    }
    return number.substring(number.length() - num);
  }

  /**
   * <pre>
   * 描述:n位随机数字.
   * 作者:ZhangYi
   * 时间:2015年1月22日 下午3:04:41
   * 参数：(参数列表)
   * &#64;param len 随机数长度
   * &#64;param flag 首位是否允许为0(true:首位大于0,false:首位任意数)
   * &#64;return
   * </pre>
   */
  public static String randomNumber(int len, boolean flag) {
    StringBuffer number = new StringBuffer(Integer.toString(new Random().nextInt(10)));
    if (flag) {
      while (number.toString().equals("0")) {
        number = new StringBuffer(Integer.toString(new Random().nextInt(10)));
      }
    }
    while (number.toString().length() < len) {
      number.append(Integer.toString(new Random().nextInt(10)));
    }
    return number.toString();
  }

  /**
   * <pre>
   * 描述:字符串填充0为固定长度,超出则截取.
   * 作者:ZhangYi
   * 时间:2015年11月17日 下午6:29:07
   * 参数：(参数列表)
   * &#64;param source 源字符串
   * &#64;param len 字符串长度
   * &#64;param flag 填充位置(true:左填充/右截取,false:右填充/左截取)
   * &#64;return
   * </pre>
   */
  public static String fillString(String source, int len, boolean flag) {
    if (source.length() < len) {
      while (source.length() < len) {
        if (flag) {
          source = "0" + source;
        } else {
          source += "0";
        }
      }
    } else {
      if (flag) {
        source = source.substring(source.length() - len);
      } else {
        source = source.substring(0, len);
      }
    }
    return source;
  }

  /**
   * Base64解密.
   * 
   * @param content -
   * @return -
   */
  public static String decryBase64(String content) {
    try {
      if (content.contains(" ") && !isEmptyStr(content)) {
        content = content.replaceAll(" ", "\\+");
      }
      return new String(new BASE64Decoder().decodeBuffer(content), ENCODING_UTF8);
    } catch (IOException ioe) {
      log.error("解密失败：", ioe);
      return content;
    }
  }

  /**
   * 字符串特殊字符过滤.
   * 
   * @param str -
   * @return -
   */
  public static String replaceStr(String str) {
    if (isEmptyStr(str)) {
      return str;
    } else {
      return str.replace("%", "%25").replace("+", "%2B").replace("?", "%3F").replace("#", "%23")
          .replace("&", "%26").replace("=", "%3D");
    }
  }

  /**
   * 
   * <pre>
   * 描述: 解析区域全名称.
   * 作者:wafer
   * 时间:2017年1月25日 上午11:20:27
   * 参数：(参数列表)
   * </pre>
   */
  public static String parsZoneFullName(String fullName) {
    String zoneFullName = "";
    String zoneName = "";
    if (!isEmptyStr(fullName)) {
      String[] name = fullName.split(StringUtil.DELIMITER_COMMA);
      if (null != name && name.length > 0) {
        for (int i = name.length - 1; i >= 1; i--) {
          if (name[i].contains("OU=")) {
            if (isEmptyStr(zoneName)) {
              zoneName = name[i];
            } else {
              zoneName += name[i];
            }
          }
        }
      }
      if (!isEmptyStr(zoneName)) {
        zoneFullName = zoneName.replace("OU=", "");
      }
    }
    return zoneFullName;
  }

  /**
   * 
   * <pre>
   * 描述: 解析区域全名称.
   * 作者:wafer
   * 时间:2017年1月25日 上午11:20:27
   * 参数：(参数列表)
   * </pre>
   */
  public static String parsZoneFullName2(String fullName) {
    String zoneFullName = "";
    String zoneName = "";
    if (!isEmptyStr(fullName)) {
      String[] name = fullName.split(StringUtil.DELIMITER_COMMA);
      if (null != name && name.length > 0) {
        for (int i = name.length - 1; i >= 1; i--) {
          if (name[i].contains("OU=")) {
            if (isEmptyStr(zoneName)) {
              zoneName = name[i];
            } else {
              zoneName += "/" + name[i];
            }
          }
        }
      }
      if (!isEmptyStr(zoneName)) {
        zoneFullName = zoneName.replace("OU=", "");
      }
    }
    return zoneFullName;
  }

  /**
   * 
   * <pre>
   * 描述:字符串排序.
   * 作者:ChenLei
   * 时间:2017年3月15日 上午11:00:43.
   * 参数：(参数列表)
   * </pre>
   * 
   * @param str 原始字符串
   * @param separator 分隔符
   */
  public static String sort(String str, String separator) {
    List<String> list = new ArrayList<String>();
    Collections.addAll(list, str.split(separator));
    Collections.sort(list);
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      buf.append(list.get(i)).append(separator);
    }
    String result = buf.toString();
    if (result.indexOf(separator) > -1) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  /**
   * 描述:根据一串数字经过一系列运算得出一个6位数字串（如果来源串相同，则多次生成结果也相同）.
   * 
   * @param resource 来源Long
   * @return 字符串
   */
  public static String encodeRandomNumber(int resource) {
    if (resource == 0) {
      return null;
    }
    String str = String.valueOf(resource);
    if (str.length() > 6) {
      str = String.valueOf(resource).substring(0, 6);
    } else {
      StringBuffer sb = new StringBuffer(str);
      for (int i = 0; i < 6 - String.valueOf(resource).length(); i++) {
        sb.append("0");
      }
      str = sb.toString();
    }
    StringBuffer result = new StringBuffer("");
    int[] intArr = stringToInts(str);
    for (int index : intArr) {
      result.append((index + 5) % 10);
    }
    return result.toString();
  }

  /**
   * <pre>
   * 描述:去掉回车符号.
   * 参数：(参数列表)
   * </pre>
   * 
   * @param string 字符串
   * @return string
   */
  public static String replaceEnter(String string) {
    if (string != null && string.length() > 0) {
      string = string.replaceAll("(\r|\n|\r\n|\n\r)", " ");
      string = string.replaceAll("\"", "\\\\" + "\"");
      string = string.replaceAll("\'", "\\\\" + "\'");
      return string;
    } else {
      return "";
    }
  }
  
  /**
   * string转int.
   */
  public static int[] stringToInts(String string) {
    int[] num = new int[string.length()];
    for (int i = 0; i < string.length(); i++) {
      num[i] = Integer.parseInt(string.substring(i, i + 1));
    }
    return num;
  }

  /**
   * 
   * <pre>
   * 描述: 主方法测试.
   * 作者:wafer
   * 时间:2017年2月15日 下午3:19:21
   * 参数：(参数列表)
   * </pre>
   */
  public static void main(String[] args) {
    /*
     * System.out.println(fillString("1001", 3, false)); System.out.println("13659285211[大陆]: " +
     * telMatches("13659285211")); System.out.println("+8613659285211[大陆]: " +
     * telMatches("+8613659285211")); System.out.println("+85293505959[香港]: " +
     * telMatches("+85293505959")); System.out.println("+85362954356[澳门]: " +
     * telMatches("+85362954356")); System.out.println("+8860932637679[台湾]: " +
     * telMatches("+886932637679")); System.out.println("029-86707016[西安]: " +
     * telMatches("029-86707016")); System.out.println("02986707016[西安]: " +
     * telMatches("02986707016")); System.out.println("825216[短号]: " + telMatches("825216"));
     * System.out.println(parsZoneFullName( "OU=会议室,OU=二级区域,OU=一级区域,DC=wafersystems,DC=com"));
     * System.out.println(randomHexNumber(8)); System.out.println(reverseString2("56789"));
     * System.out.println(arrayToString(new String[] {"12", "69", "fds"}, true));
     * System.out.println(arrayToString(new String[] {"12", "69", "fds"}, false));
     * System.out.println(extractionAssemble("123654ggg"));
     * System.out.println(sort("5;4;6;3;7;2;9;1", ";"));
     */
    // System.out.println(encodeRandomNumber(5234));
    String a = padLeft("000", "0", 3);
    System.out.println(a);
//    System.out.println(isMatchEmail("@com.com"));
  }

  /**
   * <pre>
   * 描述:判断是否存在
   * 作者:ChenLei
   * 时间:2017年10月18日 下午3:45:46
   * 参数：(参数列表).
   * </pre>
   * 
   * @param names -
   * @param name -
   */
  public static boolean isContains(String names, String name) {
    String[] nameArr = names.split(";");
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < nameArr.length; i++) {
      list.add(nameArr[i].trim());
    }
    if (list.contains(name)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * <pre>
   * 描述:反序列化
   * 作者:guoerpeng
   * 时间:2017年11月13日 下午4:09:17
   * 参数：(参数列表).
   * </pre>
   * 
   * @param byt -
   */
  public static Object unserizlize(final byte[] byt) {
    ObjectInputStream oii = null;
    ByteArrayInputStream bis = null;
    bis = new ByteArrayInputStream(byt);
    try {
      oii = new ObjectInputStream(bis);
      Object obj = oii.readObject();
      return obj;
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return null;
  }

  /**
   * 判断邮箱是否符合规范.
   * 
   * @param content -
   * @return -
   */
  public static boolean isMatchEmail(String content) {
    return isMatch(content,
        "^([a-z0-9A-Z_-]+[_-|\\.]?)+[_a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
  }

  /**
   * Title: isMatch. Description: 判断是否与给定的模式匹配
   * 
   * @param value 给定的字符串
   * @param pattern 给定的模式
   * @return boolean
   */
  public static synchronized boolean isMatch(String value, String pattern) {
    if (isEmptyStr(value)) {
      return false;
    } else {
      try {
        Pattern pt;
        if (REGEX_PATTERN.containsKey(pattern)) {
          pt = REGEX_PATTERN.get(pattern);
        } else {
          // 及时编译模式
          pt = compiler.compile(pattern);
          // 缓冲该模式
          REGEX_PATTERN.put(pattern, pt);
        }
        return matcher.matches(value, pt);
      } catch (Exception ex) {
        return false;
      }
    }
  }
//
//  /**.
//   * 描述：将汉字转换为全拼
//   *
//   * @param src 源字符串（汉语）
//   * @return 字符串（拼音）
//   */
//  public static String getPingYin(String src) {
//    char[] source = src.toCharArray();
//    String[] centre;
//    HanyuPinyinOutputFormat pinYinFormat = new HanyuPinyinOutputFormat();
//    pinYinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//    pinYinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//    pinYinFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
//    StringBuffer convert = new StringBuffer("");
//    try {
//      for (int i = 0; i < source.length; i++) {
//        // 判断是否为汉字字符
//        if (Character.toString(source[i]).matches("[\\u4E00" //
//            + "-\\u9FA5" //
//            + "]+")) {
//          centre = PinyinHelper.toHanyuPinyinStringArray(source[i], pinYinFormat);
//          convert.append(centre[0]);
//        } else {
//          convert.append(Character.toString(source[i]));
//        }
//      }
//      return convert.toString();
//    } catch (Exception e1) {
//      e1.printStackTrace();
//      log.error("转换拼音失败，原字符串为："+src);
//    }
//    return convert.toString();
//  }

  public static String padLeft(String str,String padStr,int length){
    StringBuffer newStr = new StringBuffer(str);
    int padLength = length - str.length();
    if(padLength>0){
      for(int i=0;i<padLength;i++){
        newStr.insert(0, padStr);
      }
    }
    return newStr.toString();
  }
  
  /**
   * <pre>
   * 描述： 数组匹配，计算arr1的所有元素是否都在arr2中
   * 作者：'WangSS'
   * 时间： 2018年9月25日上午11:29:23
   * </pre>
   * 
   * @param arr1
   * @param arr2
   * @return
   */
  public static boolean arrMatch(String[] arr1, String[] arr2) {
    if (null == arr1) {
      return true;
    }
    if (null == arr2) {
      return false;
    }
    if (arr1.length > arr2.length) {
      return false;
    }
    for (String str1 : arr1) {
      boolean exist = false;
      for (String str2 : arr2) {
        if (null != str1 && str1.equals(str2)) {
          exist = true;
          break;
        }
      }
      if (!exist)
        return false;
    }
    return true;
  }

}
