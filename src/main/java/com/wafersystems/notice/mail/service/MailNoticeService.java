package com.wafersystems.notice.mail.service;

import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.util.ConfConstant;

/**
 * ClassName: TaskReportService Description:
 * 
 * @author Administrator
 */
public interface MailNoticeService {

  /**
   * Description: 邮件发送 author waferzy DateTime 2016-3-10 下午2:37:55.
   * 
   * @param subject 邮件主题
   * @param to 邮件接收方(多个之间用逗号隔开)
   * @param copyTo 邮件抄送(多个之间用逗号隔开)
   * @param type 邮件模版类型(使用VM模版或html格式邮件)
   * @param temple 邮件模版
   * @param con 邮件填充内容
   * @param count 邮件重发次数
   */
  void sendMail(String subject, String to, String copyTo, ConfConstant.TypeEnum type, String temple,
                TemContentVal con, Integer count) throws Exception;
}
