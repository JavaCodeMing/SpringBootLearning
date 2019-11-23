```
SpringBoot2.x 默认使用Logback记录日志,但是可通过配置来提供对常用日志的支持,如:Java Util Logging,
    Log4J, Log4J2和Logback;每种Logger都可以通过配置使用控制台或者文件输出日志内容;
Logback是log4j框架的作者开发的新一代日志框架,它效率更高、能够适应诸多的运行环境,同时天然支持SLF4J;
(详情参考Logback中文翻译文档:https://github.com/Volong/logback-chinese-manual/)
1.日志框架简介:
    [1]现有的日志框架: JUL、JCL、Jboss-logging、logback、log4j、log4j2、slf4j…
        日志门面(日志的抽象层)                        日志的实现
        SLF4j(Simple Logging Facade for Java)         Log4j   Logback
        JCL(Jakarta Commons Logging)                JUL(java.util.logging)
        jboss-logging                                Log4j2
        (在实际工作中用的话,就需要左边选一个门面(抽象层),右边来选一个实现;)
        (SLF4j/Logback/Log4j是同一个人写的,LogBack是log4j的升级版;Log4j2是apache公司的)
2.添加日志依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
    </dependency>
    (该依赖包含:SLF4j和Logback,可使Logback开箱即用)
3.Logback默认输出内容格式:
    日志: 2019-05-10 15:05:03.368 INFO 14404 --- [ main] com.springboot.Application : Starting Application
    (1)时间日期: 精确到毫秒
    (2)日志级别: TRACE < DEBUG < INFO < WARN < ERROR < FATAL
    (3)进程ID:
    (4)分隔符: ---(标识实际日志的开始)
    (5)线程名：方括号括起来(可能会截断控制台输出)
    (6)Logger名: 通常使用源代码的类名
    (7)日志内容: 
4.控制台及文件输出日志和日志级别控制:
    [1]控制台输出:
        Spring Boot中默认配置ERROR、WARN和INFO级别的日志输出到控制台;
        开启debug模式,会输出更多容器框架的日志(应用的日志输出级别不会变更):
            命令行开启debug: java -jar springTest.jar --debug
            在application.properties中配置: debug=true
    [2]文件输出: 需在application.properties中设置logging.file或logging.path属性
        logging.file: 设置文件,可以是绝对路径,也可以是相对路径;如:logging.file=my.log
        logging.path: 设置目录,会在该目录下创建spring.log文件,并写入日志内容,如: logging.path=/var/log
        (如果只配置 logging.file,会在项目的当前路径下生成一个 xxx.log 日志文件)
        (如果只配置 logging.path,在 /var/log文件夹生成一个日志文件为 spring.log)
    [3]日志级别控制: 在application.properties中设置 "logging.level.* = LEVEL"
        logging.level: 日志级别控制前缀,*为包名或Logger名
        LEVEL: 选项 TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
        (logging.level.com.springboot=DEBUG: com.springboot包下所有class以DEBUG级别输出)
        (logging.level.root=WARN: root日志以WARN级别输出)
5.自定义日志配置:
    由于日志服务一般都在ApplicationContext创建前就初始化了,它并不是必须通过Spring的配置文件控制;
        因此通过系统属性和传统的Spring Boot外部配置文件依然可以很好的支持日志控制和管理;
    [1]日志配置文件名:
        (1)根据不同的日志系统,能被正确加载的默认配置文件名:
            Logback: logback-spring.xml, logback-spring.groovy, logback.xml, logback.groovy
            Log4j:      log4j-spring.properties, log4j-spring.xml, log4j.properties, log4j.xml
            Log4j2:  log4j2-spring.xml, log4j2.xml
            JUL:      logging.properties
            (SpringBoot官方推荐优先使用带有"-spring"的文件名作为日志配置,其会添加一些springboot特有的配置项)
       (2)自定义日志配置文件名: 在application.properties中,通过logging.config属性指定日志配置文件名
            (如: logging.config=classpath:logging-config.xml)
    [2]logback 配置详解:
        (1)根节点<configuration>包含的属性: scan,scanPeriod,debug
            (1)scan: 配置文件变更后是否重新加载,默认值为true
            (2)scanPeriod: 设置监测配置文件是否有修改的时间间隔
                (当scan为true时,此属性生效;无单位则默认为毫秒;默认的时间间隔为1分钟)
            debug: 是否打印出logback内部日志信息,来实时查看logback运行状态; 默认值为false
        (2)根节点<configuration>的子节点:
            [1]<contextName>: 设置上下文名称
                每个logger都关联到logger上下文,默认上下文名称为"default";但可以使用<contextName>
                设置成其他名字,用于区分不同应用程序的记录;一旦设置,不能修改;
                <configuration scan="true" scanPeriod="60 seconds" debug="false">  
                    <contextName>myAppName</contextName>  
                    <!-- 其他配置省略-->  
                </configuration> 
            [2]<property>: 用来定义变量值的标签,包含的属性:name和value;
                通过定义的值会被插入到logger上下文中;定义变量后,可以通过"${}"来使用变量;
                <configuration scan="true" scanPeriod="60 seconds" debug="false">  
                    <property name="APP_Name" value="myAppName" />   
                    <contextName>${APP_Name}</contextName>  
                    <!-- 其他配置省略-->  
                </configuration>
            [3]<timestamp>: 获取时间戳字符串,包含的属性:key(必要),datePattern(必要),timeReference(可选)
                (1)key: 标识此<timestamp>的名字;
                (2)datePattern: 设置将当前时间(解析配置文件的时间)转换为字符串模式,遵循Java.txt.SimpleDateFormat格式
                (3)timeReference: 时间戳引用时间,默认为解析配置文件的时间,也可设置为上下文初始化时间(即contextBirth)
                //将解析配置文件的时间作为上下文名称
                <configuration scan="true" scanPeriod="60 seconds" debug="false">                
                    <timestamp key="bySecond" datePattern="yyyyMMdd'T'HHmmss"/>   
                    <contextName>${bySecond}</contextName>  
                    <!-- 其他配置省略-->  
                </configuration>
            [4]设置logger的两个标签:<logger>,<root>
                (1)<logger>: 用来设置某一个包或者具体的某一个类的日志打印级别以及指定<appender>;
                    <loger>包含的属性: name,level(可选),addtivity(可选)
                        name: 用来指定受此loger约束的某一个包或者具体的某一个类
                        level: 用来设置打印级别,大小写无关;如果未设置此属性,那么当前loger将会继承上级的级别;
                            TRACE,DEBUG,INFO,WARN,ERROR,ALL和OFF,外加特俗值INHERITED或者同义词NULL(强制执行上级的级别)
                        addtivity: 是否向上级loger传递打印信息,默认是true;    
                    <logger>可以包含零个或多个<appender-ref>元素,引用的appender将会添加到当前logger;
                (2)<root>: 也是<logger>元素,但是它是根logger; 必选节点,用来指定最基础的日志输出级别
                    <root>包含的属性: level (因为名称为root)
                        level: 用来设置打印级别,大小写无关,默认是DEBUG;
                            TRACE,DEBUG,INFO,WARN,ERROR,ALL和OFF,不能设置为INHERITED或者同义词NULL;
                    <root>可以包含零个或多个<appender-ref>元素,引用的appender将会添加到当前loger;
            [5]<appender>: 用来格式化日志输出节点,包含属性name和class,class用来指定具体实现的输出策略:
                (1)ch.qos.logback.core.ConsoleAppender: 将日志输出到控制台
                    1)<encoder>: 对日志进行格式化;Encoder类型
                    2)<target>: 指定输出目标;可选值:System.out(默认)或System.err;String类型
                    3)<withJansi>: 是否支持ANSI color codes,默认为false;boolean类型
                    <configuration>  
                        <appender name="console" class="ch.qos.logback.core.ConsoleAppender"> 
                            <!-- encoder默认使用ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
                            <encoder>  
                                <pattern>%-4relative [%thread] %-5level %logger{35} - %msg %n</pattern>
                            </encoder>  
                        </appender>  
                        <root level="DEBUG">  
                            <appender-ref ref="console" />  
                        </root>  
                    </configuration>
                (2)ch.qos.logback.core.FileAppender: 把日志添加到文件
                    1)<file>: 被写入的文件名,可以是相对/绝对目录,若上级目录不存在会自动创建,没有默认值;String类型
                    2)<append>: 是否以追加方式输出,默认为true;boolean类型
                    3)<encoder>: 对记录事件进行格式化;Encoder类型
                    4)<prudent>: 是否安全写入,开启效率低,默认值为fales;boolean类型
                    <configuration>  
                        <timestamp key="bySecond" datePattern="yyyyMMdd'T'HHmmss">
                        <appender name="file" class="ch.qos.logback.core.FileAppender">
                            <!-- 利用之前创建的timestamp来创建唯一的文件-->
                            <file>testFile-${bySecond}.log</file>  
                            <append>true</append>
                            <!-- 将immediateFlush设置为false可以获得更高的日志吞吐量 -->
                            <immediateFlush>true</immediateFlush>
                            <!-- encoder默认使用ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
                            <encoder>  
                                <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>  
                            </encoder>  
                        </appender>          
                        <root level="DEBUG">  
                            <appender-ref ref="file" />  
                        </root>  
                    </configuration>
                (3)ch.qos.logback.core.rolling.RollingFileAppender: 滚动记录文件
                    (先将日志记录到指定文件,当符合某个条件时,将日志记录到其他文件)
                    1)<file>: 被写入的文件名,可以是相对/绝对目录,若上级目录不存在会自动创建,没有默认值;String类型
                    2)<append>: 是否以追加方式输出,默认为true;boolean类型
                    3)<encoder>: 对记录事件进行格式化;Encoder类型
                    4)<rollingPolicy>: 当发生日志切换时,指定RollingFileAppender的行为,涉及文件移动和重命名;RollingPolicy类型
                    5)<triggeringPolicy>: 决定什么时候发生日志切换(如日期,日志文件大小到达一定值);TriggeringPolicy类型
                    6)<prudent>: 严格模式(是否支持多个进程同时操作一个文件);boolean类型
                        FixedWindowRollingPolicy 不支持prudent模式;
                        TimeBasedRollingPolicy 支持prudent模式,但是需要满足一下两条约束:
                            ①在prudent模式中,日志文件的压缩是不被允许,不被支持的
                            ②不能设置file属性
            [6]RollingPolicy有几个常见的实现类:
                (1)TimeBasedRollingPolicy: 最受欢迎的日志滚动策略,它的滚动策略是基于时间的(如按天按月)
                    1)<fileNamePattern>: 必选节点(String),决定了日志滚动时,归档日志的命名策略;由文件名,以及一个%d转移符组成;
                        %d{}花括号中需要包含符合SimpleDateFormat约定的时间格式,如果未指定,直接是%d,则默认相当于%d{yyyy-MM-dd};
                        需要注意的是,在RollingPolicy节点的父节点appender节点中,<file>节点的值可以显示声明,或忽略;如果声明
                        file属性,你可以达到分离当前有效日志文件以及归档日志文件的目的;设置成之后,当前有效日志文件的名称永远
                        都是file属性指定的值,当发生日志滚动时,再根据fileNamePattern的值更改存档日志的名称,然后创建一个新的
                        有效日志文件,名为file属性指定的值;如果不指定,则当前有效日志文件名根据fileNamePattern变更;
                        同样需要注意的是,在%d{}中,不管是“/”还是“\”都被认为是文件分隔符;
                        多个%d转移符的情况:
                            fileNamePaatern的值允许包含多个%d的情况,但是只有一个%d作为主要的日志滚动周期的参考值;
                            其余非主要的%d需要包含一个"aux"的参数;
                                <fileNamePattern>/var/log/%d{yyyy/MM, aux}/myapplication.%d{yyyy-MM-dd}.log</fileNamePattern>
                                (按年月划分目录,再将按日期天数命名的归档日志存放在对应月份文件夹;在每天0点的时候发生日志切换)
                        时区问题: 你可以将日期转换成相应时区的时间
                            aFolder/test.%d{yyyy-MM-dd-HH, UTC}.log  //世界协调时间
                            aFolder/test.%d{yyyy-MM-dd-HH, GMT}.log  //格林尼治时间
                    2)<maxHistory>: 可选节点(int),声明归档日志最大保留时间,需删除的归档日志所存在的目录也会被合适的删除掉;
                    3)<totalSizeCap>: 可选节点(int),声明归档日志的最大存储量;当超过这个值,最老的归档日志文件也会被删除;
                    4)<cleanHistoryOnStart>: 可选节点(boolean),默认为false;若为true,则当appender启动时,会删除所有归档日志文件;
                    <configuration>  
                        <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                            <file>testFile.log</file>  
                            <append>true</append>
                            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                                <!-- 按天轮转 -->
                                <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
                                <!-- 保存30天的历史记录,最大大小为30GB -->
                                <maxHistory>30</maxHistory>
                                <totalSizeCap>3GB</totalSizeCap>
                            </rollingPolicy>
                            <!-- encoder默认使用ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
                            <encoder>  
                                <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>  
                            </encoder>  
                        </appender>          
                        <root level="DEBUG">  
                            <appender-ref ref="file" />  
                        </root>  
                    </configuration>
                (2)SizeAndTimeBasedRollingPolicy: 
                    1)<fileNamePattern>: 必选节点(String),决定了日志滚动时,归档日志的命名策略;
                    2)<maxFileSize>: 可选节点(int),决定单个日志文件的大小,达到该大小进行日志滚动归档;
                    3)<maxHistory>: 可选节点(int),声明归档日志最大保留时间,需删除的归档日志所存在的目录也会被合适的删除掉;
                    4)<totalSizeCap>: 可选节点(int),声明归档日志的最大存储量;当超过这个值,最老的归档日志文件也会被删除;
                    5)<cleanHistoryOnStart>: 可选节点(boolean),默认为false;若为true,则当appender启动时,会删除所有归档日志文件;
                    <configuration>  
                        <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                            <file>logFile.log</file>  
                            <append>true</append>
                            <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                                <!-- 按天轮转 -->
                                <fileNamePattern>logFile-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                                <maxFileSize>100MB</maxFileSize>
                                <maxHistory>60</maxHistory>
                                <totalSizeCap>20GB</totalSizeCap>
                            </rollingPolicy>
                            <!-- encoder默认使用ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
                            <encoder>  
                                <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>  
                            </encoder>  
                        </appender>          
                        <root level="DEBUG">  
                            <appender-ref ref="file" />  
                        </root>  
                    </configuration>
                    (注意: 除了%d之外还有%i,这两个占位符都是强制要求的)
                (3)FixedWindowRollingPolicy: 在日志切换时根据固定窗口算法滚动策略
                    1)<fileNamePattern>: 必选节点(String),决定了日志滚动时,归档日志的命名策略;
                    2)<minIndex>: 必选节点(int),表示窗口索引的下界;
                    3)<maxIndex>: 必选节点(int),表示窗口索引的上界;
                    (appender子节点的<file>是强制要求的)
                    <configuration>
                        <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                            <file>test.log</file>
                            <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
                                <fileNamePattern>tests.%i.log.zip</fileNamePattern>
                                <minIndex>1</minIndex>
                                <maxIndex>3</maxIndex>
                            </rollingPolicy>
                            <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
                                <maxFileSize>5MB</maxFileSize>
                            </triggeringPolicy>
                            <encoder>
                                <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
                            </encoder>
                        </appender>
                        <root level="DEBUG">
                            <appender-ref ref="file" />
                        </root>
                    </configuration>
            [7]fileNamePattern的介绍:
                (1)/wombat/foo.%d: 每天轮转(晚上零点),由于省略了指定 %d 的日期格式,所以默认为 yyyy-MM-dd;
                    没有设置file属性: 在2006.11.23这一天的日志都会输出到 /wombat/foo.2006-11-23 这个文件;
                        晚上零点以后,日志将会输出到 wombat/foo.2016-11-24 这个文件;
                    设置file的值为/wombat/foo.txt: 在2016.11.23这一天的日志将会输出到/wombat/foo.txt这个文件;
                        在晚上零点的时候,foo.txt 将会被改名为 /wombat/foo.2016-11-23;然后将创建一个新的 foo.txt,
                        11.24 号这一天的日志将会输出到这个新的文件中;
                (2)/wombat/%d{yyyy/MM}/foo.txt: 每个月开始的时候轮转;
                    没有设置file属性: 在2016.10这一个月中的日志将会输出到 /wombat/2006/10/foo.txt;
                        在 10.31 晚上凌晨以后，11 月份的日志将会被输出到 /wombat/2006/11/foo.txt;
                    设置file的值为/wombat/foo.txt: 在 2016.10，这个月份的日志都会输出到 /wombat/foo.txt;
                        新的文件在 10.31 晚上零点的时候,/wombat/foo.txt 将会被重命名为 /wombat/2006/10/foo.txt,并会
                        创建一个/wombat/foo.txt ,11 月份的日志将会输出到这个文件;依此类推;
                (3)/wombat/foo.%d{yyyy-ww}.log: 每周的第一天(取决于时区);每次轮转发生在每周的第一天,其它的跟上一个例子类似;
                (4)/wombat/foo%d{yyyy-MM-dd_HH}.log: 每小时轮转;跟之前的例子类似;
                (5)/wombat/foo%d{yyyy-MM-dd_HH-mm}.log: 每分钟轮转;跟之前的例子类似;
                (6)/wombat/foo%d{yyyy-MM-dd_HH-mm, UTC}.log: 每分钟轮转;跟之前的例子类似,不过时间格式是 UTC;
                (7)/foo/%d{yyyy-MM, aux}/%d.log: 每天轮转;归档文件在包含年月的文件夹下;
                    第一个 %d 被辅助标记;第二个 %d 为主要标记,但是日期格式省略了;因此,轮转周期为每天(由第二个 %d 控制),
                    文件夹的名字依赖年与月;例如,2016.11时,所有的归档文件都会在"/foo/2006-11/"文件夹下;
                (8)/wombat/foo.%d.gz: 每天轮转(晚上零点),自动将归档文件压缩成 GZIP 格式;
                    file 属性没有设置: 在2009.11.23,日志将会被输出到"/wombat/foo.2009-11-23"这个文件;但在晚上零点的时候,
                        文件将会被压缩成"/wombat/foo.2009-11-23.gz";11.24的日志将会被直接输出到"/wombat/folder/foo.2009-11-24"
                    file 属性的值设置为"/wombat/foo.txt": 2009.11.23日志将被输出到"/wombat/foo.txt";在晚上零点时,该文件会被压
                        缩成"/wombat/foo.2009-11-23.gz";并创建一个新的"/wombat/foo.txt",11.24的日志将会被输出到该文件;依此类推;
            [8]pattern中转换字符与可选参数的意义:
                (1)logger{length}: 输出 logger 的名字作为日志事件的来源;(length为logger名的长度(含"."))
                    logger名最右边部分永远不会被简写,即使它的长度比length大;其它的部分可能被缩短成单字符,但永不会被移除;
                (2)contextName: 输出日志事件附加到的 logger 上下文的名字;
                (3)date{pattern [,timezone]}: 用于输出日志事件的日期;
                (4)message: 输出与日志事件相关联的,由应用程序提供的日志信息;
                (5)level: 输出日志事件的级别;
                (6)relative: 输出应用程序启动到创建日志事件所花费的毫秒数;
                (7)thread: 输出生成日志事件的线程名;
                (8)n: 输出平台所依赖的行分割字符;
                (在给定的转换模式上下文中,% 有特殊的含义;如果作为字面量,需要进行转义;例如,"%d %p % %m%n")
                (转换字符与字面量字符直接相连可能造成解析出错,可通过传空参"{}进行区分)
6.实例:
    [1]引入web依赖和logging依赖(springboot默认使用logback):
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
    [2]编写logback的配置文件(/src/main/resources/logback-spring.xml):
        <?xml version="1.0" encoding="UTF-8"?>
        <configuration scan="true" scanPeriod="60 seconds" debug="false">
            <contextName>logback</contextName>
            <property name="log.path" value="/var/log/app" />
            <!--输出到控制台-->
            <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
                <!-- encoder默认使用ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
                <encoder>
                    <pattern>%date %-4relative %contextName [%thread] %-5level %logger{35} - %msg %n</pattern>
                </encoder>
            </appender>
            <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                <file>${log.path}/logFile.log</file>
                <append>true</append>
                <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                    <!-- 按天轮转,并达到指定文件大小后按窗口归档 -->
                    <fileNamePattern>${log.path}/logFile-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                    <maxFileSize>30MB</maxFileSize>
                    <maxHistory>30</maxHistory>
                    <totalSizeCap>10GB</totalSizeCap>
                </rollingPolicy>
                <!-- encoder默认使用ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
                <encoder>
                    <pattern>%date %-4relative %contextName [%thread] %-5level %logger{35} - %msg %n</pattern>
                </encoder>
            </appender>
            <logger name="com.springboot.controller.LearnController" level="WARN" additivity="false">
                <appender-ref ref="file"/>
                <appender-ref ref="console"/>
            </logger>
            <root level="info">
                <appender-ref ref="console"/>
                <appender-ref ref="file"/>
            </root>
        </configuration>
    [3]编写测试Controller:
        @Controller
        public class LearnController {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            @PostMapping("/login")
            @ResponseBody
            public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response) {
                //日志级别从低到高分为TRACE<DEBUG<INFO<WARN<ERROR<FATAL,如果设置为WARN,则低于WARN的信息都不会输出;
                logger.trace("日志输出 trace");
                logger.debug("日志输出 debug");
                logger.info("日志输出 info");
                logger.warn("日志输出 warn");
                logger.error("日志输出 error");
                Map<String, Object> map = new HashMap<String, Object>();
                String userName = request.getParameter("userName");
                String password = request.getParameter("password");
                if (!userName.equals("") && password != "") {
                    User user = new User(userName, password);
                    request.getSession().setAttribute("user", user);
                    map.put("result", "1");
                } else {
                    map.put("result", "0");
                }
                return map;
            }
        }
    [4]测试: post方法访问http://192.168.178.128:8081/web/login (参数: userName:Kim,password:123456)
```
