```
Swagger是一款可以快速生成符合RESTful风格API并进行在线调试的插件;
REST实际上为Representational State Transfer的缩写,翻译为“表现层状态转化”;如果一个架构符合REST原则,就称它为RESTful架构;
表现层即资源的展现在你面前的形式,比如文本可以是JSON格式的,也可以是XML形式的,甚至为二进制形式的;
状态转换为通过HTTP协议(包含了一些操作资源的方法,如:GET 用来获取资源,POST用来新建资源,PUT用来更新资源,
DELETE用来删除资源,PATCH用来更新资源的部分属性)的方法来操作资源的过程;
1.传统URL请求和RESTful风格请求的区别: (RESTful只是一种风格,并不是一种强制性的标准)
	描述          传统请求                    方法          RESTful请求           方法
	查询     /user/query?name=mrbird          GET          /user?name=mrbird      GET
	详情       /user/getInfo?id=1             GET          /user/1                GET
	创建     /user/create?name=mrbird         POST         /user                  POST
	修改     /user/update?name=mrbird&id=1    POST         /user/1                PUT
	删除     /user/delete?id=1                GET          /user/1                DELETE
	(传统请求通过URL来描述行为,如create,delete等;RESTful请求通过URL来描述资源;)
	(RESTful请求通过HTTP请求的方法来描述行为,比如DELETE,POST,PUT等,并且使用HTTP状态码来表示不同的结果)
	(RESTful请求通过JSON来交换数据)
2.Spring Boot中包含了一些注解,对应于HTTP协议中的方法:
	@GetMapping:	对应HTTP中的GET方法
	@PostMapping:	对应HTTP中的POST方法
	@PutMapping:	对应HTTP中的PUT方法
	@DeleteMapping:	对应HTTP中的DELETE方法
	@PatchMapping:	对应HTTP中的PATCH方法
3.Swagger常用注解:
	[1]@Api: 修饰整个类
	    (1)tags: 描述Controller的作用
	    @Api(tags={"用户操作接口"})
	[2]@ApiOperation: 作用于方法上
	    (1)value: 用于方法简短叙述
            (2)notes: 用于方法详细描述
            (3)tags: 可以重新分组(视情况而用)
            @ApiOperation(value="获取用户信息",tags={"获取用户信息copy"},notes="注意问题")
	[3]@ApiParam: 用于方法参数前,表示对参数的添加元数据(说明或是否必填等)
	    (1)name: 参数名 
            (2)value: 参数说明 
            (3)required: 是否必填
            public int updateUserInfo(@RequestBody @ApiParam(name="用户对象",value="json格式",required=true) User user){}
	[4]@ApiModel: 用于实体类上,用实体类接收参数
	    (1)value: 表示对象名
            (2)description: 描述
            @ApiModel(value="user对象",description="用户对象user")
            public class User implements Serializable{}
    [5]@ApiModelProperty: 用于实体类属性上,表示对实体类中属性的说明
        (1)value: 属性说明 
        (2)name: 属性名字 
        (3)dataType: 属性类型 
        (4)required: 是否必填 
        (5)example: 举例说明 
        (6)hidden: 隐藏
        @ApiModelProperty(value="用户名",name="username",example="conan")
        private String username;
    [6]@ApiResponse: 用于方法,和@ApiResponses组合使用,HTTP响应的其中1个描述
    [7]@ApiResponses: 用于方法,HTTP响应整体描述
    [8]@ApiIgnore: 用于类,方法,方法参数,表示被swagger忽略
    [9]@ApiError: 用于方法,发生错误返回的信息
    [10]@ApiImplicitParam: 用于方法,和@ApiImplicitParam组合使用,表示单独的请求参数
    [11]@ApiImplicitParams: 用于方法,包含多个@ApiImplicitParam
	(1)name: 参数名
        (2)value: 参数说明 
        (3)dataType: 数据类型 
        (4)paramType: 参数类型 
        (5)example: 举例说明
4.引入web依赖和Swagger依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.8.0</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.8.0</version>
    </dependency>
5.编写Swagger的配置类:
    @Configuration
    @EnableSwagger2
    public class SwaggerConfig {
    	@Bean
    	public Docket buildDocket(){
    	    return new Docket(DocumentationType.SWAGGER_2)
    	    	.apiInfo(buildApiInf())
    	    	.select()
    	    	.apis(RequestHandlerSelectors.basePackage("com.example.swagger2.controller"))
    	    	.paths(PathSelectors.any())
    	    	.build();
    	}
    	private ApiInfo buildApiInf(){
    	    return new ApiInfoBuilder()
    	    	.title("系统RESTful API文档")
    	    	.contact(new Contact("Conan",null,null))
    	    	.version("1.0")
    	    	.build();
    	}
    }
6.编写RESTful风格的Controller:
    @Api("用户Controller")
    @Controller
    @RequestMapping("user")
    public class UserController {
    	@ApiIgnore
    	public @ResponseBody String hello(){
    	    return "hello";
    	}
    	@ApiOperation(value = "获取用户信息",notes = "根据用户ID获取用户信息")
    	@ApiImplicitParam(name = "id",value = "用户ID",required = true,dataType = "Long",paramType = "path")
    	@GetMapping("/query/{id}")
    	public @ResponseBody User getUserId(@PathVariable("id") Long id){
    	    User user = new User();
    	    user.setId(id);
    	    user.setName("Conan");
    	    user.setAge(7);
    	    return user;
    	}
    	@ApiOperation(value = "新增用户",notes = "根据用户实体创建用户")
    	@ApiImplicitParam(name = "user",value = "用户实体",required = true,dataType = "User")
    	@PostMapping("/add")
    	public @ResponseBody Map<String,Object> addUser(@RequestBody User user ){
    	    Map<String,Object> map = new HashMap<>();
    	    map.put("result","success");
    	    return map;
    	}
    	@ApiOperation(value = "更新用户",notes = "根据用户ID更新用户")
    	@ApiImplicitParams({
    	    @ApiImplicitParam(name = "id",value = "用户ID",required = true,dataType = "Long",paramType = "path"),
    	    @ApiImplicitParam(name = "user",value = "用户实体",required = true,dataType = "User")
    	})
    	@PutMapping("/update/{id}")
    	public @ResponseBody Map<String,Object> updateUser(@PathVariable("id") String id,@RequestBody User user){
    	    Map<String, Object> map = new HashMap<>();
    	    map.put("result", "success");
    	    return map;
    	}
    	@ApiOperation(value = "删除用户",notes = "根据用户ID删除用户")
    	@ApiImplicitParam(name = "id",value = "用户ID",required = true,dataType = "Long",paramType = "path")
    	@DeleteMapping("/delete/{id}")
    	public @ResponseBody Map<String,Object> deleteUser(@PathVariable("id") String id){
    	    Map<String, Object> map = new HashMap<>();
    	    map.put("result", "success");
    	    return map;
    	}
    }
7.启动&测试: http://localhost:8080/swagger-ui.html
```
