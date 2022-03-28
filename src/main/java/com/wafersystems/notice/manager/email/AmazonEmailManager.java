package com.wafersystems.notice.manager.email;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.common.base.Strings;
import com.wafersystems.notice.config.MailProperties;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Smtp邮件工具类
 *
 * @author wafer
 */
@Slf4j
@Service(MailConstants.MAIL_SERVER_TYPE_AMAZON)
public class AmazonEmailManager extends AbstractEmailManager {

  @Autowired
  private MailProperties mailProperties;

  /**
   * 发送邮件
   *
   * @param mailBean 邮件对象
   * @throws Exception Exception
   */
  @Override
  public void send(MailBean mailBean, MailServerConf mailServerConf) throws Exception {
    String accessKey = mailProperties.getAccessKey();
    String secretKey = mailProperties.getSecretKey();
    if (!Strings.isNullOrEmpty(accessKey)) {
      Destination destination = new Destination().withToAddresses(mailBean.getToEmails());
      Content subj = new Content().withData(mailBean.getSubject());
      MessageBody bodyMessage = MessageBody.getMessageBodyFromText(getMessage(mailBean));
      Content textBody = new Content().withData(bodyMessage.toString());
      log.info("aws send mail content is {}", bodyMessage.toString());
      Body body = new Body().withHtml(textBody);

      Message message = new Message().withSubject(subj).withBody(body);
      SendEmailRequest req = new SendEmailRequest().withSource(mailServerConf.getName()
        + " <" + mailServerConf.getFrom() + ">")
        .withDestination(destination).withMessage(message);

      AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
        .withRegion(Regions.US_EAST_1)
        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
        .build();
      try {
        client.sendEmail(req);
      } finally {
        client.shutdown();
      }
    } else {
      throw new IllegalStateException("SES Mail provider wasn't configured well.");
    }
  }

  @Override
  public R check(BaseCheckDTO dto, Integer tenantId, MailServerConf mailServerConf) {
    return null;
  }


/*  public static void main(String[] args) {
    makeSend("DanielChoi@swireproperties.com", "威思客", "该邮件用于验证邮件配置，收到该邮件，则您的邮箱配置正确！");
  }*/
}
