package com.example.autoconfig.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dengzhiming
 * @date 2020/02/03
 */
@Configuration
public class HelloWorldConfiguration {
    @Bean
    public String hello() {
        return "hello world";
    }
}
