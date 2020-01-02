```
1.引入web依赖和mongodb依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
2.Mongo Shell或者Mongo Compass工具创建数据库testdb,并新增user文档:
    (1)使用Mongo Shell:
        1)创建数据库testdb: use testdb
        2)新增user文档: db.createCollection(user)
    (2)使用Mongo Compass工具创建数据库: 图形化操作
3.配置文件application.yml里配置Mongo DB:
    spring:
        data:
            mongodb:
                host: localhost
                # MongoDB的默认端口为27017
                port: 27017
                database: testdb
4.创建User实体类:
    // 声明文档对象,名称为user
    @Document(collection = "user")
    public class User {
        //@Id标注主键字段
        @Id 
        private String id;
        private String name;
        private Integer age;
        private String description;
        // get set 略
    }
    (String类型的主键值插入MongoDB时会自动生成;若对象中某属性为非表字段,
    可用注解@Transient排除)
5.编写Mapper接口:
    @Repository
    public interface UserMapper extends MongoRepository<User, String> {
        //通过年龄段,用户名,描述(模糊查询)(自定义方法)
        List<User> findByAgeBetweenAndNameEqualsAndDescriptionIsLike(Integer from,
            Integer to, String name, String description);
    }
    (MongoRepository提供了一些增删改查的方法,通过继承可直接使用)
    (自定义方法,在输入findBy后,IDEA会根据实体对象的属性和SQL的各种关键字自动组合提示)
6.编写Service接口:
    public interface UserService {
        // 查询文档内所有数据
        List<User> selectAll();
        // 通过id查询文档内的数据
        Optional<User> selectById(String id);
        // 向文档内插入一条记录
        User create(User user);
        // 通过id更新文档内容
        void updateById(String id,User user);
        // 通过id删除文档内容
        void deleteById(String id);
        // 通过条件进行模糊查询,并将结果进行分页
        Page<User> selectByCondition(int size, int page, User user);
    }
7.编写Service实现类:
    @Service
    public class UserServiceImpl implements UserService {
        // 排序和分页需要使用MongoTemplate对象来完成
        @Autowired
        private MongoTemplate template;
        @Autowired
        private UserMapper mapper;
        @Override
        public List<User> selectAll() {
            return this.mapper.findAll();
        }
        @Override
        public Optional<User> selectById(String id) {
            return this.mapper.findById(id);
        }
        @Override
        public User create(User user) {
            user.setId(null);
            return this.mapper.save(user);
        }
        @Override
        public void updateById(String id, User user) {
            this.mapper.findById(id)
                    .ifPresent(
                            u -> {
                                u.setName(user.getName());
                                u.setAge(user.getAge());
                                u.setDescription(user.getDescription());
                                this.mapper.save(u);
                            }
                    );
        }
        @Override
        public void deleteById(String id) {
            this.mapper.findById(id)
                    .ifPresent( user -> this.mapper.deleteById(id));
        }
        // size表示每页显示的条数,page表示当前页码数(0表示第一页)
        // 通过name和description来模糊查询用户信息再分页,并且查询结果使用age字段降序排序
        @Override
        public Page<User> selectByCondition(int size, int page, User user) {
            Query query = new Query();
            Criteria criteria = new Criteria();
            if(!StringUtils.isEmpty(user.getName())){
                criteria.and("name").is(user.getName());
            }
            if(!StringUtils.isEmpty(user.getDescription())){
                criteria.and("description").regex(user.getDescription());
            }
            query.addCriteria(criteria);
            Sort sort = new Sort(Sort.Direction.DESC,"age");
            PageRequest pageable = PageRequest.of(page, size, sort);
            List<User> users = template.find(query.with(pageable), User.class);
            return PageableExecutionUtils.getPage(users,pageable,
                ()-> template.count(query,User.class));
        }
    }
8.编写controller: (RESTful风格)
    @RestController
    public class UserController {
        @Autowired
        private UserService service;
        @GetMapping("/user/")
        public List<User> getUsers(){
            return this.service.selectAll();
        }
        @GetMapping("/user/{id}")
        public User getUser(@PathVariable String id){
            return this.service.selectById(id).orElse(null);
        }
        @PostMapping("/user/")
        public User createUser(User user){
            return this.service.create(user);
        }
        @DeleteMapping("/user/{id}")
        public void deleteUser(@PathVariable String id){
            this.service.deleteById(id);
        }
        @PutMapping("/user/{id}")
        public void updateUser(@PathVariable String id,User user){
            this.service.updateById(id,user);
        }
        @GetMapping("/user/condition")
        public Page<User> getUserByCondition(int size,int page,User user){
            return this.service.selectByCondition(size, page, user);
        }
    }
```
