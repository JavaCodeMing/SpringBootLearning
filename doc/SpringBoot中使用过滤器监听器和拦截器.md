```
1.过滤器(Filter): 是Servlet的的一个实用技术了;可通过过滤器,对请求进行拦截,比如读取session判
    断用户是否登录、判断访问的请求URL是否有访问权限(黑白名单)等;主要还是可对请求进行预处理;
    [1]@WebFilter + @Component: 
        //@WebFilter时Servlet3.0新增的注解,原先实现过滤器,需要在web.xml中进行配置;
        //而现在通过此@WebFilter注解对Filter进行配置,通过@Component注解交给IOC管理注册;
        @Component
        @WebFilter(filterName = "customFilter",urlPatterns = "/*")
        public class CustomFilter implements Filter {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                System.out.println("Filter 初始化");
            }
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                FilterChain filterChain) throws IOException, ServletException {
                System.out.println("Filter 开始执行过滤");
                filterChain.doFilter(servletRequest,servletResponse);
                System.out.println("Filter 执行过滤结束");
            }
            @Override
            public void destroy() {
                System.out.println("Filter 销毁");
            }
        }
    [2]@WebFilter + @ServletComponentScan:
        (1)编写自定义Filter类:
            //@ServletComponentScan添加到入口类上可使Servlet、Filter、Listener分别通过
            //@WebServlet、@WebFilter、@WebListener 注解自动注册,无需其他代码;
            @WebFilter(filterName = "customFilter",urlPatterns = "/*")
            public class CustomFilter implements Filter {
                @Override
                public void init(FilterConfig filterConfig) throws ServletException {
                    System.out.println("Filter 初始化");
                }
                @Override
                public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                    FilterChain filterChain) throws IOException, ServletException {
                    System.out.println("Filter 开始执行过滤");
                    filterChain.doFilter(servletRequest,servletResponse);
                    System.out.println("Filter 执行过滤结束");
                }
                @Override
                public void destroy() {
                    System.out.println("Filter 销毁");
                }
            }
        (2)修改入口类:
            @SpringBootApplication
            @ServletComponentScan
            public class SpringBootFilterListenerInterceptorApplication {
                public static void main(String[] args) {
                    SpringApplication
                        .run(SpringBootFilterListenerInterceptorApplication.class, args);
                }
            }
    [3]@Configuration + @Bean + FilterRegistrationBean:
        (1)编写自定义Filter类:
            public class CustomFilter implements Filter {
                @Override
                public void init(FilterConfig filterConfig) throws ServletException {
                    System.out.println("Filter 初始化");
                }
                @Override
                public void doFilter(ServletRequest servletRequest, ServletResponse 
                    servletResponse,FilterChain filterChain) throws IOException,ServletException {
                    System.out.println("Filter 开始执行过滤");
                    filterChain.doFilter(servletRequest,servletResponse);
                    System.out.println("Filter 执行过滤结束");
                }
                @Override
                public void destroy() {
                    System.out.println("Filter 销毁");
                }
            }
        (2)编写配置类:
            @Configuration
            public class WebConfig {
                @Bean
                public FilterRegistrationBean<CustomFilter> getFilterRegistrationBean(){
                    FilterRegistrationBean<CustomFilter> registrationBean = 
                        new FilterRegistrationBean<>();
                    //当过滤器有注入其他bean类时,可直接通过@bean的方式进行实体类过滤器,这样
                    //不可自动注入过滤器使用的其他bean类;当然,若无其他bean需要获取时,
                    //可直接new CustomFilter(),也可使用getBean的方式;
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
    [4]多个过滤器指定执行顺序:
        (1)通过过滤器的名字,进行顺序的约定,比如LogFilter和AuthFilter,此时AuthFilter就会比
            LogFilter先执行,因为首字母A比L前面;
        (2)通过@Order指定执行顺序,值越小,越先执行;
        (3)注册多个FilterRegistrationBean,按设置的Order属性值执行,值越小,越先执行;
2.监听器(Listener): 是servlet规范中定义的一种特殊类;用于监听servletContext、HttpSession和
    servletRequest等域对象的创建和销毁事件;监听域对象的属性发生修改的事件;用于在事件发生前、
    发生后做一些必要的处理;一般用于获取在线人数等业务需求;
    [1]@WebListener + @Component:
        @Component
        @WebListener
        public class CustomListener implements ServletRequestListener {
            @Override
            public void requestDestroyed(ServletRequestEvent sre) {
                System.out.println("Listener 销毁");
            }
            @Override
            public void requestInitialized(ServletRequestEvent sre) {
                System.out.println("Listener 初始化");
            }
        }
    [2]@WebListener + @ServletComponentScan:
        (1)编写自定义Listener类:
            public class CustomListener implements ServletRequestListener {
                @Override
                public void requestDestroyed(ServletRequestEvent sre) {
                    System.out.println("Listener 销毁");
                }
                @Override
                public void requestInitialized(ServletRequestEvent sre) {
                    System.out.println("Listener 初始化");
                }
            }
        (2)修改入口类:
            @SpringBootApplication
            @ServletComponentScan
            public class SpringBootFilterListenerInterceptorApplication {
                public static void main(String[] args) {
                    SpringApplication
                        .run(SpringBootFilterListenerInterceptorApplication.class, args);
                }
            }
3.拦截器(HandlerInterceptor): 类似于Servlet开发中的过滤器Filter,用于处理器进行预处理和后处理;
    一般用于日志记录、权限检查、性能监控等场景中;
    [1]WebMvcConfigurationSupport + @Configuration: 适用于不需要返回逻辑视图的情况
        继承WebMvcConfigurationSupport会使Spring Boot的WebMvc自动配置失效
        (WebMvcAutoConfiguration自动化配置),导致无法视图解析器无法解析并返回到对应的视图;
        (1)编写自定义HandlerInterceptor类:
            public class CustomHandlerInterceptor implements HandlerInterceptor {
                @Override
                public boolean preHandle(HttpServletRequest request,HttpServletResponse 
                    response, Object handler) throws Exception {
                    System.out.println("HandlerInterceptor 拦截处理前的操作");
                    // 返回false,则请求中断
                    return true;
                }
                // postHandle只有当被拦截的方法没有抛出异常成功时才会处理
                @Override
                public void postHandle(HttpServletRequest request,HttpServletResponse response,
                    Object handler, ModelAndView modelAndView) throws Exception {
                    System.out.println("HandlerInterceptor 进行拦截处理");
                }
                // afterCompletion方法无论被拦截的方法抛出异常与否都会执行
                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse 
                    response, Object handler, Exception ex) throws Exception {
                    System.out.println("HandlerInterceptor 拦截处理后的操作");
                }
            }
        (2)编写配置类:
            @Configuration
            public class InterceptorConf extends WebMvcConfigurationSupport {
                @Override
                protected void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new CustomHandlerInterceptor());
                }
            }
    [2]WebMvcConfigurer + @Configuration: 适用于返回逻辑视图的情况
        springboot1.x中通过继承WebMvcConfigurerAdapter间接实现WebMvcConfigurer,升级到springBoot2.x
        后使用了java8的特性default方法,无需再要适配器类,所以直接实现 WebMvcConfigurer接口即可;
        按逻辑来说这种方式才是正规的做法;
        (1)编写自定义HandlerInterceptor类:
            public class CustomHandlerInterceptor implements HandlerInterceptor {
                @Override
                public boolean preHandle(HttpServletRequest request,HttpServletResponse 
                    response, Object handler) throws Exception {
                    System.out.println("HandlerInterceptor 拦截处理前的操作");
                    // 返回false,则请求中断
                    return true;
                }
                // postHandle只有当被拦截的方法没有抛出异常成功时才会处理
                @Override
                public void postHandle(HttpServletRequest request,HttpServletResponse response,
                    Object handler, ModelAndView modelAndView) throws Exception {
                    System.out.println("HandlerInterceptor 进行拦截处理");
                }
                // afterCompletion方法无论被拦截的方法抛出异常与否都会执行
                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse 
                    response, Object handler, Exception ex) throws Exception {
                    System.out.println("HandlerInterceptor 拦截处理后的操作");
                }
            }
        (2)编写配置类:
            @Configuration
            public class InterceptorConf1 implements WebMvcConfigurer {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new CustomHandlerInterceptor());
                }
            }
4.编写测试Controller:
    @RestController
    public class LearnController {
        @GetMapping("/test")
        public String test(){
            System.out.println("controller 执行");
            return "controller return";
        }
    }
5.测试结果总结: (请求链路说明)
    -> Filter init -> ExecutorService -> Listener requestInitialized -> DispatcherServlet ->
    Filter doFilter -> Interceptor preHandle -> Controller -> Interceptor postHandle ->
    Interceptor afterCompletion -> Listener requestDestroyed -> ExecutorService -> Filter destroy
```
