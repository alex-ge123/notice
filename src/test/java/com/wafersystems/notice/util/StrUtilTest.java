package com.wafersystems.notice.util;

import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2019/12/27 10:50
 */
public class StrUtilTest {

  @Test
  public void testIsEmptyStr() {
    StrUtil.isEmptyStr(null);
  }

  @Test
  public void testIsNullObject() {
    StrUtil.isNullObject(null);
  }

  @Test
  public void testRegStr() {
    StrUtil.regStr("aa+-=fsdf#4534542341234");
  }

  @Test
  public void testGetFileNameNoEx() {
    StrUtil.getFileNameNoEx("aba.txt");
  }
}