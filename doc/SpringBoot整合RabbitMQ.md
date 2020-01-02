1.安装: (windows)
    [1]安装Erlang:
        (1)下载: http://www.erlang.org/downloads
        (2)配置Erlang环境变量: 
            1)新建系统变量: 
                变量名: ERLANG_HOME  变量值: D:\Develop\Erlang10.5
            2)修改系统变量: (在Path变量里添加以下内容)
                %ERLANG_HOME%\bin;
        (3)在cmd中通过命令测试Erlang是否安装成功: erl
    [2]安装RabbitMQ:
        (1)下载: http://www.rabbitmq.com/download.html
            1)exe安装地址: http://www.rabbitmq.com/install-windows.html
            2)解压缩安装地址: http://www.rabbitmq.com/install-windows-manual.html
        (2)配置RabbitMQ环境变量:
            1)新建系统变量: 
                变量名: RABBITMQ_SERVER 变量值: D:\Develop\rabbitmq_server-3.8.1
            2)修改系统变量: (在Path变量里添加以下内容)
                %RABBITMQ_SERVER%\sbin
        (3)安装插件: rabbitmq-plugins.bat enable rabbitmq_management
        (4)启动rabbitmq: rabbitmq-server.bat 
        (5)浏览器中访问rabbitmq控制台: http://localhost:15672 (默认用户名密码: guest,guest)
        (RabbitMQ的状态查询命令: rabbitmqctl status)
2.RabbitMQ的相关概念:
    [1]生产者和消费者:
        (1)Producer(生产者): 投递消息(包含两部分: 消息体和标签)的一方;
            1)消息体: 也称为payload,一般是带有业务逻辑结构的数据,如一个JSON字符串;
            2)标签: 用来标书该消息,如一个交换机的名称和一个路由键;
            (生产者把消息交由RabbitMQ,RabbitMQ之后会根据标签把消息发送给感兴趣的消费者)
        (2)Consumer(消费者): 接收消息的一方;
            1)消费消息时,只是消费消息体(payload),消息的标签在消息路由的过程中被丢弃,存入队列的消息只有消息体;
        (3)Broker(消息中间件的服务节点): RabbitMQ服务节点或服务实例;
    [2]Queue(队列): RabbitMQ的内部对象,用于存储消息;
    [3]交换器,路由键,绑定:
        (1)Exchange(交换器): 
            1)交换器的四种类型:
                ①direct(直连交换机): 
                    把消息路由到哪些BindingKey和RoutingKey完全匹配的队列中;
                ②fanout(扇型交换机): 
                    把所有发送到该交换器的消息路由到所有与该交换器绑定的队列中;
                ③topic(主题交换机):    
                    把消息路由到BindingKey和RoutingKey相匹配的队列中;
                ④headers(头交换机): 
                    不依赖于路由键的匹配规则来路由消息,而是根据发送的消息内容中的headers属性进行匹配;(性能差)
            2)生产者将消息发送到Exchange,由交换器将消息路由到一个或多个队列中,若路由不到则返回给生产者或丢弃;
    (2)RoutingKey(路由键):
        1)生产者将消息发给交换器的时候,一般指定一个RoutingKey,用来指定该消息的路由规则;
        2)此RoutingKey需要与交换器类型和绑定键(BindingKey)联合使用才能最终生效;
        3)在交换器类型和绑定键固定时,生产者可以在发送消息给交换器时,通过指定RoutingKey来决定消息流向哪里;
    (3)Binding(绑定): 通过绑定将交换器与队列关联起来,在绑定时指定绑定键可使消息正确路由到队列;
3.RabbitMQ运转流程:
    [1]生产者发送消息:
        (1)生产者连接到RabbitMQ Broker,建立连接(Connection),开启一个信道(Channel);
        (2)生产者声明一个交换器,并设置相关属性,比如交换机类型,是否持久化等;
        (3)生产者声明一个队列并设置相关属性,比如是否排他,是否持久化,是否自动删除等;
        (4)生产者通过路由键将交换器和队列绑定起来;
        (5)生产者发送消息至RabbitMQ Broker,其中包含路由键,交换器等信息;
        (6)相应的交换器根据接收到的路由键查找相匹配的队列;
        (7)如果找到,则将从生产者发送过来的消息存入相应的队列中;
        (8)如果没有找到,则根据生产者配置的属性选择丢弃还是回退给生产者;
        (9)关闭信道;
        (10)关闭连接;
    [2]消费者接收消息的过程:
        (1)消费者连接到RabbitMQ Broker,建立连接(Connection),开启一个信道(Channel);
        (2)消费者向RabbitMQ Broker请求消费相应队列中的消息,可能会设置相应的回调函数,以及做一些准备工作;
        (3)等待RabbitMQ Broker回应并投递相应队列中的消息,消费者接收消息;
        (4)消费者确认(ack)接收的消息;
        (5)RabbitMQ从队列中删除相应已经被确认的消息;
        (6)关闭信道;
        (7)关闭连接;
4.SpringBoot整合RabbitMQ:
    [1]引入依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    [2]编写application.yml配置:
        spring:
          rabbitmq:
            #RabbitMQ的用户名,默认为guest
            username: guest
            #RabbitMQ的密码,默认为guest
            password: guest
            addresses: localhost:5672
            cache:
              connection:
                #缓存连接模式,默认一个连接,多个channel
                mode: channel
                #多个连接,多个channel
                #mode: connection
    [3]几种交换机的使用:
        (1)Direct(直连交换机):
            1)申明一个消费者,申明一个queue和Exchange(若不存在),并binding;
                @Component
                @RabbitListener(bindings = @QueueBinding(
                    value = @Queue("myDirectQueue"),
                    exchange = @Exchange(value = "myDirectExchange", type = ExchangeTypes.DIRECT),
                    key = "mine.direct"
                ))
                public class MyDirectListener {
                    //listenerAdapter
                    //@param msg 消息内容,当只有一个参数的时候可以不加@Payload注解
                    @RabbitHandler
                    public void onMessage(@Payload String msg) {
                        System.out.println("来自 myDirectExchange 的消息:" + msg);
                    }
                }
            2)生产者:
                @Autowired
                private RabbitTemplate rabbitTemplate;
                @GetMapping("/direct")
                public String sendMsgByDirect() {
                    //参数: 交换机,routingKey,消息内容
                    rabbitTemplate.convertAndSend("myDirectExchange","mine.direct","this is a message");
                    return "success";
                }
        (2)Default(默认交换机):
            (名称为空字符串的直连交换机,一个queue若不指定binding的交换机,就被绑定到默认交换机上,routingKey为queue的名称)
            1)消费者: 
                @Component
                @RabbitListener(queuesToDeclare = @Queue("myDefaultExchange"))
                public class MyDefaultListener {
                    @RabbitHandler
                    public void onMessage(String msg) {
                        System.out.println("来自 myDefaultExchange 的消息:" + msg);
                    }
                }
            2)生产者:
                @Autowired
                private RabbitTemplate rabbitTemplate;
                @GetMapping("/default")
                public String sendMsgByDefault() {
                    //参数: 队列,消息内容
                    rabbitTemplate.convertAndSend("myDefaultExchange","this is a message");
                    return "success";
                }
        (3)Fanout(扇型交换机):
            1)申明两个消费者,对应queue-one和queue-two 都与 myFanoutExchange绑定,routingKey随意
                @Component
                @RabbitListeners({
                    @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("myFanoutQueue-one"),
                        exchange = @Exchange(value = "myFanoutExchange", type = ExchangeTypes.FANOUT),
                        key = "key.one")),
                     @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("myFanoutQueue-two"),
                        exchange = @Exchange(value = "myFanoutExchange", type = ExchangeTypes.FANOUT),
                        key = "key.two")),
                })
                public class MyFanoutListener {
                    @RabbitHandler
                    public void onMessage(@Payload String msg, @Headers Map<String, Object> headers) {
                        System.out.println("来自 " + headers.get(AmqpHeaders.CONSUMER_QUEUE) + " 的消息:" + msg);
                    }
                }
            2)生产者:
                @Autowired
                private RabbitTemplate rabbitTemplate;
                @GetMapping("/fanout")
                public String sendMsgByFanout() {
                    //参数: 交换机,routingKey(随意),消息内容
                    rabbitTemplate.convertAndSend("myFanoutExchange","key.one","this is a message");
                    return "success";
                }
        (4)Topic(主题交换机):
            1)申明多个消费者: ("#": 匹配一个或多个单词,"*": 匹配一个单词)
                @Component
                public class MyTopicListener {
                    @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("province-news-queue"),
                        exchange = @Exchange(value = "news-exchange", type = ExchangeTypes.TOPIC),
                        key = "province.#"))
                    @RabbitHandler
                    public void provinceNews(String msg) {
                        System.out.println("来自省TV的消息:" + msg);
                    }
                    @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("city-news-queue"),
                        exchange = @Exchange(value = "news-exchange", type = ExchangeTypes.TOPIC),
                        key = "province.city.#"))
                    @RabbitHandler
                    public void cityNews(String msg) {
                        System.out.println("来自市TV的消息:" + msg);
                    }
                    @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("street-news-queue"),
                        exchange = @Exchange(value = "news-exchange", type = ExchangeTypes.TOPIC),
                        key = "province.city.street.*"))
                    @RabbitHandler
                    public void streetNews(String msg) {
                        System.out.println("来自街区TV的消息:" + msg);
                    }
                }
            2)生产者:
                @GetMapping("/topic")
                public String sendMsgByTopic() {
                    //模拟某人在商店买彩票中奖了
                    rabbitTemplate.convertAndSend("news-exchange","province.city.street.shop","有人中了大奖");
                    return "success";
                }
        (5)Headers(头交换机):
            1)定义两个消费者: (一个全匹配,一个任意匹配)
                @Component
                public class MyHeadListener {
                    //任意匹配
                    @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("headQueue-one"),
                        exchange = @Exchange(value = "myHeadExchange", type = ExchangeTypes.HEADERS),
                        arguments = {
                            @Argument(name = "key-one", value = "1"),
                            @Argument(name = "key-two", value = "2"),
                            @Argument(name = "x-match", value = "any")
                    }))
                    public void anyMatchOnMessage(String msg) {
                        System.out.println("来自 headQueue-one 的消息:" + msg);
                    }
                    //全匹配
                    @RabbitListener(
                        bindings = @QueueBinding(value = @Queue("headQueue-two"),
                        exchange = @Exchange(value = "myHeadExchange", type = ExchangeTypes.HEADERS),
                        arguments = {
                            @Argument(name = "key-one", value = "1"),
                            @Argument(name = "x-match", value = "all")
                    }))
                    public void allMatchOnMessage(String msg) {
                        System.out.println("来自 headQueue-two 的消息:" + msg);
                    }
                }
            2)生产者:
                @GetMapping("/head")
                public String sendMsgByHead() {
                    rabbitTemplate.convertAndSend("myHeadExchange", "", "this is a message", message -> {
                        MessageProperties properties = message.getMessageProperties();
                        properties.setHeader("key-one", "1");
                        return message;
                    });
                    return "success";
                }
    [4]手动ack:
        (1)消息确认模式:
            1)在amqp协议中消息确认有两种模式:
                ①自动确认模式(automatic acknowledgement model): 当消息代理将消息发送给应用后立即删除;
                ②显式确认模式(explicit acknowledgement model): 待应用发送一个确认回执后再删除消息;
            2)而在spring-boot-starter-amqp,spring定义了三种:
                ①NONE: 没有ack的意思,对应rabbitMQ的自动确认模式;
                ②MANUAL: 手动模式,对应rabbitMQ的显式确认模式;
                ③AUTO: 自动模式,对应rabbitMQ的显式确认模式;
            3)注意:
                ①spring-amqp中的自动模式与rabbit中的自动模式是不一样的;
                ②在spring-amqp中MANUAL 与 AUTO的关系有点类似于在spring中手动提交事务与自动提交事务的区别,
                    一个是手动发送ack,一个是在方法执行完,没有异常的情况下自动发送ack;
        (2)代码实现:
            1)设置消费者的消息确认模式:
                @Configuration
                public class ListenerConfig {
                    @Bean("myListenerFactory")
                    public RabbitListenerContainerFactory myFactory(ConnectionFactory connectionFactory){
                        SimpleRabbitListenerContainerFactory containerFactory = 
                            new SimpleRabbitListenerContainerFactory();
                        containerFactory.setConnectionFactory(connectionFactory);
                        //设置消费者的消息确认模式
                        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
                        return containerFactory;
                    }
                }
            2)手动确认/拒绝消息:
                @Component
                @RabbitListener(
                    containerFactory = "myListenerFactory",
                    bindings = @QueueBinding(value = @Queue("myManualAckQueue"),
                    exchange = @Exchange(value = "myManualAckExchange", type = ExchangeTypes.DIRECT),
                    key = "mine.manual"))
                public class MyAckListener {
                    @RabbitHandler
                    public void onMessage(@Payload String msg, 
                          @Headers Map<String, Object> headers, 
                          Channel channel) throws Exception{
                        try {
                            System.out.println(msg);
                            //消息确认,(deliveryTag,multiple是否确认所有消息)
                            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
                        } catch (Exception e) {
                            //消息拒绝(deliveryTag,multiple,requeue拒绝后是否重新回到队列)
                            channel.basicNack((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false, false);
                            // 拒绝一条
                            // channel.basicReject();
                        }
                    }
                }
            3)设置消息拒绝策略:
                (拒绝策略是指:当消息被消费者拒绝时该如何处理,丢弃或者是重新回到队列;)
                ①在MANUAL模式下,在拒绝消息的方法中设置:
                    //消息拒绝(deliveryTag,multiple,requeue拒绝后是否重新回到队列)
                    channel.basicNack((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false, false);
                ②在AUTO 模式下可通过RabbitListenerContainerFactory或是ListenerContainer设置:
                    @Bean("myListenerFactory")
                    public RabbitListenerContainerFactory myFactory(ConnectionFactory connectionFactory){
                        SimpleRabbitListenerContainerFactory containerFactory =
                            new SimpleRabbitListenerContainerFactory();
                        containerFactory.setConnectionFactory(connectionFactory);
                        //自动ack
                        containerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO);
                        //拒绝策略,true回到队列 false丢弃
                        containerFactory.setDefaultRequeueRejected(false);
                        return containerFactory;
                    }
                    (注: 默认的拒绝策略是回到队列,所以,如果队列只有一个消费者的话就会产生死循环;)
            4)测试手动确认及消息拒绝:
                @GetMapping("/ack")
                public String sendAckMsg() {
                    //模拟某人在商店买彩票中奖了
                    rabbitTemplate.convertAndSend("myManualAckExchange","mine.manual","this is a message");
                    return "success";
                }
    [5]work模式(能者多劳):
        (1)默认情况: 
            有多个消费者在一个队列上,消息是公平的分发给消费者,一人一个轮着来,不考虑各消费者之间处理能力的差异,
            这可以通过设置预处理消息数(prefetchCount)缓解,或是使用work-能者多劳模式;
        (2)work-能者多劳模式: 
            1)在配置类中添加一下配置:
                @Bean("workListenerFactory")
                public RabbitListenerContainerFactory workListenerFactory(ConnectionFactory connectionFactory) {
                    SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
                    containerFactory.setConnectionFactory(connectionFactory);
                    //最大的并发的消费者数量
                    containerFactory.setMaxConcurrentConsumers(10);
                    //最小的并发消费者的数量
                    containerFactory.setConcurrentConsumers(1);
                    //消息确认机制更改为手动(MANUAL或AUTO)
                    containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
                    //work模式下公平分配(每次读取1条消息,在消费者未回执确认之前,不在进行下一条消息的投送)
                    containerFactory.setPrefetchCount(1);
                    return containerFactory;
                }
            2)定义两个消费者:(消费同一个queue上的消息)
                @Component
                public class WorkListener {
                    private volatile static AtomicInteger one = new AtomicInteger(0);
                    private volatile static AtomicInteger two = new AtomicInteger(0);
                    @RabbitListener(containerFactory = "workListenerFactory",queuesToDeclare = @Queue("workQueue"))
                    public void onMessageOne(@Payload Message message, Channel channel) throws InterruptedException, IOException {
                        Thread.sleep(300);
                        System.out.println("consumer-one 第 " + one.incrementAndGet() + " 个消息 :" + new String(message.getBody()));
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                    }

                    @RabbitListener(containerFactory = "workListenerFactory",queuesToDeclare = @Queue("workQueue"))
                    public void onMessageTwo(@Payload Message message, Channel channel) throws InterruptedException, IOException {
                        Thread.sleep(600);
                        System.out.println("consumer-two 第 " + two.incrementAndGet() + " 个消息 :" + new String(message.getBody()));
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                    }
                }
            2)生产者:
                @Autowired
                private RabbitTemplate rabbitTemplate;
                @GetMapping("/work")
                public String work() {
                    for (int i = 0; i < 66; i++) {
                        rabbitTemplate.convertAndSend("workQueue", "this is a message");
                    }
                    return "success";
                }
    [6]消息格式转换:
        rabbirMQ中的消息对应到java中对应的实体类是 org.springframework.amqp.core.Message;
        消息转换接口MessageConverter有两个主要方法toMessage 和fromMessage,将发送的内容与Message的互转;
        (1)SimpleMessageConverter:(spring中默认使用)
            1)转化方法toMessage: 根据 object instanceof xxx 转化,自行查看源码;
            2)转化方法fromMessage: 根据MessageProperties的ContentType转换,自行查看源码;
        (2)Jackson2JsonMessageConverter: (常用的将object与json互转)
            1)生产者:
                //①直接设置转化器
                @Autowired
                private RabbitTemplate rabbitTemplate;
                @GetMapping("/messageConverter")
                private String messageConverter() {
                    //实际项目不建议这么干,spring 默认单例模式,
                    //所以最好自己构建一个"jasonRabbitTemplate",使用时注入
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.convertAndSend("jsonQueue", new Student("zhangSan",15,"男"));
                    return "success";
                }
                //②在配置类中设置转化器:
                //配置类中定义支持消息转化的rabbitTemplate
                @Bean("jasonTemplate")
                public RabbitTemplate jasonRabbitTemplate(ConnectionFactory connectionFactory) {
                    RabbitTemplate rabbitTemplate = new RabbitTemplate();
                    rabbitTemplate.setConnectionFactory(connectionFactory);
                    //设置转化类
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    return rabbitTemplate;
                }
                //在发送消息处使用支持消息转化的rabbitTemplate
                @Qualifier("jasonTemplate")
                @Autowired
                private RabbitTemplate jasonRabbitTemplate;
                @GetMapping("/messageConverter")
                private String messageConverter() {
                     jasonRabbitTemplate.convertAndSend("jsonQueue", new Student("zhangSan",15,"男"));
                }
            2)消费者:
                //在配置类中设置支持消息转化的工厂类
                @Bean("jsonListenerFactory")
                public RabbitListenerContainerFactory jsonListenerFactory(ConnectionFactory connectionFactory){
                    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
                    factory.setConnectionFactory(connectionFactory);
                    //设置消费者接收时的消息转化类
                    factory.setMessageConverter(new Jackson2JsonMessageConverter());
                    return factory;
                }
                //消费类中使用支持消息转化的工厂类
                @Component
                @RabbitListener(containerFactory = "jsonListenerFactory",
                    queuesToDeclare = @Queue("jsonQueue"))
                public class JasonListener {
                    @RabbitHandler
                    public void onMessage(Student student) {
                        System.out.println(student);
                    }
                }
    [7]延迟队列:(基于消息TTL与死信交换或基于插件)
        (1)死信队列与延迟队列: 
            1)死信队列: 用来保存处理失败或者过期的消息,确保消息不被丢失以便排查问题;
            2)延迟队列: 顾名思义就是消息在队列中存在一定时间后再被消费;(如订单超时自动取消)
        (2)基于消息TTL与死信交换:
            1)死信交换: 为队列设置一个死信exchange和routingKey,当队列上产生死信时,死信会被投递到设置好的exchange及对应的routingKey;
            2)产生死信的方式:
                ①被拒绝,并且拒绝后没有重新进入队列的消息;
                ②消息TTL(Time To Live)过期的消息;
                ③超过队列长度而被删除的消息;
            3)思路:
                ①为消息设置过期时间;
                ②生产者发送消息时,把消息投递到一个没有消费者的队列(mine.ttl.queue),使消息成为死信;
                ③为队列(mine.ttl.queue)设置死信交换(mine.dead.letter.exchange);
                ④为死信交换设置死信队列(mine.dead.letter.queue),并添加消费者,监听消息;
            4)代码实现:
                //编写配置类:
                @Configuration
                public class TtlConfig {
                    @Bean("myTtlExchange")
                    public DirectExchange ttlExchange() {
                        return new DirectExchange("mine.ttl.exchange");
                    }
                    //定义队列 绑定死信队列(其实是绑定的交换器,然后通过交换器路由键绑定队列) 设置过期时间
                    @Bean("myTtlQueue")
                    public Queue ttlQueue() {
                        Map<String, Object> args = new HashMap<>(3);
                        //声明死信交换器
                        args.put("x-dead-letter-exchange", "mine.dead.letter.exchange");
                        //声明死信路由键
                        args.put("x-dead-letter-routing-key", "mine.dead.letter.key");
                        //可在发送消息时设置过期时间,也可在配置类中设置整个队列的过期时间,两个都设置以最早过期时间为准
                        //声明队列消息过期时间
                        args.put("x-message-ttl", 10000);
                        return new Queue("mine.ttl.queue", true, false, false, args);
                    }
                    //队列绑定
                    @Bean
                    @DependsOn({"myTtlExchange", "myTtlQueue"})
                    public Binding bindingOrderDirect(Queue myTtlQueue, DirectExchange myTtlExchange) {
                        return BindingBuilder.bind(myTtlQueue).to(myTtlExchange).with("mine.ttl.key");
                    }
                }
                //消费者
                @Component
                public class MyDeadLetterListener {
                    @RabbitListener(
                        containerFactory = "jsonListenerFactory",
                        bindings = {@QueueBinding(
                            value = @Queue(value = "mine.dead.letter.queue"),
                            exchange = @Exchange(value = "mine.dead.letter.exchange"),
                            key = {"mine.dead.letter.key"})})
                    public void getDLMessage(Student user) {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        // 模拟执行任务
                        System.out.println(now.format(dateTimeFormatter) +" 延迟队列之消费消息：" + user.toString() );
                    }
                }
                //生产者
                @Qualifier("jasonTemplate")
                @Autowired
                private RabbitTemplate jasonRabbitTemplate;
                @GetMapping("/ttlMsg")
                public List<Student> directDelayMQ() {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    List<Student> students = new ArrayList<>();
                    students.add(new Student("张三", 20, "男"));
                    students.add(new Student("李四", 24, "男"));
                    students.add(new Student("王五", 21, "女"));
                    for (Student student : students) {
                        jasonRabbitTemplate.convertAndSend("mine.ttl.exchange", "mine.ttl.key", student,
                            message -> {
                                 //可在发送消息时设置过期时间,也可在配置类中设置整个队列的过期时间,两个都设置以最早过期时间为准
                                message.getMessageProperties().setExpiration("15000");
                                return message;
                        });
                        System.out.println(now.format(dateTimeFormatter) + " 消息发送："+student.toString());
                    }
                    return students;
                }
        (3)基于rabbitmq-delayed-message-exchange插件:
            1)下载插件: https://bintray.com/rabbitmq/community-plugins/rabbitmq_delayed_message_exchange
            2)选择与RabbitMQ版本匹配的版本,解压成.ez的文件,放到RabbitMQ安装目录的plugins文件夹下;
            3)停止服务器,开启插件,启动服务器: (插件前有[E*]表示插件启用了)
                开启插件: rabbitmq-plugins enable rabbitmq_delayed_message_exchange
                关闭插件: rabbitmq-plugins disable rabbitmq_delayed_message_exchange
                查看插件: rabbitmq-plugins list
            4)消费者:
                @Component
                public class MyPluginListener {
                    @RabbitListener(
                        containerFactory = "jsonListenerFactory",
                        bindings = {@QueueBinding(
                            value = @Queue(value = "mine.plugin.delay.queue"),
                            exchange = @Exchange(
                                value = "mine.plugin.delay.exchange",
                                type = "x-delayed-message",
                                arguments = {@Argument(name="x-delayed-type",value = ExchangeTypes.DIRECT)}),
                            key = {"mine.plugin.key"})})
                    public void getPDLMessage(Student user) {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        // 模拟执行任务
                        System.out.println(now.format(dateTimeFormatter) +" 延迟队列之消费消息：" + user.toString() );
                    }
                }
