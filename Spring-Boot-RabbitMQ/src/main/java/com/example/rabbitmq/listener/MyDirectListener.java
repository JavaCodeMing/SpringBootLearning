package com.example.rabbitmq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MyDirectListener {

    /**
     * listenerAdapter
     *
     * @param msg 消息内容,当只有一个参数的时候可以不加@Payload注解
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("myDirectQueue"),
                    exchange = @Exchange(value = "myDirectExchange", type = ExchangeTypes.DIRECT),
                    key = "mine.direct")
    )
    public void onMessage(@Payload String msg) {
        System.out.println("来自 myDirectExchange 的消息:" + msg);
    }
}
