package com.wafersystems.notice.weather.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("PMD.UnusedPrivateField")
@Data
@AllArgsConstructor
@Entity
@Table(name = "ntc_weather")
public class Weather implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String city;
  @Column(length = 10000)
  private String info;
  private Long createTime;
  /**
   * 类型 INFO(天气信息)|PM(空气质量)
   */
  @JSONField(serialize = false)
  private String type;
  /**
   * 空气质量信息
   */
  @Transient
  private String pm;

  /**
   *构造函数. 
   */
  public Weather(String city, String info, Long createTime, String type) {
    this.city = city;
    this.info = info;
    this.createTime = createTime;
    this.type = type;
  }
}
