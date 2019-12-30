package com.example.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyWorkListener {
    private volatile static AtomicInteger one = new AtomicInteger(0);
    private volatile static AtomicInteger two = new AtomicInteger(0);
    @RabbitListener(containerFactory = "workListenerFactory",queuesToDeclare = @Queue("workQueue"))
    public void onMessageOne(@Payload Message message, Channel channel) throws InterruptedException, IOException {
        Thread.sleep(300);
        System.out.println("consumer-one 第 " + one.incrementAndGet() + " 个消息 :" + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(containerFactory = "workListenerFactory",queuesToDeclare = @Queue("workQueue"))
    public void onMessageTwo(@Payload Message message, Channel channel) throws InterruptedException, IOException {
        Thread.sleep(600);
        System.out.println("consumer-two 第 " + two.incrementAndGet() + " 个消息 :" + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}