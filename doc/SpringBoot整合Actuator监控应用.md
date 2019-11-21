```
Actuator提供了一系列的RESTful API可为我们提供细致的应用信息。
1.Actuator接口列表:(Actuator提供的接口,可以分为三大类:配置接口、度量接口和其它接口)
    HTTP方法 接口                        描述
    GET      /actuator                   列出所有可用接口
    GET      /actuator/auditevents       公开当前应用程序的审核事件信息
    GET      /actuator/beans             描述应用程序上下文里全部的Bean,以及它们的关系
    GET      /actuator/conditions        显示在配置和自动配置类上评估的条件以及它们匹配或不匹配的原因
    GET      /actuator/configprops       显示所有@ConfigurationProperties的整理列表
    GET      /actuator/env               获取全部环境属性
    GET      /actuator/env/{name}        根据名称获取特定的环境属性值
    GET      /actuator/flyway            显示已应用的任何Flyway数据库迁移
    GET      /actuator/health            报告应用程序的健康指标,这些值由HealthIndicator的实现类提供
    GET      /actuator/heapdump          提供了来自应用程序JVM的堆转储
    GET      /actuator/httptrace         显示HTTP跟踪信息(默认情况下,最后100个HTTP请求 - 响应交换)
    GET      /actuator/info              获取应用程序的定制信息,这些信息由info打头的属性提供
    GET      /actuator/liquibase         显示已应用的任何Liquibase数据库迁移
    GET      /actuator/logfile           提供对应用程序日志文件内容的访问
    GET      /actuator/loggers           提供显示和修改应用程序中loggers配置的功能
    GET      /actuator/mappings          描述全部的URI路径,以及它们和控制器(包含Actuator端点)的映射关系
    GET      /actuator/metrics           报告各种应用程序度量信息,比如内存用量和HTTP请求计数
    GET      /actuator/metrics/{name}    报告指定名称的应用程序度量值
    GET      /actuator/prometheus        以Prometheus服务器刮取所需的格式提供SpringBoot应用程序的度量
    GET      /actuator/scheduledtasks    显示应用程序中的计划任务
    GET      /actuator/sessions          提供有关由Spring会话管理的应用程序的HTTP会话的信息
    POST     /actuator/shutdown          关闭应用程序,要求management.endpoint.shutdown.enabled设置为true
    GET      /actuator/threaddump        显示当前应用线程状态信息
  Web应用程序(Spring MVC，Spring WebFlux或Jersey),则还可以使用以下附加接口:
    GET      /actuator/heapdump          返回GZip压缩hprof堆转储文件
    GET      /actuator/jolokia           通过HTTP公开JMX bean(当Jolokia在类路径上时,不适用于WebFlux)
    GET      /actuator/logfile           返回日志文件的内容(如果已设置logging.file或logging.path属性)
    GET      /actuator/prometheus        以可以由Prometheus服务器抓取的格式公开指标
2.接口启用和暴露:
    [1]启用接口: (默认会启用除了shutdown之外所有的接口)
        可以使用management.endpoint.<id>.enabled启用接口: 如 management.endpoint.shutdown.enabled = true
    [2]暴露接口: (选择性地暴露接口,防敏感信息泄露)
        (1)内置端点的默认曝光:
            ID                JMX       WEB
            auditevents       是        没有
            beans             是        没有
            conditions        是        没有
            configprops       是        没有
            env               是        没有
            flyway            是        没有
            health            是        是
            heapdump          N/A       没有
            httptrace         是        没有
            info              是        是
            jolokia           N/A       没有
            logfile           N/A       没有
            loggers           是        没有
            liquibase         是        没有
            metrics           是        没有
            mappings          是        没有
            prometheus        N/A       没有
            scheduledtasks    是        没有
            sessions          是        没有
            shutdown          是        没有
            threaddump        是        没有
        (2)配置更改:
            属性                                        默认
            management.endpoints.jmx.exposure.exclude    
            management.endpoints.jmx.exposure.include    *
            management.endpoints.web.exposure.exclude    
            management.endpoints.web.exposure.include    info, health
            (通过以上配置更改应用的暴露的接口)
            (例:management.endpoints.jmx.exposure.include = health,info)
            (例:management.endpoints.web.exposure.include = health,info,metrics)
2.Actuator的引入和配置:
    [1]引入Actuator的依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    [2]配置Actuator:
        management:
            endpoint:
                shutdown:
                    enabled: true       #启用接口:/actuator/shutdown
            endpoints:
                web:
                    exposure:
                        include: '*'    #接口暴露设置
            server:
                port: 8080
3.Actuator接口详解:    (https://docs.spring.io/spring-boot/docs/2.0.2.RELEASE/actuator-api/html)
    [1]auditevents: 提供有关应用程序的审核事件的信息
        (1)查询参数: (使用查询参数来限制其返回的事件)
            参数         描述
            after        将事件限制为在给定时间之后发生的事件,可选的
            principal    将事件限制为具有给定主体的事件,可选的
            type         将事件限制为具有给定类型的事件,可选的
            (http://localhost:8080/actuator/auditevents?principal=alice&after=2018-05-09T13%3A28%3A25.84Z&type=logout)
        (2)响应结构
            路径                  类型        描述
            events                Array       一系列审计事件
            events.[].timestamp   String      事件发生时间的时间戳
            events.[].principal   String      触发事件的主体
            events.[].type        String      事件的类型
    [2]beans: 提供有关应用程序bean的信息
        (1)响应结构:
            路径                            类型        描述
            contexts                        Object      由id键确定的应用程序上下文
            contexts.*.parentId             String      父应用程序上下文的ID(如果有的话)
            contexts.*.beans                Object      应用程序上下文中的bean按名称键
            contexts.*.beans.*.aliases      Array       别名的名称
            contexts.*.beans.*.scope        String      bean的范围
            contexts.*.beans.*.type         String      bean的完全限定类型
            contexts.*.beans.*.resource     String      定义bean的资源(如果有的话)
            contexts.*.beans.*.dependencies Array       任何依赖项的名称
        (http://localhost:8080/actuator/beans)
    [3]conditions: 提供有关计算配置和自动配置类的条件的信息
        (1)响应结构:
            路径                            类型        描述
            contexts                        Object      由id键确定的应用程序上下文
            contexts.*.positiveMatches      Object      具有匹配条件的类和方法
            contexts.*.positiveMatches.*    String      条件的名称
                .[].condition
            contexts.*.positiveMatches.*    String      为什么条件匹配的细节
                .[].message
            contexts.*.negativeMatches      Object      具有不匹配条件的类和方法
            contexts.*.negativeMatches.*    Array       匹配的条件
                .notMatched
            contexts.*.negativeMatches.*    String      条件的名称
                .notMatched.[].condition    
            contexts.*.negativeMatches.*    String      详细说明为什么条件不匹配
                .notMatched.[].message
            contexts.*.negativeMatches.*    Array       匹配的条件
                .matched
            contexts.*.negativeMatches.*    String      条件的名称
                .matched.[].condition
            contexts.*.negativeMatches.*    String      条件匹配原因的详细信息
                .matched.[].message    
            contexts.*.unconditionalClasses Array       无条件自动配置类的名称(如果有的话)
            contexts.*.parentId             String      父应用程序上下文的ID(如果有的话)
        (http://localhost:8080/actuator/conditions)
    [4]configprops: 提供有关应用程序的@ConfigurationProperties的bean
        (1)响应结构:
            路径                            类型        描述
            contexts                        Object      由id键确定的应用程序上下文
            contexts.*.beans.*              Object      @ConfigurationProperties以bean名命名的bean
            contexts.*.beans.*.prefix       String      应用于bean属性名称的前缀
            contexts.*.beans.*.properties   Object      bean的属性作为名称-值对
            contexts.*.parentId             String      父应用程序上下文的ID(如果有的话)
        (http://localhost:8080/actuator/configprops)
    [5]env: 提供有关应用程序的Environment
        (1)检索整个环境的响应结构:
            路径                            类型        描述
            activeProfiles                  Array       活动配置文件的名称(如果有的话)
            propertySources                 Array       按优先顺序排列的属性源
            propertySources.[].name         String      属性源的名称
            propertySources.[].properties   Object      属性源中按属性名称键确定的属性
            propertySources.[].properties   String      属性值
                .*.value    
            propertySources.[].properties   String      属性的来源(如有的话)
                .*.origin
            (http://localhost:8080/actuator/env)
        (2)检索单个属性的响应结构:
            路径                            类型        描述
            property                        Object      环境中的属性(如果找到)
            property.source                 String      属性源的名称
            property.value                  String      属性值
            activeProfiles                  Array       活动配置文件的名称(如果有的话)
            propertySources                 Array       按优先顺序排列的属性源
            propertySources.[].name         String      属性源的名称
            propertySources.[].properties   Object      属性源中按属性名称键确定的属性
            propertySources.[].properties   String      属性值
                .*.value    
            propertySources.[].properties   String      属性的来源(如有的话)
                .*.origin
            (http://localhost:8080/actuator/env/com.example.cache.max-size)
    [6]flyway: 提供有关由Flyway执行的数据库迁移的信息
        (1)响应结构:
            路径                            类型        描述
            contexts                        Object      由id键确定的应用程序上下文
            contexts.*.flywayBeans.*        Array       由Flyway实例执行的迁移(按Flyway bean名称键控)
                .migrations
            contexts.*.flywayBeans.*        Number      迁移的校验和(如果有的话)
                .migrations.[].checksum
            contexts.*.flywayBeans.*        String      迁移的描述(如果有的话)
                .migrations.[].description
            contexts.*.flywayBeans.*        Number      应用迁移的执行时间(毫秒)
                .migrations.[].executionTime
            contexts.*.flywayBeans.*        String      安装了应用迁移的用户(如果有的话)
                .migrations.[].installedBy
            contexts.*.flywayBeans.*        String      安装应用迁移的时间戳(如果有的话)
                .migrations.[].installedOn
            contexts.*.flywayBeans.*        Number      应用迁移的级别(如果有的话);后来的移民有更高的级别
                .migrations.[].installedRank
            contexts.*.flywayBeans.*        String      用于执行迁移的脚本的名称(如果有的话)
                .migrations.[].script
            contexts.*.flywayBeans.*        String      迁徙状态
                .migrations.[].state
            contexts.*.flywayBeans.*        String      迁徙类型
                .migrations.[].type
            contexts.*.flywayBeans.*        String      应用迁移后的数据库版本(如果有的话)
                .migrations.[].version
            contexts.*.parentId             String      父应用程序上下文的ID(如果有的话)
            迁徙状态: 
                PENDING, ABOVE_TARGET, BELOW_BASELINE, BASELINE, IGNORED, MISSING_SUCCESS, 
                MISSING_FAILED, SUCCESS, UNDONE, AVAILABLE, FAILED, OUT_OF_ORDER, 
                FUTURE_SUCCESS, FUTURE_FAILED, OUTDATED, SUPERSEDED
            迁徙类型:
                SCHEMA, BASELINE, SQL, UNDO_SQL, JDBC, UNDO_JDBC, SPRING_JDBC, 
                UNDO_SPRING_JDBC, CUSTOM, UNDO_CUSTOM
        (http://localhost:8080/actuator/flyway)
    [7]health: 提供有关应用程序运行状况的详细信息
        (1)响应结构:
            路径                            类型        描述
            status                          String      应用程序的总体状态
            details                         Object      详细说明应用程序的健康状况
            details.*.status                String      应用程序的特定部分的状态
            details.*.details               Object      详细说明应用程序特定部分的健康状况
        (http://localhost:8080/actuator/health)
    [8]httptrace: 提供有关HTTP请求-响应交换的信息
        (1)响应结构:
            路径                            类型        描述
            traces                          Array       跟踪HTTP请求-响应交换的数组
            traces.[].timestamp             String      跟踪交换何时发生的时间戳
            traces.[].principal             Object      交易所的负责人(如有的话)
            traces.[].principal.name        String      校长的名字
            traces.[].request.method        String      请求的http方法
            traces.[].request.remoteAddress String      接收请求的远程地址(如果知道的话)
            traces.[].request.uri           String      请求的URI
            traces.[].request.headers       Object      请求的标头,按标头名称键控
            traces.[].request.headers.*.[]  Array       标头的值
            traces.[].response.status       Number      答复情况
            traces.[].response.headers      Object      响应的标题,按标题名称键定
            traces.[].response.headers.*.[] Array       标头的值
            traces.[].session               Object      与交换相关的会话(如果有的话)
            traces.[].session.id            String       会话的ID
            traces.[].timeTaken             Number       用来处理交换的时间,以毫秒为单位    
        (http://localhost:8080/actuator/httptrace)
    [9]info: 提供有关应用程序的一般信息(springboot提供build和git贡献)
        (1)build响应结构: 
            路径                            类型        描述
            artifact                        String      应用程序的工件ID(如果有的话)
            group                           String      应用程序的组ID(如果有的话)
            name                            String      应用程序的名称(如果有的话)
            version                         String      应用程序的版本(如果有的话)
            time                            Varies      应用程序构建的时间戳(如果有的话)
        (2)git响应结构:
            路径                            类型        描述
            branch                          String      Git分支机构的名称(如果有的话)
            commit                          Object      Git提交的细节,如果有的话
            commit.time                     Varies      提交的时间戳(如果有的话)
            commit.id                       String      提交的ID(如果有的话)
        (http://localhost:8080/actuator/info)
    [10]liquibase: 提供有关Liquibase应用的数据库更改集的信息
        (1)响应结构:
            路径                            类型        描述
            contexts                        Object      由id键确定的应用程序上下文
            contexts.*.liquibaseBeans.*     Array       更改由Liquibase bean创建的集合,并按bean名称进行键控
                .changeSets        
            contexts.*.liquibaseBeans.*     String      变更集的作者
                .changeSets[].author    
            contexts.*.liquibaseBeans.*     String      包含更改集的更改日志
                .changeSets[].changeLog    
            contexts.*.liquibaseBeans.*     String      对更改集的注释
                .changeSets[].comments    
            contexts.*.liquibaseBeans.*     Array       更改集的上下文
                .changeSets[].contexts    
            contexts.*.liquibaseBeans.*     String      执行更改集的时间戳
                .changeSets[].dateExecuted  
            contexts.*.liquibaseBeans.*     String      运行更改集的部署的ID
                .changeSets[].deploymentId  
            contexts.*.liquibaseBeans.*     String      更改集的描述
                .changeSets[].description   
            contexts.*.liquibaseBeans.*     String      更改集的执行类型(EXECUTED,FAILED,SKIPPED,RERAN,MARK_RAN)
                .changeSets[].execType    
            contexts.*.liquibaseBeans.*     String      更改集的ID
                .changeSets[].id    
            contexts.*.liquibaseBeans.*     Array       更改集关联的标签
                .changeSets[].labels    
            contexts.*.liquibaseBeans.*     String      更改集的校验和
                .changeSets[].checksum    
            contexts.*.liquibaseBeans.*     Number      更改集的执行顺序
                .changeSets[].orderExecuted 
            contexts.*.liquibaseBeans.*     String      与更改集关联的标记(如果有的话)
                .changeSets[].tag    
            contexts.*.parentId             String      父应用程序上下文的ID(如果有的话)
        (http://localhost:8080/actuator/liquibase)
    [11]loggers: 提供对应用程序记录器及其级别配置的访问
        (1)检索所有记录器的响应结构:
            路径                            类型        描述
            levels                          Array       日志系统支持的级别
            loggers                         Object      以名字命名的伐木者
            loggers.*.configuredLevel       String      已配置的记录器级别(如果有的话)
            loggers.*.effectiveLevel        String      记录器的有效水平
            (http://localhost:8080/actuator/loggers)
        (2)检索单个记录器的响应结构:
            路径                            类型        描述
            configuredLevel                 String      已配置的记录器级别(如果有的话)
            effectiveLevel                  String      记录器的有效水平
            (http://localhost:8080/actuator/loggers/com.example)
    [12]mappings: 提供有关应用程序的请求映射的信息
        (1)响应结构:
            路径                            类型        描述
            contexts                        Object      由id键确定的应用程序上下文
            contexts.*.mappings             Object      上下文中的映射,按映射类型键控
            contexts.*.mappings             Object      Dispatcher servlet映射(如果有的话)
                .dispatcherServlets    
            contexts.*.mappings             Array       servlet过滤器映射(如果有的话)
                .servletFilters    
            contexts.*.mappings.servlets    Array       servlet映射(如果有的话)
            contexts.*.mappings             Object      Dispatcher处理程序映射(如果有的话)
                .dispatcherHandlers    
            contexts.*.parentId             String      父应用程序上下文的ID(如果有的话)
            (http://localhost:41235/actuator/mappings)
        (2)dispatcherServlets的响应结构:
            路径                                    类型        描述
            *                                       Array        Dispatcherservlet映射(若有的话)是由DispatcherServlet bean名称键决定的
            *.[].details                            Object       其他实现-映射的特定细节,可选的
            *.[].handler                            String       映射的处理程序
            *.[].predicate                          String       映射的谓词
            *.[].details.handlerMethod              Object       将处理到此映射的请求的方法(如果有的话)的详细信息
            *.[].details.handlerMethod.className    String       方法类的完全限定名
                        
            *.[].details.handlerMethod.name         String       方法的名称
            *.[].details.handlerMethod.descriptor   String       Java语言规范中指定的方法的描述符
                    
            *.[].details.requestMappingConditions   Object       请求映射条件的详细信息
            *.[].details.requestMappingConditions   Array        消费条件的详细信息
                .consumes    
            *.[].details.requestMappingConditions   String       消耗的媒体类型
                .consumes.[].mediaType    
            *.[].details.requestMappingConditions   Boolean      是否否定媒体类型
                .consumes.[].negated    
            *.[].details.requestMappingConditions   Array        标题条件的详细信息
                .headers    
            *.[].details.requestMappingConditions   String       标题的名称
                .headers.[].name    
            *.[].details.requestMappingConditions   String       标头的要求值(如果有的话)
                .headers.[].value    
            *.[].details.requestMappingConditions   Boolean      值是否被否定
                .headers.[].negated    
            *.[].details.requestMappingConditions   Array        处理的http方法
                .methods    
            *.[].details.requestMappingConditions   Array        Params条件的细节
                .params    
            *.[].details.requestMappingConditions   String       参数的名称
                .params.[].name    
            *.[].details.requestMappingConditions   String       参数的要求值(如果有的话)
                .params.[].value    
            *.[].details.requestMappingConditions   Boolean      值是否被否定
                .params.[].negated    
            *.[].details.requestMappingConditions   Array        标识映射处理的路径的模式
                .patterns    
            *.[].details.requestMappingConditions   Array        生产条件的详细信息
                .produces    
            *.[].details.requestMappingConditions   String       制作媒体类型
                .produces.[].mediaType    
            *.[].details.requestMappingConditions   Boolean      是否否定媒体类型
                .produces.[].negated    
        (3)servlets响应结构:
            路径                                    类型        描述
            [].mappings                             Array       servlet的映射
            [].name                                 String      servlet的名称
            [].className                            String      servlet的类名
        (4)dispatcherHandlers响应结构:
            路径                                    类型        描述
            *                                       Array       Dispatcher处理程序映射(若有的话)由Dispatcher处理程序bean名称键决定
            *.[].details                            Object      其他实现-映射的特定细节,可选的
            *.[].handler                            String      映射的处理程序
            *.[].predicate                          String      映射的谓词
            *.[].details.requestMappingConditions   Object      请求映射条件的详细信息
            *.[].details.requestMappingConditions   Array       消费条件的详细信息
                .consumes    
            *.[].details.requestMappingConditions   String      消耗的媒体类型
                .consumes.[].mediaType    
            *.[].details.requestMappingConditions   Boolean     是否否定媒体类型
                .consumes.[].negated    
            *.[].details.requestMappingConditions   Array       标题条件的详细信息
                .headers    
            *.[].details.requestMappingConditions   String      标题的名称
                .headers.[].name    
            *.[].details.requestMappingConditions   String      标头的要求值(如果有的话)
                .headers.[].value    
            *.[].details.requestMappingConditions   Boolean     值是否被否定
                .headers.[].negated    
            *.[].details.requestMappingConditions   Array       处理的http方法
                .methods    
            *.[].details.requestMappingConditions   Array       Params条件的细节
                .params    
            *.[].details.requestMappingConditions   String      参数的名称
                .params.[].name    
            *.[].details.requestMappingConditions   String      参数的要求值(如果有的话)
                .params.[].value    
            *.[].details.requestMappingConditions   Boolean     值是否被否定
                .params.[].negated    
            *.[].details.requestMappingConditions   Array       标识映射处理的路径的模式
                .patterns    
            *.[].details.requestMappingConditions   Array       生产条件的详细信息
                .produces    
            *.[].details.requestMappingConditions   String      制作媒体类型
                .produces.[].mediaType    
            *.[].details.requestMappingConditions   Boolean     是否否定媒体类型
                .produces.[].negated    
            *.[].details.handlerMethod              Object      将处理到此映射的请求的方法(如果有的话)的详细信息
            *.[].details.handlerMethod.className    String      方法类的完全限定名
            *.[].details.handlerMethod.name         String      方法的名称
            *.[].details.handlerMethod.descriptor   String      Java语言规范中指定的方法的描述符
            *.[].details.handlerFunction            Object      将处理到此映射的请求的函数(如果有的话)的详细信息
            *.[].details.handlerFunction.className  String      函数类的完全限定名
    [13]metrics: 提供对应用程序度量的访问
        (1)检索度量名称响应结构:
            路径                                    类型        描述
            names                                   Array       已知度量的名称
            (http://localhost:8080/actuator/metrics)
        (2)检索指定度量:
            查询参数:
                参数        描述
                tag         用于表格中向下钻取的标记;(name:value)
            响应结构:
                路径                                类型        描述
                name                                String      度量的名称
                measurements                        Array       公制的测量
                measurements[].statistic            String      计量统计(TOTAL, TOTAL_TIME, COUNT, MAX, VALUE, UNKNOWN, ACTIVE_TASKS, DURATION)
                measurements[].value                Number      测量值
                availableTags                       Array       可用于向下钻取的标签
                availableTags[].tag                 String      标签的名字
                availableTags[].values              Array       标记的可能值
                计量统计: TOTAL, TOTAL_TIME, COUNT, MAX, VALUE, UNKNOWN, ACTIVE_TASKS, DURATION
            (http://localhost:8080/actuator/metrics/jvm.memory.max)
            (http://localhost:8080/actuator/metrics/jvm.memory.max?tag=area%3Anonheap&tag=id%3ACompressed+Class+Space)
    [14]scheduledtasks: 提供有关应用程序的计划任务的信息
        (1)响应结构:
            路径                                    类型        描述
            cron                                    Array       Cron任务(如果有的话)
            cron.[]runnable.target                  String      将被执行的目标
            cron.[].expression                      String      Cron表达式
            fixedDelay                              Array       固定延迟任务(如果有的话)
            fixedDelay.[]runnable.target            String      将被执行的目标
            fixedDelay.[].initialDelay              Number      延迟,以毫秒为单位,在第一次执行之前
            fixedDelay.[].interval                  Number      间隔,以毫秒为单位,介于上一次执行结束和下一次执行开始之间
            fixedRate                               Array       固定费率任务(如果有的话)
            fixedRate.[].runnable.target            String      将被执行的目标
            fixedRate.[].interval                   Number      间隔,以毫秒为单位,间隔于每次执行的开始之间
            fixedRate.[].initialDelay               Number      延迟,以毫秒为单位,在第一次执行之前
            (http://localhost:8080/actuator/scheduledtasks)
    [15]sessions: 提供有关由Spring会话管理的应用程序的HTTP会话的信息
        (1)检索会话
            查询参数:
                参数        描述
                username    用户名称
            响应结构:
                路径                                类型        描述
                sessions                            Array       给定用户名的会话
                sessions.[].id                      String      会话的ID
                sessions.[].attributeNames          Array       存储在会话中的属性的名称
                sessions.[].creationTime            String      创建会话的时间戳
                sessions.[].lastAccessedTime        String      上次访问会话的时间戳
                sessions.[].maxInactiveInterval     Number      会话到期前的最长允许不活动时间(以秒为单位)
                sessions.[].expired                 Boolean     会话是否已过期
            (http://localhost:8080/actuator/sessions?username=alice)
        (2)检索单个会话:
            响应结构:
                路径                                类型        描述
                id                                  String      会话的ID
                attributeNames                      Array       存储在会话中的属性的名称
                creationTime                        String      创建会话的时间戳
                lastAccessedTime                    String      上次访问会话的时间戳
                maxInactiveInterval                 Number      会话到期前的最长允许不活动时间(以秒为单位)
                expired                             Boolean     会话是否已过期
            (http://localhost:8080/actuator/sessions/4db5efcc-99cb-4d05-a52c-b49acfbb7ea9)
    [16]shutdown: 用于关闭应用程序
        (1)响应结构:
            路径                                类型        描述
            message                             String      描述请求结果的消息
            (http://localhost:8080/actuator/shutdown)
    [17]threaddump: 提供了来自应用程序JVM的线程转储
        (1)响应结构:
            路径                                 类型        描述
            threads                              Array       JVM的线程
            threads.[].blockedCount              Number      线程被阻塞的总次数
            threads.[].blockedTime               Number      线程阻塞的时间以毫秒为单位(-1如果禁用线程争用监视)
            threads.[].daemon                    Boolean     线程是否是守护进程线程(仅在Java 9或更高版本上可用)
            threads.[].inNative                  Boolean     线程是否正在执行本机代码
            threads.[].lockName                  String      线程被阻塞的对象的描述(如果有的话)
            threads.[].lockInfo                  Object      对象,其线程被阻塞等待
            threads.[].lockInfo.className        String      锁定对象的完全限定类名
            threads.[].lockInfo.identityHashCode Number      锁定对象的标识哈希代码
            threads.[].lockedMonitors            Array       被此线程锁定的监视器(如果有的话)
            threads.[].lockedMonitors.[]         String      锁对象的类名
                .className                       
            threads.[].lockedMonitors.[]         Number      锁定对象的标识哈希代码
                .identityHashCode                
            threads.[].lockedMonitors.[]         Number      锁定监视器的堆栈深度
                .lockedStackDepth                
            threads.[].lockedMonitors.[]         Object      锁定监视器的堆栈帧
                .lockedStackFrame        
            threads.[].lockedSynchronizers       Array       被此线程锁定的同步器
            threads.[].lockedSynchronizers.[]    String      锁定同步器的类名
                .className        
            threads.[].lockedSynchronizers.[]    Number      锁定同步器的标识哈希码
                .identifyHashCode        
            threads.[].lockOwnerId               Number      拥有阻塞线程的对象的线程的ID(-1如果线程未被阻塞)
            threads.[].lockOwnerName             String      拥有阻塞线程对象的线程的名称(如果有的话)
            threads.[].priority                  Number      线程的优先级(仅在Java 9或更高版本上可用)
            threads.[].stackTrace                Array       线程的堆栈跟踪
            threads.[].stackTrace.[]             String      包含此条目标识的执行点的类加载器的名称(若有的话)(仅在Java 9或更高版本上可用)
                .classLoaderName            
            threads.[].stackTrace.[].className   String      包含此条目标识的执行点的类的名称
            threads.[].stackTrace.[].fileName    String      包含此条目标识的执行点的源文件的名称(若有的话)
            threads.[].stackTrace.[].lineNumber  Number      此条目标识的执行点的行号(若未知的话是否定的)
            threads.[].stackTrace.[].methodName  String      方法的名称
            threads.[].stackTrace.[].moduleName  String      包含此条目标识的执行点的模块名称(若有的话)(仅在Java 9或更高版本上可用)
            threads.[].stackTrace.[]             String      包含此条目标识的执行点的模块的版本(若有的话)(仅在Java 9或更高版本上可用)
                .moduleVersion        
            threads.[].stackTrace.[]             Boolean     执行点是否是本机方法
                .nativeMethod        
            threads.[].suspended                 Boolean     线程是否挂起
            threads.[].threadId                  Number      线程的ID
            threads.[].threadName                String      线程的名称
            threads.[].threadState               String      线程的状态(NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED)
            threads.[].waitedCount               Number      线程等待通知的总次数
            threads.[].waitedTime                Number      线程等待的时间以毫秒为单位(-1如果禁用线程争用监视)
        (http://localhost:8080/actuator/threaddump)
```
