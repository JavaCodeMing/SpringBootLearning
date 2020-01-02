package com.example.server.config;

import com.example.server.service.DemoService;
import com.example.server.service.impl.DemoServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * Created by dengzhiming on 2019/8/4
 */
@Configuration
public class CxfConfig {

    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        // Servlet注册类,参数1为Servlet对象,参数2为请求到Servlet的地址
        return new ServletRegistrationBean<>(new CXFServlet(), "/demo/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    // 将提供的服务交给IOC管理,并提供给EndpointImpl发布
    @Bean
    public DemoService demoService() {
        return new DemoServiceImpl();
    }

    // 发布多个服务时,创建多个,并使用@Qualifier指定不同的名称
    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), demoService());
        endpoint.publish("/api");
        return endpoint;

    }
}
