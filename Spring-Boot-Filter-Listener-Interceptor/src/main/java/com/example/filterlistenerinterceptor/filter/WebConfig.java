package com.example.filterlistenerinterceptor.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengzhiming on 2019/6/1
 */
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<CustomFilter> getFilterRegistrationBean() {
        FilterRegistrationBean<CustomFilter> registrationBean = new FilterRegistrationBean<>();
        //当过滤器有注入其他bean类时,可直接通过@bean的方式进行实体类过滤器,这样不可自动注入过滤器使用的其他bean类;
        //当然,若无其他bean需要获取时,可直接new CustomFilter(),也可使用getBean的方式;
        registrationBean.setFilter(new CustomFilter());
        //过滤器名称
        registrationBean.setName("customFilter");
        //拦截路径
        List<String> list = new ArrayList<>();
        list.add("/*");
        registrationBean.setUrlPatterns(list);
        //设置顺序
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
