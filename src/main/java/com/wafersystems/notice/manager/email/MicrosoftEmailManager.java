package com.wafersystems.notice.manager.email;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.*;
import com.wafersystems.notice.constants.MailConstants;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.dto.BaseCheckDTO;
import com.wafersystems.virsical.common.core.exception.BusinessException;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 邮件接口实现
 *
 * @author wafer
 */
@Slf4j
@Service(MailConstants.MAIL_SERVER_TYPE_MICROSOFT)
public class MicrosoftEmailManager extends AbstractEmailManager {
  @Override
  public void send(MailBean mailBean, MailServerConf conf) throws Exception {
    final CompletableFuture<IAuthenticationResult> tokenFuture = getTokenFuture(conf);
    final Message message = generateMessage(mailBean);
    sendMail(conf, tokenFuture, message);
  }

  private void sendMail(MailServerConf conf, CompletableFuture<IAuthenticationResult> tokenFuture, Message message) throws InterruptedException, ExecutionException {
    // 发送邮件
    final HttpResponse execute = HttpRequest.post("https://graph.microsoft.com/v1.0/users/" + conf.getMicrosoftFrom() + "/sendMail")
      .body(JSON.toJSONString(message))
      .header("Content-type", "application/json")
      .header("Authorization", "Bearer " + tokenFuture.get().accessToken())
      .header("Accept", "application/json")
      .timeout(10000)
      .execute();
    // 发送结果
    final int status = execute.getStatus();
    if (200 > status || 300 < status) {
      log.error("Microsoft邮件发送失败，status={},body={}", status, execute.body());
      throw new BusinessException("Microsoft邮件发送失败");
    }
  }

  private Message generateMessage(MailBean mailBean) throws Exception {
    // 构造邮件体
    Message message = new Message();
    message.subject = mailBean.getSubject();
    ItemBody body = new ItemBody();
    body.contentType = BodyType.HTML;
    body.content = getMessage(mailBean);
    message.body = body;

    // 收件人
    LinkedList<Recipient> toRecipientsList = new LinkedList<>();
    final String[] toEmails = mailBean.getToEmails().split(CommonConstants.COMMA);
    for (String toEmail : toEmails) {
      Recipient toRecipients = new Recipient();
      EmailAddress emailAddress = new EmailAddress();
      emailAddress.address = toEmail;
      toRecipients.emailAddress = emailAddress;
      toRecipientsList.add(toRecipients);
    }
    message.toRecipients = toRecipientsList;

    // 抄送人
    LinkedList<Recipient> ccRecipientsList = new LinkedList<>();
    final String[] copyTos = mailBean.getCopyTo().split(CommonConstants.COMMA);
    for (String copyTo : copyTos) {
      Recipient ccRecipients = new Recipient();
      EmailAddress emailAddress1 = new EmailAddress();
      emailAddress1.address = copyTo;
      ccRecipients.emailAddress = emailAddress1;
      ccRecipientsList.add(ccRecipients);
    }
    message.ccRecipients = ccRecipientsList;

    return message;
  }

  private CompletableFuture<IAuthenticationResult> getTokenFuture(MailServerConf conf) throws MalformedURLException, InterruptedException, ExecutionException {
    // 获取token
    ConfidentialClientApplication app = ConfidentialClientApplication.builder(
      conf.getClientId(),
      ClientCredentialFactory.createFromSecret(conf.getClientSecret()))
      .authority("https://login.microsoftonline.com/" + conf.getOfficeTenantId() + "/")
      .build();

    // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
    // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
    ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
      Collections.singleton(conf.getScope()))
      .build();

    return app.acquireToken(clientCredentialParam);
  }

  @Override
  public R check(BaseCheckDTO dto, Integer tenantId, MailServerConf mailServerConf) {
    try {
      final CompletableFuture<IAuthenticationResult> tokenFuture = getTokenFuture(mailServerConf);
      // 构造邮件体
      Message message = new Message();
      message.subject = "邮件配置测试";
      ItemBody body = new ItemBody();
      body.contentType = BodyType.TEXT;
      body.content = "该邮件用于验证邮件配置，收到该邮件，则您的邮箱配置正确！";
      message.body = body;

      // 收件人
      LinkedList<Recipient> toRecipientsList = new LinkedList<>();
      Recipient toRecipients = new Recipient();
      EmailAddress emailAddress = new EmailAddress();
      emailAddress.address = dto.getToMail();
      toRecipients.emailAddress = emailAddress;
      toRecipientsList.add(toRecipients);
      message.toRecipients = toRecipientsList;

      // 发送邮件
      sendMail(mailServerConf, tokenFuture, message);

      sendCheckLog(null, null, CommonConstants.SUCCESS, tenantId);
      return R.ok();
    } catch (Exception e) {
      log.warn("邮箱检测失败！", e);
      StringWriter stringWriter = new StringWriter();
      e.printStackTrace(new PrintWriter(stringWriter));
      sendCheckLog(e.getMessage(), stringWriter.toString(), CommonConstants.FAIL, tenantId);
      return R.builder().code(CommonConstants.FAIL).msg(e.getMessage()).data(stringWriter.toString()).build();
    }
  }
}
