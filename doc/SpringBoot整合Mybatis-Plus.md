```
1.引入mybatis-plus依赖及相关依赖:
    <dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<!--mybatis-plus和mybatis依赖-->
	<dependency>
		<groupId>com.baomidou</groupId>
		<artifactId>mybatis-plus-boot-starter</artifactId>
		<version>3.1.1</version>
	</dependency>
	<!--代码生成器所需依赖-->
	<dependency>
		<groupId>com.baomidou</groupId>
		<artifactId>mybatis-plus-generator</artifactId>
	<version>3.1.1</version>
	<!--mybatis-plus代码生成所需模板,项目业务中不需要-->
	<dependency>
		<groupId>org.apache.velocity</groupId>
		<artifactId>velocity</artifactId>
		<version>1.7</version>
	</dependency>
	<!--thymeleaf模板引擎-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
	<!--MySQL数据库驱动-->
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
	</dependency>
	<!--druid数据源-->
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>druid-spring-boot-starter</artifactId>
		<version>1.1.10</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependency>
2.创建表并初始化测试数据:
	DROP TABLE IF EXISTS user;
	CREATE TABLE user (
		id 	 	BIGINT(20) 	NOT NULL 		 COMMENT '主键ID',
		name 	VARCHAR(30) DEFAULT NULL COMMENT '姓名',
		age  	INT(11) 	  DEFAULT NULL COMMENT '年龄',
		email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
		PRIMARY KEY (id)
	);
	DELETE FROM user;
	INSERT INTO user (id, name, age, email) VALUES
	(1, 'Jone', 18, 'test1@baomidou.com'),
	(2, 'Jack', 20, 'test2@baomidou.com'),
	(3, 'Tom', 28, 'test3@baomidou.com'),
	(4, 'Sandy', 21, 'test4@baomidou.com'),
	(5, 'Billie', 24, 'test5@baomidou.com');
3.编写代码生成器:
	@Test
	public void codeGenerator(){
		//代码生成器对象
		AutoGenerator mpg = new AutoGenerator();
		//数据源配置
		DataSourceConfig dataSourceConfig = new DataSourceConfig();
		dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
		dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8");
		dataSourceConfig.setUsername("root");
		dataSourceConfig.setPassword("root");
		mpg.setDataSource(dataSourceConfig);
		//包名配置
		PackageConfig packageConfig = new PackageConfig();
		//该配置会在mapper目录下生成xml目录保存xml文件,因在自定义配置中重新生成了可删除
		packageConfig.setParent("com.example.mybatisplus");
		mpg.setPackageInfo(packageConfig);
		//策略配置
		StrategyConfig strategyConfig = new StrategyConfig();
		strategyConfig.setNaming(NamingStrategy.underline_to_camel);
		strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
    strategyConfig.setControllerMappingHyphenStyle(true);
		strategyConfig.setInclude("user");  //设置表名,多个以英文逗号分隔
		mpg.setStrategy(strategyConfig);
		//全局策略配置
		GlobalConfig globalConfig = new GlobalConfig();
		String projectPath = System.getProperty("user.dir");
		globalConfig.setOutputDir(projectPath+"/src/main/java");
		globalConfig.setOpen(false);
		globalConfig.setAuthor("dengzhiming");
		globalConfig.setServiceName("%sService");
		mpg.setGlobalConfig(globalConfig);
		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};
		//如果模板引擎是velocity,默认模板引擎,需添加其依赖
		String templatePath = "/templates/mapper.xml.vm";
		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名,如果你Entity设置了前后缀、此处注意xml的名称会跟着发生变化
				// 在/src/main/resources/mapper/目录下重新生成xml文件
				return projectPath + "/src/main/resources/mapper/" + 
					tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
			}
		});
		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);
		mpg.execute();
	}
4.配置yml文件:
	# 服务器项目根路径
	server:
		servlet:
			context-path: /web
	spring:
		datasource:
			druid:
				#数据库访问配置，使用Druid数据源
				driver-class-name: com.mysql.cj.jdbc.Driver
				url: jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8
				username: root
				password: root
				# 连接池配置
				initial-size: 5
				min-idle: 5
				max-active: 20
				# 连接等待超时时间
				max-wait: 30000
				# 配置检测可以关闭的空闲连接间隔时间(检测空闲连接的周期)
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
				# 配置监控统计拦截的filters,去掉后监控界面sql无法统计,'wall'用于防火墙
				filters: stat,wall
				# Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
				aop-patterns: com.springboot.service.*
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
					# 访问路径为/druid时,跳转到StatViewServlet
					url-pattern: /druid/*
					# 是否能够重置数据
					reset-enable: false
					# 需要账号密码才能访问控制台
					login-username: admin
					login-password: admin
					# IP白名单
					# allow: 127.0.0.1
					#　IP黑名单（共同存在时，deny优先于allow）
					# deny: 192.168.1.218
				# 配置StatFilter
				filter:
					stat:
						log-slow-sql: true
	mybatis-plus:
		#指定MyBatis Mapper所对应的XML文件位置
		mapper-locations: classpath:/mapper/*.xml
		#启动时是否检查MyBatis XML文件的存在,默认不检查
		checkConfigLocation: true
5.运行测试类中的代码生气方法,生成所需的基础代码;
6.在入口类上添加mybatis接口扫描注解:
    @MapperScan("com.example.mybatisplus.mapper")
    @SpringBootApplication
    public class SpringBootMybatisPlusApplication {
        public static void main(String[] args) {
            SpringApplication.run(SpringBootMybatisPlusApplication.class, args);
        }
    }
7.测试:
	[1]测试通用Service:
	  @Autowired
    private UserService service;
		@Test
		public void testService(){
			//User user = service.getOne(new QueryWrapper<User>().eq("id","1"));
			User user = service.getById("1");
			System.out.println(user);
		}
	[2]测试通用Mapper:
	  @Autowired
    private UserMapper mapper;
		@Test
		public void testMapper(){
			User user = mapper.selectOne(new QueryWrapper<User>().eq("id", "1"));
			System.out.println(user);
		}
	[3]测试自定义SQL:
		(1)在mapper接口中添加自定义方法:
			public interface UserMapper extends BaseMapper<User> {
				User findById(String id);
			}
		(2)在mapper xml文件中添加自定义SQL:
			<select id="findById" resultType="com.example.mybatisplus.entity.User">
				select * from user where id = #{id}
			</select>
		(3)测试:
			@Test
			public void testSql(){
				User user = mapper.findById("2");
				System.out.println(user);
			}
```
