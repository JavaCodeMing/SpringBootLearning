package com.example.activemq.service;

import com.example.activemq.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by dengzhiming on 2019/11/2
 */
@Component
public class Consumer2 {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    // 接收点对点发送的字符串信息,用来测试queue的负载均衡效果
    @JmsListener(destination = "queue_string",containerFactory = "queueListenerFactory")
    public void receiveQueue(String text) {
        log.info("consumer2收到queue_string信息:{}",text);
    }

    // 接收点对点发送的对象信息,用来测试queue的负载均衡效果
    @JmsListener(destination = "queue_user",containerFactory = "queueListenerFactory")
    public void receiveQueue(User user) {
        log.info("consumer2收到queue_user信息:{}",user.toString());
    }

    // 接收订阅模式下发送的字符串信息,和Consumer1一起测试,测试多个消费者消费一个订阅消息
    @JmsListener(destination = "topic_string",containerFactory = "topicListenerFactory")
    public void receiveTopic(String text) {
        log.info("consumer2收到topic_string信息:{}",text);
    }

    // 接收订阅模式下发送的对象信息,和Consumer1一起测试,测试多个消费者消费一个订阅消息
    @JmsListener(destination = "topic_user",containerFactory = "topicListenerFactory")
    public void receiveTopic(User user) {
        log.info("consumer2收到topic_user信息:{}",user.toString());
    }

    // 接收订阅模式下延迟发送的信息,和Consumer1一起测试,测试多个消费者消费一个订阅消息
    @JmsListener(destination = "topic_delay_string",containerFactory = "topicListenerFactory")
    public void receiveDelayTopic(String text) {
        log.info("consumer2收到topic_delay_string延时信息:{},接收时间:{}",text, LocalDateTime.now());
    }
}
