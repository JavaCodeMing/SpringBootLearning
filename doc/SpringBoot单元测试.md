```
编写单元测试可以帮助开发人员编写高质量的代码，提升代码质量，减少Bug，便于重构。
在Spring Boot中开启单元测试只需引入spring-boot-starter-test即可,其包含了一些主流的测试库:
    (使用命令: mvn dependency:tree 查看隐式依赖)
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-test</artifactId>
    	<scope>test</scope>
    </dependency>
    JUnit: 标准的单元测试Java应用程序
    Spring Test & Spring Boot Test: 对Spring Boot应用程序的单元测试提供支持
    Mockito: Java mocking框架,用于模拟任何Spring管理的Bean(如在单元测试中模拟第三方系统Service接口返回的数据,而不会去真正调用第三方系统)
    AssertJ: 一个流畅的assertion库,同时也提供了更多的期望值与测试返回值的比较方式
    Hamcrest: 库的匹配对象(也称为约束或谓词)
    JsonPath: 提供类似XPath那样的符号来获取JSON数据片段
    JSONassert: 对JSON对象或者JSON字符串断言的库
1.标准Spring Boot测试单元的代码结构:
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class ApplicationTest {
    	...
    }
2.JUnit4注解: (作用于方法上)
    @BeforeClass: 针对所有测试,只执行一次,且必须为static void 
    @AfterClass: 针对所有测试,只执行一次,且必须为static void 
    @Before: 初始化方法,对于每一个测试方法都要执行一次
    @After: 释放资源,对于每一个测试方法都要执行一次
    @Test: 测试方法,在这里可以测试期望异常和超时时间
    @Ignore: 忽略的测试方法
    (JUnit4的单元测试用例执行顺序为:@BeforeClass ->@Before ->@Test ->@After ->@AfterClass)
    (每一个测试方法的调用顺序为: @Before -> @Test -> @After)
3.Assert提供的常用assert方法:
    assertEquals("message",A,B):	判断A对象和B对象是否相等,这个判断在比较两个对象时调用了equals()方法
    assertSame("message",A,B):		判断A对象与B对象是否相同,使用的是==操作符
    assertTrue("message",A):		判断A条件是否为真
    assertFalse("message",A):		判断A条件是否不为真
    assertNotNull("message",A):		判断A对象是否不为null
    assertArrayEquals("message",A,B):	判断A数组与B数组是否相等
4.MockMvc: 用来模拟一个MVC环境,向Controller发送请求然后得到响应
    在单元测试中，使用MockMvc前需要进行初始化:
    	private MockMvc mockMvc;
    	@Autowired
    	private WebApplicationContext wac;
    	@Before
    	public void setupMockMvc(){
    		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    	}
    [1]MockMvc模拟MVC请求:
    	(1)模拟一个get请求: mockMvc.perform(MockMvcRequestBuilders.get("/hello?name={name}","Mike"));
    	(2)模拟一个post请求:mockMvc.perform(MockMvcRequestBuilders.post("/user/{id}", 1));
    	(3)模拟文件上传:    mockMvc.perform(MockMvcRequestBuilders.multipart("/fileupload").file("file", "文件内容".getBytes("utf-8")));
	(4)模拟请求参数: 
	    // 模拟发送一个message参数，值为hello
	    mockMvc.perform(MockMvcRequestBuilders.get("/hello").param("message", "hello"));
	    // 模拟提交一个checkbox值，name为hobby，值为sleep和eat
	    mockMvc.perform(MockMvcRequestBuilders.get("/saveHobby").param("hobby", "sleep", "eat"));
	(5)使用MultiValueMap构建参数:
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
	    params.add("name", "mrbird");
	    params.add("hobby", "sleep");
	    params.add("hobby", "eat");
	    mockMvc.perform(MockMvcRequestBuilders.get("/hobby/save").params(params));
	(6)模拟发送JSON参数:
	    ①直接编写json字符串:
	    	String jsonStr = "{\"username\":\"Dopa\",\"passwd\":\"ac3af72d9f95161a502fd326865c2f15\",\"status\":\"1\"}";
	    	mockMvc.perform(MockMvcRequestBuilders.post("/user/save").content(jsonStr.getBytes()));
	    ②使用Jackson技术序列化对象:
	    	@Autowired
	    	ObjectMapper objectMapper;
	    	User user = new User();
	    	user.setUsername("Dopa");
	    	user.setPasswd("ac3af72d9f95161a502fd326865c2f15");
	    	user.setStatus("1");
	    	String userJson = objectMapper.writeValueAsString(user);
	    	mockMvc.perform(MockMvcRequestBuilders.post("/user/save").content(userJson.getBytes()));
	(7)模拟Session和Cookie:
	     mockMvc.perform(MockMvcRequestBuilders.get("/index").sessionAttr(name, value));
	     mockMvc.perform(MockMvcRequestBuilders.get("/index").cookie(new Cookie(name, value)));
	(8)设置请求的Content-Type:
	     mockMvc.perform(MockMvcRequestBuilders.get("/index").contentType(MediaType.APPLICATION_JSON_UTF8));
	(9)设置返回格式为JSON:
	     mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1).accept(MediaType.APPLICATION_JSON));
	(10)模拟HTTP请求头:
	     mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1).header(name, values));
    [2]MockMvc处理返回结果:
    	(1)期望成功调用,即HTTP Status为200:
    	    mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1))
    		.andExpect(MockMvcResultMatchers.status().isOk());
    	(2)期望返回内容是application/json:
    	    mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1))
    	    	.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    	(3)检查返回JSON数据中某个值的内容:
    	    mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1))
    	    	.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("mike"));
    	    (这里使用到了jsonPath,$代表了JSON的根节点;更多关于jsonPath的介绍: )
    	    (https://github.com/json-path/JsonPath, https://goessner.net/articles/JsonPath/)
    	(4)判断Controller方法是否返回某视图:
    	    mockMvc.perform(MockMvcRequestBuilders.post("/index"))
    	    	.andExpect(MockMvcResultMatchers.view().name("index.html"));
    	(5)比较Model:
    	    mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1))
    	    	.andExpect(MockMvcResultMatchers.model().size(1))
    	    	.andExpect(MockMvcResultMatchers.model().attributeExists("password"))
    	    	.andExpect(MockMvcResultMatchers.model().attribute("username", "mike"));
    	(6)比较forward或者redirect:
    	    mockMvc.perform(MockMvcRequestBuilders.get("/index"))
    	    	.andExpect(MockMvcResultMatchers.forwardedUrl("index.html"));
    	    // 或者
    	    mockMvc.perform(MockMvcRequestBuilders.get("/index"))
    	    	.andExpect(MockMvcResultMatchers.redirectedUrl("index.html"));
    	(7)比较返回内容,使用content():
    	    // 返回内容为hello
    	    mockMvc.perform(MockMvcRequestBuilders.get("/index"))
    	    	.andExpect(MockMvcResultMatchers.content().string("hello"));
    	    // 返回内容是XML，并且与xmlCotent一样
    	    mockMvc.perform(MockMvcRequestBuilders.get("/index"))
    	    	.andExpect(MockMvcResultMatchers.content().xml(xmlContent));
    	    // 返回内容是JSON ，并且与jsonContent一样
    	    mockMvc.perform(MockMvcRequestBuilders.get("/index"))
    	    	.andExpect(MockMvcResultMatchers.content().json(jsonContent));
    	(8)输出响应结果:
    	    mockMvc.perform(MockMvcRequestBuilders.get("/index"))
    	    	.andDo(MockMvcResultHandlers.print());
5.使用Mybatis搭建一个测试环境:
    [1]引入web依赖,mybatis依赖,mysql驱动依赖和druid数据源驱动依赖:
    	<dependency>
    	    <groupId>org.springframework.boot</groupId>
    	    <artifactId>spring-boot-starter-web</artifactId>
    	</dependency>
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
    [2]在application.yml文件中配置Druid数据源连接池及监控:
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
    [3]创建数据库表和插入数据:
    	CREATE TABLE T_USER (
    	    ID 		INTEGER 	NOT NULL 	COMMENT	'ID',
    	    USERNAME 	VARCHAR(20) 	NOT NULL 	COMMENT '用户名',
    	    PASSWD 	VARCHAR(128) 	NOT NULL 	COMMENT '密码',
    	    CREATE_TIME DATETIME 	NULL 		COMMENT '创建时间',
    	    STATUS 	CHAR(1) 	NOT NULL 	COMMENT '是否有效 1:有效  0:锁定'
    	);
    	ALTER TABLE T_USER MODIFY ID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY;	
    	INSERT INTO T_USER VALUES ('2', 'tester', '243e29429b340192700677d48c09d992', '2019-04-21 17:20:21', '1');
    	INSERT INTO T_USER VALUES ('1', 'mike', '42ee25d1e43e9f57119a00d0a39e5250', '2019-04-21 10:52:48', '1');
    [4]创建对应JavaBean:
    	public class User {
    	    private Long id;
    	    private String username;
    	    private String passwd;
    	    private Date createTime;
    	    private String status;
    	    // get,set略
    	)
    [5]编写基于注解的Mapper接口:
        @Repository
    	@Mapper
    	public interface UserMapper {
    	    int add(User user);
    	    int update(User user);
    	    int deleteById(String id);
    	    User queryUserById(String id);
    	}
    [6]编写Mapper的实现: (已在yml中配置mapper xml实现扫描路径)
    	<?xml version="1.0" encoding="UTF-8" ?>
    	<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
    	<mapper namespace="com.example.test.dao.UserMapper">
    	    <resultMap id="userResultMap" type="com.example.test.bean.User">
    	    	<id property="id" column="ID" />
    	    	<result property="username" column="USERNAME"/>
    	    	<result property="passwd" column="PASSWD"/>
    	    	<result property="createTime" column="CREATE_TIME"/>
    	    	<result property="status" column="STATUS"/>
    	    </resultMap>
    	    <insert id="add">
    	    	insert into t_user (USERNAME,PASSWD,CREATE_TIME,STATUS) values(#{username},#{passwd},#{createTime},#{status})
	    </insert>
	    <update id="update">
	    	update t_user set USERNAME=#{username},PASSWD=#{passwd},STATUS=#{status} where ID=#{id}
	    </update>
	    <delete id="deleteById">
	    	delete from t_user where ID=#{id}
	    </delete>
	    <select id="queryUserById" resultMap="userResultMap">
	    	select * from t_user where ID=#{id}
	    </select>
	</mapper>
	[7]编写service接口:
	    public interface UserService {
	    	int add(User user);
	    	int update(User user);
	    	int deleteById(String id);
	    	User queryUserById(String id);
	    }
	[8]编写service实现:
	    @Service("userService")
	    public class UserServiceImpl implements UserService {
	    	@Autowired
	    	UserMapper userMapper;
	    	@Override
	    	public int add(User user) {
	    	    return this.userMapper.add(user);
	    	}
	    	@Override
	    	public int update(User user) {
	    	    return this.userMapper.update(user);
	    	}
	    	@Override
	    	public int deleteById(String id) {
	    	    return this.userMapper.deleteById(id);
	    	}
	    	@Override
	    	public User queryUserById(String id) {
	    	    return this.userMapper.queryUserById(id);
	    	}
	    }
	[9]编写controller:
	    @Controller
	    public class UserController {
	    	@Autowired
	    	UserService userService;
	    	@RequestMapping( value = "/queryuser/{id}", method = RequestMethod.GET)
	    	@ResponseBody
	    	public User queryUserById(@PathVariable("id") String id){
	    	    return this.userService.queryUserById(id);
	    	}
	    	@RequestMapping(value = "/adduser", method = RequestMethod.POST)
	    	public void addUser(User user){
	    	    this.userService.add(user);
	    	}
	    	@RequestMapping(value = "/updateuser", method = RequestMethod.POST)
	    	public void updateUser(User user){
	    	    this.userService.update(user);
	    	}
	    	@RequestMapping(value = "/deleteuser", method = RequestMethod.GET)
	    	public void deleteById(String id){
	    	    this.userService.deleteById(id);
	    	}
	    }
6.编写单元测试:
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class SpringBootTestingApplicationTests {
    	@Autowired
    	ObjectMapper objectMapper;
    	@Autowired
    	UserService userService;
    	@Autowired
    	private WebApplicationContext wac;
    	private MockMvc mockMvc;
    	@Before
    	public void setupMockMvc(){
    	    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    	}
    	@Test
    	public void test1(){
    	    // 测试service queryUserById
    	    User user = this.userService.queryUserById("1");
    	    Assert.assertEquals("用户名为mike", "mike", user.getUsername());
    	}
    	@Test
    	@Transactional  //该事务注解可在方法执行完自动回滚数据
    	public void test2(){
    	    // 测试service add
    	    User user = new User();
    	    user.setUsername("JUnit");
    	    user.setPasswd("123456");
    	    user.setStatus("1");
    	    user.setCreateTime(new Date());
    	    this.userService.add(user);
    	}
    	@Test
    	public void test3() throws Exception {
    	    // 测试controller queryuser
    	    mockMvc.perform(MockMvcRequestBuilders.get("/queryuser/1")
    	    	.contentType(MediaType.APPLICATION_JSON_UTF8))
    	    .andExpect(MockMvcResultMatchers.status().isOk())
    	    .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("mike"))
    	    .andDo(MockMvcResultHandlers.print());
    	}
    }
```
