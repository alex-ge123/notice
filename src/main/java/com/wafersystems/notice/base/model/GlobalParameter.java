package com.wafersystems.notice.base.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/5/17 15:11 Company:
 * wafersystems
 * @author wafer
 */
@SuppressWarnings("PMD.UnusedPrivateField")
@Data
@Entity
@Table(name = "ntc_parameter")
public class GlobalParameter implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "param_key")
  private String paramKey;
  @Column(name = "param_value")
  private String paramValue;
  @Column(name = "param_desc")
  private String paramDesc;
}
