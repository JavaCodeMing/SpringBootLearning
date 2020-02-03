# 模式注解
```text
    Stereotype Annotation俗称为模式注解,Spring中常见的模式注解有@Service,@Repository,@Controller等
都派生自@Component注解;凡是被@Component及其派生的注解所标注的类都会被Spring扫描并纳入到IOC容器中;
```
```text
1.@Component "派生性"和"层次性":
    [1]案例: 自定义@CustomService注解
        @Target({ElementType.TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @Service
        public @interface CustomService {
            String value() default "";
        }
    [2]分析派生性和层次性:
        (1)派生性: 
            在@Service的源码中,其是被@Component注解标注,因此@Service是@Component派生出来的模式注解;
            在@CustomService中,其是被@Service注解标注,因此@CustomService也是@Component派生出来的模式注解;
        (2)层次性: (@Component,@Service,@CustomService的层次关系如下)
            └─@Component
                └─@Service
                    └─@CustomService
    [3]测试模式注解的作用:
        (1)新建一个Spring Boot工程,版本为2.2.0.RELEASE,artifactId为autoconfig,并引入web依赖;
        (2)在com.example.autoconfig下创建annotation包,然后创建一个@CustomService注解:
            @Target({ElementType.TYPE})
            @Retention(RetentionPolicy.RUNTIME)
            @Documented
            @Service
            public @interface CustomService {
                String value() default "";
            }
        (3)在com.example.autoconfig下新建service包,然后创建一个TestService类: 
            @CustomService
            //@Service
            public class TestService {
            }
        (4)在com.example.autoconfig下新建bootstrap包,然后创建一个ServiceBootStrap类,
            用于测试注册TestService并从IOC容器中获取它:
            @ComponentScan("com.example.autoconfig.service")
            public class ServiceBootstrap {
                public static void main(String[] args) {
                    ConfigurableApplicationContext context = new SpringApplicationBuilder(ServiceBootstrap.class)
                        .web(WebApplicationType.NONE)
                        .run(args);
                    TestService testService = context.getBean("testService", TestService.class);
                    System.out.println("TestService Bean: " + testService);
                    context.close();
                }
            }
        (5)分别测试TestService类上的两个注解,查看控制台是否能正常打印,以验证通过对应注解是否将TestService注册到IOC容器;
(注: @Component注解只包含一个value属性定义,所以其派生的注解也只能包含一个vlaue属性定义)
```
# @Enable模块驱动
```text
@Enable模块驱动在Spring Framework 3.1后开始支持;通过@Enable模块驱动,可开启相应的模块功能;
    (这里的模块通俗的来说就是一些为了实现某个功能的组件的集合)
@Enable模块驱动可以分为"注解驱动"和"接口编程"两种实现方式;
```
```text
1.注解驱动: 
    [1]基于注解驱动的示例分析:
        (1)Spring中@EnableWebMvc源码:
            @Retention(RetentionPolicy.RUNTIME)
            @Target({ElementType.TYPE})
            @Documented
            @Import({DelegatingWebMvcConfiguration.class})
            public @interface EnableWebMvc {
            }
        (2)@EnableWebMvc注解通过@Import导入一个配置类DelegatingWebMvcConfiguration:
            (该配置类又继承自WebMvcConfigurationSupport,里面定义了一些Bean的声明)
            public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
                private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();
                public DelegatingWebMvcConfiguration() {}
                ......
            }
        (3)结论:
            基于注解驱动的@Enable模块驱动其实就是通过@Import来导入一个配置类,以此实现相应模块的组件注册,
            当这些组件注册到IOC容器中,这个模块对应的功能也就可以使用了;
    [2]定义基于注解驱动的@Enable模块驱动:
        (1)在com.example.autoconfig下新建configuration包,然后创建一个HelloWorldConfiguration配置类:
            @Configuration
            public class HelloWorldConfiguration {
                @Bean
                public String hello() {
                    return "hello world";
                }
            }
        (2)在com.example.autoconfig.annotation下创建一个EnableHelloWorld注解定义:
            (在该注解类上通过@Import导入了刚刚创建的配置类)
            @Target({ElementType.TYPE})
            @Retention(RetentionPolicy.RUNTIME)
            @Documented
            @Import(HelloWorldConfiguration.class)
            public @interface EnableHelloWorld {
            }
        (3)在com.example.autoconfig.bootstrap下创建TestEnableBootstap启动类来测试@EnableHelloWorld注解是否生效:
            @EnableHelloWorld
            public class TestEnableBootstap {
                public static void main(String[] args) {
                    ConfigurableApplicationContext context = new SpringApplicationBuilder(TestEnableBootstap.class)
                        .web(WebApplicationType.NONE)
                        .run(args);
                    String hello = context.getBean("hello", String.class);
                    System.out.println("hello Bean: " + hello);
                    context.close();
                }
            }
        (4)运行TestEnableBootstap启动类的main方法,控制台输出如下: 
            hello Bean: hello world
            (说明自定义的基于注解驱动的@EnableHelloWorld是可行的)
2.接口编程:
    [1]基于接口编程方式的示例分析:
        (1)Spring中@EnableCaching源码:
            @Target({ElementType.TYPE})
            @Retention(RetentionPolicy.RUNTIME)
            @Documented
            @Import({CachingConfigurationSelector.class})
            public @interface EnableCaching {
                boolean proxyTargetClass() default false;
                AdviceMode mode() default AdviceMode.PROXY;
                int order() default 2147483647;
            }
        (2)@EnableCaching注解通过@Import导入了CachingConfigurationSelector类,该类间接实现了ImportSelector接口,
            通过ImportSelector来实现组件注册;
        (3)结论:
            通过接口编程实现@Enable模块驱动的本质是通过@Import来导入接口ImportSelector实现类,该实现类里可以
            定义需要注册到IOC容器中的组件,以此实现相应模块对应组件的注册;
    [2]定义基于接口编程的@Enable模块驱动:
        (1)在com.example.autoconfig下新建selector包,创建HelloWorldImportSelector类实现ImportSelector接口:
            public class HelloWorldImportSelector implements ImportSelector {
                @Override
                public String[] selectImports(AnnotationMetadata importingClassMetadata) {
                    return new String[]{HelloWorldConfiguration.class.getName()};
                }
            }
        (2)修改@EnableHelloWorld注解:
            @Target({ElementType.TYPE})
            @Retention(RetentionPolicy.RUNTIME)
            @Documented
            @Import(HelloWorldImportSelector.class)
            public @interface EnableHelloWorld {
            }
        (3)运行TestEnableBootstap启动类的main方法,控制台输出如下: 
            hello Bean: hello world
            (说明自定义的基于注解驱动的@EnableHelloWorld是可行的)
```
# 自动装配
```text
1.Spring Boot中的自动装配技术底层主要用到了下面这些技术:
    (1)Spring 模式注解装配
    (2)Spring @Enable 模块装配
    (3)Spring 条件装配装(深入学习Spring组件注册中有介绍)
    (4)Spring 工厂加载机制
    [1]Spring工厂加载机制的实现类为SpringFactoriesLoader,查看其源码:
        public final class SpringFactoriesLoader {
            public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
            private static final Log logger = LogFactory.getLog(SpringFactoriesLoader.class);
            private static final Map<ClassLoader, MultiValueMap<String, String>> cache = new ConcurrentReferenceHashMap();
            private SpringFactoriesLoader() {}
            ......
        }
    [2]该类的方法会读取META-INF目录下的spring.factories配置文件:
        (spring-boot-autoconfigure-2.2.0.RELEASE.jar\META-INF\spring.factories)
        当启动类被@EnableAutoConfiguration标注后,Spring会去扫描该文件里的所有类,看是否可以纳入到IOC容器中进行管理;
    [3]案例分析: org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration的源码:
        @ConditionalOnClass({RedisOperations.class})
        @EnableConfigurationProperties({RedisProperties.class})
        @Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})
        public class RedisAutoConfiguration {
            public RedisAutoConfiguration() {}
            ......
        }
        (1)@Configuration为模式注解;
        (2)@EnableConfigurationProperties为@Enable模块装配技术;
        (3)@ConditionalOnClass为条件装配技术;
2.自定义一个自动装配实现:
    [1]新建配置类HelloWorldAutoConfiguration:
        @Configuration
        @EnableHelloWorld
        @ConditionalOnProperty(name = "helloworld", havingValue = "true")
        public class HelloWorldAutoConfiguration {
        }
    [2]在resources目录下新建META-INF目录,并创建spring.factories文件:
        # Auto Configure
        org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
        com.example.autoconfig.configuration.HelloWorldAutoConfiguration
    [3]在配置文件application.properties中添加配置: helloworld=true
    [4]创建EnableAutoConfigurationBootstrap,测试下HelloWorldAutoConfiguration是否生效:
        @EnableAutoConfiguration
        public class EnableAutoConfigurationBootstrap {
            public static void main(String[] args) {
                ConfigurableApplicationContext context = new SpringApplicationBuilder(EnableAutoConfigurationBootstrap.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
                String hello = context.getBean("hello", String.class);
                System.out.println("hello Bean: " + hello);
                context.close();
            }
        }
    [5]运行EnableAutoConfigurationBootstrap类的main方法,控制台输出如下: 
        hello Bean: hello world
        (说明我们自定义的自动装配已经成功了)
3.简要分析下代码的运行逻辑:
    [1]Spring 的工厂加载机制会自动读取META-INF目录下spring.factories文件内容;
    [2]在spring.factories定义了:
        org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
        com.example.autoconfig.configuration.HelloWorldAutoConfiguration
        (在测试类上使用了@EnableAutoConfiguration注解标注,那么HelloWorldAutoConfiguration就会被Spring扫描,
         看是否符合要求,如果符合则纳入到IOC容器中;)
    [3]HelloWorldAutoConfiguration上的@ConditionalOnProperty的注解作用为:
        当配置文件中配置了helloworld=true,则这个类符合扫描规则;
        @EnableHelloWorld注解是前面例子中自定义的模块驱动注解,其引入了hello这个Bean,所以IOC容器中便会存在hello这个Bean了;
    [4]通过上面的步骤,就可以通过上下文获取到hello这个Bean了;
```
