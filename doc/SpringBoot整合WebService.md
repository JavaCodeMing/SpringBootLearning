```
1.在Spring-Boot-WebService目录下新建Module: WebService-Server
    [1]引入web-services依赖,cxf相关依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web-services</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-frontend-jaxws</artifactId>
            <version>3.3.4</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-transports-http</artifactId>
            <version>3.3.4</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    [2]编写服务端配置类:
        @Configuration
        public class CxfConfig {
            @Bean(name = "cxfServlet")
            public ServletRegistrationBean<CXFServlet> cxfServlet(){
                // Servlet注册类,参数1为Servlet对象,参数2为请求到Servlet的地址
                return new ServletRegistrationBean<>(new CXFServlet(),"/demo/*");
            }
            @Bean(name = Bus.DEFAULT_BUS_ID)
            public SpringBus springBus(){
                return new SpringBus();
            }
            // 将提供的服务交给IOC管理,并提供给EndpointImpl发布
            @Bean
            public DemoService demoService() {
                return new DemoServiceImpl();
            }
            // 发布多个服务时,创建多个此类方法,并使用@Qualifier指定不同的名称
            @Bean
            public Endpoint endpoint(){
                EndpointImpl endpoint = new EndpointImpl(springBus(), demoService());
                endpoint.publish("/api");
                return endpoint;
            }
        }
    [3]编写服务端提供服务的接口:
        // name: Web Service的名称;targetNamespace: 指定名称空间,一般使用接口实现类的包名的反缀
        @WebService(name = "DemoService",targetNamespace = "http://impl.service.server.example.com")
        public interface DemoService {
            String sayHello(String user);
        }
    [4]编写服务端提供服务的接口实现类:
        // serviceName: 对外发布的服务名; 
	// targetNamespace: 指定名称空间,一般使用接口实现类的包名的反缀
        // endpointInterface:服务接口的全类名
        @WebService(serviceName = "DemoService"
                ,targetNamespace = "http://impl.service.server.example.com"
                ,endpointInterface = "com.example.server.service.DemoService")
        public class DemoServiceImpl implements DemoService {
            @Override
            public String sayHello(String user) {
                return user + ",现在的时间: "+ new Date();
            }
        }
2.在Spring-Boot-WebService目录下新建Module: WebService-Client
    [1]引入web依赖,web-services依赖,cxf相关依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web-services</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-frontend-jaxws</artifactId>
            <version>3.3.4</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-transports-http</artifactId>
            <version>3.3.4</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    [2]修改application.properties文件设置项目启动端口:
        server.port=8081
    [3]编写Controller测试WebService服务:
        @RestController
        public class TestController {
            @GetMapping("/test")
            public void test() throws Exception {
                // 创建动态客户端
                JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
		// 根据服务端的配置拼接而得
                Client client = factory.createClient("http://localhost:8080/demo/api?wsdl");
                // 需要密码的情况需要加上用户名和密码
                // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME,PASS_WORD));
                HTTPConduit conduit = (HTTPConduit) client.getConduit();
                HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                httpClientPolicy.setConnectionTimeout(2000);  //连接超时
                httpClientPolicy.setAllowChunking(false);     //取消块编码
                httpClientPolicy.setReceiveTimeout(120000);   //响应超时
                conduit.setClient(httpClientPolicy);
                //client.getOutInterceptors().addAll(interceptors);//设置拦截器
                try{
                    Object[] objects ;
                    // invoke("方法名",参数1,参数2,参数3....);
                    objects = client.invoke("sayHello", "qiqi");
                    System.out.println("返回数据:" + objects[0]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    [4]在Controller中测试网上公开发布的WebService服务:
        (1)可用的一些WebService服务地址: http://www.webxml.com.cn/zh_cn/index.aspx
        (2)以天气预报服务的getWeatherbyCityName接口为例:
            (http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx)
        (3)编写Controller:
            @RestController
            public class TestController {
                @GetMapping("/testWeather")
                public void testWeather(){
                    String weatherInfo = getWeather("北京");
                    System.out.println(weatherInfo);
                }
                // 从接口文档中获取SOAP的请求头,并替换其中的标志符号为用户输入的城市
		// (http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx?op=getWeatherbyCityName)
                private static String getSoapRequest(String city) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                            .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                            .append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ")
                            .append("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">")
                            .append("<soap:Body> <getWeatherbyCityName xmlns=\"http://WebXml.com.cn/\">")
                            .append("<theCityName>")
                            .append(city)
                            .append("</theCityName> </getWeatherbyCityName>")
                            .append("</soap:Body></soap:Envelope>");
                    return sb.toString();
                }
                // 通过接口文档的请求头来构建SOAP请求,发送SOAP请求给服务器端,并返回流
                private static InputStream getSoapInputStream(String city) throws Exception {
                    try {
                        String soap = getSoapRequest(city);
                        // 通过请求的服务地址(即为Endpoint)构建URL对象,并使用URL对象开启连接
                        URL url = new URL("http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx");
                        URLConnection conn = url.openConnection();
                        conn.setUseCaches(false);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        // 为连接设置请求头属性(服务接口文档中请求头信息)
                        conn.setRequestProperty("Content-Length", Integer.toString(soap.length()));
                        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                        conn.setRequestProperty("SOAPAction", "http://WebXml.com.cn/getWeatherbyCityName");
                        // 将请求的XML信息写入连接的输出流
                        OutputStream os = conn.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                        osw.write(soap);
                        osw.flush();
                        osw.close();
                        // 获取连接中请求得到的输入流
                        return conn.getInputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                // 对服务器端返回的XML进行解析(请求响应的XML信息在服务接口说明中)
                private static String getWeather(String city) {
                    try {
                        Document doc;
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        InputStream is = getSoapInputStream(city);
                        assert is != null;
                        doc = db.parse(is);
                        NodeList nl = doc.getElementsByTagName("string");
                        StringBuffer sb = new StringBuffer();
                        for (int count = 0; count < nl.getLength(); count++) {
                            Node n = nl.item(count);
                            if(n.getFirstChild().getNodeValue().equals("查询结果为空！")) {
                                sb = new StringBuffer(" ");
                                break ;
                            }
                            sb.append(n.getFirstChild().getNodeValue()).append(" \n");
                        }
                        is.close();
                        return sb.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
```
