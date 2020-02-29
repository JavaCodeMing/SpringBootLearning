```text
1.项目的pom文件中添加相关依赖:
    <!--web依赖-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!--JdbcTemplate依赖-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <!--MySQL驱动的依赖-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
2.配置数据库连接池和Druid数据源:
    [1]项目的pom文件中添加Druid数据源驱动的依赖:
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.21</version>
        </dependency>
    [2]在application.yml中配置Druid数据源:
    (Spring Boot的数据源配置的默认类型是org.apache.tomcat.jdbc.pool.Datasource)
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
        (上述配置配置了Druid作为连接池,还开启了Druid的监控功能;其他配置可参考
            官方wiki——https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter)
    [3]运行项目,Druid监控后台:http://localhost:8080/web/druid
3.使用JdbcTemplate:
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
        //库表对应的实体对象
        public class StudentObj implements RowMapper<Student>{
            @Override
            public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
                Student student = new Student();
                student.setSno(rs.getString("sno"));
                student.setName(rs.getString("sname"));
                student.setSex(rs.getString("ssex"));
                return student;
            }
        }
    [3]编写Mapper接口:
        public interface StudentMapper {
            int add(Student student);
            int update(Student student);
            int deleteBysno(String sno);
            List<Map<String,Object>> queryStudentsListMap();
            Student queryStudentBySno(String sno);
        }
    [4]编写Mapper的实现类:
        @Repository("StudentMapper")
        public class StudentMapperImpl implements StudentMapper {
            @Autowired
            private JdbcTemplate jdbcTemplate;
            @Override
            public int add(Student student) {
                // 较少字段使用: jdbcTemplate
                // String sql = "insert into student(sno,sname,ssex) values(?,?,?)";
                // Object[] args = { student.getSno(), student.getName(), student.getSex() };
                // int[] argTypes = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
                // return this.jdbcTemplate.update(sql, args, argTypes);
                // 较多字段使用: NamedParameterJdbcTemplate; 返回结果,可直接使用List<Map<String, Object>>来接收
                String sql = "insert into student(sno,sname,ssex) values(:sno,:name,:sex)";
                NamedParameterJdbcTemplate npjt = 
                    new NamedParameterJdbcTemplate(Objects.requireNonNull(this.jdbcTemplate.getDataSource()));
                return npjt.update(sql, new BeanPropertySqlParameterSource(student));
            }
            @Override
            public int update(Student student) {
                String sql = "update student set sname = ?,ssex = ? where sno = ?";
                Object[] args = {student.getName(), student.getSex(), student.getSno()};
                int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
                return this.jdbcTemplate.update(sql, args, argTypes);
            }
            @Override
            public int deleteBysno(String sno) {
                String sql = "delete from student where sno = ?";
                Object[] args = {sno};
                int[] argTypes = {Types.VARCHAR};
                return this.jdbcTemplate.update(sql, args, argTypes);
            }
            @Override
            public List<Map<String, Object>> queryStudentsListMap() {
                String sql = "select * from student";
                return this.jdbcTemplate.queryForList(sql);
            }
            @Override
            public Student queryStudentBySno(String sno) {
                String sql = "select * from student where sno = ?";
                Object[] args = {sno};
                int[] argTypes = {Types.VARCHAR};
                List<Student> studentList = 
                    this.jdbcTemplate.query(sql, args, argTypes, new StudentObj());
                if (studentList.size() > 0) {
                    return studentList.get(0);
                } else {
                    return null;
                }
            }
        }
    [5]编写Service接口:
        public interface StudentService {
            int add(Student student);
            int update(Student student);
            int deleteBysno(String sno);
            List<Map<String, Object>> queryStudentListMap();
            Student queryStudentBySno(String sno);
        }
    [6]编写Service的实现类:
        @Service("studentService")
        public class StudentServiceImpl implements StudentService {
            @Autowired
            private StudentMapper studentMapper;
            @Override
            public int add(Student student) { return this.studentMapper.add(student); }
            @Override
            public int update(Student student) { return this.studentMapper.update(student); }
            @Override
            public int deleteBysno(String sno) { return this.studentMapper.deleteBysno(sno); }
            @Override
            public List<Map<String, Object>> queryStudentListMap() {
                return this.studentMapper.queryStudentsListMap();
            }
            @Override
            public Student queryStudentBySno(String sno) {
                return this.studentMapper.queryStudentBySno(sno);
            }
        }
    [7]编写controller:
        @RestController
        public class TestController {
            @Autowired
            private StudentService studentService;
            @GetMapping("/querystudent/{sno}")
            public Student queryStudentById(@PathVariable String sno) {
                return this.studentService.queryStudentBySno(sno);
            }
            @GetMapping("/queryallstudent")
            public List<Map<String, Object>> queryAllStudent() {
                return this.studentService.queryStudentListMap();
            }
            @PostMapping("/addstudent")
            public int saveStudent(@RequestBody Student student) {
                return this.studentService.add(student);
            }
            @PutMapping("/updatestudent")
            public int updatestudent(@RequestBody Student student){
                return this.studentService.update(student);
            }
            @DeleteMapping("/deletestudent/{sno}")
            public int deleteStudentById(@PathVariable String sno) {
                return this.studentService.deleteBysno(sno);
            }
        }
    [8]测试插入数据: http://localhost:8080/web/addstudent
       {
        "sno": "004",
        "name": "Maria",
        "sex": "F"
        }
       测试查询所有学生数据: http://localhost:8080/web/queryallstudent
       测试更新数据: http://localhost:8080/web/updatestudent
       {
        "sno": "004",
        "name": "Conan",
        "sex": "M"
        }
       测试删除数据: http://localhost:8080/web/deletestudent/004
```
