```
1.引入相关依赖:
    <!-- web依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- quartz依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-quartz</artifactId>
    </dependency>
    <!-- mysql驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <!-- druid数据源驱动 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.21</version>
    </dependency>
    <!-- mybatis依赖 -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.1</version>
    </dependency>
    <!-- pagehelper依赖 -->
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.2.12</version>
    </dependency>
    <!-- JDBC连接池 -->
    <dependency>
        <groupId>com.mchange</groupId>
        <artifactId>c3p0</artifactId>
        <version>0.9.5.4</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.9</version>
    </dependency>
2.编写任务调度配置类:
    @Configuration
    public class SchedulerConfig {
        // PropertiesFactoryBean: spring管理配置文件的工厂类
        @Bean
        public Properties quartzProperties() throws IOException {
            PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
            // 通过ClassPathResource将属性文件转化Resource对象,并交由PropertiesFactoryBean管理
            propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
            //在quartz.properties中的属性被读取并注入后再初始化对象
            propertiesFactoryBean.afterPropertiesSet();
            return propertiesFactoryBean.getObject();
        }
        @Bean("schedulerFactory")
        public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
            SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
            factoryBean.setQuartzProperties(quartzProperties());
            return factoryBean;
        }
        //quartz初始化监听器
        //这个监听器可以监听到工程的启动,在工程停止再启动时可以让已有的定时任务继续进行
        @Bean
        public QuartzInitializerListener initializerListener(){
            return new QuartzInitializerListener();
        }
        //通过SchedulerFactoryBean获取Scheduler的实例
        @Bean("scheduler")
        public Scheduler scheduler() throws IOException {
            return schedulerFactoryBean().getScheduler();
        }
    }
3.编写quartz.properties文件:(/src/main/resource/)
    #设置线程池的实现类
    org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
    #设置线程池里的现场个数
    org.quartz.threadPool.threadCount=5
    #设置优先级,默认值为5
    org.quartz.threadPool.threadPriority=5
    org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread=true
    #被认为misfired前的容忍时间
    org.quartz.jobStore.misfireThreshold=5000
    #设置持久化策略
    org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
    #设置DriverDelegate的具体类来负责执行特定数据库可能需要的任何JDBC工作
    org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
    #设置JDBCJobStore将JobDataMaps中的所有值都作为字符串存储
    org.quartz.jobStore.useProperties=true
    #配置表前缀
    org.quartz.jobStore.tablePrefix=QRTZ_
    #设置JobStore使用哪个DataSource
    org.quartz.jobStore.dataSource=qzDS
    #配置数据库连接池
    org.quartz.dataSource.qzDS.driver=com.mysql.cj.jdbc.Driver
    org.quartz.dataSource.qzDS.URL=jdbc:mysql://localhost:3306/quartz?serverTimezone=GMT%2B8
    org.quartz.dataSource.qzDS.user=root
    org.quartz.dataSource.qzDS.password=root
    org.quartz.dataSource.qzDS.maxConnections=10
4.编写任务类:
    [1]基础任务接口BaseJob:
        public interface BaseJob extends Job {
            @Override
            void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException;
        }
    [2]任务的具体实现类HelloJob:
        public class HelloJob implements BaseJob {
            private static Logger logger = LoggerFactory.getLogger(HelloJob.class);
            public HelloJob() {}
            @Override
            public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                logger.info("HelloJob执行时间: " + new Date());
            }
        }
    [3]任务的具体实现类NewJob:
        public class NewJob implements BaseJob {
            private static Logger logger = LoggerFactory.getLogger(NewJob.class);
            public NewJob() {}
            @Override
            public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                logger.info("NewJob执行的时间: " + new Date());
            }
        }
5.编写任务触发信息类:
    public class JobAndTrigger implements Serializable {
        // JobDetail
        private String jobName;
        private String jobGroup;
        private String jobClassName;
        // Trigger
        private String triggerName;
        private String triggerGroup;
        // SimpleTrigger
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        private Date startTime;
        private Integer repeatCount;
        private Long repeatInterval;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        private Date endTime;
        // CronTrigger
        private String cronExpression;
        private String timeZoneId;
        // get,set略
    }
6.编写任务触发信息Mapper接口及实现:
    [1]接口:
        @Repository
        @Mapper
        public interface JobAndTriggerMapper {
            List<JobAndTrigger> getTriggerAndDetails();
        }
    [2]执行目录(/src/main/resources/file/)下的quartz-mysql.sql(官方建表语句);
        (注: 我使用的数据库的名称为: "quartz",如需更改,请将涉及到数据库配置的地方都改掉)
    [2]xml实现:
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <mapper namespace="com.example.quartz.dao.JobAndTriggerMapper">
            <select id="getTriggerAndDetails" resultType="com.example.quartz.entity.JobAndTrigger">
                SELECT DISTINCT
                T1.JOB_NAME         AS "jobName"
                ,T1.JOB_GROUP       AS "jobGroup"
                ,T1.JOB_CLASS_NAME  AS "jobClassName"
                ,T2.TRIGGER_NAME    AS "triggerName"
                ,T2.TRIGGER_GROUP   AS "triggerGroup"
                ,T3.CRON_EXPRESSION AS "cronExpression"
                ,T3.TIME_ZONE_ID    AS "timeZoneId"
                ,FROM_UNIXTIME(T2.START_TIME/1000)      AS "startTime"
                ,T4.REPEAT_COUNT    AS "repeatCount"
                ,T4.REPEAT_INTERVAL AS "repeatInterval"
                FROM QRTZ_JOB_DETAILS T1
                INNER JOIN QRTZ_TRIGGERS T2
                ON T1.JOB_GROUP= T2.TRIGGER_GROUP
                AND T1.JOB_NAME = T2.JOB_NAME
                LEFT JOIN QRTZ_CRON_TRIGGERS T3
                ON T2.TRIGGER_NAME = T3.TRIGGER_NAME
                AND T2.TRIGGER_GROUP = T3.TRIGGER_GROUP
                LEFT JOIN QRTZ_SIMPLE_TRIGGERS T4
                ON T2.TRIGGER_NAME = T4.TRIGGER_NAME
                AND T2.TRIGGER_GROUP = T4.TRIGGER_GROUP
            </select>
        </mapper>
    
7.在yml文件中配置启动端口,数据源,mybatis:
    server:
        port: 8080
    spring:
        datasource:
            druid:
                type: com.alibaba.druid.pool.DruidDataSource
                driver-class-name: com.mysql.cj.jdbc.Driver
                url: jdbc:mysql://localhost:3306/quartz?serverTimezone=GMT%2B8
                username: root
                password: root
                # 连接池的配置
                initial-size: 5
                min-idle: 5
                max-active: 20
                # 连接等待超时时间
                max-wait: 30000
                # 配置检测可以关闭的空闲连接间隔时间
                time-between-eviction-runs-millis: 60000
                # 配置连接在池中的最小生存时间
                min-evictable-idle-time-millis: 300000
                validation-query: select '1' from dual
                test-while-idle: true
                test-on-borrow: false
                test-on-return: false
                # 打开PSCache,并且指定每个连接上的PSCache的大小
                pool-prepared-statements: true
                max-open-prepared-statements: 20
                max-pool-prepared-statement-per-connection-size: 20
                # 配置监控统计拦截的filters,去掉后监控界面的sql无法统计,'wall'用于防火墙
                filters: stat,wall
                # Spring监控的AOP切入点,如x.y.z.service.*,配置多个英文逗号分隔
                aop-patterns: com.springboot.service.*
                #WebStatFilter配置
                web-stat-filter:
                    enabled: true
                    # 添加过滤规则
                    url-pattern: /*
                    # 忽略过滤的格式
                    exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
                #StatViewFilter配置
                stat-view-servlet:
                    enabled: true
                    #设置进入Druid监控界面的请求
                    url-pattern: /druid/*
                    #是否能够重置数据
                    reset-enable: false
                    #设置访问Druid控制台的账户和密码
                    #login-username: admin
                    #login-password: admin
                    # IP白名单
                    # allow: 127.0.0.1
                    #　IP黑名单（共同存在时，deny优先于allow）
                    # deny: 192.168.1.218
                #配置StatFilter
                filter:
                    stat:
                        log-slow-sql: true
                    wall:
                        config:
                            multi-statement-allow: true
    mybatis:
        # mapper xml实现扫描路径
        # 如果Mapper.xml与Mapper.class在同一个包下且同名,spring扫描Mapper.class的同时会自动扫描
        # 同名的Mapper.xml并装配到Mapper.class;如果Mapper.xml与Mapper.class不在同一个包下或者不
        # 同名,就必须使用配置mapperLocations指定mapper.xml的位置
        mapper-locations: classpath:mapper/*.xml
        configuration:
            # debug时log打印到控制台的两种方式:
            #   1.logging.level.yourdaoclasspackagename=debug (指定某个包下的SQL打印出来)
            #   2.mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
            #     (所有包下的SQL都会打印出来)
            log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
8.编写任务触发信息Service接口及实现:
    [1]接口:
        public interface JobAndTriggerService {
            List<JobAndTrigger> getTriggerAndDetails();
            void addjob(JobAndTrigger jobAndTrigger);
            void pausejob(String triggerName, String triggerGroup);
            void resumejob(String triggerName, String triggerGroup);
            void deletejob(String triggerName, String triggerGroup);
            void reschedulejob(JobAndTrigger jobAndTrigger);
        }
    [2]实现类:
        @Service
        public class JobAndTriggerServiceImpl implements JobAndTriggerService {
            @Autowired
            private JobAndTriggerMapper mapper;
            @Autowired
            @Qualifier("scheduler")
            private Scheduler scheduler;
            private static Logger logger=LoggerFactory.getLogger(JobAndTriggerServiceImpl.class);
            @Override
            public List<JobAndTrigger> getTriggerAndDetails() {
                return  mapper.getTriggerAndDetails();
            }
            @Override
            public void addjob(JobAndTrigger jobAndTrigger) {
                try {
                    if(StringUtils.isNoneEmpty(
                            jobAndTrigger.getJobName()
                            ,jobAndTrigger.getJobClassName()
                            ,jobAndTrigger.getJobGroup()
                            ,jobAndTrigger.getTriggerName()
                            ,jobAndTrigger.getTriggerGroup())){
                        if(StringUtils.isNoneEmpty(jobAndTrigger.getCronExpression())){
                            addCronJob(jobAndTrigger);
                        }else {
                            addSimpleJob(jobAndTrigger);
                        }
                    }
                }catch (Exception e){
                    logger.info("创建定时任务失败");
                    e.printStackTrace();
                }
            }
            //CronTrigger
            private void addCronJob(JobAndTrigger jobAndTrigger) throws Exception {
                // 启动Scheduler
                scheduler.start();
                // 构建JobDetail
                JobDetail jobDetail = JobBuilder.newJob(
                    getClass(jobAndTrigger.getJobClassName()).getClass())
                        .withIdentity(jobAndTrigger.getJobName(), jobAndTrigger.getJobGroup())
                        .build();
                // 根据参数中的Cron表达式构建ScheduleBuilder
                CronScheduleBuilder cronSchedule = 
                    CronScheduleBuilder.cronSchedule(jobAndTrigger.getCronExpression());
                if(!StringUtils.isEmpty(jobAndTrigger.getTimeZoneId())){
                    cronSchedule.inTimeZone(TimeZone.getTimeZone(jobAndTrigger.getTimeZoneId()));
                }
                // 构建CronTrigger
                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobAndTrigger.getTriggerName(),jobAndTrigger.getTriggerGroup())
                    .withSchedule(cronSchedule)
                    .build();
                // 将jobDetail和CronTrigger绑定,并纳入到Scheduler中
                scheduler.scheduleJob(jobDetail,cronTrigger);
            }
            //SimpleTrigger
            private void addSimpleJob(JobAndTrigger jobAndTrigger) throws Exception {
                // 启动Scheduler
                scheduler.start();
                // 构建JobDetail
                JobDetail jobDetail = JobBuilder.newJob(
                    getClass(jobAndTrigger.getJobClassName()).getClass())
                        .withIdentity(jobAndTrigger.getJobName(), jobAndTrigger.getJobGroup())
                        .build();
                // 构建SimpleTrigger
                SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobAndTrigger.getTriggerName(),jobAndTrigger.getTriggerGroup())
                    .startAt(jobAndTrigger.getStartTime())
                    .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(jobAndTrigger.getRepeatInterval())
                            .withRepeatCount(jobAndTrigger.getRepeatCount()))
                    .build();
                // 将jobDetail和simpleTrigger绑定,并纳入到Scheduler中
                scheduler.scheduleJob(jobDetail,simpleTrigger);
            }
            @Override
            public void pausejob(String triggerName, String triggerGroup) {
                try {
                    scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName,triggerGroup));
                }catch (SchedulerException e){
                    logger.info("定时任务暂停失败");
                    e.printStackTrace();
                }
            }
            @Override
            public void resumejob(String triggerName, String triggerGroup) {
                try {
                    scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName,triggerGroup));
                }catch (SchedulerException e){
                    logger.info("定时任务恢复失败");
                    e.printStackTrace();
                }
            }
            @Override
            public void deletejob(String triggerName, String triggerGroup) {
                try {
                    scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName,triggerGroup));
                }catch (SchedulerException e){
                    logger.info("定时任务删除失败");
                    e.printStackTrace();
                }
            }
            @Override
            public void reschedulejob(JobAndTrigger jobAndTrigger) {
                try {
                    if(StringUtils.isNoneEmpty(
                            jobAndTrigger.getTriggerName()
                            ,jobAndTrigger.getTriggerGroup())){
                        if(StringUtils.isNoneEmpty(jobAndTrigger.getCronExpression())){
                            updateCronJob(jobAndTrigger);
                            return;
                        }
                        if (jobAndTrigger.getStartTime()!= null
                                || jobAndTrigger.getRepeatCount() != null
                                || jobAndTrigger.getRepeatInterval() != null
                                || jobAndTrigger.getEndTime() != null){
                            updateSimpleJob(jobAndTrigger);
                        }
                    }
                }catch (SchedulerException e){
                    logger.info("定时任务更新失败");
                    e.printStackTrace();
                }
            }
            private void updateCronJob(JobAndTrigger jobAndTrigger) throws SchedulerException {
                TriggerKey triggerKey = TriggerKey.triggerKey(
                    jobAndTrigger.getTriggerName(),jobAndTrigger.getTriggerGroup());
                // 根据参数中的Cron表达式构建ScheduleBuilder
                CronScheduleBuilder cronSchedule = 
                    CronScheduleBuilder.cronSchedule(jobAndTrigger.getCronExpression());
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                CronTrigger cronTrigger = trigger.getTriggerBuilder()
                    .withIdentity(triggerKey).withSchedule(cronSchedule).build();
                scheduler.rescheduleJob(triggerKey,cronTrigger);
            }
            private void updateSimpleJob(JobAndTrigger jobAndTrigger) throws SchedulerException {
                Date startTime = jobAndTrigger.getStartTime();
                Integer count = jobAndTrigger.getRepeatCount();
                Long interval = jobAndTrigger.getRepeatInterval();
                Date endTime = jobAndTrigger.getEndTime();
                TriggerKey triggerKey = TriggerKey.triggerKey(
                    jobAndTrigger.getTriggerName(), jobAndTrigger.getTriggerGroup());
                SimpleTrigger trigger = (SimpleTrigger)scheduler.getTrigger(triggerKey);
                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                if(count != null){ scheduleBuilder.withRepeatCount(count); }
                if(interval != null){ scheduleBuilder.withIntervalInMilliseconds(interval); }
                TriggerBuilder<SimpleTrigger> triggerBuilder = 
                    trigger.getTriggerBuilder().withIdentity(triggerKey);
                if(startTime != null){ triggerBuilder.startAt(startTime); }
                if(endTime != null){ triggerBuilder.endAt(endTime); }
                if(count != null || interval != null){triggerBuilder.withSchedule(scheduleBuilder);}
                scheduler.rescheduleJob(triggerKey,triggerBuilder.build());
            }
            private BaseJob getClass(String className) throws Exception {
                Class<?> clazz = Class.forName(className);
                return (BaseJob)clazz.newInstance();
            }
        }
9.编写Controller:
    @RestController
    @RequestMapping("/job")
    public class SchedulerController {
        @Autowired
        private JobAndTriggerService service;
        // 查询任务列表
        @GetMapping("/queryjob")
        public Map<String, Object> queryjob(
            @RequestParam("pageNum") Integer pageNum,@RequestParam("pageSize") Integer pageSize){
            PageHelper.startPage(pageNum,pageSize);
            List<JobAndTrigger> list = service.getTriggerAndDetails();
            PageInfo<JobAndTrigger> page = new PageInfo<>(list);
            Map<String,Object> map = new HashMap<>();
            map.put("JobAndTrigger",page);
            map.put("number", page.getTotal());
            return map;
        }
        // 添加任务
        @PostMapping("/addjob")
        public void addjob(@RequestBody JobAndTrigger jobAndTrigger){
            this.service.addjob(jobAndTrigger);
        }
        //暂停任务
        @PostMapping("/pausejob")
        public void pausejob(
            @RequestParam("triggerName") String triggerName
            ,@RequestParam("triggerGroup") String triggerGroup) {
            this.service.pausejob(triggerName,triggerGroup);
        }
        //恢复任务
        @PostMapping("/resumejob")
        public void resumejob(
            @RequestParam("triggerName") String triggerName
            ,@RequestParam("triggerGroup") String triggerGroup){
            this.service.resumejob(triggerName,triggerGroup);
        }
        //删除任务
        @PostMapping("/deletejob")
        public void deletejob(
            @RequestParam("triggerName") String triggerName
            ,@RequestParam("triggerGroup") String triggerGroup){
            this.service.deletejob(triggerName,triggerGroup);
        }
        //更新任务
        @PostMapping("/reschedulejob")
        public void reschedulejob(@RequestBody JobAndTrigger jobAndTrigger){
            this.service.reschedulejob(jobAndTrigger);
        }
    }
10.注意事项:
    [1]springboot中mybatis设置驼峰映射的问题: (变更为上文的映射方式)
        当在properties文件或yml文件中设置了扫描xml文件:
            mybatis.mapperLocations= classpath:mapper/*.xml
        则设置驼峰映射后请求报错:
            mybatis.configuration.map-underscore-to-camel-case= true
    [2]从前端传递带日期的json对象,后端使用JavaBean接收时需要格式化日期格式和时区(否则使用默认时区)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    [3]使用postman通过请求体传递对象到后端时,需要在Headers中添加一下信息:
        Content-Type : application/json
```
