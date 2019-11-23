package com.example.xssfilter.config;

import com.example.xssfilter.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/5/25
 */
@Configuration
public class XssFilterConfig {
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        Map<String, String> initParameter = new HashMap<>();
        initParameter.put("excludes", "/favicon.ico,/img/*,/js/*,/css/*");
        initParameter.put("isIncludeRichText", "true");
        filterRegistrationBean.setInitParameters(initParameter);
        return filterRegistrationBean;
    }
}
