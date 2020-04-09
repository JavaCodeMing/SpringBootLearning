package com.example.rabbitmq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyFanoutListener {
    @RabbitListeners({
            @RabbitListener(
                    bindings = @QueueBinding(
                            value = @Queue("myFanoutQueue-one"),
                            exchange = @Exchange(value = "myFanoutExchange", type = ExchangeTypes.FANOUT),
                            key = "key.one")),

            @RabbitListener(
                    bindings = @QueueBinding(
                            value = @Queue("myFanoutQueue-two"),
                            exchange = @Exchange(value = "myFanoutExchange", type = ExchangeTypes.FANOUT),
                            key = "key.two")),
    })
    public void onMessage(@Payload String msg, @Headers Map<String, Object> headers) {
        System.out.println("来自 " + headers.get(AmqpHeaders.CONSUMER_QUEUE) + " 的消息:" + msg);
    }

}
