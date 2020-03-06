
# Spring Boot 的基础配置

```text
1.Banner的定制与关闭
    [1]定制: 在src/main/resources目录下新建banner.txt文件，然后将自己的图案黏贴进去即可(定制网址:http://www.network-science.de/ascii/)
    [2]关闭: 修改入口类的main方法
	public static void main(String[] args) {
	    SpringApplication app = new SpringApplication(DemoApplication.class);
	    app.setBannerMode(Mode.OFF);
	    app.run(args);
	}
2.全局配置文件: application.properties
    [1]自定义属性值:(通过注解@Value("${属性名}")):
    	(1)在application.properties中自定义属性:
    	    book.name=Configuration
    	    book.title=Spring Boot
    	(2)定义一个JavaBean并交由spring管理,通过@Value("${属性名}")来加载配置文件中的属性值为JavaBean的属性赋值
    	    @Component
    	    public class BlogProperties {
    	    	@Value("${book.name}")
    	    	private String name;
    	    	@Value("${book.title}")
    	    	private String title;
    	    	// get,set略	
    	    }
    	(3)在其他类注入该JavaBean,可获取到赋值后的JavaBean属性
    [2]自定义属性值:(通过注解@ConfigurationProperties(prefix="XXX")):
    	(1)在application.properties中自定义属性:
    	    book.name=Configuration
    	    book.title=Spring Boot
    	(2)通过@ConfigurationProperties(prefix="XXX")注解定义一个和配置文件对应的Bean:
    	    @ConfigurationProperties(prefix="book")
    	    public class ConfigBean {
    	    	private String name;
    	    	private String title;
    	    	// get,set略
    	    }
    	(3)还需在Spring Boot入口类加上注解@EnableConfigurationProperties({YYY.class})来启用该配置:
    	    @SpringBootApplication
    	    @EnableConfigurationProperties({ConfigBean.class})
    	    public class Application {
    	    	public static void main(String[] args) {
    	    	    SpringApplication.run(Application.class, args);
    	    	}
    	    }
    	(4)在其他类注入该JavaBean,可获取到赋值后的JavaBean属性
    [3]属性间的引用: 在application.properties配置文件中,通过"${XXX}"实现属性的相互引用:
    	book.name=Configuration
    	book.title=Spring Boot
    	book.wholeTitle=${book.name}--${book.title}
3.自定义配置文件
	[1]除在application.properties配置属性,还可新建其他.properties文件:
	    (1)新建test.properties,并配置属性:
		test.name=kimi
		test.age=25
	    (2)通过@Configuration、@ConfigurationProperties(prefix="XXX")和@PropertySource("属性文件的位置")定义与该配置文件对应的Bean:
		@Configuration
		@ConfigurationProperties(prefix="test")
		@PropertySource("classpath:test.properties")
		public class TestConfigBean {
		    private String name;
		    private int age;
		    // get,set略
		}
	     (3)还需在Spring Boot入口类加上注解@EnableConfigurationProperties({YYY.class})来启用该配置:	
		@SpringBootApplication
		@EnableConfigurationProperties({TestConfigBean.class})
		public class Application {
			public static void main(String[] args) {
				SpringApplication.run(Application.class, args);
			}
		}
	    (4)在其他类注入该JavaBean,可获取到赋值后的JavaBean属性
4.通过命令行设置属性值和禁用命令行修改项目配置:
    [1]命令java -jar xxx.jar --server.port=8081 等效于 application.properties文件中server.port属性的值为8081
    [2]禁用命令行修改项目配置: 修改main方法
    	public static void main(String[] args) {
    	    SpringApplication app = new SpringApplication(Application.class);
    	    app.setAddCommandLineProperties(false);
    	    app.run(args);
    	}
5.使用xml配置:(springboot中不推荐使用xml文件进行配置)
    [1]入口类里通过注解@ImportResource({"classpath:some-application.xml"})来引入xml配置文件
6.Profile配置:(针对不同的环境下使用不同的配置文件)
    [1]正常启动,使用多环境配置文件:
    	(1)以application-{profile}.properties的格式命名配置文件({profile}为环境标识)
    	    application-dev.properties：开发环境
    		server.port=8080
    	    application-prod.properties：生产环境
    		server.port=8081
    	(2)在application.properties文件中通过spring.profiles.active属性来设置生效的配置文件
    		spring.profiles.active=dev
    [2]命令行设置属性启动,使用多环境配置文件:
    	命令行设置spring.profiles.active属性的值: java-jar xxx.jar--spring.profiles.active={profile}
7.YAML语法:
    [1]基本语法: k:(空格)v : 表示一对键值对(空格必须有);
        (以空格的缩进来控制层级关系;只要是左对齐的一列数据,都是同一个层级的;属性和值也是大小写敏感)
    [2]值的写法:
        (1)字面量: 普通的值(数字,字符串,布尔)
            k: v: 字面直接来写;字符串默认不用加上单引号或者双引号;
            1)值加双引号: 不会转义字符串里面的特殊字符;特殊字符会作为本身想表示的意思;
                name: "zhangsan \n lisi": 输出 zhangsan 换行 lisi
            2)值加单引号: 会转义特殊字符,特殊字符最终只是一个普通的字符串数据;
                name: 'zhangsan \n lisi':输出 zhangsan \n lisi
        (2)对象、Map(属性和值)(键值对):
            1)多行写法: 
                friends:
                  lastName: zhangsan 
                  age: 20
            2)行内写法:
                friends: {lastName: zhangsan,age: 18}
        (3)数组(List、Set):
            用"- 值"表示数组中的一个元素;
            1)多行写法:
                pets: 
                  ‐ cat 
                  ‐ dog 
                  ‐ pig
            2)行内写法:
                pets: [cat,dog,pig]
```
