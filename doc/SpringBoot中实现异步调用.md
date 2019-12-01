```
1.引入web依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
2.开启异步支持: (入口类上加上@EnableAsync注解)
    @SpringBootApplication
    @EnableAsync
    public class DemoApplication {
        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
        }
    }
3.编写支持异步调用的服务:
    @Service
    public class TestService {
        //底层默认使用logback
        private Logger logger = LoggerFactory.getLogger(this.getClass());
        @Async
        public void asyncMethod() {
            sleep();
            logger.info("异步方法内部线程名称：{}", Thread.currentThread().getName());
        }
        public void syncMethod() { sleep(); }
        private void sleep() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
4.编写测试controller:
    @RestController
    public class TestController {
        private Logger logger = LoggerFactory.getLogger(this.getClass());
        @Autowired
        private TestService testService;
        @GetMapping("async")
        public void testAsync() {
            long start = System.currentTimeMillis();
            logger.info("异步方法开始");
            testService.asyncMethod();
            logger.info("异步方法结束");
            long end = System.currentTimeMillis();
            logger.info("总耗时：{} ms", end - start);
        }
        @GetMapping("sync")
        public void testSync() {
            long start = System.currentTimeMillis();
            logger.info("同步方法开始");
            testService.syncMethod();
            logger.info("同步方法结束");
            long end = System.currentTimeMillis();
            logger.info("总耗时：{} ms", end - start);
        }
    }
5.启动项目,分别测试同步方法和异步方法的调用:
    同步: http://localhost:8080/sync 
        同步方法开始
        同步方法结束
        总耗时:2004 ms 
    异步: http://localhost:8080/async
        异步方法开始
        异步方法结束
        总耗时:4 ms
        异步方法内部线程名称：task-1
    (当遇到异步方法时,会新启一个线程来执行异步方法)
    (默认情况下的异步线程池配置使得线程不能被重用,每次调用异步方法都会新建一个线程)
6.自定义异步线程池: 
    [1]自定义线程池的配置(使用ThreadPoolTaskExecutor的方法):
        corePoolSize: 线程池核心线程的数量,默认值为1;(默认异步线程池配置使得线程不能被重用的原因)
        maxPoolSize: 线程池维护的线程的最大数量,默认值为Integer.MAX_VALUE;
            只有当核心线程都被用完并且缓冲队列满后,才会开始申超过请核心线程数的线程;
        queueCapacity: 缓冲队列;
        keepAliveSeconds: 超出核心线程数外的线程在空闲时候的最大存活时间,默认为60秒;
        threadNamePrefix: 线程名前缀;
        waitForTasksToCompleteOnShutdown: 是否等待所有线程执行完毕才关闭线程池,默认值为false;
        awaitTerminationSeconds: waitForTasksToCompleteOnShutdown的等待的时长,默认值为0,即不等待;
        rejectedExecutionHandler: 当没有线程可以被使用时的处理策略(拒绝任务),默认策略为abortPolicy;
        包含下面四种策略:
        callerRunsPolicy: 用于被拒绝任务的处理程序,它直接在execute方法的调用线程中运行被拒绝的任务;
            如果执行程序已关闭,则会丢弃该任务;
        abortPolicy: 直接抛出java.util.concurrent.RejectedExecutionException异常;
        discardOldestPolicy: 当线程池中的数量等于最大线程数时,抛弃线程池中最后一个要执行的任务,
            并执行新传入的任务;
        discardPolicy: 当线程池中的数量等于最大线程数时,不做任何动作;
    [2]添加自定义异步线程:
        @Configuration
        public class AsyncPoolConfig {
            @Bean
            public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor(){
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                executor.setCorePoolSize(20);
                executor.setMaxPoolSize(200);
                executor.setQueueCapacity(25);
                executor.setKeepAliveSeconds(200);
                executor.setThreadNamePrefix("asyncThread");
                executor.setWaitForTasksToCompleteOnShutdown(true);
                executor.setAwaitTerminationSeconds(60);
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                executor.initialize();
                return executor;
            }
        }
    [3]使用自定义异步线程池时,@Async注解需指定线程池Bean名称:
        @Service
        public class TestService {
            ......
            @Async("asyncThreadPoolTaskExecutor")
            public void asyncMethod() {
            ......
            }
            ......
        }
7.处理异步回调:
    [1]Future及其实现类的关系:
        Future(接口) --> ListenableFuture(接口) --> AsyncResult(类,Spring实现的Future实现类)
        @Async("asyncThreadPoolTaskExecutor")
        public Future<String> asyncMethod() {
            sleep();
            logger.info("异步方法内部线程名称：{}", Thread.currentThread().getName());
            return new AsyncResult<>("hello async");
        }
    [2]异步回调的具体使用:
        (1)在service的异步方法中使用Future包装需要返回的对象:
            @Service
            public class TestReturnService {
                //底层默认使用logback
                private Logger logger = LoggerFactory.getLogger(this.getClass());
                @Async("asyncThreadPoolTaskExecutor")
                public Future<String> asyncReturnMethod(){
                    sleep();
                    logger.info("异步方法内部线程名称：{}", Thread.currentThread().getName());
                    return new AsyncResult<>("hello async");
                }
                private void sleep() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        (2)在调用异步方法的地方,使用Future接口的get方法获取异步方法真实返回的对象:
            @RestController
            public class TestController {
                private Logger logger = LoggerFactory.getLogger(this.getClass());
                @Autowired
                private TestService testService;
                @Autowired
                private TestReturnService testReturnService;
                @GetMapping("asyncReturn")
                public String testAsyncReturn() throws ExecutionException, InterruptedException {
                    long start = System.currentTimeMillis();
                    logger.info("异步方法开始");
                    Future<String> stringFuture = testReturnService.asyncReturnMethod();
                    String result = stringFuture.get();
                    logger.info("异步方法返回值：{}", result);
                    logger.info("异步方法结束");
                    long end = System.currentTimeMillis();
                    logger.info("总耗时：{} ms", end - start);
                    return result;
                }
            (Future的get方法为阻塞方法,只有当异步方法返回内容了,程序才会继续往下执行;)
            (get(long timeout, TimeUnit unit)重载方法可设置超时时间,即异步方法在设定时间内没有返回值的话,
             直接抛出java.util.concurrent.TimeoutException异常)
```
