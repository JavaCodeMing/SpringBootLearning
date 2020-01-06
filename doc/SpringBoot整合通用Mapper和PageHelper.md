```
使用通用Mapper简化对单表的CRUD操作,使用PageHelper分页插件实现自动拼接分页SQL,
并使用MyBatis Geneator来自动生成实体类、Mapper接口和Mapper xml代码;
通用Mapper官方文档: https://github.com/abel533/Mapper/wiki
PageHelper官方文档: https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md
1.引入Mybatis依赖,通用mapper依赖,pagehelper依赖,MySQL驱动依赖和druid数据源依赖:
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>tk.mybatis</groupId>
        <artifactId>mapper-spring-boot-starter</artifactId>
        <version>2.1.5</version>
    </dependency>
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.2.11</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <!--这里关乎运行时使用的MySQL驱动,默认为8.x版本-->
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>
2.添加MyBatis Geneator插件: 
    <plugin>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-maven-plugin</artifactId>
        <version>1.3.7</version>
        <configuration>
            <!--Geneator的配置文件的位置-->
            <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
            <!-- 是否覆盖 -->
            <overwrite>true</overwrite>
            <!--允许移动生成的文件 -->
            <verbose>true</verbose>
        </configuration>
        <dependencies>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <!--使用Maven运行MyBatis Geneator要配置5.x版本MySQL驱动,否则会有问题-->
                <version>5.1.47</version>
            </dependency>
            <dependency>
                <groupId>tk.mybatis</groupId>
                <artifactId>mapper</artifactId>
                <version>4.0.0</version>
            </dependency>
        </dependencies>
    </plugin>
3.在yml文件或property文件中配置server,数据库及连接池,mybatis和pagehelper:
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
    mybatis:
        # type-aliases扫描路径
        type-aliases-package: com.example.mapperpagehelper.bean
        # mapper xml实现扫描路径
        mapper-locations: classpath:mapper/*.xml
    #mappers 多个接口时逗号隔开
    mapper:
        mappers: com.example.mapperpagehelper.config.MyMapper
        not-empty: false
        identity: mysql
    #pagehelper
    pagehelper:
        # 指定分页插件使用哪种方言
        helperDialect: mysql
        # 分页合理化参数,pageNum<=0时查第一页,pageNum>pages查最后一页
        reasonable: true
        # 支持通过Mapper接口参数来传递分页参数
        supportMethodsArguments: true
        # 配置参数映射,用于从对象中根据属性名取值
        params: count=countSql
4.编写自定义通用接口MyMapper:
    public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {}
5.在路径src/main/resources/下新建generatorConfig.xml:
    <!DOCTYPE generatorConfiguration
    PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
    "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
    <generatorConfiguration>
        <context id="Mysql" defaultModelType="flat" targetRuntime="MyBatis3Simple">
            <property name="javaFileEncoding" value="UTF-8"/>
            <property name="javaFormatter" value="org.mybatis.generator.api.dom.DefaultJavaFormatter"/>
            <property name="xmlFormatter" value="org.mybatis.generator.api.dom.DefaultXmlFormatter"/>
            <!--通用Mapper插件-->
            <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
                <!--该value值对应自定义通用接口MyMapper的全类名-->
                <property name="mappers" value="com.example.mapperpagehelper.config.MyMapper"/>
                <property name="caseSensitive" value="true"/>
                <property name="forceAnnotation" value="true"/>
                <property name="beginningDelimiter" value="`"/>
                <property name="endingDelimiter" value="`"/>
            </plugin>
            <commentGenerator type="DEFAULT">
                <property name="suppressAllComments" value="true"/>
                <property name="suppressDate" value="true"/>
            </commentGenerator>
            <!--连接数据库的配置-->
            <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                connectionURL="jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8"
                userId="root"
                password="root">
            </jdbcConnection>
            <javaTypeResolver type="DEFAULT">
                <property name="forceBigDecimals" value="false"/>
            </javaTypeResolver>
            <!--此targetPackage值对应生成JavaBean的包名-->
            <javaModelGenerator targetPackage="com.example.mapperpagehelper.bean" targetProject="src/main/java">
                <property name="enableSubPackages" value="true"/>
                <property name="trimStrings" value="true"/>
            </javaModelGenerator>
            <sqlMapGenerator targetPackage="mapper" targetProject="src/main/resources">
                <property name="enableSubPackages" value="true"/>
            </sqlMapGenerator>
            <!--此targetPackage值对应生成Mapper接口的包名-->
            <javaClientGenerator type="XMLMAPPER" targetPackage="com.example.mapperpagehelper.mapper" 
                targetProject="src/main/java">
                <property name="enableSubPackages" value="true"/>
                <!--当配置的通用Mapper插件中mappers的值和此处rootInterface的值一样时,会导致重复继承-->
                <!--<property name="rootInterface" value="com.example.mapperpagehelper.config.MyMapper"/>-->
            </javaClientGenerator>
            <table tableName="T_USER" domainObjectName="User">
                <generatedKey column="id" sqlStatement="MySql" identity="true" type="post"/>
            </table>
        </context>
    </generatorConfiguration>
6.建表语句:
    CREATE TABLE T_USER (
        ID              INT(10)         NOT NULL        COMMENT  'ID',
        USERNAME        VARCHAR(20)     NULL            COMMENT  '用户名',
        PASSWD          VARCHAR(128)    NULL            COMMENT  '密码',
        CREATE_TIME     TIMESTAMP       NULL            COMMENT  '创建时间',
        STATUS          CHAR(1)         NULL            COMMENT  '状态'
    );
    ALTER TABLE T_USER MODIFY ID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY;
7.在IDEA中的Terminal中项目根路径下执行生成代码的maven命令: mvn mybatis-generator:generate
8.编写通用Service及通用实现类BaseService:
    public interface IService<T> {
        List<T> selectAll();
        T selectByKey(Object key);
        int save(T entity);
        int delete(Object key);
        int updateAll(T entity);
        int updateNotNull(T entity);
        List<T> selectByExample(Object example);
    }
    @Service("iService")
    public abstract class BaseServiceImpl<T> implements IService<T> {
        @Resource
        protected Mapper<T> mapper;
        @Override
        public List<T> selectAll() {
            //说明: 查询所有数据
            return mapper.selectAll();
        }
        @Override
        public T selectByKey(Object key) {
            //说明: 根据主键字段进行查询,方法参数必须包含完整的主键属性,查询条件使用等号
            return mapper.selectByPrimaryKey(key);
        }
        @Override
        public int save(T entity) {
            //说明: 保存一个实体,null的属性也会保存,不会使用数据库默认值
            return mapper.insert(entity);
        }
        @Override
        public int delete(Object key) {
            //说明: 根据主键字段进行删除,方法参数必须包含完整的主键属性
            return mapper.deleteByPrimaryKey(key);
        }
        @Override
        public int updateAll(T entity) {
            //说明: 根据主键更新实体全部字段,null值会被更新
            return mapper.updateByPrimaryKey(entity);
        }
        @Override
        public int updateNotNull(T entity) {
            //根据主键更新属性不为null的值
            return mapper.updateByPrimaryKeySelective(entity);
        }
        @Override
        public List<T> selectByExample(Object example) {
            //说明: 根据Example条件进行查询
            //重点: 这个查询支持通过Example类指定查询列,通过selectProperties方法指定查询列
            return mapper.selectByExample(example);
        }
    }
9.编写自定义Service及实现类:
    public interface UserService extends IService<User> {
    }
    @service("userService")
    public class UserServiceImpl extends BaseService<User> implements UserService {
    }
10.在入口类上添加通用Mapper的@MapperScan注解来扫描mapper接口:
    @SpringBootApplication
    @MapperScan("com.example.mapperpagehelper.mapper")
    public class SpringbootApplication {
        public static void main(String[] args) {
            SpringApplication.run(SpringbootApplication.class, args);
        }
    }
11.进行接口的测试:
    [1]测试插入功能:
        @Autowired
        private UserService userService;
        @Test
        public void testInsert() {
            User user = new User();
            user.setUsername("mike");
            user.setPasswd("ac089b11709f9b9e9980e7c497268dfa");
            user.setCreateTime(new Date());
            user.setStatus("0");
            this.userService.save(user);
        }
    [2]测试查询功能:
        @Autowired
        private UserService userService;
        @Test
        public void testQuery() {
            Example example = new Example(User.class);
            example.createCriteria().andCondition("username like '%i%'");
            example.setOrderByClause("id desc");
            List<User> list = this.userService.selectByExample(example);
            for (User user : list) {
                System.out.println(user.getUsername());
            }
            System.out.println("-------");
            List<User> all = this.userService.selectAll();
            for (User user : all) {
                System.out.println(user.getUsername());
            }
            System.out.println("-------");
            User u = new User();
            u.setId(3);
            User user = this.userService.selectByKey(u);
            System.out.println(user.getUsername());
        }
    [3]测试删除功能:
        @Autowired
        private UserService userService;
        @Test
        public void testDelete() {
            User user = new User();
            user.setId(4);
            this.userService.delete(user);
        }
    [4]测试分页功能:
        // 查看拼接的SQL,在yml或property配置文件中配置logging.level.mapper接口的包名: debug
        @Autowired
        private UserService userService;
        @Test
        public void testPage() {
            PageHelper.startPage(2,2);
            List<User> users = this.userService.selectAll();
            for (User user : users) {
                System.out.println(user.getUsername());
            }
        }
```
