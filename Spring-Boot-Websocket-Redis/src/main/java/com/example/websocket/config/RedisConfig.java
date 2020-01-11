package com.example.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author dengzhiming
 * @date 2020/1/11
 */
@Configuration
public class RedisConfig {
    @Value("${redis.channel.msgToAll}")
    private String msgToAll;
    @Value("${redis.channel.userStatus}")
    private String userStatus;

    /**
     * redis消息监听器容器
     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定
     * 该消息监听器通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
     *
     * @param connectionFactory 连接工厂
     * @param listenerAdapter   消息监听适配器类
     * @return redis消息监听容器类
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //添加topic监听: websocket.msgToAll
        container.addMessageListener(listenerAdapter, new PatternTopic(msgToAll));
        //添加topic监听: websocket.userStatus
        container.addMessageListener(listenerAdapter, new PatternTopic(userStatus));
        return container;
    }

}
