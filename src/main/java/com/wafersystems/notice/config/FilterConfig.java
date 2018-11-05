package com.wafersystems.notice.config;

import com.wafersystems.notice.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置过滤器
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean myFiletr(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new AuthFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
