```text
1.获取HTTPS证书:
    [1]正常情况下 HTTPS 证书需要从证书授权中心获得,这样获得的证书才具有公信力,也会被各种浏览器客户端所认可;
    [2]常见的证书品牌: Symantec,GeoTrustm,TrustAsia,Symantec 等;
    [3]可使用Java自带的keytool生成HTTPS证书:
        (1)keytool工具使用说明: 
            D:\>keytool
            密钥和证书管理工具
            命令:
             -certreq            生成证书请求
             -changealias        更改条目的别名
             -delete             删除条目
             -exportcert         导出证书
             -genkeypair         生成密钥对
             -genseckey          生成密钥
             -gencert            根据证书请求生成证书
             -importcert         导入证书或证书链
             -importpass         导入口令
             -importkeystore     从其他密钥库导入一个或所有条目
             -keypasswd          更改条目的密钥口令
             -list               列出密钥库中的条目
             -printcert          打印证书内容
             -printcertreq       打印证书请求的内容
             -printcrl           打印 CRL 文件的内容
             -storepasswd        更改密钥库的存储口令
        (2)keytool -genkeypair命令说明:
            D:\>keytool -genkeypair --help
            keytool -genkeypair [OPTION]...
            生成密钥对
            选项:
             -alias <alias>                  要处理的条目的别名
             -keyalg <keyalg>                密钥算法名称
             -keysize <keysize>              密钥位大小
             -sigalg <sigalg>                签名算法名称
             -destalias <destalias>          目标别名
             -dname <dname>                  唯一判别名
             -startdate <startdate>          证书有效期开始日期/时间
             -ext <value>                    X.509 扩展
             -validity <valDays>             有效天数
             -keypass <arg>                  密钥口令
             -keystore <keystore>            密钥库名称
             -storepass <arg>                密钥库口令
             -storetype <storetype>          密钥库类型
             -providername <providername>    提供方名称
             -providerclass <providerclass>  提供方类名
             -providerarg <arg>              提供方参数
             -providerpath <pathlist>        提供方类路径
             -v                              详细输出
             -protected                      通过受保护的机制的口令
    [4]使用keytool生成自签名证书: (输完命令可一路Enter(即非必填)到最后确认输入一个"是")
        D:\>keytool -genkeypair -alias springboot_https -keypass 123456 -keyalg RSA 
            -keysize 1024 -validity 365 -keystore d:/springboot_https.keystore -storepass 123456
        您的名字与姓氏是什么?
        [Unknown]:  
        您的组织单位名称是什么?
        [Unknown]:  
        您的组织名称是什么?
        [Unknown]:  
        您所在的城市或区域名称是什么?
        [Unknown]:  
        您所在的省/市/自治区名称是什么?
        [Unknown]:  
        该单位的双字母国家/地区代码是什么?
        [Unknown]:  
        CN=null, OU=null, O=null, L=null, ST=null, C=null是否正确?
        [否]:  是
        D:\>  
    [5]查看生成的证书信息: (密钥库类型是JKS,后面用到)
        D:\>keytool -list -keystore springboot_https.keystore
        输入密钥库口令:
        
        密钥库类型: JKS
        密钥库提供方: SUN
        
        您的密钥库包含 1 个条目
        
        springboot_https, 2020-3-5, PrivateKeyEntry,
        证书指纹 (SHA1): F1:EF:DF:ED:0A:8F:DA:13:B5:86:65:4E:FE:A1:87:1F:03:51:06:4D
        D:\>
    (自己生成的 HTTPS 证书只能用来自己测试,真正用于网络上时,浏览器会无法显示证书信息)
2.配置 HTTPS 证书:
    [1]将生成的HTTPS证书文件拷贝到Springboot项目的src/main/resource目录下:
    [2]application.yml文件中配置HTTPS相关信息:
        # 配置 HTTPS 相关信息
        server:
          port: 443
          # 为了后面的配置使用,暂时无用
          http-port: 80
          ssl:
            enabled: true
            # 证书文件
            key-store: classpath:springboot_https.keystore
            # 密码
            key-password: 123456
            # 密钥库类型
            key-store-type: JKS
            key-alias: springboot_https
4.测试HTTPS证书:
    [1]编写controller:
        @RestController
        public class HttpsController {
            @GetMapping(value = "/hello")
            public String hello() {
                return "Hello HTTPS";
            }
        }
    [2]启动项目,访问: https://localhost/hello
    (由于是自己生成的证书,会提示不安全,继续访问即可)
5.HTTP跳转HTTPS:
    [1]HTTPS已经可以访问了,但是HTTP却不能访问,大多数情况下在启用了HTTPS之后,
        都会希望HTTP的请求会自动跳转到HTTPS;
    [2]编写配置类把HTTP请求直接转发到HTTPS:
        @Configuration
        public class Http2Https {
            @Value("${server.port}")
            private int sslPort;
            @Value("${server.http-port}")
            private int httpPort;
            @Bean
            public TomcatServletWebServerFactory servletContainerFactory() {
                TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
                    @Override
                    protected void postProcessContext(Context context) {
                        SecurityConstraint securityConstraint = new SecurityConstraint();
                        securityConstraint.setUserConstraint("CONFIDENTIAL");
                        SecurityCollection collection = new SecurityCollection();
                        collection.addPattern("/*");
                        securityConstraint.addCollection(collection);
                        context.addConstraint(securityConstraint);
                    }
                };
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setScheme("http");
                connector.setPort(httpPort);
                connector.setRedirectPort(sslPort);
                tomcat.addAdditionalTomcatConnectors(connector);
                return tomcat;
            }
        }
    [3]再次启动之后,使用 http://localhost/hello 访问会自动跳转到 https://localhost/hello.
6.免费证书申请: (可以在腾讯云上免费申请)
    [1]免费版 DV SSL 证书申请: https://cloud.tencent.com/document/product/400/35224
    [2]安装证书:
        (1)Apache 服务器证书安装:
            https://cloud.tencent.com/document/product/400/35243
        (2)Nginx 服务器证书安装:
            https://cloud.tencent.com/document/product/400/35244
        (3)Tomcat 服务器证书安装:
            https://cloud.tencent.com/document/product/400/35224
        (4)Windows IIS 服务器证书安装:
            https://cloud.tencent.com/document/product/400/35225
```