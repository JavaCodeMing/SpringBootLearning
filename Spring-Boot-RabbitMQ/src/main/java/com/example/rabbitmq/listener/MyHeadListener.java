package com.example.rabbitmq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class MyHeadListener {

    /**
     * 任意匹配
     *
     * @param msg 消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("headQueue-one"),
            exchange = @Exchange(value = "myHeadExchange", type = ExchangeTypes.HEADERS),
            arguments = {
                    @Argument(name = "key-one", value = "1"),
                    @Argument(name = "key-two", value = "2"),
                    @Argument(name = "x-match", value = "any")
            }))
    public void anyMatchOnMessage(String msg) {
        System.out.println("来自 headQueue-one 的消息:" + msg);
    }

    /**
     * 全匹配
     *
     * @param msg 消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("headQueue-two"),
            exchange = @Exchange(value = "myHeadExchange", type = ExchangeTypes.HEADERS),
            arguments = {
                    @Argument(name = "key-one", value = "1"),
                    @Argument(name = "x-match", value = "all")
            }))
    public void allMatchOnMessage(String msg) {
        System.out.println("来自 headQueue-two 的消息:" + msg);
    }

}
