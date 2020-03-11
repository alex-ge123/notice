package com.wafersystems.notice.base.model;

import lombok.Data;

import javax.validation.constraints.Max;
import java.io.Serializable;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/5/17 15:11 Company:
 * wafersystems
 *
 * @author wafer
 */
@Data
public class ParameterDTO implements Serializable {

  private String paramKey;

  @Max(value = 250)
  private String paramValue;
  private String lang;
}
