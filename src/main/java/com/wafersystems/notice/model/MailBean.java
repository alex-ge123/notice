package com.wafersystems.notice.model;

import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * MailBean
 *
 * @author wafer
 */
@Data
@Builder
public class MailBean implements Serializable {

  private String uuid;
  private String routerKey;
  private String toEmails;
  private String copyTo;
  private String subject;

  /**
   * 邮件数据(html格式)
   */
  private Map data;

  /**
   * 邮件数据(模版形式)
   */
  private MailDTO mailDTO;

  /**
   * 邮件传入模板
   */
  private String template;

  /**
   * 使用模版类型
   */
  private ConfConstant.TypeEnum type;
}
