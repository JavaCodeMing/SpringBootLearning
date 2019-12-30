package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

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