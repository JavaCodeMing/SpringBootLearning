package com.example.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = @Queue("myDefaultExchange"))
public class MyDefaultListener {

    @RabbitHandler
    public void onMessage(String msg) {
        System.out.println("来自 myDefaultExchange 的消息:" + msg);
    }
}