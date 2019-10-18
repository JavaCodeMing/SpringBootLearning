
# Spring Boot 的基础配置

```text
1.Banner的定制与关闭
	定制: 在src/main/resources目录下新建banner.txt文件，然后将自己的图案黏贴进去即可(定制网址:http://www.network-science.de/ascii/)
	关闭: 修改入口类的main方法
			public static void main(String[] args) {
				SpringApplication app = new SpringApplication(DemoApplication.class);
				app.setBannerMode(Mode.OFF);
				app.run(args);
			}
2.全局配置文件: application.properties
	[1]自定义属性值:(通过注解@Value("${属性名}")):
		①在application.properties中自定义属性:
			book.name=Configuration
			book.title=Spring Boot
		②定义一个JavaBean并交由spring管理,通过@Value("${属性名}")来加载配置文件中的属性值为JavaBean的属性赋值
			@Component
			public class BlogProperties {
				@Value("${book.name}")
				private String name;
				@Value("${book.title}")
				private String title;
				// get,set略	
			}
		③在其他类注入该JavaBean,可获取到赋值后的JavaBean属性
	[2]自定义属性值:(通过注解@ConfigurationProperties(prefix="XXX")):
		①在application.properties中自定义属性:
			book.name=Configuration
			book.title=Spring Boot
		②通过@ConfigurationProperties(prefix="XXX")注解定义一个和配置文件对应的Bean:
			@ConfigurationProperties(prefix="book")
			public class ConfigBean {
				private String name;
				private String title;
				// get,set略
			}
		③还需在Spring Boot入口类加上注解@EnableConfigurationProperties({YYY.class})来启用该配置:
			@SpringBootApplication
			@EnableConfigurationProperties({ConfigBean.class})
			public class Application {
				public static void main(String[] args) {
					SpringApplication.run(Application.class, args);
				}
			}
		④在其他类注入该JavaBean,可获取到赋值后的JavaBean属性
	[3]属性间的引用: 在application.properties配置文件中,通过"${XXX}"实现属性的相互引用:
		book.name=Configuration
		book.title=Spring Boot
		book.wholeTitle=${book.name}--${book.title}
3.自定义配置文件
	[1]除在application.properties配置属性,还可新建其他.properties文件:
		①新建test.properties,并配置属性:
			test.name=kimi
			test.age=25
		②通过注解@Configuration、@ConfigurationProperties(prefix="XXX")和@PropertySource("属性文件的位置")定义一个与该配置文件对应的Bean:
			@Configuration
			@ConfigurationProperties(prefix="test")
			@PropertySource("classpath:test.properties")
			public class TestConfigBean {
				private String name;
				private int age;
				// get,set略
			}
		③还需在Spring Boot入口类加上注解@EnableConfigurationProperties({YYY.class})来启用该配置:	
			@SpringBootApplication
			@EnableConfigurationProperties({TestConfigBean.class})
			public class Application {
				public static void main(String[] args) {
					SpringApplication.run(Application.class, args);
				}
			}
		④在其他类注入该JavaBean,可获取到赋值后的JavaBean属性
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
		①以application-{profile}.properties的格式命名配置文件({profile}为环境标识)
			application-dev.properties：开发环境
				server.port=8080
			application-prod.properties：生产环境
				server.port=8081
		②在application.properties文件中通过spring.profiles.active属性来设置生效的配置文件
			spring.profiles.active=dev
	[2]命令行设置属性启动,使用多环境配置文件:
		命令行设置spring.profiles.active属性的值: java-jar xxx.jar--spring.profiles.active={profile}
```
