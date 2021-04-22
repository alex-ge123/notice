package com.wafersystems.notice.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wafer
 */
@Data
@ToString
@Entity
@Table(name = "mail_microsoft_record")
public class MicrosoftRecordDTO implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * uuid
   */
  private String uuid;

  /**
   * 事件id
   */
  private String eventid;

  /**
   * 创建时间
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Date createtime;
}
