package com.codeming.starter;

/**
 * @author dengzhiming
 * @date 2020/3/5 23:14
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//web应用才生效
@ConditionalOnWebApplication
//让属性文件生效
@EnableConfigurationProperties(HelloProperties.class)
//声明是一个配置类
@Configuration
public class HelloServiceAutoConfiguration {
    @Autowired
    private HelloProperties helloProperties;
    @Bean
    public HelloService helloService(){
        HelloService helloService = new HelloService();
        helloService.setHelloProperties(helloProperties);
        return helloService;
    }
}
