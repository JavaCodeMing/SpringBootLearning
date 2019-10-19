# Spring Boot 整合MyBatis配置Druid多数据源

```text
1.回顾在Spring整合Mybatis时,在Spring的配置文件中配置MyBatis SqlSessionFactory的配置:
	<!-- mybatis 的SqlSessionFactory -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean" scope="prototype">
		<property name="dataSource" ref="dataSource"/>
		<property name="configLocation" value="classpath:mybatis-config.xml"/>
	</bean>
	(所以实际上在Spring Boot中配置MyBatis多数据源的关键在于创建SqlSessionFactory的时候为其分配不同的数据源)
2.引入mybatis依赖、oracle驱动依赖、mysql驱动依赖和druid数据源驱动依赖:
	<dependency>
		<groupId>org.mybatis.spring.boot</groupId>
		<artifactId>mybatis-spring-boot-starter</artifactId>
		<version>1.3.2</version>
	</dependency>
	<!-- oracle驱动(oracle的安装目录中有,但可能不新) -->
	<!-- https://mvnrepository.com/artifact/oracle/ojdbc7 -->
	<dependency>
		<groupId>oracle</groupId>
		<artifactId>ojdbc7</artifactId>
		<version>12.1.0.2</version>
	</dependency>
	<!-- mysql驱动 -->
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
	</dependency>
	<!-- druid数据源驱动 -->
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>druid-spring-boot-starter</artifactId>
		<version>1.1.10</version>
	</dependency>
3.安装Oracle驱动ojdbc7安装到本地Maven仓库:
	[1]下载ojdbc6.jar文件后,将其放到比较好找的目录下,比如D盘根目录。
	[2]运行Maven安装命令: (按Maven坐标安装进Maven仓库)
		mvn install:install-file -Dfile=D:/ojdbc7-12.1.0.2.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.2 -Dpackaging=jar
	[3]在项目的pom中引入Oracle驱动ojdbc7：
		<dependency>
			<groupId>com.oracle</groupId>
			<artifactId>ojdbc7</artifactId>
			<version>12.1.0.2</version>
		</dependency>
		(如果依赖引入无法解析,则可能是使用的Maven仓库没对应)
4.在application.yml文件中配置多数据源:
	# 项目服务器的根路径
	server:
		servlet:
			context-path: /web
	spring:
		datasource:
			druid:
				# 数据库访问配置,使用druid数据源(该数据源配置属于自定义部分,无自动提示)
				# 自定义连接池配置需要每个数据源各配置一整套,否则不生效
				# 数据源1 mysql
				mysql:
					driver-class-name: com.mysql.cj.jdbc.Driver
					url: jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8
					username: root
					password: root
					
					# mysql连接池的配置
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
				# 数据源2 oracle
				oracle:
					driver-class-name: oracle.jdbc.OracleDriver
					url: jdbc:oracle:thin:@localhost:1521:orcl
					username: scott
					password: tigger
					
					# oracle连接池的配置
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
					login-username: admin
					login-password: admin
					# IP白名单
					# allow: 127.0.0.1
					# IP黑名单（共同存在时，deny优先于allow）
					# deny: 192.168.1.218
				#配置StatFilter
				filter:
					stat:
						log-slow-sql: true
5.根据yml文件中配置的多数据源编写相应的配置类MysqlDatasourceConfig和OracleDatasourceConfig:
	@Configuration
	@MapperScan(basePackages = MysqlDatasourceConfig.PACKAGE, sqlSessionFactoryRef = "mysqlSqlSessionFactory")
	public class MysqlDatasourceConfig {
		// mysqldao扫描路径
		static final String PACKAGE = "com.springboot.mysqldao";
		// mybatis mapper 扫描路径
		static final String MAPPER_LOCATION = "classpath:mapper/mysql/*.xml";
		@Primary
		@Bean(name = "mysqldatasource")
		@ConfigurationProperties("spring.datasource.druid.mysql")
		public DataSource mysqlDataSource(){
			return DruidDataSourceBuilder.create().build();
		}
		@Primary
		@Bean(name="mysqlTransactionManager")
		public DataSourceTransactionManager mysqlTransactionManager(@Qualifier("mysqldatasource") DataSource dataSource){
			return new DataSourceTransactionManager(dataSource);
		}
		@Primary    //标注的Bean在有多个同类Bean时,优先被考虑
		@Bean(name = "mysqlSqlSessionFactory")
		public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqldatasource") DataSource dataSource) throws Exception {
			final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
			sessionFactory.setDataSource(dataSource);
			// 如果不使用xml的方式配置mapper，则可以省去下面这行mapper location的配置。
			sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MysqlDatasourceConfig.MAPPER_LOCATION));
			return sessionFactory.getObject();
		}
	}
	@Configuration
	@MapperScan(basePackages = OracleDatasourceConfig.PACKAGE,sqlSessionFactoryRef = "oracleSqlSessionFactory")
	public class OracleDatasourceConfig {
		// oracledao扫描路径
		static final String PACKAGE = "com.springboot.oracledao";
		// mybatis mapper扫描路径
		static final String MAPPER_LOCATION = "classpath:mapper/oracle/*.xml";
		@Bean(name = "oracledatasource")
		@ConfigurationProperties("spring.datasource.druid.oracle")
		public DataSource oracleDataSource(){
			return DruidDataSourceBuilder.create().build();
		}
		@Bean(name = "oracleTransactionManager")
		public DataSourceTransactionManager oracleTransactionManager(@Qualifier("oracledatasource") DataSource dataSource){
			return new DataSourceTransactionManager(dataSource);
		}
		@Bean(name = "oracleSqlSessionFactory")
		public SqlSessionFactory oracleSqlSessionFactory(@Qualifier("oracledatasource") DataSource dataSource) throws Exception {
			final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
			sessionFactory.setDataSource(dataSource);
			//如果不使用xml的方式配置mapper,则可以省去下面这行mapper location的配置。
			sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(OracleDatasourceConfig.MAPPER_LOCATION));
			return sessionFactory.getObject();
		}
	}
	@Configuration注解: 用于定义配置类,可替换xml配置文件,被注解的类内部包含有一个或多个被@Bean注解的方法,
		这些方法将会被AnnotationConfigApplicationContext或AnnotationConfigWebApplicationContext类进行扫描,并用于构建bean定义,初始化Spring容器
	@ConfigurationProperties: 与@Bean一起用将返回或声明对象交给SpringIOC管理
	    加在方法上: 根据配置文件的信息调用返回对象的set方法(set方法必须为public)
	    加在类上: 根据配置文件的信息调用声明对象的set方法(set方法必须为public)
	@Mapper注解: @Mapper作用在接口类上,在编译之后会得到相应接口的实例;
	@MapperScan注解: @MapperScan作用在配置类上或入口类上,指定接口类所在包,编译之后得到包下所有接口的实例
		basePackages: 扫描包路径(可多值)
		sqlSessionFactoryRef: sqlSessionFactory实例的引用
	@Qualifier注释: 注入JavaBean对象时指定名称(用于可匹配到多个Bean时,消除歧义)
	@Autowired注解: spring的默认注入注解(用于只能匹配到1个Bean时)
6.多数据源功能测试:
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
		@Mapper
		public interface MysqlStudentMapper {
			List<Map<String, Object>> getAllStudents();
		}
		@Mapper
		public interface OracleStudentMapper {
			List<Map<String, Object>> getAllStudents();
		}
	[3]编写Mapper的实现:
		(1)MysqlStudentMapper.xml
		    <?xml version="1.0" encoding="UTF-8" ?>
		    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		    		"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
		    <mapper namespace="com.springboot.mysqldao.MysqlStudentMapper">
		    	<select id="getAllStudents" resultType="java.util.Map">
		    		select * from student
		    	</select>
		    </mapper>
		(2)OracleStudentMapper.xml
		    <?xml version="1.0" encoding="UTF-8" ?>
		    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		    		"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
		    <mapper namespace="com.springboot.oracledao.OracleStudentMapper">
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
			private OracleStudentMapper oracleStudentMapper;
			@Autowired
			private MysqlStudentMapper mysqlStudentMapper;
			@Override
			@Transactional								//主数据源的事务
			public List<Map<String, Object>> getAllStudentsFromMysql() {
				return this.mysqlStudentMapper.getAllStudents();
			}
			@Override
			@Transactional("oracleTransactionManager")	//从数据源的事务
			public List<Map<String, Object>> getAllStudentsFromOracle() {
				return this.oracleStudentMapper.getAllStudents();
			}
		}
	[6]编写Controller:
		@RestController
		public class StudentController {
			@Autowired
			private StudentService studentService;
			@RequestMapping("/querystudentsfromoracle")
			public List<Map<String,Object>> queryStudentsFromOracle(){
				return this.studentService.getAllStudentsFromOracle();
			}
			@RequestMapping("/querystudentsfrommysql")
			public List<Map<String,Object>> queryStudentsFromMysql(){
				return this.studentService.getAllStudentsFromMysql();
			}
		}
	[7]测试MySQL数据源: http://localhost:8080/web/querystudentsfrommysql
	   测试Oracle数据源: http://localhost:8080/web/querystudentsfromoracle
```