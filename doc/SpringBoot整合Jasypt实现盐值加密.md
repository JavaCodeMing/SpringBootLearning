```
https://github.com/ulisesbocchio/jasypt-spring-boot
1.Jasypt可为Springboot加密的信息很多,主要有:
    [1]System Property: 系统变量
    [2]Envirnment Property: 环境变量
    [3]Command Line argument: 命令行参数
    [4]Application.properties: 应用配置文件
    [5]Yaml properties: 应用配置文件
    [6]other custom property sources: 其它配置文件
2.Jasypt整合到Springboot的三种方式:
    [1]jasypt-spring-boot-starter:
        (1)项目使用@SpringBootApplication或@EnableAutoConfiguration注解;
        (2)在pom中加入以下依赖即可对整个Spring的环境的配置信息进行加密解密:
            <dependency>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-spring-boot-starter</artifactId>
                <version>3.0.1</version>
            </dependency>
    [2]jasypt-spring-boot:
        (1)项目不使用@SpringBootApplication或@EnableAutoConfiguration注解,
            在配置Java类中加上注解@EnableEncryptableProperties,并引入以下依赖:
            <dependency>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-spring-boot</artifactId>
                <version>3.0.1</version>
            </dependency>
    [3]只对特定配置加密解密:
        (1)引入以下依赖:
            <dependency>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-spring-boot</artifactId>
                <version>3.0.1</version>
            </dependency>
        (2)使用注解@EncryptablePropertySource指定配置文件:
            @Configuration
            @EncryptablePropertySource(name="EncryptedProperties",value="classpath:encrypted.properties")
            public class MyApplication {
            }
3.生成加密字符的方式:
    [1]Java命令行:
        (1)Jasypt仓库中提供了一个类专门用于加密解密,提供了main方法,调用如下:
            java -cp ./jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI 
			  password=pkslow algorithm=PBEWithMD5AndTripleDES input=larry
    [2]脚本命令:
        (1)Jasypt提供了脚本,可以直接用于加密解密,下载地址: http://www.jasypt.org/download.html
        (2)在bin目录下面,根据自己的系统选择使用什么脚本来生成密文,使用参数与Java命令一样,使用如下:
            sh encrypt.sh password=pkslow algorithm=PBEWithMD5AndTripleDES input=larry
    [3]Java代码:
        (1)简单文本加密:
            1)文本加密是加密中最经常遇到的需求,如通讯消息、交易流水、账号信息等非常敏感的信息,
				许多场景下都需要加密储存,然后读取展示的时候再解密;
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                //设置加密密钥
                textEncryptor.setPassword("MySalt");
                //加密信息
                String encryptedText = textEncryptor.encrypt("This is a secret message.");
                System.out.println("encryptedText:" + encryptedText);
                //解密
                String decryptedText = textEncryptor.decrypt(encryptedText);
                System.out.println("decryptedText:" + decryptedText);
        (2)单向密码加密:
            ①用户密码是极其敏感的信息,不应把密码明文储存在数据库中,需把明文密码进行加密处理后,
				再把密文储存在数据库中;
            ②方案一: 数据库中的密文解密成明文,再与用户输入的密码进行对比;
            ③方案二: 把用户输入的密码进行加密,把加密后的密文与数据库的密文进行对比;
            BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
            //加密密码
            String encryptedPassword = encryptor.encryptPassword("MyPassword");
            //检查密码：正确
            System.out.println(encryptor.checkPassword("MyPassword", encryptedPassword));
            //检查密码：错误
            System.out.println(encryptor.checkPassword("myPassword", encryptedPassword));
        (3)改变加密算法:
            1)Jasypt提供了灵活的加密/解密操作,可自定义地使用不同的算法进行加密解密:
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                //设置密钥
                encryptor.setPassword("MySalt");
                //设置加密算法
                encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
                //加密信息
                String encryptedText = encryptor.encrypt("My secret message.");
                System.out.println("encryptedText: " + encryptedText);
                //解密
                String decryptedText = encryptor.decrypt(encryptedText);
                System.out.println("decryptedText: " + decryptedText);
        (4)多线程解密:
            1)解密通常是比加密更难的过程,Jasypt提供了多线程解密操作,可以并行解密;
            2)一般建议可以设置与机器处理器核数一致的线程数进行解密;
            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            //设置线程数为6
            encryptor.setPoolSize(6);
            //设置密钥
            encryptor.setPassword("MySalt");
            //设置算法
            encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
            //加密
            String encryptedText = encryptor.encrypt("My secret message.");
            System.out.println("encryptedText: " + encryptedText);
            //解密
            String decryptedText = encryptor.decrypt(encryptedText);
            System.out.println("decryptedText: " + decryptedText);
4.配置密文与其它项:
    [1]配置密文: 生成密文后,就要把密文配置在相应的位置
        #配置密文的默认格式: ENC(密文)
        #此格式可通过jasypt.encryptor.property.prefix和jasypt.encryptor.property.suffix配置
        username: ENC(SUfiOs8MvmAUjg+oWl/6dQ==)
        jasypt:
          encryptor:
            #加密的盐值
            password: pkslow
            #配置加密算法
            algorithm: PBEWithMD5AndTripleDES
    [2]其它配置项:
        (1)jasypt.encryptor.password: 加密的盐值(大小写敏感),必填;
        (2)jasypt.encryptor.algorithm: 加密算法,非必填;
            默认值:PBEWITHHMACSHA512ANDAES_256
        (3)jasypt.encryptor.key-obtention-iterations: 加密密钥的哈希迭代次数;
            默认值: 1000
        (4)jasypt.encryptor.pool-size: 线程池大小;
            默认值: 1
        (5)jasypt.encryptor.provider-name: 加密器的提供者;
            默认值: SunJCE
        (6)jasypt.encryptor.provider-class-name: 加密器的提供者类名;
        (7)jasypt.encryptor.salt-generator-classname: 加盐的类名;
            默认值: org.jasypt.salt.RandomSaltGenerator
        (8)jasypt.encryptor.iv-generator-classname: 初始向量IV生成器的类名;
            默认值: org.jasypt.iv.RandomIvGenerator
        (9)jasypt.encryptor.string-output-type: 字节数组的输出类型;
            默认值: base64
        (10)jasypt.encryptor.proxy-property-sources: 是否使用代理;
            默认值: false
        (11)jasypt.encryptor.skip-property-sources: 不会被加解密的属性集合;
5.安放密钥方式: (密钥是非常重要的信息,决定了密文是否真正安全)
    [1]放在application.properties:
        能获得配置文件的人就能知道密钥,不够安全;存在密文和密钥放在同一个配置文件的风险;
    [2]JVM参数:
        启动Java程序时加参数:-Djasypt.encryptor.password=xxx,只有部署程序的人知道密钥,安全;
    [3]服务器的环境变量:
        密钥放在linux系统的环境变量中,只有能拿到服务器访问权限的人,才有可能知道密钥;
            # 配置profile文件
            export JASYPT_PASSWORD = pkslow
            # 生效 
            source /etc/profile
            # 运行java程序时
            java -jar -Djasypt.encryptor.password=${JASYPT_PASSWORD} xxx.jar
    [4]使用自定义的Encryptor来存放:
        把密钥写在代码里,只有能获得jar包并反编译的人,才能获得密文;
        @Bean("jasyptStringEncryptor")
        public StringEncryptor stringEncryptor() {
          PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
          SimpleStringPBEConfig config = new SimpleStringPBEConfig();
          config.setPassword("password");
          config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
          config.setKeyObtentionIterations("1000");
          config.setPoolSize("1");
          config.setProviderName("SunJCE");
          config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
          config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
          config.setStringOutputType("base64");
          encryptor.setConfig(config);
          return encryptor;
        }
        jasypt.encryptor.bean=jasyptStringEncryptor
        (把密钥的一部分写在代码里,另一部分通过外部方式来配置,这样就会更加安全)
6.测试:
    [1]引入依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.1</version>
        </dependency>
    [2]编写application.yml:
        name: ENC(Xt2kU2GFjee7eZj/oc/rQg==)
        jasypt:
          encryptor:
            password: password
            algorithm: PBEWithMD5AndDES
            key-obtention-iterations: 1000
            pool-size: 1
            provider-name: SunJCE
            salt-generator-classname: org.jasypt.salt.RandomSaltGenerator
            iv-generator-classname: org.jasypt.iv.NoIvGenerator
            string-output-type: base64
    [3]编写测试controller:
        @RestController
        @RequestMapping("/jasypt")
        public class TestController {
            @Value("${name}")
            private String password;
            @GetMapping("/name")
            public Mono<String> sendNormalText() {
                System.out.println(password);
                return Mono.just(password);
            }
        }        
```
