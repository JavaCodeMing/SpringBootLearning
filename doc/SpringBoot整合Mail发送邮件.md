```
1.相关名词解释:
    POP3: Post Office Protocol 3的简称,即邮局协议的第3个版本,它规定怎样将个人
        计算机连接到Internet的邮件服务器和下载电子邮件的电子协议;它是因特网
        电子邮件的第一个离线协议标准,POP3允许用户从服务器上把邮件存储到本地
        主机(即自己的计算机)上,同时删除保存在邮件服务器上的邮件,而POP3服务器
        则是遵循POP3协议的接收邮件服务器,用来接收电子邮件的;
    SMTP: 全称是“Simple Mail Transfer Protocol”,即简单邮件传输协议;它是一组
        用于从源地址到目的地址传输邮件的规范,通过它来控制邮件的中转方式;SMTP
        协议属于 TCP/IP 协议簇,它帮助每台计算机在发送或中转信件时找到下一个
        目的地;SMTP 服务器就是遵循 SMTP 协议的发送邮件服务器;
        简单地说就是,要求必须在提供了账户名和密码之后才可以登录 SMTP 服务器;
    IMAP: 全称是Internet Mail Access Protocol,即交互式邮件存取协议,它是跟POP3
        类似邮件访问标准协议之一;不同的是,开启了IMAP后,您在电子邮件客户端收取
        的邮件仍然保留在服务器上,同时在客户端上的操作都会反馈到服务器上,如:
        删除邮件,标记已读等,服务器上的邮件也会做相应的动作;所以无论从浏览器
        登录邮箱或者客户端软件登录邮箱,看到的邮件以及状态都是一致的;
    IMAP和POP3有什么区别:
        POP3协议允许电子邮件客户端下载服务器上的邮件,但在客户端的操作(如移动
            邮件、标记已读等),不会反馈到服务器上;
        IMAP提供webmail与电子邮件客户端之间的双向通信,客户端的操作都会反馈到
            服务器上,对邮件进行的操作,服务器上的邮件也会做相应的动作;
        IMAP为用户带来更为便捷和可靠的体验;POP3更易丢失邮件或多次下载相同的邮件;
    免费邮箱客户端授权码:
        邮箱客户端授权码是为了避免您的邮箱密码被盗后,盗号者通过客户端登录邮箱
            而独特设计的安防功能;
        可以针对邮箱客户端设置唯一随机授权码,使用此授权码代替邮箱密码登录邮箱;
2.引入mail依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
3.在application.yml中添加邮件相关的配置: 
    [1]QQ邮箱配置: application-qq.yml(已测试成功)
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
                                enable: true  #开启SSL(使用587端口时无法连接QQ邮件服务器)
                        starttls:
                            enable: true      #需要TLS认证 保证发送邮件安全验证
                            required: true
    [2]网易系(126/163/yeah)邮箱配置: application-126/163/yeah.yml(已测试成功)
        spring:
            mail:
                host: smtp.163.com            #发送邮件服务器(smtp.126/163/yeah.com)
                username: xxx@163.com         #126/163/yeah邮箱(xx@126/163/yeah.com)
                #授权码参考:http://help.mail.163.com/faq.do?m=list&categoryID=197
                password: xxxxxxxx              #客户端授权码
                protocol: smtp
                default-encoding: utf-8         #编码格式
                properties:
                    mail:
                        smtp:
                            auth: true           #开启认证
                            port: 994            #465或者994
                            starttls:
                                enable: true     #需要TLS认证 保证发送邮件安全验证
                                required: true
                            ssl:
                                enable: true    #开启SSL
    [3]在application.yml中通过spring.profiles.active属性来选择使用哪套配置:
        spring:
            profiles:
                active: qq
4.发送简单的邮件:
    [1]编写Controller:
        @RestController
        @RequestMapping("/email")
        public class EmailController {
            @Autowired
            private JavaMailSender javaMailSender;
            @Value("${spring.mail.username}")
            private String from;
            @RequestMapping("sendSimpleEmail")
            public String sendSimpleEmail(){
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(from);
                    message.setTo("xxxxxx@163.com");                    // 接收地址
                    message.setSubject("一封简单的邮件");               // 标题
                    message.setText("使用Spring Boot发送简单邮件。");   // 内容
                    javaMailSender.send(message);
                    return "发送成功！";
                }catch (Exception e){
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }
    [2]访问测试: http://localhost:8080/email/sendSimpleEmail
5.发送HTML格式的邮件:
    [1]编写Controller:
        @RestController
        @RequestMapping("/email")
        public class EmailController {
            @Autowired
            private JavaMailSender javaMailSender;
            @Value("${spring.mail.username}")
            private String from;
            @RequestMapping("sendHtmlEmail")
            public String sendHtmlEmail(){
                MimeMessage message = null;
                try {
                    message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message,true);
                    helper.setFrom(from);
                    helper.setTo("xxxxxx@163.com");
                    helper.setSubject("一封HTML格式的邮件");
                    // 带HTML格式的内容
                    StringBuffer buffer = new StringBuffer("<p style='color:#42b983'>使用Spring Boot发送HTML格式邮件。</p>");
                    helper.setText(buffer.toString(),true);
                    javaMailSender.send(message);
                    return "发送成功！";
                }catch (Exception e){
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }
    [2]访问测试: http://localhost:8080/email/sendHtmlEmail
6.发送带附件的邮件:
    [1]编写Controller:
        @RestController
        @RequestMapping("/email")
        public class EmailController {
            @Autowired
            private JavaMailSender javaMailSender;
            @Value("${spring.mail.username}")
            private String from;
            @RequestMapping("sendAttachmentsMail")
            public String sendAttachmentsMail(){
                MimeMessage message = null;
                try {
                    message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message,true);
                    helper.setFrom(from);
                    helper.setTo("xxxxxx@163.com");
                    helper.setSubject("一封带附件的邮件");
                    helper.setText("详情参见附件内容！");
                    // 传入附件
                    FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/file/项目文档.docx"));
                    helper.addAttachment("项目文档.docx",file);
                    javaMailSender.send(message);
                    return "发送成功！";
                }catch (Exception e){
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }
    [2]访问测试: http://localhost:8080/email/sendAttachmentsMail    
7.发送带静态资源的邮件:
    [1]编写Controller:
        @RestController
        @RequestMapping("/email")
        public class EmailController {
            @Autowired
            private JavaMailSender javaMailSender;
            @Value("${spring.mail.username}")
            private String from;
            @RequestMapping("sendInlineMail")
            public String sendInlineMail(){
                MimeMessage message = null;
                try {
                    message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message,true);
                    helper.setFrom(from);
                    helper.setTo("xxxxxx@163.com");
                    helper.setSubject("一封带静态资源的邮件");
                    helper.setText("<html><body>很优秀：<img src='cid:img'/></body></html>",true);
                    // 传入附件
                    FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/img/优秀.jpg"));
                    // helper.addInline("img", file);中的img和图片标签里cid后的名称相对应
                    helper.addInline("img",file);
                    javaMailSender.send(message);
                    return "发送成功！";
                }catch (Exception e){
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }
    [2]访问测试: http://localhost:8080/email/sendInlineMail
8.使用模板发送邮件: (使用的模板解析引擎为Thymeleaf)
    [1]引入Thymeleaf依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
    [2]在template目录下创建一个emailTemplate.html模板:
        <!DOCTYPE html>
        <html lang="zh" xmlns:th="http://www.thymeleaf.org">
        <head>
            <meta charset="UTF-8" />
            <title>模板</title>
        </head>
        
        <body>
            您好，您的验证码为<span th:text="${code}"></span>，请在两分钟内使用完成操作。
        </body>
        </html>
    [3]编写Controller: (发送HTML邮件+变量绑定)
        @RestController
        @RequestMapping("/email")
        public class EmailController {
            @Autowired
            private JavaMailSender javaMailSender;
            @Autowired
            private TemplateEngine engine;
            @Value("${spring.mail.username}")
            private String from;
            @RequestMapping("sendTemplateEmail")
            public String sendTemplateEmail(String code){
                MimeMessage message = null;
                try {
                    message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message,true);
                    helper.setFrom(from);
                    helper.setTo("xxxxxx@163.com");
                    helper.setSubject("邮件摸板测试");
                    // 处理邮件模板
                    Context context = new Context();
                    context.setVariable("code",code);
                    String template = engine.process("emailTemplate", context);
                    helper.setText(template,true);
                    javaMailSender.send(message);
                    return "发送成功！";
                }catch (Exception e){
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }
    [4]访问测试: http://localhost:8080/email/sendTemplateEmail?code=123456
```
