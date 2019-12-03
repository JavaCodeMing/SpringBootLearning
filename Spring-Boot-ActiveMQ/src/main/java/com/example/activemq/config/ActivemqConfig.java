package com.example.activemq.config;

import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Created by dengzhiming on 2019/11/2
 */
@Configuration
@EnableJms
public class ActivemqConfig {

    @Bean
    public JmsListenerContainerFactory queueListenerFactory(ConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false);
        // 设置并发数
        factory.setConcurrency("10");
        // 设置是否开启事务,开启事务则需要手动提交
        factory.setSessionTransacted(false);
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory queueListenerACKFactory(ConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // 是否开启发布订阅模式
        factory.setPubSubDomain(false);
        // 设置并发数
        factory.setConcurrency("10");
        // 设置连接工厂
        factory.setConnectionFactory(connectionFactory);
        // 设置是否开启事务,开启事务则需要手动提交
        //factory.setSessionTransacted(false);
        // 设置会签模式为activemq独有的单条确认模式: 4
        factory.setSessionAcknowledgeMode(ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory topicListenerFactory(ConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        // 设置并发数
        factory.setConcurrency("10");
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public Queue queueString(){
        return new ActiveMQQueue("queue_string");
    }

    @Bean
    public Queue queueUser(){
        return new ActiveMQQueue("queue_user");
    }

    @Bean
    public Queue queueString2Way(){
        return new ActiveMQQueue("queue_string_2way");
    }

    @Bean
    public Queue queueStringACK(){
        return new ActiveMQQueue("queue_string_ack");
    }

    @Bean
    public Topic topicString(){
        return new ActiveMQTopic("topic_string");
    }

    @Bean
    public Topic topicUser(){
        return new ActiveMQTopic("topic_user");
    }

    @Bean
    public Topic delayTopicString(){
        return new ActiveMQTopic("topic_delay_string");
    }
}
