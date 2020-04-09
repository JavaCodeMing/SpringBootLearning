package com.example.rabbitmq.listener;

import com.example.rabbitmq.domain.Student;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MyDeadLetterListener {
    @RabbitListener(
            containerFactory = "jsonListenerFactory",
            bindings = {@QueueBinding(
                    value = @Queue(value = "mine.dead.letter.queue"),
                    exchange = @Exchange(value = "mine.dead.letter.exchange"),
                    key = {"mine.dead.letter.key"})})
    public void getDLMessage(Student user) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 模拟执行任务
        System.out.println(now.format(dateTimeFormatter) +" 延迟队列之消费消息：" + user.toString() );
    }
}
