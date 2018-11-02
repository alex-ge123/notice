package com.wafersystems.notice.mail.model;

import com.wafersystems.notice.util.StrUtil;
import lombok.Data;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.Locale;

@Data
public class TemContentVal implements Serializable {

  private static final long serialVersionUID = 1L;
  private String logo;
  private String value1;
  private String value2;
  private String value3;
  private String value4;
  private String value5;
  private String value6;
  private String value7;
  private String value8;
  private String value9;
  private String value10;
  private String value11;
  private String value12;
  private String value13;
  private String value14;
  private String value15;
  private String value16;
  private String value17;
  private String value18;
  private String value19;
  private String value20;
  private String value21;
  private String value22;
  private String value23;
  private String value24;
  private Locale locale;
  private transient ApplicationContext resource;
  private String imageDirectory;// 图片目录地址

  public String getValue1() {
    return StrUtil.regStr(value1);
  }

  public String getValue2() {
    return StrUtil.regStr(value2);
  }

  public String getValue3() {
    return StrUtil.regStr(value3);
  }

  public String getValue4() {
    return StrUtil.regStr(value4);
  }

  public String getValue5() {
    return StrUtil.regStr(value5);
  }

  public String getValue6() {
    return StrUtil.regStr(value6);
  }

  public String getValue7() {
    return StrUtil.regStr(value7);
  }

  public String getValue8() {
    return StrUtil.regStr(value8);
  }

  public String getValue9() {
    return StrUtil.regStr(value9);
  }

  public String getValue10() {
    return StrUtil.regStr(value10);
  }

  public String getValue11() {
    return StrUtil.regStr(value11);
  }

  public String getValue12() {
    return StrUtil.regStr(value12);
  }

  public String getValue13() {
    return StrUtil.regStr(value13);
  }

  public String getValue14() {
    return StrUtil.regStr(value14);
  }

  public String getValue15() {
    return StrUtil.regStr(value15);
  }

  public String getValue16() {
    return StrUtil.regStr(value16);
  }

  public String getValue18() {
    return StrUtil.regStr(value18);
  }
  
  public String getValue19() {
    return StrUtil.regStr(value19);
  }
  public String getValue20() {
    return StrUtil.regStr(value20);
  }
  public String getValue21() {
    return StrUtil.regStr(value21);
  }
  public String getValue22() {
    return StrUtil.regStr(value22);
  }
  public String getValue23() {
    return StrUtil.regStr(value23);
  }
  public String getValue24() {
    return StrUtil.regStr(value24);
  }

}
