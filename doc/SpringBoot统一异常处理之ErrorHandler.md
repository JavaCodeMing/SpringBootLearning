```
1.引入web依赖、commons-lang3依赖和Thymeleaf依赖
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency> <!--添加Thymeleaf依赖 -->
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.8.1</version>
    </dependency>
2.创建存储错误信息的Bean:
    public class ErrorInfo {
        // 发生时间
        private String time;
        // 访问路径
        private String url;
        // 错误类型
        private String error;
        // 错误的堆栈轨迹
        private String stackTrace;
        // 状态码
        private int statusCode;
        // 状态描述
        private String reasonPhrase;
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getStackTrace() { return stackTrace; }
        public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public String getReasonPhrase() { return reasonPhrase; }
        public void setReasonPhrase(String reasonPhrase) { this.reasonPhrase = reasonPhrase;}
    }
3.错误信息的构建类:
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Component
    public class ErrorInfoBuilder implements HandlerExceptionResolver, Ordered {
        // 错误KEY
        private final static String ERROR_NAME= "error";
        // 错误配置(ErrorConfiguration)
        private ErrorProperties errorProperties;
        public ErrorProperties getErrorProperties() { return errorProperties; }
        public void setErrorProperties(ErrorProperties errorProperties) {
            this.errorProperties = errorProperties;
        }
        // 错误构造器 (Constructor) 传递配置属性：server.xx -> server.error.xx
        public ErrorInfoBuilder(ServerProperties serverProperties) {
            this.errorProperties = serverProperties.getError();
        }
        // 构建错误信息(ErrorInfo)
        public ErrorInfo getErrorInfo(HttpServletRequest request){
            return getErrorInfo(request,getError(request));
        }
        // 构建错误信息(ErrorInfo)
        ErrorInfo getErrorInfo(HttpServletRequest request, Throwable error){
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setTime(LocalDateTime.now().toString());
            errorInfo.setUrl(request.getRequestURL().toString());
            errorInfo.setError(error.toString());
            errorInfo.setStackTrace(getStackTrace(error,isIncludeStackTrace(request)));
            errorInfo.setStatusCode(getHttpStatus(request).value());
            errorInfo.setReasonPhrase(getHttpStatus(request).getReasonPhrase());
            return errorInfo;
        }
        /**
        * 获取错误.(Error/Exception)
        * 获取方式：通过Request对象获取(Key="javax.servlet.error.exception").
        * @see DefaultErrorAttributes #addErrorDetails
        */
        private Throwable getError(HttpServletRequest request){
            // 根据HandlerExceptionResolver接口方法来获取错误.
            Throwable error = (Throwable) request.getAttribute(ERROR_NAME);
            // 根据Request对象获取错误
            if(error == null){
                error = (Throwable) request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
                String message = (String)request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE);
                if(StringUtils.isEmpty(message)){
                    HttpStatus httpStatus = getHttpStatus(request);
                    message = "Unknown Exception But "+ httpStatus.value()+" "+httpStatus.getReasonPhrase();
                }
                error = new Exception(message);
            }else {
                while (error instanceof ServletException && error.getCause() != null){
                    error = error.getCause();
                }
            }
            return error;
        }
        /**
        * 获取通信状态(HttpStatus)
        * @see AbstractErrorController #getStatus
        */
        private HttpStatus getHttpStatus(HttpServletRequest request){
            Integer statusCode = (Integer) request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE);
            try{
                return statusCode != null ? HttpStatus.valueOf(statusCode):HttpStatus.INTERNAL_SERVER_ERROR;
            }catch (Exception e){
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        /**
        * 获取堆栈轨迹(StackTrace)
        * @see DefaultErrorAttributes  #addStackTrace
        */
        private String getStackTrace(Throwable error, boolean flag){
            if(!flag){
                return "omitted";
            }
            StringWriter stackTrace = new StringWriter();
            error.printStackTrace(new PrintWriter(stackTrace));
            stackTrace.flush();
            return stackTrace.toString();
        }
        /**
        * 判断是否包含堆栈轨迹.(isIncludeStackTrace)
        * @see BasicErrorController #isIncludeStackTrace
        */
        private boolean isIncludeStackTrace(HttpServletRequest request){
            //读取错误配置(默认: server.error.include-stacktrace=NEVER)
            ErrorProperties.IncludeStacktrace includeStacktrace = errorProperties.getIncludeStacktrace();
            //情况1: 若includeStacktrace为ALWAYS
            if(includeStacktrace == ErrorProperties.IncludeStacktrace.ALWAYS){
                return true;
            }
            //情况2: 若请求参数含trace
            if(includeStacktrace == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM){
                String parameter = request.getParameter("trace");
                return parameter != null && !"false".equals(parameter.toLowerCase());
            }
            //情况3: 其他情况
            return false;
        }
        // 提供优先级或用于排序
        @Override
        public int getOrder() { return Ordered.HIGHEST_PRECEDENCE; }
        //发生异常会被前置处理器捕获到,然后交给HandlerExceptionResolver的该方法进行解析并分派处理
        @Override
        public ModelAndView resolveException(HttpServletRequest httpServletRequest, 
            HttpServletResponse httpServletResponse, Object handler, Exception e) {
            httpServletRequest.setAttribute(ERROR_NAME,e);
            return null;
        }
    }
4.编写统一异常处理类:
    //@ExceptionHandler是用来拦截系统运行时抛出的相应异常,其有效作用域是其所处的Controller
    //@ControllerAdvice可使控制器增强,声明一个拦截全局异常的@ExceptionHandler
    @ControllerAdvice
    public class GlobalErrorHandler {
        // 错误信息页
        private final static String DEFAULT_ERROR_VIEW = "error";
        // 错误信息构建器
        @Autowired
        private ErrorInfoBuilder errorInfoBuilder;
        //根据业务规则,统一处理异常。
        @ExceptionHandler(Exception.class)
        @ResponseBody
        public Object exceptionHandler(HttpServletRequest request,Throwable error){
            //1.若为AJAX请求,则返回异常信息(Json)
            if(isAjaxRequest(request)){
                return errorInfoBuilder.getErrorInfo(request,error);
            }
            //2.其余请求,则返回指定的异常信息页(View)
            return new ModelAndView(DEFAULT_ERROR_VIEW,"errorInfo",
                errorInfoBuilder.getErrorInfo(request,error));
        }
        private boolean isAjaxRequest(HttpServletRequest request){
            return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        }
    }
5.在\src\main\resources\templates目录下,创建测试异常页面error.html:
    <!-- 该页面的解析依赖thymeleaf -->
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>GlobalError</title>
    </head>
    <body>
        <h1>服务异常，请稍后再试！</h1>
        <div th:object="${errorInfo}">
            <h3 th:text="*{'发生时间：'+time}"></h3>
            <h3 th:text="*{'访问地址：'+url}"></h3>
            <h3 th:text="*{'问题类型：'+error}"></h3>
            <h3 th:text="*{'通信状态：'+statusCode+','+reasonPhrase}"></h3>
            <h3 th:text="*{'堆栈信息：'+stackTrace}"></h3>
        </div>
    </body>
    </html>
6.编写异常测试Controller:
    @RestController
    public class LearnController {
        // 随机抛异常
        private void randomException() throws Exception {
            //异常集合
            Exception[] exceptions = {
                    new NullPointerException(),
                    new ArrayIndexOutOfBoundsException(),
                    new NumberFormatException(),
                    new SQLException()
            };
            //发生概率
            double probabity = 0.75;
            if (Math.random() < probabity) {
                throw exceptions[(int) (Math.random() * exceptions.length)];
            }
            //情况2: 继续运行
        }
        // 模拟用户数据访问
        @GetMapping("/test")
        public List<String> index() throws Exception{
            randomException();
            return Arrays.asList("正常用户数据1!","正常用户数据2! 请按F5刷新!!");
        }
    }
7.访问测试: http://localhost:8080/test
```
