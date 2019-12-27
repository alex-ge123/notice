package com.wafersystems.notice.util;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author shennan
 * @date 2019/12/27 10:37
 */
public class DateUtilTest {

  @Test
  public void testFormatDateTime() {
    Date date = DateUtil.formatDateTime("1574804581000");
    Assert.assertEquals(String.valueOf(date.getTime()), "1574804581000");
  }
}