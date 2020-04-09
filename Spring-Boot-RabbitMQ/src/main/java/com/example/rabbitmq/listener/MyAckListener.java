package com.example.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyAckListener {
    @RabbitListener(
            containerFactory = "myListenerFactory",
            bindings = @QueueBinding(
                    value = @Queue("myManualAckQueue"),
                    exchange = @Exchange(value = "myManualAckExchange", type = ExchangeTypes.DIRECT),
                    key = "mine.manual"))
    public void onMessage(@Payload String msg, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        try {
            System.out.println("来自 myManualAckQueue 的消息:" + msg);
            int i = 1 / 0;
            //消息确认,(deliveryTag,multiple:是否确认所有消息)
            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
            System.out.println("来自 myManualAckQueue 的消息被正常消费并确认!");
        } catch (Exception e) {
            //消息拒绝(deliveryTag,multiple,requeue:拒绝后是否重新回到队列)
            channel.basicNack((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false, false);
            System.out.println("来自 myManualAckQueue 的消息消费异常,被拒绝了!");
            // 拒绝一条
            // channel.basicReject();
        }
    }
}
