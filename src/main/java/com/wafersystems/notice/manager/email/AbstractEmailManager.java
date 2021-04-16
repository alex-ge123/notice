package com.wafersystems.notice.manager.email;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.config.FreemarkerMacroMessage;
import com.wafersystems.notice.config.loader.MysqlMailTemplateLoader;
import com.wafersystems.notice.constants.ConfConstant;
import com.wafersystems.notice.model.MailBean;
import com.wafersystems.notice.model.MailServerConf;
import com.wafersystems.virsical.common.core.constant.NoticeMqConstants;
import com.wafersystems.virsical.common.core.constant.enums.MsgActionEnum;
import com.wafersystems.virsical.common.core.constant.enums.MsgTypeEnum;
import com.wafersystems.virsical.common.core.dto.MailDTO;
import com.wafersystems.virsical.common.core.dto.MailResultDTO;
import com.wafersystems.virsical.common.core.dto.MessageDTO;
import freemarker.template.Configuration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件工具类
 *
 * @author wafer
 */
@Slf4j
@Component
public abstract class AbstractEmailManager {

  @Setter
  private VelocityEngine velocityEngine;

  @Autowired
  private Configuration configuration;

  @Autowired
  private MysqlMailTemplateLoader mysqlMailTemplateLoader;

  @Autowired
  private FreemarkerMacroMessage messageService;

  @Autowired
  private AmqpTemplate rabbitTemplate;

  /**
   * 发送邮件
   *
   * @param mailBean       邮件对象
   * @param mailServerConf mailServerConf
   * @throws Exception Exception
   */
  public abstract void send(MailBean mailBean, MailServerConf mailServerConf) throws Exception;

  /**
   * 模板解析
   *
   * @param mailBean mailBean
   * @return mailBean
   */
  public String getMessage(MailBean mailBean) throws Exception {
    VelocityContext context;
    try (StringWriter writer = new StringWriter();) {
      if (ConfConstant.TypeEnum.VM.equals(mailBean.getType())) {
        log.debug("使用模版" + mailBean.getTemplate());
        context = new VelocityContext();
        context.put("TemVal", mailBean.getMailDTO());
        Template temple = velocityEngine.getTemplate(mailBean.getTemplate(), "UTF-8");
        temple.merge(context, writer);
        return writer.toString();
      } else if (ConfConstant.TypeEnum.FM.equals(mailBean.getType())) {
        //freemarker
        log.debug("使用模版" + mailBean.getTemplate());
        //freemarker 配置模板加载器
        configuration.setTemplateLoader(mysqlMailTemplateLoader);
        //配置共享变量
        configuration.setSharedVariable("loccalMessage", messageService);

        final Map<String, Object> objectMap = this.attributeToMap(mailBean);
        //加载模板
        freemarker.template.Template template = configuration.getTemplate(mailBean.getTemplate(), mailBean.getMailDTO().getLocale());
        //模板渲染
        final String contextStr = FreeMarkerTemplateUtils.processTemplateIntoString(template, objectMap);
        //是否使用基础模板
        if (!mailBean.getMailDTO().isUseBaseTemplate()) {
          return contextStr;
        }
        //加载基础模板
        freemarker.template.Template baseTemplate = configuration.getTemplate("baseTemplate", mailBean.getMailDTO().getLocale());
        objectMap.put("productTemplateContent", contextStr);
        //渲染基础模板
        return FreeMarkerTemplateUtils.processTemplateIntoString(baseTemplate, objectMap);
      } else {
        log.debug("使用html模版" + mailBean.getTemplate());
        context = new VelocityContext(mailBean.getData());
        velocityEngine.evaluate(context, writer, "", mailBean.getTemplate());
        return writer.toString();
      }
    } catch (VelocityException ex) {
      log.error(" VelocityException : " + mailBean.getSubject(), ex);
      throw ex;
    }
  }

  /**
   * 将mailDto中所有参数转至data（map）
   *
   * @param mailBean mailBean
   * @return Map<String, Object>
   */
  private Map<String, Object> attributeToMap(MailBean mailBean) {
    int dtoValueCount = 50;
    final MailDTO mailDTO = mailBean.getMailDTO();
    Map<String, Object> data = mailDTO.getData();
    if (ObjectUtil.isNull(data)) {
      data = new HashMap<>(dtoValueCount);
    }
    final Method[] declaredMethods = mailDTO.getClass().getDeclaredMethods();
    for (Method method : declaredMethods) {
      final String name = method.getName();
      try {
        if (method.getReturnType().getName().contains("String")
          && StrUtil.startWith(name, "get")
          && !StrUtil.equals("getData", name)) {
          String value = (String) method.invoke(mailDTO);
          if (StrUtil.isNotBlank(value)) {
            data.put(StrUtil.removePreAndLowerFirst(name, "get"), value);
          }
        }
      } catch (Exception e) {
        log.warn("反射获取值[{}]失败", name);
      }
    }
    final HashMap<String, Object> resultMap = new HashMap<>();
    resultMap.put("locale", mailDTO.getLocale());
    resultMap.putAll(data);
    return resultMap;
  }

  /**
   * 邮件发送结果通知
   *
   * @param uuid      uuid
   * @param routerKey 路由键
   * @param result    邮件发送结果
   */
  public void sendResult(String uuid, String routerKey, boolean result) {
    if (StrUtil.isNotBlank(uuid) && StrUtil.isNotBlank(routerKey)) {
      MessageDTO dto = new MessageDTO(MsgTypeEnum.ONE.name(), MsgActionEnum.SHOW.name(), new MailResultDTO(uuid, result));
      rabbitTemplate.convertAndSend(NoticeMqConstants.EXCHANGE_DIRECT_NOTICE_RESULT_MAIL, routerKey, JSON.toJSONString(dto));
      log.debug("发送邮件发送结果uuid={}，result={}", uuid, result);
    }
  }
}
