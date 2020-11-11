package com.wafersystems.notice.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 模板状态调整
 *
 * @author wafer
 */
@Data
@ToString
public class TemplateStateUpdateDTO implements Serializable {

  /**
   * 状态 0：正常 1：禁用
   */
  private int state;

  /**
   * 是否全部修改
   */
  private boolean updateAll;

  /**
   * id列表
   */
  private String id;
}
