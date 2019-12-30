package com.example.rabbitmq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {
    @Bean("myListenerFactory")
    public RabbitListenerContainerFactory myListenerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        //设置消费者的消息确认模式: MANUAL模式
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        /*//AUTO模式: 自动ack
        containerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //拒绝策略,true回到队列 false丢弃
        containerFactory.setDefaultRequeueRejected(false);*/
        return containerFactory;
    }

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

    @Bean("jasonTemplate")
    public RabbitTemplate jasonRabbitTemplate(ConnectionFactory connectionFactory) {
        Jackson2JsonMessageConverter messageConverter =
                new Jackson2JsonMessageConverter();
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置转化类
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean("jsonListenerFactory")
    public RabbitListenerContainerFactory jsonListenerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}