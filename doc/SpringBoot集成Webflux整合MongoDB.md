```text
1.引入webflux和reactive mongodb依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>
2.开启Reactive MongoDB: (在入口类上添加@EnableReactiveMongoRepositories注解)
    @SpringBootApplication
    @EnableReactiveMongoRepositories
    public class WebfluxApplication {
        public static void main(String[] args) {
            SpringApplication.run(WebfluxApplication.class, args);
        }
    }
3.在yml配置文件里配置MongoDB连接:
    spring:
        data:
            mongodb:
                host: localhost
                # 启动MongoDB使用的端口号
                port: 27017
                # MongoDB中操作的database,需要创建
                database: webflux
4.Mongo Shell或者Mongo Compass工具创建数据库webflux,并新增user文档:
    (1)使用Mongo Shell:
        1)创建数据库webflux: use webflux
        2)新增user文档: db.createCollection(user)
    (2)使用Mongo Compass工具创建数据库: 图形化操作
5.创建User实体类:
    @Document(collection = "user")
    public class User {
        @Id
        private String id;
        private String name;
        private Integer age;
        private String description;
        // get set 略
    }
6.编写Mapper接口: (ReactiveMongoRepository提供的方法都是响应式的)
    @Repository
    public interface UserDao extends ReactiveMongoRepository<User, String> {
    }
7.编写Service接口:
    public interface UserService {
        // 查询文档内所有数据
        Flux<User> selectAll();
        // 通过id查询文档内的数据
        Mono<User> selectById(String id);
        // 向文档内插入一条记录
        Mono<User> create(User user);
        // 通过id更新文档内容
        Mono<User> updateById(String id,User user);
        // 通过id删除文档内容
        Mono<Void> deleteById(String id);
    }
8.编写Service实现类:
    @Service
    public class UserServiceImpl implements UserService {
        @Autowired
        private UserMapper mapper;
        @Override
        public Flux<User> selectAll() {
            return this.mapper.findAll();
        }
        @Override
        public Mono<User> selectById(String id) {
            return this.mapper.findById(id);
        }
        @Override
        public Mono<User> create(User user) {
            return this.mapper.save(user);
        }
        @Override
        public Mono<User> updateById(String id, User user) {
            return this.mapper.findById(id)
                .flatMap(
                    u -> {
                        u.setName(user.getName());
                        u.setAge(user.getAge());
                        u.setDescription(user.getDescription());
                        return this.mapper.save(user);
                    }
            );
        }
        @Override
        public Mono<Void> deleteById(String id) {
            return this.mapper.findById(id).flatMap(user -> this.mapper.delete(user));
        }
    }
9.编写Controller:
    @RestController
    public class UserController {
        @Autowired
        private UserService service;
        @GetMapping("/user")
        public Flux<User> getUsers(){
            return this.service.selectAll();
        }
        @GetMapping("/user/{id}")
        public Mono<ResponseEntity<User>> getUser(@PathVariable String id){
            return this.service.selectById(id)
                    .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                    .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        @PostMapping("/user")
        public Mono<User> createUser(User user){
            return this.service.create(user);
        }
        @PutMapping("/user/{id}")
        public Mono<ResponseEntity<User>> updateUser(@PathVariable String id,User user){
            return this.service.updateById(id,user)
                    .map(u -> new ResponseEntity<>(u,HttpStatus.OK))
                    .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        @DeleteMapping("/user/{id}")
        public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id){
            return this.service.deleteById(id)
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                    .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }
```