package com.example.rabbitmq.listener;

import com.example.rabbitmq.domain.Student;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.*;

/**
 * Created by dengzhiming on 2019/12/30
 */
@Component
public class MyPluginListener {
    @RabbitListener(
            containerFactory = "jsonListenerFactory",
            bindings = {@QueueBinding(
                    value = @Queue(value = "mine.plugin.delay.queue"),
                    exchange = @Exchange(
                            value = "mine.plugin.delay.exchange",
                            type = "x-delayed-message",
                            arguments = {@Argument(name="x-delayed-type",value = ExchangeTypes.DIRECT)}),
                    key = {"mine.plugin.key"})})
    public void getPDLMessage(Student user) {
        LocalDateTime now = now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 模拟执行任务
        System.out.println(now.format(dateTimeFormatter) +" 延迟队列之消费消息：" + user.toString() );
    }
}
