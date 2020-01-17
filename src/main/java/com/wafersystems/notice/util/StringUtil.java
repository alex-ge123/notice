package com.wafersystems.notice.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

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
 *
 * @author wafer
 */
@Slf4j
public class StringUtil {

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
    return StrUtil.isEmpty(str);
  }
}
