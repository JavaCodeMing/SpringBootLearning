```
跨站脚本攻击(Cross Site Scripting),为了不和层叠样式表(Cascading Style Sheets: CSS)的缩写混淆,
    故将跨站脚本攻击缩写为XSS;恶意攻击者往Web页面里插入恶意Script代码,当用户浏览该页之时,嵌入
    其中Web里面的Script代码会被执行,从而达到恶意攻击用户的目的;
1.引入web依赖、jsoup依赖和commons-lang3依赖(工具类依赖):
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.11.3</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.8.1</version>
    </dependency>
2.编写Xss过滤工具:
    public class JsoupUtil {
        //根据jsoup内置常用白名单，构建白名单对象
        private static final Whitelist WHITELIST = Whitelist.relaxed();
        //配置过滤化参数,不对代码进行格式化
        private static final Document.OutputSettings OUTPUT_SETTINGS = 
            new Document.OutputSettings().prettyPrint(false);
        //设置自定义的标签和属性
        static {
            /*
            * addTags(String...): 添加标签
            * addAttributes(String, String...): 往指定标签中添加属性
            * addEnforcedAttribute(String, String, String): 往指定标签指定属性添加值
            * addProtocols(java.lang.String, String, String...): 往指定标签指定属性添加协议
            * preserveRelativeLinks(): 是否保留元素的URL属性中的相对链接，或将它们转换为绝对链接
            *                          ,默认为false;为false时将会把baseUri和元素的URL属性拼接起来;
            */
            WHITELIST.addAttributes(":all", "style");
            WHITELIST.preserveRelativeLinks(true);
        }
        public static String clean(String content) {
            /*
            * baseUri ,非空
            * 如果baseUri为空字符串或者不符合Http://xx类似的协议开头,属性中的URL链接将会被删除
            * 如果WHITELIST.preserveRelativeLinks(false), 会将baseUri和属性中的URL链接进行拼接
            */
            return Jsoup.clean(content, "", WHITELIST, OUTPUT_SETTINGS);
        }
    }
    Jsoup内置了几种常见的白名单:
        白名单对象                              标签                               说明
        none                                     无                          只保留标签内文本内容
        simpleText                          b,em,i,strong,u                  简单的文本标签
        basic                        a,b,blockquote,br,cite,code,dd,         基本使用的标签
                                     dl,dt,em,i,li,ol,p,pre,q,small,span,
                                     strike,strong,sub,sup,u,ul
        basicWithImages              basic的基础上添加了img标签及img标签的    基本使用的加上img标签 
                                     src,align,alt,height,width,title属性
        relaxed                      a,b,blockquote,br,caption,cite,         在basicWithImages的基础
                                     code,col,colgroup,dd,div,dl,dt,         上又增加了一部分部分标签
                                     em,h1,h2,h3,h4,h5,h6,i,img,li,ol,p,
                                     pre,q,small,span,strike,strong,sub,sup,
                                     table,tbody,td,tfoot,th,thead,tr,u,ul
3.编写XssHttpServletRequestWrapper用来过滤HTTP请求中参数包含的恶意字符:
    public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest orgRequest = null;
        private boolean isIncludeRichText = false;
        XssHttpServletRequestWrapper(HttpServletRequest request, boolean isIncludeRichText) {
            super(request);
            orgRequest = request;
            this.isIncludeRichText = isIncludeRichText;
        }
        /**
        * 覆盖getHeader方法,将参数名和参数值都做xss过滤。
        * 如果需要获得原始的值,则通过super.getHeaders(name)来获取
        * getHeaderNames 也可能需要覆盖
        */
        @Override
        public String getHeader(String name) {
            name = JsoupUtil.clean(name);
            String value = super.getHeader(name);
            if (StringUtils.isNotBlank(value)) {
                value = JsoupUtil.clean(value);
            }
            return value;
        }
        /**
        * 覆盖getParameter方法,将参数名和参数值都做xss过滤。
        * 如果需要获得原始的值,则通过super.getParameterValues(name)来获取
        * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
        */
        @Override
        public String getParameter(String name) {
            if (("content".equals(name) || name.endsWith("WithHtml")) && !isIncludeRichText) {
                return super.getParameter(name);
            }
            name = JsoupUtil.clean(name);
            String value = super.getParameter(name);
            if (StringUtils.isNotBlank(value)) {
                value = JsoupUtil.clean(value);
            }
            return value;
        }
        /**
        * 覆盖getParameterValues方法
        * 如果需要获得原始的值,则通过super.getParameterValues(name)来获取
        */
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (ArrayUtils.isNotEmpty(values)) {
                values = Stream.of(values).map(JsoupUtil::clean).toArray(String[]::new);
            }
            return values;
        }
        //获取最原始的request
        private HttpServletRequest getOrgRequest(){
            return orgRequest;
        }
        //获取原始的request的静态方法
        public static HttpServletRequest getOrgRequest(HttpServletRequest request){
            if(request instanceof XssHttpServletRequestWrapper){
                return ((XssHttpServletRequestWrapper)request).getOrgRequest();
            }
            return request;
        }
    }
4.编写请求拦截器来过滤掉Xss攻击:
    public class XssFilter implements Filter {
        // 是否过滤富文本内容
        private boolean IS_INCLUDE_RICH_TEXT = false;
        // 不过滤的请求
        private List<String> excludes = new ArrayList<>();
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            String isIncludeRichText = filterConfig.getInitParameter("isIncludeRichText");
            if(StringUtils.isNotBlank(isIncludeRichText)){
                IS_INCLUDE_RICH_TEXT = BooleanUtils.toBoolean(isIncludeRichText);
            }
            String temp = filterConfig.getInitParameter("excludes");
            if(StringUtils.isNotBlank(temp)){
                String[] url = temp.split(",");
                excludes.addAll(Arrays.asList(url));
            }
        }
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
            FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            if(handleExcludeURL(request,response)){
                filterChain.doFilter(servletRequest,servletResponse);
            }else {
                XssHttpServletRequestWrapper xssHttpServletRequestWrapper = 
                    new XssHttpServletRequestWrapper(request,IS_INCLUDE_RICH_TEXT);
                filterChain.doFilter(xssHttpServletRequestWrapper,servletResponse);
            }
        }
        private boolean handleExcludeURL(HttpServletRequest request,HttpServletResponse response){
            if(excludes == null || excludes.isEmpty()){
                return false;
            }
            String url = request.getServletPath();
            for(String pattern: excludes){
                Pattern compile = Pattern.compile("^" + pattern);
                Matcher matcher = compile.matcher(url);
                if(matcher.find()){ return true; }
            }
            return false;
        }
        @Override
        public void destroy() {}
    }
5.对定义的XssFilter进行注册配置:
    @Configuration
    public class XssFilterConfig {
        @Bean
        public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
            FilterRegistrationBean<XssFilter> filterRegistrationBean = 
                new FilterRegistrationBean<>();
            filterRegistrationBean.setFilter(new XssFilter());
            filterRegistrationBean.setOrder(1);
            filterRegistrationBean.setEnabled(true);
            filterRegistrationBean.addUrlPatterns("/*");
            Map<String,String> initParameter = new HashMap<>();
            initParameter.put("excludes","/favicon.ico,/img/*,/js/*,/css/*");
            initParameter.put("isIncludeRichText","true");
            filterRegistrationBean.setInitParameters(initParameter);
            return filterRegistrationBean;
        }
    }
6.编写测试的Controller:
    @Controller
    public class LearnController {
        @PostMapping("/testParam")
        @ResponseBody
        public String testParam(@RequestParam String param){
            return param;
        }
    }
7.测试: 
    请求地址: http://localhost:8080/testParam
    参数: param: <a href="http://www.baidu.com/" onclick="alert("模拟XSS攻击");">sss</a>
            //<a href="http://www.baidu.com/">sss</a>
          param: <p><a href="www.test.xhtml/favicon.ico">test</a>
            //<p><a>test</a></p>
```
