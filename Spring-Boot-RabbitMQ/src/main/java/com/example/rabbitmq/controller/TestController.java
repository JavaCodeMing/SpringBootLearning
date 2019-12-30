package com.example.rabbitmq.controller;

import com.example.rabbitmq.domain.Student;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengzhiming on 2019/12/20
 */
@RestController
public class TestController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Qualifier("jasonTemplate")
    @Autowired
    private RabbitTemplate jasonRabbitTemplate;

    @GetMapping("/direct")
    public String sendMsgByDirect() {
        //参数: 交换机,routingKey,消息内容
        rabbitTemplate.convertAndSend("myDirectExchange", "mine.direct", "this is a message");
        return "success";
    }

    @GetMapping("/default")
    public String sendMsgByDefault() {
        //参数: 队列,消息内容
        rabbitTemplate.convertAndSend("myDefaultExchange", "this is a message");
        return "success";
    }

    @GetMapping("/fanout")
    public String sendMsgByFanout() {
        //参数: 交换机,routingKey(随意),消息内容
        rabbitTemplate.convertAndSend("myFanoutExchange", "key.one", "this is a message");
        return "success";
    }

    @GetMapping("/topic")
    public String sendMsgByTopic() {
        //模拟某人在商店买彩票中奖了
        rabbitTemplate.convertAndSend("news-exchange", "province.city.street.shop", "有人中了大奖");
        return "success";
    }

    @GetMapping("/head")
    public String sendMsgByHead() {
        rabbitTemplate.convertAndSend("myHeadExchange", "", "this is a message", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setHeader("key-one", "1");
            return message;
        });
        return "success";
    }

    @GetMapping("/ack")
    public String sendAckMsg() {
        rabbitTemplate.convertAndSend("myManualAckExchange", "mine.manual", "this is a message");
        return "success";
    }

    @GetMapping("/work")
    public String work() {
        for (int i = 0; i < 66; i++) {
            rabbitTemplate.convertAndSend("workQueue", "this is a message");
        }
        return "success";
    }

    @GetMapping("/messageConverter")
    public String messageConverter() {
        //实际项目不建议这么干,spring单例模式,
        //所以最好自己构建一个"jasonRabbitTemplate",使用时注入
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("jsonQueue", new Student("zhangSan", 15, "男"));
        //jasonRabbitTemplate.convertAndSend("jsonQueue", new Student("zhangSan",15,"男"));
        return "success";
    }

    @GetMapping("/ttlMsg")
    public List<Student> directDelayMQ() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Student> students = new ArrayList<>();
        students.add(new Student("张三", 20, "男"));
        students.add(new Student("李四", 24, "男"));
        students.add(new Student("王五", 21, "女"));
        for (Student student : students) {
            jasonRabbitTemplate.convertAndSend("mine.ttl.exchange", "mine.ttl.key", student,
                    message -> {
                        //可在发送消息时设置过期时间,也可在配置类中设置整个队列的过期时间,两个都设置以最早过期时间为准
                        message.getMessageProperties().setExpiration("15000");
                        return message;
                    });
            System.out.println(now.format(dateTimeFormatter)+ " 消息发送："+student.toString());
        }
        return students;
    }

    @GetMapping("/pluginMsg")
    public List<Student> directPluginDelayMQ() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Student> students = new ArrayList<>();
        students.add(new Student("张三", 20, "男"));
        students.add(new Student("李四", 24, "男"));
        students.add(new Student("王五", 21, "女"));
        for (Student student : students) {
            rabbitTemplate.convertAndSend("mine.plugin.delay.exchange", "mine.plugin.key", student,
                    message -> {
                        // 设置5秒过期
                        message.getMessageProperties().setHeader("x-delay", 5000);
                        return message;
                    });
            System.out.println(now.format(dateTimeFormatter) + " 消息发送：" + student.toString());
        }
        return students;
    }
}
