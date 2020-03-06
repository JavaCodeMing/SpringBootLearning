```text
1.引入web依赖和test依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
2.编写配置类:
    @Configuration
    @EnableAsync
    public class ExecutorConfig {
        @Bean
        public Executor asynServiceExcutor(){
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            // 设置核心线程数
            executor.setCorePoolSize(5);
            // 设置最大线程数
            executor.setMaxPoolSize(10);
            // 设置队列大小
            executor.setQueueCapacity(100);
            // 设置线程池中线程名字的前缀
            executor.setThreadNamePrefix("asynTaskThread-");
            // 设置拒绝策略
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            // 线程池初始化
            executor.initialize();
            return executor;
        }
    }
3.编写service接口:
    public interface AsyncService {
        void writeTest(CountDownLatch countDownLatch);
    }
4.编写service接口的实现类:
    @Service
    public class AsyncServiceImpl implements AsyncService {
        private Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);
        @Override
        @Async("asynServiceExcutor")    // 配置类中定义的并发执行器
        public void writeTest(CountDownLatch countDownLatch) {
            try {
                logger.info("线程[{}]开始执行", Thread.currentThread().getId());
                Thread.sleep(1000);
                logger.info("线程[{}]执行结束", Thread.currentThread().getId());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();
            }
        }
    }
5.编写测试方法:
    @Autowired
    AsyncService asyncService;
    @Test
    public void test1() throws InterruptedException {
        int count = 60;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i <= count; i++){
            asyncService.writeTest(countDownLatch);
        }
        // 防止多线程执行任务时,主线程先结束
        countDownLatch.await();
    }
```