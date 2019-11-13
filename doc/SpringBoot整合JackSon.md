```text
1.自定义ObjectMapper:
    (1)编写自定义ObjectMapper并交给IOC管理:
    	@Configuration
    	public class JacksonConfig {
    	    @Bean
    	    public ObjectMapper getObjectMapper(){
    	    	ObjectMapper objectMapper = new ObjectMapper();
    	    	// 此处仅对日期类型做了格式化处理,也可做其他处理
    	    	objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    	    	return objectMapper;
    	    }
    	}
    (2)编写要序列化的JavaBean:
    	public class User implements Serializable {
    	    private static final long serialVersionUID = 6222176558369919436L;
    	    private String userName;
    	    private int age;
    	    private String password;
    	    private Date birthday;
    	    // get,set略
    	}
    (3)编写测试Controller:
    	@Controller
    	public class TestController {
    	    @RequestMapping("getuser")
    	    @ResponseBody
    	    public User getUser() {
    	    	User user = new User();
    	    	user.setUserName("Mike");
    	    	user.setBirthday(new Date());
    	    	return user;
    	    }
    	}
    	(@ResponseBody注解可以将方法返回的对象序列化成JSON,自定义ObjectMapper在此处生效)
2.序列化: @ResponseBody将返回的对象序列化为JSON、ObjectMapper的writeValueAsString方法将对象序列化为JSON再返回字符串
    @Controller
    public class TestController {
    	@Autowired
    	ObjectMapper objectMapper;
    	@RequestMapping("serialization")
    	@ResponseBody
    	public String serialization() {
    	    try {
    	    	User user = new User();
    	    	user.setUserName("Mike");
    	    	user.setBirthday(new Date());
    	    	String str = objectMapper.writeValueAsString(user);
    	    	return str;
    	    } catch (Exception e) {
    	    	e.printStackTrace();
    	    }
    	    return null;
    	}
    }
3.反序列化: 树遍历和绑定对象
    (1)树遍历:
    	@Controller
    	public class TestController {
    	    @Autowired
    	    ObjectMapper objectMapper;
    	    @RequestMapping("readjsontostring")
    	    @ResponseBody
    	    public String readJsonToString(){
    	    	try {
    	    	    String json = "{\"userName\":\"Mike\",\"age\":25,\"hobby\":{\"first\":\"sleep\",\"second\":\"eat\"}}";
	    	    JsonNode jsonNode = this.objectMapper.readTree(json);
	    	    String userName = jsonNode.get("userName").asText();
	    	    String age = jsonNode.get("age").asText();
	    	    JsonNode hobby = jsonNode.get("hobby");
	    	    String first = hobby.get("first").asText();
	    	    String second = hobby.get("second").asText();
	    	    return "userName:" + userName + " age:" + age + " hobby:"+ first + "," + second;
	    	}catch (Exception e){
	    	    e.printStackTrace();
	    	}
	    	return null;
	    }
	}
	(readTree方法可以接受一个字符串或者字节数组、文件、InputStream等,返回JsonNode作为根节点,
	 你可以像操作XML DOM那样操作遍历JsonNode以获取数据)
    (2)绑定对象: (将JSON数据封装进对象)
    	@Controller
    	public class TestController {
    	    @Autowired
    	    ObjectMapper objectMapper;
    	    @RequestMapping("readjsontoobject")
    	    @ResponseBody
    	    public String readJsonToObject(){
    	    	try {
    	    	    String json = "{\"userName\":\"Mike\",\"age\":25}";
    	    	    User user = objectMapper.readValue(json, User.class);
    	    	    String name = user.getUserName();
    	    	    int age = user.getAge();
    	    	    return name + " " + age;
    	    	}catch (Exception e){
    	    	    e.printStackTrace();
    	    	}
    	    	return null;
    	    }
    	}
4.Jackson注解:
    (1)@JsonProperty: 序列化时为JSON Key指定一个别名,作用在属性上
    	public class User implements Serializable {
    	    private static final long serialVersionUID = 6222176558369919436L;
    	    private String userName;
    	    private int age;
    	    private String password;
    	    @JsonProperty("bth")
    	    private Date birthday;
    	    // get,set略
    	}
    	// {"userName":"Mike","age":0,"password":null,"bth":"2019-04-24 20:28:32"}
    (2)@Jsonlgnore: 序列化时忽略此属性,作用在属性上
    	public class User implements Serializable {
    	    private static final long serialVersionUID = 6222176558369919436L;
    	    private String userName;
    	    private int age;
    	    @JsonIgnore
    	    private String password;
    	    private Date birthday;
    	    // get,set略
    	}
    	// {"userName":"Mike","age":0,"birthday":"2019-04-24 20:27:40"}
    (3)@JsonIgnoreProperties: 序列化时忽略一组属性,作用于类上
    	@JsonIgnoreProperties({ "password", "age" })
    	public class User implements Serializable {
    		...
    	}
    	// {"userName":"Mike","birthday":"2019-04-24 20:31:20"}
    (4)@JsonFormat: 序列化时对注释的日期属性格式化,作用在属性上
    	public class User implements Serializable {
    	    private static final long serialVersionUID = 6222176558369919436L;
    	    private String userName;
    	    private int age;
    	    private String password;
    	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    	    private Date birthday;
    	    // get,set略
    	}
    	// {"userName":"Mike","age":0,"password":null,"birthday":"2019-04-24 12:33:03"}
    (5)@JsonNaming: 指定一个命名策略,作用于类或者属性上(Jackson自带了多种命名策略,也可实现自己的命名策略)
    	// 全部小写,多字由下划线连接策略
    	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    	public class User implements Serializable {
    		...
    	}
    	// {"user_name":"mrbird","bth":"2018-04-02 10:52:12"}
    	PropertyNamingStrategy: 
    	    KebabCase/KebabCaseStrategy: 肉串策略 - 单词小写,使用连字符'-'连接
    	    SnakeCase/SnakeCaseStrategy: 蛇形策略 - 单词小写,使用下划线'_'连接;即老版本中的LowerCaseWithUnderscoresStrategy
    	    LowerCase/LowerCaseStrategy: 小写策略 - 简单的把所有字母全部转为小写,不添加连接符
    	    UpperCamelCase/UpperCamelCaseStrategy: 驼峰策略 - 单词首字母大写其它小写,不添加连接符;即老版本中的PascalCaseStrategy
    (6)@JsonSerialize: 指定一个实现类来自定义序列化,实现类必须继承JsonSerializer抽象类
        // 配置类
        public class UserSerializer extends JsonSerializer<User> {
            @Override
            public void serialize(User user, JsonGenerator jsonGenerator, 
                SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("user-name",user.getUserName());
                jsonGenerator.writeEndObject();
            }
        }
        // JavaBean
        @JsonSerialize(using = UserSerializer.class)
        public class User implements Serializable {
        	...
        }
        // {"user-name":"Mike"}
    (7)@JsonDeserialize: 指定一个实现类来自定义反序列化,实现类必须继承JsonDeserializer抽象类
    	// 配置类
    	public class UserDeserializer extends JsonDeserializer<User> {
    	    @Override
    	    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) 
    	        throws IOException, JsonProcessingException {
    	    	JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    	    	String userName = node.get("user-name").asText();
    	    	User user = new User();
    	    	user.setUserName(userName);
    	    	return user;
    	    }
    	}
    	// JavaBean
    	@JsonDeserialize (using = UserDeserializer.class)
    	public class User implements Serializable {
    		...
    	}
    	// 测试
    	@Autowired
    	ObjectMapper objectMapper;
    	@RequestMapping("readjsonasobject")
    	@ResponseBody
    	public String readJsonAsObject(){
    	    try {
    	    	String json = "{\"user-name\":\"Mike\"}";
    	    	User user = objectMapper.readValue(json, User.class);
    	    	String name = user.getUserName();
    	    	return name;
    	    }catch (Exception e){
    	    	e.printStackTrace();
    	    }
    	    return null;
    	}
    	// Mike
    (8)@JsonView: 定义一个序列化组,作用在属性与类或方法上
    	// JavaBean
    	public class User implements Serializable {
    	    private static final long serialVersionUID = 6222176558369919436L;
    	    // 	定义两个接口代表了两个序列化组的名称
    	    public interface UserBaseView {};
    	    public interface AllUserFieldView extends UserBaseView {};
    	    @JsonView(UserBaseView.class)
    	    private String userName;
    	    @JsonView(UserBaseView.class)
    	    private int age;
    	    @JsonView(AllUserFieldView.class)
    	    private String password;
    	    @JsonView(AllUserFieldView.class)
    	    private Date birthday;
    	    ...	
    	}
    	// 测试
    	@JsonView(User.UserNameView.class)
    	@RequestMapping("getuser")
    	@ResponseBody
    	public User getUser() {
    	    User user = new User();
    	    user.setUserName("Mike");
    	    user.setAge(25);
    	    user.setPassword("123456");
    	    user.setBirthday(new Date());
    	    return user;
    	}
    	// {"userName":"Mike","age":25}
5.集合的反序列化
    [1]通过注解＠RequestBody将提交的JSON自动映射到方法参数上:
    	@RequestMapping("updateuser")
    	@ResponseBody
    	public int updateUser(@RequestBody List<User> list){
    	    return list.size();
    	}
    	(方法参数定义的泛型运行时会被保留在字节码中,所以Spring Boot能识别List包含的泛型类型从而能正确反序列化)
    	(集合对象未包含泛型定义或泛型在运行时被擦除，则反序列化不能得到期望的结果)
    [2]反例: (集合对象未包含泛型定义或泛型在运行时被擦除)
    	@Autowired
    	ObjectMapper objectMapper;
    	@RequestMapping("customize")
    	@ResponseBody
    	public String customize() throws JsonParseException, JsonMappingException, IOException {
    	    String jsonStr = "[{\"userName\":\"Mike\",\"age\":25},{\"userName\":\"scott\",\"age\":27}]";
    	    List<User> list = objectMapper.readValue(jsonStr, List.class);
    	    String msg = "";
    	    for (User user : list) {
    	    	msg += user.getUserName();
    	    }
    	    return msg;
    	}
    	// java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to com.springboot.bean.User
    [3]反例修正: Jackson提供了JavaType,用来指明集合类型
    	@Autowired
    	ObjectMapper objectMapper;
    	@RequestMapping("customize")
    	@ResponseBody
    	public String customize() throws JsonParseException, JsonMappingException, IOException {
    	    String jsonStr = "[{\"userName\":\"Mike\",\"age\":25},{\"userName\":\"scott\",\"age\":27}]";
    	    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, User.class);
    	    List<User> list = objectMapper.readValue(jsonStr, type);
    	    String msg = "";
    	    for (User user : list) {
    	    	msg += user.getUserName();
    	    }
    	    return msg;
    	}
    	// Mikescott
```
