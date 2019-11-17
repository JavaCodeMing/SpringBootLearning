```text
此处介绍Spring Boot整合Redis和Ehcache,并通过缓存注解的方式来使用;
1.公共部分:
    [1]引入MyBatis依赖、MySQL驱动依赖和Druid数据源驱动依赖:
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
    [2]在application.yml中配置Druid数据源:
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
				# Spring监控AOP切入点,如x.y.z.service.*,配置多个英文逗号分隔
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
    [3]创建数据库表插入数据及相应JavaBean:
	    (1)建表插数:
		    CREATE TABLE STUDENT1 (
		    	SNO VARCHAR(3) NOT NULL ,
		    	NAME VARCHAR(9) NOT NULL ,
		    	SEX CHAR(1) NOT NULL 
		    );
		    INSERT INTO STUDENT1 VALUES ('001', 'KangKang', 'M');
		    INSERT INTO STUDENT1 VALUES ('002', 'Mike', 'M');
		    INSERT INTO STUDENT1 VALUES ('003', 'Jane', 'F');
	    (2)创建对应JavaBean:
		    public class Student implements Serializable{
		    	private static final long serialVersionUID = -339516038496531943L;
		    	private String sno;
		    	private String name;
		    	private String sex;
		    	// get,set略
		    }
	[4]编写Mapper接口:
		//@MapperScan用于指定Mapper接口的扫描目录,@Mapper只对注释的类起作用
		@Repository
		@Mapper
		public interface StudentMapper {
			int add(Student student);
			int update(Student student);
			int deleteById(String sno);
			Student queryStudentById(String id);
		}
    [5]开启缓存功能,并添加缓存依赖:
        (1)在Spring Boot入口类或配置类上加入@EnableCaching注解开启缓存功能;
        (2)根据选择的缓存框架,添加相应的缓存依赖:
            <!--redis的依赖,也包含了cache中的相关依赖-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-redis</artifactId>
            </dependency>
            <!--该注解中包含了Ehcache依赖-->
		    <dependency>
		    	<groupId>org.springframework.boot</groupId>
		    	<artifactId>spring-boot-starter-cache</artifactId>
		    </dependency>
	[6]编写Service接口,加入缓存注解:
		public interface StudentService {
		    int add(Student student);
            Student update(Student student);
            int deleteById(String sno);
            Student queryStudentById(String sno);
		}
	[7]编写service的实现类:
		@Service("studentService")
    @CacheConfig(cacheNames = "student")
    public class StudentServiceImpl implements StudentService {
        private final StudentMapper studentMapper;
        @Autowired
        public StudentServiceImpl(StudentMapper studentMapper) {
            this.studentMapper = studentMapper;
        }
        @CachePut(key = "#p0.sno")
        @Override
        public Student add(Student student) {
            this.studentMapper.add(student);
            return this.studentMapper.queryStudentById(student.getSno());
        }
        @CachePut(key = "#p0.sno")
        @Override
        public Student update(Student student) {
            this.studentMapper.update(student);
            return this.studentMapper.queryStudentById(student.getSno());
        }
        @CacheEvict(key = "#p0", allEntries = true)
        @Override
        public void deleteById(String sno) {
            this.studentMapper.deleteById(sno);
        }
        @Cacheable(key = "#p0")
        @Override
        public Student queryStudentById(String sno) {
            return this.studentMapper.queryStudentById(sno);
        }
    }
    @CacheConfig: 主要用于配置该类中会用到的一些共用的缓存配置。
			(配置了该数据访问对象中返回的内容将存储于名为student的缓存对象中,也可不使用该注解,直接通过@Cacheable自己配置缓存集的名字来定义)
		@Cacheable: 配置了queryStudentBySno方法的返回值将被加入缓存。
			(同时在查询时,会先从缓存中获取,若不存在才再发起对数据库的访问)
			该注解主要有下面几个参数:
				value、cacheNames: 两个等同的参数(cacheNames为Spring4新增,作为value的别名)用于指定缓存存储的集合名;
					由于Spring4中新增了@CacheConfig,因此在Spring3中原本必须有的value属性也成为非必需项了;
				key: 缓存对象存储在Map集合中的key值,非必需;
					默认策略: 按照函数的所有参数组合作为key值
					自定义策略: 通过Spring的EL表达式来指定key(EL表达式可以使用方法参数及它们对应的属性)
						使用方法参数时我们可以直接使用“#参数名”或者“#p参数index”;(key="#id"、key="#p0"、key="#user.id"、key="#p0.id")
					(更多关于SpEL表达式的详细内容可参考: https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)
				condition: 缓存对象的条件,非必需,也需使用SpEL表达式,只有满足表达式条件的内容才会被缓存
					(@Cacheable(key = "#p0", condition = "#p0.length() < 3"),表示只有当第一个参数的长度小于3的时候才会被缓存;)
				unless: 另一个缓存条件参数,非必需,需使用SpEL表达式;该条件是在方法被调用之后才做判断的,所以它可以通过对result进行判断;
				keyGenerator: 用于指定key生成器,非必需; 
					若需要指定一个自定义的key生成器,我们需要去实现org.springframework.cache.interceptor.KeyGenerator接口,并使用该参数来指定;
				cacheManager: 用于指定使用哪个缓存管理器,非必需;只有当有多个时才需要使用;
				cacheResolver: 用于指定使用那个缓存解析器,非必需;
					需通过org.springframework.cache.interceptor.CacheResolver接口来实现自己的缓存解析器,并用该参数指定;
		@CachePut: 配置于方法上,能够根据参数定义条件来进行缓存,其缓存的是方法的返回值,它与@Cacheable不同的是,它每次不检查缓存直接调用方法,所以主要用于数据新增和修改操作上;
			它的参数与@Cacheable类似,具体功能可参考上面对@Cacheable参数的解析;
		@CacheEvict: 配置于方法上,通常用在删除方法上,用来从缓存中移除相应数据;
			除了同@Cacheable一样的参数之外,它还有下面两个参数:
				allEntries: 非必需,默认为false;当为true时,会移除所有数据;
				beforeInvocation:非必需,默认为false,会在调用方法之后移除数据;当为true时,会在调方法之前移除数据;
	[8]编写测试Controller:
	    @RestController
        public class StudentController {
            private final StudentService studentService;
            @Autowired
            public StudentController(StudentService studentService)         {
                this.studentService = studentService;
            }
            @RequestMapping("/add")
            public String add(Student student){
                try {
                    studentService.add(student);
                    return "success";
                }catch (Exception e){
                    e.printStackTrace();
                }
                return "false";
            }
            @RequestMapping("/update")
            public String update(Student student){
                try {
                    studentService.update(student);
                    return "success";
                }catch (Exception e){
                    e.printStackTrace();
                }
                return "false";
            }
            @RequestMapping("/delete")
            public String deleteById(@RequestParam("sno") String sno){
                try {
                    studentService.deleteById(sno);
                    return "success";
                }catch (Exception e){
                    e.printStackTrace();
                }
                return "false";
            }
            @RequestMapping("/query")
            public Student queryStudentById(@RequestParam("sno") String sno){
                return studentService.queryStudentById(sno);
            }
        }
2.使用Redis的剩余步骤:
	[1]在application.yml中配置Redis:
		spring:
			redis:
				# Redis数据库索引(默认为0)
				database: 0
				# Redis服务器地址
				host: 127.0.0.1
				# Redis服务器连接端口
				port: 6379
				timeout: 30s  # 数据库连接超时时间,2.0 中该参数的类型为Duration,这里在配置的时候需要指明单位
				# 连接池配置,2.0中直接使用jedis或者lettuce配置连接池
				jedis:
					pool:
						#最大连接数据库连接数,设 0 为没有限制
						max-active: 8
						#最大建立连接等待时间;如果超过此时间将接到异常;设为-1表示无限制;
						max-wait: -1
						#最大等待连接中的数量,设 0 为没有限制
						max-idle: 8
						#最小等待连接中的数量,设 0 为没有限制
						min-idle: 0
	[2]创建Redis配置类:
		@Configuration
		@EnableCaching //启用缓存,这个注解很重要
		public class RedisConfig extends CachingConfigurerSupport {
			// 自定义缓存key生成策略
			@Bean
			public KeyGenerator keyGenerator() {
				return new KeyGenerator() {
					@Override
					public Object generate(Object target, java.lang.reflect.Method method, Object... params) {
						StringBuffer sb = new StringBuffer();
						sb.append(target.getClass().getName());
						sb.append(method.getName());
						for (Object obj : params) {
							sb.append(obj.toString());
						}
						return sb.toString();
					}
				};
			}
			// 缓存管理器
			@Bean
			public CacheManager cacheManager(RedisConnectionFactory factory) {
				// 生成一个默认配置，通过config对象即可对缓存进行自定义配置
				RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
				// 设置缓存的默认过期时间，也是使用Duration设置
				config = config.entryTtl(Duration.ofMinutes(1))
				                // 不缓存空值
				               .disableCachingNullValues();     
				// 设置一个初始化的缓存空间set集合
				Set<String> cacheNames =  new HashSet<>();	
				cacheNames.add("my-redis-cache1");
				cacheNames.add("my-redis-cache2");
				// 对每个缓存空间应用不同的配置
				Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
				configMap.put("my-redis-cache1", config);
				configMap.put("my-redis-cache2", config.entryTtl(Duration.ofSeconds(120)));
				// 使用自定义的缓存配置初始化一个cacheManager
				RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
				     // 注意这两句的调用顺序,一定要先调用该方法设置初始化的缓存名,再初始化相关的配置
						.initialCacheNames(cacheNames)  
						.withInitialCacheConfigurations(configMap)
						.build();
				return cacheManager;
			}
			@Bean
			public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
				StringRedisTemplate template = new StringRedisTemplate(factory);
				setSerializer(template);// 设置序列化工具
				template.afterPropertiesSet();
				return template;
			}
			private void setSerializer(StringRedisTemplate template) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
				ObjectMapper om = new ObjectMapper();
				om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
				om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
				jackson2JsonRedisSerializer.setObjectMapper(om);
				template.setValueSerializer(jackson2JsonRedisSerializer);
			}
		}
	[3]编写Mapper xml实现(已在yml中配置mapper xml实现扫描路径)
		<?xml version="1.0" encoding="UTF-8" ?>    
		<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"   
			"http://mybatis.org/dtd/mybatis-3-mapper.dtd">     
		<mapper namespace="com.example.redis.dao.StudentMapper">  
			<insert id="add">
				insert into student1 (sno,name,sex) values(#{sno},#{name},#{sex})
			</insert>
			<update id="update">
				update student1 set name=#{name},sex=#{sex} where sno=#{sno}
			</update>
			<delete id="deleteById">
				delete from student1 where sno=#{sno}
			</delete>
			<select id="queryStudentById" resultType="com.example.redis.bean.Student">
				select * from student1 where sno=#{sno}
			</select>
		</mapper>
	[4]yml中配置日志输出级别以观察SQL的执行情况:(logging.level.mapper或dao接口的目录:debug)
	  logging:
		  level:
			  com.example.redis.dao: debug
	[5]Redis的Windows版本下载及使用:
		(1)下载地址:https://github.com/MicrosoftArchive/redis/releases
		(2)将Redis压缩包解压缩到指定路径下
		(3)打开一个CMD窗口,进入压缩目录(cd D:\Develop\Redis-x64-3.0.504)
		(4)启动Redis服务端: redis-server.exe redis.windows.conf
		(5)在另一个CMD终端相同目录下启动Redis客户端: redis-cli.exe -p 6379
3.使用Ehcache的剩余步骤:
	[1]在src/main/resources目录下新建ehcache.xml:
		<?xml version="1.0" encoding="UTF-8"?>
		<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="ehcache.xsd">
			<defaultCache
				maxElementsInMemory="10000"
				eternal="false"
				timeToIdleSeconds="3600"
				timeToLiveSeconds="0"
				overflowToDisk="false"
				diskPersistent="false"
				diskExpiryThreadIntervalSeconds="120"/>
			<cache 
				name="student"
				maxEntriesLocalHeap="2000"
				eternal="false"
				timeToIdleSeconds="3600"
				timeToLiveSeconds="0"
				overflowToDisk="false"
				statistics="true"/>
		</ehcache>
		(1)遇到的问题及解决办法:
		    问题: 无法识别ehcache.xsd
		    解决: 打开settings->languages&frameworks->schemas and dtds
		        添加地址: http://ehcache.org/ehcache.xsd
			      修改noNamespaceSchemaLocation的值为添加的地址
		(2)Ehcahe的配置说明:
			1)<diskStore path="F:\develop\ehcache"/>元素: 缓存数据持久化的目录地址
			2)<defaultCache>元素: 设定缓存的默认数据过期策略
			3)<cache>元素: 设定具体的命名缓存的数据过期策略
				①必要属性:
					name: Cache的名称,必须是唯一的(ehcache会把这个cache放到HashMap里)
					maxElementsInMemory: 在内存中缓存的element的最大数目
					maxElementsOnDisk: 在磁盘上缓存的element的最大数目,默认值为0,表示不限制
					eternal: 对象是否永不过期;如果为true,则缓存的数据始终有效;如果为false,那么还要根据timeToIdleSeconds,timeToLiveSeconds判断 
					overflowToDisk: 如果内存中数据超过内存限制,是否要缓存到磁盘上
				②可选属性:
					timeToIdleSeconds: 对象空闲时间,指对象在多长时间没有被访问就会失效;只对eternal为false的有效,默认值0,表示一直可以访问
					timeToLiveSeconds: 对象存活时间,指对象从创建到失效所需要的时间;只对eternal为false的有效,默认值0,表示一直可以访问
					diskPersistent: 是否在磁盘上持久化;指重启jvm后,数据是否有效,默认为false
					diskExpiryThreadIntervalSeconds: 对象检测线程运行时间间隔;标识对象状态的线程多长时间运行一次,默认是120秒;
					diskSpoolBufferSizeMB:这个参数设置DiskStore(磁盘缓存)的缓存区大小;默认是30MB;每个cache使用各自的DiskStore
					memoryStoreEvictionPolicy: Ehcache的清空策略,当达到maxElementsInMemory限制时,Ehcache将会根据指定的策略去清理内存;默认策略是LRU(最近最少使用);
						FIFO(first in first out): 这个是大家最熟的,先进先出;
						LFU(Less Frequently Used): 直白一点就是讲一直以来最少被使用的;如上面所讲,缓存的元素有一个hit属性,hit值最小的将会被清出缓存
						LRU(Least Recently Used): 最近最少使用的,缓存的元素有一个时间戳,当缓存容量满了,而又需要腾出地方来缓存新的元素的时候,那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存
					clearOnFlush: 内存数量最大时是否清除
	[2]在application.yml中指定ehcache配置加载的路径:
		spring:
			cache:
				ehcache:
					config: classpath:ehcache.xml
	[3]编写Mapper xml实现(已在yml中配置mapper xml实现扫描路径)
		<?xml version="1.0" encoding="UTF-8" ?>    
		<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"   
			"http://mybatis.org/dtd/mybatis-3-mapper.dtd">     
		<mapper namespace="com.example.ehcache.dao.StudentMapper">  
			<insert id="add">
				insert into student1 (sno,name,sex) values(#{sno},#{name},#{sex})
			</insert>
			<update id="update">
				update student1 set name=#{name},sex=#{sex} where sno=#{sno}
			</update>
			<delete id="deleteById">
				delete from student1 where sno=#{sno}
			</delete>
			<select id="queryStudentById" resultType="com.example.ehcache.bean.Student">
				select * from student1 where sno=#{sno}
			</select>
		</mapper>
  [4]yml中配置日志输出级别以观察SQL的执行情况:(logging.level.mapper或dao接口的目录:debug)
	  logging:
		  level:
			  com.example.ehcache.dao: debug
```
