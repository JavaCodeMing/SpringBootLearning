1.引入mybatis依赖、oracle驱动依赖、mysql驱动依赖、druid数据源驱动依赖和aop依赖:
	<dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>1.3.2</version>
    </dependency>
	<dependency>
        <groupId>com.oracle</groupId>
        <artifactId>ojdbc7</artifactId>
        <version>12.1.0.2</version>
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
2.在application.yml文件中配置多数据源:
	# 服务器项目根路径
	server:
		servlet:
			context-path: /web
	spring:
		datasource:
			druid:
				master:
					type: com.alibaba.druid.pool.DruidDataSource
					driver-class-name: com.mysql.cj.jdbc.Driver
					url: jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8
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
				slave:
					ds1:
						type: com.alibaba.druid.pool.DruidDataSource
						driver-class-name: oracle.jdbc.OracleDriver
						url: jdbc:oracle:thin:@localhost:1521:orcl
						username: scott
						password: tigger
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
			# 配置监控统计拦截的filters,去掉后监控界面的sql无法统计,'wall'用于防火墙(对于自定义多数据源，这样配置sql无法统计)
			#filters: stat,wall
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
				#allow: 127.0.0.1
				# IP黑名单（共同存在时，deny优先于allow）
				#deny: 192.168.1.218
			#配置StatFilter
			filter:
				stat:
					log-slow-sql: true
				wall:
					config:
						multi-statement-allow: true
	mybatis:
		# type-aliases扫描路径
		# type-aliases-package:
		# mapper xml实现扫描路径
		mapper-locations: classpath:mapper/*.xml
3.编写数据源的路由类:
	public class DynamicDataSource extends AbstractRoutingDataSource {
		//代码中的determineCurrentLookupKey方法取得一个字符串,
		//该字符串将与配置文件中的相应字符串进行匹配以定位数据源
		@Override
		protected Object determineCurrentLookupKey() {
			//DynamicDataSourceContextHolder代码中使用setDataSourceType 设置当前的数据源,
			//在路由类中使用getDataSourceType进行获取,交给AbstractRoutingDataSource进行注入使用
			return DynamicDataSourceContextHolder.getDataSourceType();
		}
	}
4.编写动态数据源上下文类:
	public class DynamicDataSourceContextHolder {
		//当使用ThreadLocal维护变量时,ThreadLocal为每个使用该变量的线程提供独立的变量副本,
		//所以每一个线程都可以独立地改变自己的副本,而不会影响其它线程所对应的副本
		private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
		//管理所有的数据源id;主要是为了判断数据源是否存在;
		public static List<String> dataSourceIds = new ArrayList<String>();
		//设置数据源
		public static void setDataSourceType(String dataSourceType) { contextHolder.set(dataSourceType); }
		//获取数据源
		public static String getDataSourceType() { return contextHolder.get(); }
		//清除数据源
		public static void clearDataSourceType() { contextHolder.remove(); }
		//判断指定DataSrouce当前是否存在
		public static boolean containsDataSource(String dataSourceId){
			return dataSourceIds.contains(dataSourceId);
		}
	}
5.编写动态数据源注册类:
    // 需要在入口类上添加导入注解: @Import(DynamicDataSourceRegister.class)
	public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
		//指定默认数据源(springboot2.0默认数据源是hikari如何想使用其他数据源可以自己配置)
		private static final String DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";
		//默认数据源
		private DataSource defaultDataSource;
		//用户自定义数据源
		private Map<String, DataSource> slaveDataSources = new HashMap<>();
		private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases(); //别名
		static {
			//由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
			aliases.addAliases("url", new String[]{"jdbc-url"});
			aliases.addAliases("username", new String[]{"user"});
		}
		@Override
		public void setEnvironment(Environment environment) {
			initDefaultDataSource(environment);
			initslaveDataSources(environment);
		}
		private void initDefaultDataSource(Environment env) {
			// 读取主数据源
			Binder binder = Binder.get(env);
			Map dsMap = binder.bind("spring.datasource.druid.master", Map.class).get();
			defaultDataSource = buildDataSource(dsMap);
		}
		private void initslaveDataSources(Environment env) {
			// 读取配置文件获取更多数据源
			Binder binder = Binder.get(env);
			HashMap map = binder.bind("spring.datasource.druid.slave", HashMap.class).get();
			for (Object o : map.entrySet()) {
				Map.Entry entry = (Map.Entry) o;
				String dsPrefix = (String) entry.getKey();
				Map<String, Object> dsMap = (Map<String, Object>) entry.getValue();
				DataSource ds = buildDataSource(dsMap);
				slaveDataSources.put(dsPrefix, ds);
			}
		}
		@Override
		public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
			Map<Object, Object> targetDataSources = new HashMap<>();
			//添加默认数据源
			targetDataSources.put("dataSource", this.defaultDataSource);
			DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");
			//添加其他数据源
			targetDataSources.putAll(slaveDataSources);
			DynamicDataSourceContextHolder.dataSourceIds.addAll(slaveDataSources.keySet());
			//创建DynamicDataSource
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClass(DynamicDataSource.class);
			beanDefinition.setSynthetic(true);
			MutablePropertyValues mpv = beanDefinition.getPropertyValues();
			mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
			mpv.addPropertyValue("targetDataSources", targetDataSources);
			//注册 - BeanDefinitionRegistry
			beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
		}
		private DataSource buildDataSource(Map dataSourceMap) {
			try {
				Object type = dataSourceMap.get("type");
				if (type == null) { type = DATASOURCE_TYPE_DEFAULT;} // 默认DataSource 
				Class<? extends DataSource> dataSourceType;
				dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
				ConfigurationPropertySource source = new MapConfigurationPropertySource(dataSourceMap);
				Binder binder = new Binder(source.withAliases(aliases));
				return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(dataSourceType)).get(); //通过类型绑定参数并获得实例对象
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
6.编写指定数据源注解类:
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface TargetDataSource {
		String value();
	}
7.编写切换数据源Advice类:
	@Aspect
	@Order(-1)//保证该AOP在@Transactional之前执行
	@Component
	public class DynamicDataSourceAspect {
		/*
		* @Before("@annotation(targetDataSource)"):
		*   @Before标注的方法会在@targetDataSource标注的方法之前执行
		*/
		@Before("@annotation(targetDataSource)")
		public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) throws Throwable {
			//获取当前的指定的数据源;
			String dsId = targetDataSource.value();
			//如果不在我们注入的所有的数据源范围之内，那么输出警告信息，系统自动使用默认的数据源。
			if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
				System.err.println("数据源[{}]不存在,使用默认数据源 > {}"+targetDataSource.value()+point.getSignature());
			} else {
				System.out.println("Use DataSource : {} > {}"+targetDataSource.value()+point.getSignature());
				//找到的话,那么设置到动态数据源上下文中
				DynamicDataSourceContextHolder.setDataSourceType(targetDataSource.value());
			}
		}
		@After("@annotation(targetDataSource)")
		public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
			System.out.println("Revert DataSource : {} > {}"+targetDataSource.value()+point.getSignature());
			//方法执行完毕之后,销毁当前数据源信息,进行垃圾回收
			DynamicDataSourceContextHolder.clearDataSourceType();
		}
	}
8.多数据源动态切换测试:
	[1]创建数据库表及插入数据(mysql5.5,oracle11g)
		Mysql:
			DROP TABLE IF EXISTS `student`;
			CREATE TABLE `student` (
			`sno` varchar(3) NOT NULL,
			`sname` varchar(9) NOT NULL,
			`ssex` char(2) NOT NULL,
			`database` varchar(10) DEFAULT NULL
			) DEFAULT CHARSET=utf8;
			INSERT INTO `student` VALUES ('001', '康康', 'M', 'mysql');
			INSERT INTO `student` VALUES ('002', '麦克', 'M', 'mysql');
		Oracle:
			DROP TABLE "SCOTT"."STUDENT";
			CREATE TABLE "SCOTT"."STUDENT" (
			"SNO" VARCHAR2(3 BYTE) NOT NULL ,
			"SNAME" VARCHAR2(9 BYTE) NOT NULL ,
			"SSEX" CHAR(2 BYTE) NOT NULL ,
			"database" VARCHAR2(10 BYTE) NULL 
			);
			INSERT INTO "SCOTT"."STUDENT" VALUES ('001', 'KangKang', 'M ', 'oracle');
			INSERT INTO "SCOTT"."STUDENT" VALUES ('002', 'Mike', 'M ', 'oracle');
			INSERT INTO "SCOTT"."STUDENT" VALUES ('003', 'Jane', 'F ', 'oracle');
			INSERT INTO "SCOTT"."STUDENT" VALUES ('004', 'Maria', 'F ', 'oracle');
	[2]编写Mapper接口:
		@Component
		@Mapper
		public interface StudentMapper {
			List<Map<String, Object>> getAllStudents();
		}
	[3]编写Mapper的实现:
	    // 需要在入口类上添加xml文件的扫描注解: @MapperScan("com.springboot.dao")
		<?xml version="1.0" encoding="UTF-8" ?>
		<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
				"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
		<mapper namespace="com.example.multidatasource.dao.StudentMapper">
			<select id="getAllStudents" resultType="java.util.Map">
				select * from student
			</select>
		</mapper>
	[4]编写Service接口:
		public interface StudentService {
			List<Map<String,Object>> getAllStudentsFromMysql();
			List<Map<String,Object>> getAllStudentsFromOracle();
		}
	[5]编写Service的实现类:
		@Service("studentService")
		public class StudentServiceImp implements StudentService {
			@Autowired
			private StudentMapper studentMapper;
			@Override
			@Transactional	//主数据源的事务(bug:从数据源不可使用该注解)
			public List<Map<String, Object>> getAllStudentsFromMaster() {
				return this.studentMapper.getAllStudents();
			}
			@Override
			@TargetDataSource("ds1")
			public List<Map<String, Object>> getAllStudentsFromSlave() {
				return this.studentMapper.getAllStudents();
			}
		}
	[6]编写Controller:
		@RestController
		public class DataSourceController {
			@Autowired
			private StudentService studentService;
			@RequestMapping("/querystudentsfromoracle")
			public List<Map<String,Object>> queryStudentsFromOracle(){
				return this.studentService.getAllStudentsFromSlave();
			}
			@RequestMapping("/querystudentsfrommysql")
			public List<Map<String,Object>> queryStudentsFromMysql(){
				return this.studentService.getAllStudentsFromMaster();
			}
		}
	[7]测试MySQL数据源: http://localhost:8080/web/querystudentsfrommysql
       测试Oracle数据源: http://localhost:8080/web/querystudentsfromoracle