package com.example.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MyDefaultListener {

    @RabbitListener(queuesToDeclare = @Queue("myDefaultExchange"))
    public void onMessage(String msg) {
        System.out.println("来自 myDefaultExchange 的消息:" + msg);
    }
}
