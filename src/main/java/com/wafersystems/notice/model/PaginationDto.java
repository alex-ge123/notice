package com.wafersystems.notice.model;

import com.wafersystems.notice.constants.ConfConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: PaginationDTO Description:分页对象.
 *
 * @author gelin
 */
@SuppressWarnings("PMD.UnusedPrivateField")
@Data
public class PaginationDto<T> implements Serializable {


  private List<T> rows;

  /**
   * 每页多少条
   */
  private int limit = Integer.parseInt(ConfConstant.DATA_DEFAULT_LENGTH);

  /**
   * 当前页码
   */
  private int page;

  /**
   * 数据总数
   */
  private int records;

  /**
   * 可显示的页数
   */
  private int total;

  /**
   * Description:.
   *
   * @return int
   */
  public int getTotal() {
    total = records / limit;
    if (records % limit != 0) {
      total++;
    }
    return total;
  }
}
