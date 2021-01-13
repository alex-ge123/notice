package com.wafersystems.notice.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/5/17 15:11 Company:
 * wafersystems
 *
 * @author wafer
 */
@Data
public class ParameterDTO implements Serializable {
  private Long id;
  @NotNull
  @Size(max = 250)
  private String paramKey;

  @NotNull
  @Size(max = 250)
  private String paramValue;

  private String paramDesc;
  private String lang;
  private String type;
}
