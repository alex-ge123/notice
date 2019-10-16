package com.wafersystems.notice.config.loader;

import com.wafersystems.notice.mail.model.MailTemplateDto;
import com.wafersystems.notice.mail.service.MailNoticeService;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

/**
 * @author shennanb
 * @date 2019/10/9 11:01
 */
@Component("mysqlMailTemplateLoader")
public class MysqlMailTemplateLoader implements TemplateLoader {

  @Autowired
  private MailNoticeService mailNoticeService;

  @Autowired
  private Configuration configuration;

  @Override
  public Object findTemplateSource(String name) {
    try {
      //freemarker 加载模板的时候会根据服务器语言环境查找多次，直到查找模板成功
      //如： 模板文件为test  服务器语言环境为zh_CN
      //则会匹配模板3次  1：test_zh_CN  2:test_zh  3:test
      //我们模板名称没有语言环境，下面代码去掉语言环境，让第一次就查找成功
//      Locale locale = configuration.getLocale();
//      String localeStr = "_" + locale.toString();
//      if (name.endsWith(localeStr)){
//        name = name.replace(localeStr,"");
//      }
      MailTemplateDto mailTemplateDto = mailNoticeService.getTempByName(name);
      return new StringTemplateSource(name, mailTemplateDto.getContent(), mailTemplateDto.getModtime().getTime());
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public long getLastModified(Object templateSource) {
    return ((StringTemplateSource) templateSource).lastModified;
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) {
    return new StringReader(((StringTemplateSource) templateSource).source);
  }

  @Override
  public void closeTemplateSource(Object templateSource) {
    //do nothing
  }

  @Data
  private static class StringTemplateSource {
    private final String name;
    private final String source;
    private final long lastModified;

    StringTemplateSource(String name, String source, long lastModified) {
      if (name == null) {
        throw new IllegalArgumentException("name == null");
      }
      if (source == null) {
        throw new IllegalArgumentException("source == null");
      }
      if (lastModified < -1L) {
        throw new IllegalArgumentException("lastModified < -1L");
      }
      this.name = name;
      this.source = source;
      this.lastModified = lastModified;
    }
  }
}
