# Spring Boot 整合MyBatis

```text
1.项目的pom文件中添加MyBatis依赖:
(Spring Boot和MyBatis版本对应关系:http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
    <dependency>
    	<groupId>org.mybatis.spring.boot</groupId>
    	<artifactId>mybatis-spring-boot-starter</artifactId>
    	<version>1.3.2</version>
    </dependency>
    (该项目基于springboot2.1.3)
2.项目的pom文件中添加MySQL驱动的依赖:(MySQL驱动版本使用springboot默认版本)
    <dependency>
    	<groupId>mysql</groupId>
    	<artifactId>mysql-connector-java</artifactId>
    </dependency>
3.使用Druid数据源:(关系型数据库连接池,https://github.com/alibaba/druid;提供连接池的功能和提供监控功能)
    [1]项目的pom文件中添加Druid数据源驱动的依赖:
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
    	(上述配置配置了Druid作为连接池,还开启了Druid的监控功能;其他配置可参考官方wiki
	    ——https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter)
    [3]运行项目,Druid监控后台:http://localhost:8080/web/druid
4.使用MyBatis:
    [1]创建数据库表及插入数据(mysql5.5)
    	CREATE TABLE STUDENT (
    	    SNO VARCHAR(3) NOT NULL ,
    	    SNAME VARCHAR(9) NOT NULL ,
    	    SSEX CHAR(1) NOT NULL 
    	);
    	INSERT INTO STUDENT VALUES ('001', 'KangKang', 'M');
    	INSERT INTO STUDENT VALUES ('002', 'Mike', 'M');
    	INSERT INTO STUDENT VALUES ('003', 'Jane', 'F');
    [2]创建对应JavaBean:
    	public class Student implements Serializable{
    	    private static final long serialVersionUID = -339516038496531943L;
    	    private String sno;
    	    private String name;
    	    private String sex;
    	    // get,set略
    	}
    [3]编写包含基本CRUD的StudentMapper
    	①基于注解
    	    @Repository
    	    @Mapper
    	    public interface StudentMapper {
    	    	@Insert("insert into student(sno,sname,ssex) values(#{sno},#{name},#{sex})")
    	    	int add(Student student);
    	    	@Update("update student set sname=#{name},ssex=#{sex} where     sno=#{sno}")
    	    	int update(Student student);
    	    	@Delete("delete from student where sno=#{sno}")
    	    	int deleteById(String sno);
    	    	@Select("select * from student where sno=#{sno}")
    	    	@Results(id = "student",value= {
    	    		@Result(property = "sno", column = "sno", javaType = String.class),
    	    		@Result(property = "name", column = "sname", javaType =     String.class),
    	    		@Result(property = "sex", column = "ssex", javaType = String.class)
    	    	})
    	    	Student queryStudentById(String sno);
    	    }
    	    (简单的语句只需要使用@Insert、@Update、@Delete、@Select这4个注解即可)
    	    (动态SQL语句需要使用@InsertProvider、@UpdateProvider、@DeleteProvider、@SelectProvider等注解)
    	②基于xml文件
    		//@MapperScan扫描Mapper所在目录,@Mapper只对注释的类起作用
    		@Repository
    		@Mapper
    		public interface StudentMapper {
    			int add(Student student);
    			int update(Student student);
    			int deleteById(String id);
    			Student queryStudentById(String id);
    		}
    		//xml文件
    		<?xml version="1.0" encoding="UTF-8" ?>    
    		<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"   
    			"http://mybatis.org/dtd/mybatis-3-mapper.dtd">     
    		<mapper namespace="com.example.mybatis.mapper.StudentMapper">  
    		    <insert id="add">
    	                insert into student(sno,sname,ssex) values(#{sno},#{name},#{sex})
                    </insert>
            	    <update id="update">
    	    	        update student set sname=#{name},ssex=#{sex} where sno=#{sno}
            	    </update>
            	    <delete id="deleteById">
    	    	        delete from student where sno=#{id}
            	    </delete>
            	    <select id="queryStudentById" resultType="com.example.mybatis.bean.Student">
    	    	        select
    		            sno		sno
    		            ,sname	name
    		            ,ssex 	sex
    	            	from student where sno=#{id}
               	    </select>
    		</mapper>
    		//在application.yml中进行MyBatis的额外配置
    		mybatis:
    		    # type-aliases扫描路径
    		    # type-aliases-package:
    		    # mapper xml实现扫描路径
    		    mapper-locations: classpath:mapper/*.xml
    [4]编写Service接口:
    	public interface StudentService {
    	    int add(Student student);
    	    int update(Student student);
    	    int deleteById(String sno);
    	    Student queryStudentById(String sno);
    	}
    [5]编写Service的实现类:
    	@Service("studentService")
    	public class StudentServiceImp implements StudentService{
    	    @Autowired
    	    private StudentMapper studentMapper;
    	    @Override
    	    public int add(Student student) {
    	    	return this.studentMapper.add(student);
    	    }
    	    @Override
    	    public int update(Student student) {
    	    	return this.studentMapper.update(student);
    	    }
    	    @Override
    	    public int deleteById(String sno) {
    	    	return this.studentMapper.deleteById(sno);
    	    }
    	    @Override
    	    public Student queryStudentById(String sno) {
    	    	return this.studentMapper.queryStudentById(sno);
    	    }
    	}
    [6]编写controller:
    	@RestController
    	public class TestController {
    	    @Autowired
    	    private StudentService service;
    	    @RequestMapping( value = "/querystudent", method = RequestMethod.GET)
    	    public Student queryStudentById(String sno) {
    	    	return this.service.queryStudentById(sno);
    	    }
    	}
    [7]启动项目访问: http://localhost:8080/web/querystudent?sno=001
```
