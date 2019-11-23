```
SpringBootAdmin(SBA)是一款基于Actuator开发的开源软件:https://github.com/codecentric/spring-boot-admin,
    以图形化界面的方式展示Spring Boot应用的配置信息、Beans信息、环境属性、线程信息、JVM状况等;
官方文档: http://codecentric.github.io/spring-boot-admin/2.1.4
[1]搭建SBA服务端:
    搭建一个SBA服务端(Server),其他被监控的SpringBoot应用作为客户端(Client),
        客户端通过HTTP的方式将自己注册到服务端,以供服务端进行监控服务
    (1)创建一个Spring Boot项目作为Server端,并为之引入SBA Server依赖:
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server</artifactId>
            <version>2.1.4</version>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server-ui</artifactId>
            <version>2.1.4</version>
        </dependency>
    (2)开启Admin监控: 在Server端的入口类上添加注解@EnableAdminServer来启用Admin监控功能
        @SpringBootApplication
        @EnableAdminServer
        public class SpringBootAdminServerApplication {
            public static void main(String[] args) {
                SpringApplication.run(SpringBootAdminServerApplication.class, args);
            }
        }
    (3)在yml中对Server端进行相关配置:
        server:
            servlet:
                context-path: /admin-server    #访问Server监控界面的接口
            port: 8080                         #Server端启动端口
    (4)启动项目,测试Server端: http://localhost:8080/admin-server
        (SpringBootAdmin的Server监控界面正常显示,则SBA服务端搭建成功(此时监控列表为空))
[2]搭建SBA客户端:
    (1)创建一个Spring Boot项目作为Client端,并为之引入SBA Client依赖:
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
            <version>2.1.4</version>
        </dependency>
    (2)在yml中对Client端进行相关配置:
        server:
            port: 8081                        #Client端启动端口
        management:
            endpoint:
                shutdown:
                    enabled: true             #允许使用http请求关闭应用
                health:
                    show-details: always      #显示health详情
            endpoints:
                web:
                    exposure:
                        include: '*'          #暴露所有接口
        spring:
            boot:
                admin:
                    client:
                        instance:
                            service-base-url: http://localhost:8081/  #当前Client端IP
                        url: http://localhost:8080/admin-server       #服务端监控地址
    (3)添加Client端的一些基本信息到监控中的yml配置:
        info:
            app:
                name: "@project.name@"
                description: "@project.description@"
                version: "@project.version@"
                spring-boot-version: "@project.parent.version@"
    (4)Client端启动后自动注册到Server端的监控中,可在Server端监控界面查看Client端的情况
[3]为SBA服务端添加邮件预警: (默认情况下对于client应用启动或者停止的时候会触发预警)
    (1)SBA服务端引入邮件依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
    (2)在SBA服务端的yml中配置邮件预警:
        spring:
            mail:
                host: smtp.qq.com           #发送邮件服务器
                username: xxxxxx@qq.com     #QQ邮箱
                #授权码参考:https://service.mail.qq.com/cgi-bin/help?subtype=1&&no=1001256&&id=28
                password: xxxxxx            #客户端授权码
                protocol: smtp              #发送邮件协议
                default-encoding: utf-8     #编码格式
                properties:
                    mail:
                        smtp:
                            auth: true        #开启认证
                            port: 465         #端口号465(开启SSL时)或587(不开启SSL时)
                            ssl:
                                enable: true      #开启SSL(使用587端口时无法连接QQ邮件服务器)
                            starttls:
                                enable: true      #需要TLS认证 保证发送邮件安全验证
                                required: true
            boot:
                admin:
                    notify:
                        mail:
                            from: xxxxxx@qq.com   #发送提醒邮件的邮箱
                            to: xxxxxx@163.com    #接受提醒邮件的邮箱
[3]SBA Server配置:
    (1)spring.boot.admin.context-path: Admin Server 保留的静态访问和API的前缀
    (2)spring.boot.admin.monitor.period: 更新应用信息的频率,单位毫秒
    (3)spring.boot.admin.monitor.status-lifetime: 被监控的应用信息的过期时间,单位毫秒
[4]SBA Client配置:
    (1)spring.boot.admin.client.enabled: 默认开启
    (2)spring.boot.admin.url: admin server 的地址列表,此设置会触发自动配置,必须
    (3)spring.boot.admin.api-path: 注册到admin server端点的 Http-path
    (4)spring.boot.admin.username:
    (5)spring.boot.admin.password:
    (6)spring.boot.admin.period: 重试注册的间隔时间
    (7)spring.boot.admin.auto-registration: 应用启动后自动执行周期性的注册任务
    (8)spring.boot.admin.auto-deregistration: 当应用关闭时,自动取消注册
    (9)spring.boot.admin.client.health-url:
    (10)spring.boot.admin.client.management-url:
    (11)spring.boot.admin.client.service-url:
    (12)spring.boot.admin.client.name: 注册时的名字
    (13)spring.boot.admin.client.prefer-ip: 
[5]邮件配置:
    (1)spring.boot.admin.notify.mail.enabled: 默认启用
    (2)spring.boot.admin.notify.mail.ignore-changes: 需要忽略的状态改变通知,逗号分隔
    (3)spring.boot.admin.notify.mail.to: 接收通知的邮箱地址,逗号分隔
    (4)spring.boot.admin.notify.mail.cc: 抄送
    (5)spring.boot.admin.notify.mail.from: 发送人
    (6)spring.boot.admin.notify.mail.subject: 主题
    (7)spring.boot.admin.notify.mail.text: 内容
```
