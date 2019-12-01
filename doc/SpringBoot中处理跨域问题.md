```
HTML5中新增的跨域资源访问(Cross-Origin Resource Sharing)特性可以让我们在开发后端系统的时候
决定资源是否允许被跨域访问;跨域是指域名不同或者端口不同或者协议不同;
跨域请求就是请求另一个与当前域名或端口或协议不同的地址;Spring从4.2版本开始提供了跨域的支持,开箱即用;
1.模拟跨域:
    [1]引入web依赖和模板引擎依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
    [2]编写controller:
        @Controller
        public class TestController {
            @RequestMapping("index")
            public String index () { return "index"; }
            @RequestMapping("hello")
            @ResponseBody
            public String hello(){ return "hello"; }
        }
    [3]在resources/templates下编写测试页面index.html:
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>跨域测试</title>
            <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
        </head>
        <body>
        <div id="hello"></div>
        </body>
        <script>
            $(function () {
                $.get("http://test.cross.com:8080/hello", function(data) {
                    $("#hello").text(data);
                })
            })
        </script>
        </html>
    [4]编辑本地hosts文件,添加域名映射:
        #IP            Hosts
        127.0.0.1    test.cross.com
    [5]访问测试: http://localhost:8080/index
        (访问未成功显示hello,且在浏览器开发者模式的控制台打印错误信息;即出现了跨域问题;)
2.使用注解驱动解决跨域问题:
    Spring 4.2后提供了@CrossOrigin注解,该注解可以标注于方法或者类上;
    [1]@CrossOrigin注解的属性:
        属性             含义
        value            指定所支持域的集合,*表示所有域都支持,默认值为*;
                         这些值对应HTTP请求头中的Access-Control-Allow-Origin;
        origins          同value
        allowedHeaders   允许请求头中的header,默认都支持
        exposedHeaders   响应头中允许访问的header,默认为空
        methods          支持请求的方法,如GET,POST,PUT等,默认和Controller中的方法上标注的一致;
        allowCredentials 是否允许cookie随请求发送，使用时必须指定具体的域
        maxAge           预请求的结果的有效期,默认30分钟
    [2]改造ontroller:
        @Controller
        public class TestController {
            @RequestMapping("index")
            public String index () { return "index"; }
            @RequestMapping("hello")
            @ResponseBody
            @CrossOrigin(value = "*")
            public String hello(){ return "hello"; }
        }
    [3]访问测试: http://localhost:8080/index
3.使用接口编程方式解决跨域问题: 
    [1]编写配置类,实现WebMvcConfigurer,重写addCorsMappings默认实现
        @Configuration
        public class WebConfigurer implements WebMvcConfigurer {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET");
            }
        }
        (表示允许所有请求支持跨域访问,并且不限定域,但是支持持GET方法)
    [2]去掉@CrossOrigin注解,重启并访问测试: http://localhost:8080/index 
4.使用过滤器解决跨域问题:
    @Configuration
    public class CorsConfig {
        @Bean
        public FilterRegistrationBean corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOrigin("*");
            source.registerCorsConfiguration("/**", config);
            FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
            bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return bean;
        }
    }
5.Actuator跨域: 
    若项目里集成了Actuator相关功能,其暴露的接口也支持跨域,只需在配置文件中添加如下配置即可:
    # Whether credentials are supported. When not set, credentials are not supported.
    management.endpoints.web.cors.allow-credentials= 
    # Comma-separated list of headers to allow in a request. '*' allows all headers.
    management.endpoints.web.cors.allowed-headers= 
    # Comma-separated list of methods to allow. '*' allows all methods. When not set, defaults to GET.
    management.endpoints.web.cors.allowed-methods= 
    # Comma-separated list of origins to allow. '*' allows all origins.When not set,CORS support is disabled.
    management.endpoints.web.cors.allowed-origins= 
    # Comma-separated list of headers to include in a response.
    management.endpoints.web.cors.exposed-headers= 
    # How long the response from a pre-flight request can be cached by clients. 
    # If a duration suffix is not specified, seconds will be used.
    management.endpoints.web.cors.max-age=1800s 
```
