package com.wafersystems.notice.mail.service.impl;

import com.wafersystems.notice.mail.model.MailBean;
import com.wafersystems.notice.mail.model.TemContentVal;
import com.wafersystems.notice.mail.service.MailNoticeService;
import com.wafersystems.notice.util.ConfConstant;
import com.wafersystems.notice.util.EmailUtil;
import com.wafersystems.notice.util.ParamConstant;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: TaskReportServiceImpl Description: .
 * 
 * @author Administrator
 */
@Log4j
@Service
public class MailNoticeServiceImpl implements MailNoticeService {

  @Autowired
  private EmailUtil mailUtil;

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
  public void sendMail(String subject, String to, String copyTo, ConfConstant.TypeEnum type,
      String temple, TemContentVal con, Integer count) throws Exception {
    log.debug("开始发送邮件。。。。。。");
    // 创建邮件
    MailBean mailBean = new MailBean();
    mailBean.setSubject(subject);
    mailBean.setToEmails(to);
    mailBean.setCopyTo(copyTo);
    mailBean.setType(type);
    mailBean.setTemplate(temple);
    mailBean.setTemVal(con);
    // 发送邮件
    try {
      mailUtil.send(mailBean);
    } catch (Exception exception) {
      count++;
      if (count < ParamConstant.getDEFAULT_REPEAT_COUNT()) {
        log.debug("主题[" + subject + "],发往[" + to + "]的邮件第" + count + "次重发......");
        this.sendMail(subject, to, copyTo, type, temple, con, count);
      } else {
        log.error("邮件发送失败：", exception);
        throw exception;
      }
    }
  }
}
