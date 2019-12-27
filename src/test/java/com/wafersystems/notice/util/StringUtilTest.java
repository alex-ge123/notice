package com.wafersystems.notice.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author shennan
 * @date 2019/12/27 10:48
 */
public class StringUtilTest {

  @Test
  public void testIsEmptyStr()  {
    boolean result1 = StringUtil.isEmptyStr("");
    boolean result2 = StringUtil.isEmptyStr("aaa");
    Assert.assertEquals(true, result1);
    Assert.assertEquals(false, result2);
  }
}