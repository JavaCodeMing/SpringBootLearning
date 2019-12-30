package com.example.swaggerdoc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                //将api的元信息设置为包含在json resourcelisting响应中
                .apiInfo(buildApiInfo())
                //设置ip和端口,或者域名
                //.host("127.0.0.1:8080")
                //启动用于api选择的生成器
                .select()
                //.apis(RequestHandlerSelectors.any())
                //要扫描的API(Controller)基础包
                .apis(RequestHandlerSelectors.basePackage("com.example.swaggerdoc.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo buildApiInfo() {

        return new ApiInfoBuilder()
                //文档标题
                .title("用户信息API文档")
                //文档描述
                .description("这里除了查看接口功能外，还提供了调试测试功能")
                //联系人
                .contact(new Contact("倾尽天下","https://github.com/JavaCodeMing","1206291365@qq.com"))
                //版本号
                .version("1.0")
                //更新此API的许可证信息
                //.license("")
                //更新此API的许可证Url
                //.licenseUrl("")
                //更新服务条款URL
                //.termsOfServiceUrl("")
                .build();

    }
}