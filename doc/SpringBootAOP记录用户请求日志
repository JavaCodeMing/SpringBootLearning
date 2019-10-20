```text
在Spring框架中，使用AOP配合自定义注解可以方便的实现用户操作的监控。
1.引入mybatis依赖、mysql驱动依赖、druid数据源驱动依赖和aop依赖
    <dependency>
    	<groupId>org.mybatis.spring.boot</groupId>
    	<artifactId>mybatis-spring-boot-starter</artifactId>
    	<version>1.3.2</version>
    </dependency>
    <dependency>
    	<groupId>mysql</groupId>
    	<artifactId>mysql-connector-java</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
	<artifactId>druid-spring-boot-starter</artifactId>
	<version>1.1.10</version>
    </dependency>
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
2.在application.yml文件中配置Druid数据源连接池及监控
    server:
	servlet:
	    context-path: /web
    spring:
    	datasource:
    	    druid:
    	    # 数据库访问配置, 使用druid数据源, 该MySQL驱动类名为新的
    	    driver-class-name: com.mysql.cj.jdbc.Driver
    	    # 设置URL时,如果是MySQL可能出现时区的问题,查看springboot设置时区
    	    url: jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8
    	    username: root
    	    password: root
    	    # 连接池配置
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
    	    # 打开PSCache，并且指定每个连接上PSCache的大小
    	    pool-prepared-statements: true
    	    max-open-prepared-statements: 20
    	    max-pool-prepared-statement-per-connection-size: 20
    	    # 配置监控统计拦截的filters, 去掉后监控界面sql无法统计, 'wall'用于防火墙
    	    filters: stat,wall
    	    # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
    	    aop-patterns: com.springboot.servie.*    
    	    # WebStatFilter配置
    	    web-stat-filter:
    	    	enabled: true
    	    	# 添加过滤规则
    	    	url-pattern: /*
    	    	# 忽略过滤的格式
    	    	exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
    	    # StatViewServlet配置
    	    stat-view-servlet:
    	    	enabled: true
    	    	# 访问路径为/druid时，跳转到StatViewServlet
    	    	url-pattern: /druid/*
    	    	# 是否能够重置数据
    	    	reset-enable: false
    	    	# 需要账号密码才能访问控制台
    	    	#login-username: admin
    	    	#login-password: admin
    	    	# IP白名单
    	    	# allow: 127.0.0.1
    	    	#　IP黑名单（共同存在时，deny优先于allow）
    	    	# deny: 192.168.1.218
    	    # 配置StatFilter
    	    filter:
    	    	stat:
    	    	    log-slow-sql: true
    mybatis:
        # type-aliases扫描路径
        # type-aliases-package:
        # mapper xml实现扫描路径
        mapper-locations: classpath:mapper/*.xml
3.自定义注解(标注要监控的方法)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Log {
    	String value() default "";
    }
4.创建保存操作日志的库表:
    CREATE TABLE SYS_LOG (
    	ID 	    INTEGER(20)   NOT NULL  COMMENT 'ID',
    	USERNAME    VARCHAR(50)   NULL 	    COMMENT '用户名',
    	OPERATION   VARCHAR(50)   NULL 	    COMMENT '用户操作',
    	TIME 	    INTEGER(11)   NULL 	    COMMENT '响应时间',
    	METHOD 	    VARCHAR(200)  NULL 	    COMMENT '请求方法',
    	PARAMS 	    VARCHAR(500)  NULL 	    COMMENT '请求参数',
    	IP 	    VARCHAR(64)   NULL 	    COMMENT 'IP地址',
    	CREATETIME  DATE 	  NULL 	    COMMENT '创建时间'
    );
    ALTER TABLE SYS_LOG MODIFY ID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY;
5.库表对应的实体JavaBean:
    public class SysLog implements Serializable{
    	private static final long serialVersionUID = -6309732882044872298L;
    	private Integer id;
    	private String  username;
    	private String  operation;
    	private Integer time;
    	private String  method;
    	private String  params;
    	private String  ip;
    	private Date    createTime;
    	// get,set略
    }
6.编写Mapper接口:
  @Repository
  @Mapper
    public interface SysLogMapper {
    	void saveSysLog(SysLog syslog);
    }
7.编写Mapper的实现:
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    		"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.example.aoplog.dao.SysLogMapper">
    	<insert id="saveSysLog" >
    	    insert into SYS_LOG(id,userName,operation,time,method,params,ip,createTime)
    	    values(#{id},#{userName},#{operation},#{time},#{method},#{params},#{ip},#{createTime})
    	</insert>
    </mapper>
8.编写切面类定义切点: (注意添加两个工具类:HttpContextUtils,IPUtils)
    @Aspect
    @Component
    public class LogAspect {
    	@Autowired
    	private SysLogMapper sysLogMapper;
    	@Pointcut("@annotation(com.example.aoplog.annotation.Log)")
    	public void poincut(){}
    	@Around("poincut()")
    	public Object around(ProceedingJoinPoint point){
    	    Object result =null;
    	    long start = System.currentTimeMillis();
    	    try {
    	    	// 执行方法
    	    	result = point.proceed();
    	    } catch (Throwable e) {
    	    	e.printStackTrace();
    	    }
    	    long time = System.currentTimeMillis() - start;
    	    saveLog(point,time);
    	    return result;
    	}
    	private void saveLog(ProceedingJoinPoint point, long time) {
    	    MethodSignature signature = (MethodSignature) point.getSignature();
    	    Method method = signature.getMethod();
    	    SysLog sysLog = new SysLog();
    	    Log annotation = method.getAnnotation(Log.class);
    	    if (annotation != null){
    	    	sysLog.setOperation(annotation.value());
    	    }
    	    String className = point.getTarget().getClass().getName();
    	    String methodName = signature.getName();
    	    sysLog.setMethod(className+"."+methodName);
       
    	    Object[] args = point.getArgs();
    	    LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
    	    String[] paramNames = u.getParameterNames(method);
    	    if(args != null && paramNames != null){
    	    	StringBuffer params = new StringBuffer();
    	    	for (int i = 0; i < args.length; i++) {
    	    	    params.append(" "+paramNames[i]+": "+args[i]);
    	    	}
    	    	sysLog.setParams(params.toString());
    	    }
    	    HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
    	    sysLog.setIp(IPUtils.getIpAddr(request));
    	    sysLog.setUserName("admin");
    	    sysLog.setTime(time);
    	    sysLog.setCreateTime(new Date());
    	    sysLogMapper.saveSysLog(sysLog);
    	}
    }
9.编写Controller测试:
    @RestController
    public class TestController {
    	@Log("查询的方法")
    	@GetMapping("/query")
    	public void query() throws InterruptedException { Thread.sleep(200); }
    	@Log("新增的方法")
    	@PostMapping("/add")
    	public void add(String name,int age,char sex) throws InterruptedException {
    		Thread.sleep(500);
    	}
    	@Log("删除的方法")
    	@DeleteMapping("/delete")
    	public void delete(String name) throws InterruptedException { Thread.sleep(100); }
    }
10.测试并查看数据库表: http://localhost:8080/web/XXX
```
