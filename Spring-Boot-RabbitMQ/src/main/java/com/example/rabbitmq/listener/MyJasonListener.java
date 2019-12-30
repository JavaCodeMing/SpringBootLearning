package com.example.rabbitmq.listener;

import com.example.rabbitmq.domain.Student;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(containerFactory = "jsonListenerFactory",queuesToDeclare = @Queue("jsonQueue"))
public class MyJasonListener {
    @RabbitHandler
    public void onMessage(@Payload Student student) {
        System.out.println(student);
    }
}