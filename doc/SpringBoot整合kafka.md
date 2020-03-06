```text
Kafka是一个分布式发布-订阅消息系统和一个强大的队列,可以处理大量的数据,
并使您能够将消息从一个端点传递到另一个端点;
1.Kafka的几个基本术语:
    Topics(主题): 属于特定类别的消息流称为主题;数据存储在主题中;
    Partition(分区): 每个主题可能有多个分区;
    Partition offset(分区偏移): 分区上每条记录的唯一序列标识;
    Replicas of partition(分区备份): 分区的备份,从不读取或写入数据;
    Brokers(经纪人): kafka集群中每个服务称为broker;
    Producers(生产者): 发送给一个或多个Kafka主题的消息的发布者;
    Consumers(消费者): 订阅一个或多个主题,并从broker提取已发布的消息来使用;
    Leader(领导者): 负责给定分区的所有读取和写入的节点;每个分区都有一个服务器充当Leader;
    Follower(追随者): 同步Leader的partition消息;
    Consumer Group(消费者组): Topic消息分配到消费者组,再由消费者组分配到具体消费实例;
    (每个分区最多只能绑定一个消费者,每个消费者可以消费多个分区)
2.Kafka安装使用:
    Kafka下载地址:http://kafka.apache.org/downloads,选择Binary downloads下载,然后解压即可;
    Kafka的配置文件位于config目录下(包含kafka和Zookeeper的配置文件),打开server.properties,
    将broker.id的值修改为1,每个broker的id都必须设置为Integer类型,且不能重复;
    [1]启动Zookeeper:
        (1)Windows下,在cmd中切换到Kafka根目录,执行启动脚本: 
            bin\windows\zookeeper-server-start.bat config\zookeeper.properties
        (2)Linux下,在终端命令行切换到Kafka根目录,以后台进程的方式执行启动脚本:
            bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
    [2]启动Kafka:
        (1)Windows下,在cmd中切换到Kafka根目录,执行启动脚本:
            bin\windows\kafka-server-start.bat config\server.properties
        (2)Linux下,在终端命令行切换到Kafka根目录,执行启动脚本:
            bin/kafka-server-start.sh config/server.properties
        当看到命令行打印started等信息,说明启动完毕;
    [3]创建Topic:
        (1)Windows下,在cmd中切换到Kafka根目录,执行创建Topic脚本:
            bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 
            --replication-factor 1 --partitions 1 --topic test
            (创建一个Topic到ZK(指定ZK的地址),副本个数为1,分区数为1,Topic的名称为test)
        (2)Linux下,在终端命令行切换到Kafka根目录,执行创建Topic脚本:
            bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 
            --partitions 1 --topic test
    [4]查看Kafka里的Topic列表:
        (1)Windows下,在cmd中切换到Kafka根目录,执行Topic脚本:
            bin\windows\kafka-topics.bat --list --zookeeper localhost:2181
        (2)Linux下,在终端命令行切换到Kafka根目录,执行Topic脚本:
            bin/kafka-topics.sh --list --zookeeper localhost:2181
    [5]查看某个Topic的具体信息: (如:test)
        (1)Windows下,在cmd中切换到Kafka根目录,执行Topic脚本:
            bin\windows\kafka-topics.bat --describe --zookeeper localhost:2181 --topic test
        (2)Linux下,在终端命令行切换到Kafka根目录,执行Topic脚本:
            bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic test
    [6]启动Producers:
        (1)Windows下,在cmd中切换到Kafka根目录,执行producer脚本:
            bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic test
            (9092为生产者的默认端口号,启动生产者后,可往test Topic里发送数据)
        (2)Linux下,在终端命令行切换到Kafka根目录,执行producer脚本:
            bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
    [7]启动Consumers:
        (1)Windows下,在cmd中切换到Kafka根目录,执行consumer脚本:
            bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 
            --topic test --from-beginning
            (from-beginning表示从头开始读取数据)
        (2)Linux下,在终端命令行切换到Kafka根目录,执行consumer脚本:
            bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 
            --topic test --from-beginning
3.Spring Boot整合Kafaka:
    [1]引入web依赖和kafka依赖:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
    [2]配置生产者:
        (1)通过配置类,配置生产者工厂及kafka模板:
            @Configuration
            public class KafkaProducerConfig {
                @Value("${spring.kafka.bootstrap-servers}")
                private String bootstrapServers;
                @Bean
                public ProducerFactory<String, String> producerFactory() {
                    Map<String, Object> configProps = new HashMap<>();
                    configProps.put(
                            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                            bootstrapServers);
                    configProps.put(
                            //key的序列化策略,String类型
                            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                            StringSerializer.class);
                    configProps.put(
                            //value的序列化策略,String类型
                            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                            StringSerializer.class);
                    return new DefaultKafkaProducerFactory<>(configProps);
                }
                //其包含了发送消息的便捷方法
                @Bean
                public KafkaTemplate<String, String> kafkaTemplate() {
                    return new KafkaTemplate<>(producerFactory());
                }
            }
        (2)配置文件application.yml中配置生产者的地址:
            spring:
                kafka:
                    bootstrap-servers: localhost:9092
    [3]编写发送消息的controller:
        @RestController
        public class SendMessageController {
            @Autowired
            private KafkaTemplate<String, String> kafkaTemplate;
            @GetMapping("send/{message}")
            public void send(@PathVariable String message) {
                // test为Topic的名称,message为要发送的消息
                this.kafkaTemplate.send("test", message);
            }
        }
       send方法是异步方法,可通过回调的方式来确定消息是否发送成功,改造controller:
        @RestController
        public class SendMessageController {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            @Autowired
            private KafkaTemplate<String, String> kafkaTemplate;
            @GetMapping("send/{message}")
            public void send(@PathVariable String message) {
                ListenableFuture<SendResult<String, String>> future = 
                    this.kafkaTemplate.send("test", message);
                future.addCallback(new ListenableFutureCallback<SendResult<String,String>>(){
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        logger.info("成功发送消息：{}，offset=[{}]", message, 
                            result.getRecordMetadata().offset());
                    }
                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error("消息：{} 发送失败，原因：{}", message, ex.getMessage());
                    }
                });
            }
        }
    [4]配置消费者:
        (1)通过配置类,配置消费者工厂和监听容器工厂:
            //配置类上需要@EnableKafka注释才能在Spring托管Bean上检测@KafkaListener注解
            @EnableKafka    
            @Configuration
            public class KafkaConsumerConfig {
                @Value("${spring.kafka.bootstrap-servers}")
                private String bootstrapServers;
                @Value("${spring.kafka.consumer.group-id}")
                private String consumerGroupId;
                @Value("${spring.kafka.consumer.auto-offset-reset}")
                private String autoOffsetReset;
                @Bean
                public ConsumerFactory<String, String> consumerFactory() {
                    Map<String, Object> props = new HashMap<>();
                    props.put(
                            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                            bootstrapServers);
                    props.put(
                            ConsumerConfig.GROUP_ID_CONFIG,
                            consumerGroupId);
                    props.put(
                            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                            autoOffsetReset);
                    props.put(
                            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                            StringDeserializer.class);
                    props.put(
                            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                            StringDeserializer.class);
                    return new DefaultKafkaConsumerFactory<>(props);
                }
                @Bean
                public ConcurrentKafkaListenerContainerFactory<String, String> 
                    kafkaListenerContainerFactory() {
                    ConcurrentKafkaListenerContainerFactory<String, String> factory
                            = new ConcurrentKafkaListenerContainerFactory<>();
                    factory.setConsumerFactory(consumerFactory());
                    return factory;
                }
            }
        (2)在application.yml里配置消费者组ID和消息读取策略:
            spring:
                kafka:
                    consumer:
                        group-id: test-consumer
                        auto-offset-reset: latest
            
            消息读取策略,包含四个可选值:
                earliest:当各分区下有已提交的offset时,从提交的offset开始消费;
                    无提交的offset时,从头开始消费;
                latest:当各分区下有已提交的offset时,从提交的offset开始消费;
                    无提交的offset时,消费新产生的该分区下的数据;
                none:topic各分区都存在已提交的offset时,从offset后开始消费;
                    只要有一个分区不存在已提交的offset,则抛出异常;
                exception:直接抛出异常;
    [5]编写消息监听器类:
        @Component
        public class KafkaMessageListener {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            // 指定监听的主题和消费者组
            @KafkaListener(topics = "test", groupId = "test-consumer")
            public void listen(String message) {
                logger.info("接收消息: {}", message);
            }
        }
4.@KafkaListener详解:
    [1]同时监听来自多个Topic的消息: @KafkaListener(topics = "topic1, topic2")
    [2]@Header注解获取当前消息来自哪个分区: 
        @KafkaListener(topics = "test", groupId = "test-consumer")
        public void listen(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            logger.info("接收消息: {}，partition：{}", message, partition);
        }
    [3]指定只接收来自特定分区的消息:
        @KafkaListener(
            groupId = "test-consumer",
            topicPartitions = @TopicPartition(
                topic = "test",
                partitionOffsets = {
                    @PartitionOffset(partition = "0", initialOffset = "0")
                }
            )
        )
        public void listen(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            logger.info("接收消息: {}，partition：{}", message, partition);
        }
       如果不需要指定initialOffset,上面代码可以简化为:
        @KafkaListener(groupId = "test-consumer", 
            topicPartitions = @TopicPartition(topic = "test", partitions = { "0", "1" }))
5.为消息监听添加消息过滤器: setRecordFilterStrategy(RecordFilterStrategy<K, V> strategy)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> 
        kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // 添加过滤配置
        factory.setRecordFilterStrategy( r -> r.value().contains("fuck"));
        return factory;
    }
    // RecordFilterStrategy接口: (是函数式接口)
    public interface RecordFilterStrategy<K, V> {
        boolean filter(ConsumerRecord<K, V> var1);
    }
6.发送复杂的消息: (通过自定义消息转换器来发送复杂的消息)
    [1]定义消息实体: 
        public class Message implements Serializable {
            private static final long serialVersionUID = 6678420965611108427L;
            private String from;
            private String message;
            public Message() { }
            public Message(String from, String message) {
                this.from = from;
                this.message = message;
            }
            @Override
            public String toString() {
                return "Message{" +
                        "from='" + from + '\'' +
                        ", message='" + message + '\'' +
                        '}';
            }
            // get set 略
        }
    [2]改造消息生产者配置:
        @Configuration
        public class KafkaProducerConfig {
            @Value("${spring.kafka.bootstrap-servers}")
            private String bootstrapServers;
            // 返回类型为ProducerFactory<String,Message>
            @Bean
            public ProducerFactory<String, Message> producerFactory() {
                Map<String, Object> configProps = new HashMap<>();
                configProps.put(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers);
                configProps.put(
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                        StringSerializer.class);
                configProps.put(
                        //将value序列化策略指定为了Kafka提供的JsonSerializer
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                        JsonSerializer.class);
                return new DefaultKafkaProducerFactory<>(configProps);
            }
            // 返回类型为KafkaTemplate<String, Message>
            @Bean
            public KafkaTemplate<String, Message> kafkaTemplate() {
                return new KafkaTemplate<>(producerFactory());
            }
        }
    [3]在controller中发送复杂消息:
        @RestController
        public class SendMessageController {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            @Autowired
            private KafkaTemplate<String, Message> kafkaTemplate;
            @GetMapping("send/{message}")
            public void sendMessage(@PathVariable String message) {
                this.kafkaTemplate.send("test", new Message("kimi", message));
            }
        }
    [4]修改消费者配置:
        @EnableKafka
        @Configuration
        public class KafkaConsumerConfig {
            @Value("${spring.kafka.bootstrap-servers}")
            private String bootstrapServers;
            @Value("${spring.kafka.consumer.group-id}")
            private String consumerGroupId;
            @Value("${spring.kafka.consumer.auto-offset-reset}")
            private String autoOffsetReset;
            @Bean
            public ConsumerFactory<String, Message> consumerFactory() {
                Map<String, Object> props = new HashMap<>();
                props.put(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers);
                props.put(
                        ConsumerConfig.GROUP_ID_CONFIG,
                        consumerGroupId);
                props.put(
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        autoOffsetReset);
                return new DefaultKafkaConsumerFactory<>(
                        props,
                        new StringDeserializer(),
                        new JsonDeserializer<>(Message.class));
            }
            @Bean
            public ConcurrentKafkaListenerContainerFactory<String,Message> 
                kafkaListenerContainerFactory(){
                ConcurrentKafkaListenerContainerFactory<String, Message> factory
                        = new ConcurrentKafkaListenerContainerFactory<>();
                factory.setConsumerFactory(consumerFactory());
                return factory;
            }
        }
    [5]修改消息监听:
        @Component
        public class KafkaMessageListener {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            // 指定监听的主题和消费者组
            @KafkaListener(topics = "test", groupId = "test-consumer")
            public void listen(Message message) {
                logger.info("接收消息: {}", message);
            }
        }
7.更多配置: 
(https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#common-application-properties)
    # APACHE KAFKA (KafkaProperties)
    spring.kafka.admin.client-id= 
    spring.kafka.admin.fail-fast=false 
    spring.kafka.admin.properties.*= 
    spring.kafka.admin.ssl.key-password= 
    spring.kafka.admin.ssl.key-store-location= 
    spring.kafka.admin.ssl.key-store-password= 
    spring.kafka.admin.ssl.key-store-type= 
    spring.kafka.admin.ssl.protocol= 
    spring.kafka.admin.ssl.trust-store-location= 
    spring.kafka.admin.ssl.trust-store-password= 
    spring.kafka.admin.ssl.trust-store-type= 
    spring.kafka.bootstrap-servers= 
    spring.kafka.client-id= 
    spring.kafka.consumer.auto-commit-interval= 
    spring.kafka.consumer.auto-offset-reset= 
    spring.kafka.consumer.bootstrap-servers= 
    spring.kafka.consumer.client-id= 
    spring.kafka.consumer.enable-auto-commit= 
    spring.kafka.consumer.fetch-max-wait= 
    spring.kafka.consumer.fetch-min-size= 
    spring.kafka.consumer.group-id= 
    spring.kafka.consumer.heartbeat-interval= 
    spring.kafka.consumer.key-deserializer= 
    spring.kafka.consumer.max-poll-records= 
    spring.kafka.consumer.properties.*= 
    spring.kafka.consumer.ssl.key-password= 
    spring.kafka.consumer.ssl.key-store-location= 
    spring.kafka.consumer.ssl.key-store-password= 
    spring.kafka.consumer.ssl.key-store-type= 
    spring.kafka.consumer.ssl.protocol= 
    spring.kafka.consumer.ssl.trust-store-location= 
    spring.kafka.consumer.ssl.trust-store-password= 
    spring.kafka.consumer.ssl.trust-store-type= 
    spring.kafka.consumer.value-deserializer= 
    spring.kafka.jaas.control-flag=required 
    spring.kafka.jaas.enabled=false 
    spring.kafka.jaas.login-module=com.sun.security.auth.module.Krb5LoginModule 
    spring.kafka.jaas.options= 
    spring.kafka.listener.ack-count= 
    spring.kafka.listener.ack-mode= 
    spring.kafka.listener.ack-time= 
    spring.kafka.listener.client-id= 
    spring.kafka.listener.concurrency= 
    spring.kafka.listener.idle-event-interval= 
    spring.kafka.listener.log-container-config= 
    spring.kafka.listener.monitor-interval= 
    spring.kafka.listener.no-poll-threshold= 
    spring.kafka.listener.poll-timeout= 
    spring.kafka.listener.type=single 
    spring.kafka.producer.acks= 
    spring.kafka.producer.batch-size= 
    spring.kafka.producer.bootstrap-servers= 
    spring.kafka.producer.buffer-memory= 
    spring.kafka.producer.client-id= 
    spring.kafka.producer.compression-type= 
    spring.kafka.producer.key-serializer= 
    spring.kafka.producer.properties.*= 
    spring.kafka.producer.retries= 
    spring.kafka.producer.ssl.key-password= 
    spring.kafka.producer.ssl.key-store-location= 
    spring.kafka.producer.ssl.key-store-password= 
    spring.kafka.producer.ssl.key-store-type= 
    spring.kafka.producer.ssl.protocol= 
    spring.kafka.producer.ssl.trust-store-location= 
    spring.kafka.producer.ssl.trust-store-password= 
    spring.kafka.producer.ssl.trust-store-type= 
    spring.kafka.producer.transaction-id-prefix= 
    spring.kafka.producer.value-serializer= 
    spring.kafka.properties.*= 
    spring.kafka.ssl.key-password= 
    spring.kafka.ssl.key-store-location= 
    spring.kafka.ssl.key-store-password= 
    spring.kafka.ssl.key-store-type= 
    spring.kafka.ssl.protocol= 
    spring.kafka.ssl.trust-store-location= 
    spring.kafka.ssl.trust-store-password= 
    spring.kafka.ssl.trust-store-type= 
    spring.kafka.streams.application-id= 
    spring.kafka.streams.auto-startup=true 
    spring.kafka.streams.bootstrap-servers= 
    spring.kafka.streams.cache-max-size-buffering= 
    spring.kafka.streams.client-id= 
    spring.kafka.streams.properties.*= 
    spring.kafka.streams.replication-factor= 
    spring.kafka.streams.ssl.key-password= 
    spring.kafka.streams.ssl.key-store-location= 
    spring.kafka.streams.ssl.key-store-password= 
    spring.kafka.streams.ssl.key-store-type= 
    spring.kafka.streams.ssl.protocol= 
    spring.kafka.streams.ssl.trust-store-location= 
    spring.kafka.streams.ssl.trust-store-password= 
    spring.kafka.streams.ssl.trust-store-type= 
    spring.kafka.streams.state-dir= 
    spring.kafka.template.default-topic= 
```