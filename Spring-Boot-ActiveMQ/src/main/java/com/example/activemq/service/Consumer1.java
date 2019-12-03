package com.example.activemq.service;

import com.example.activemq.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.LocalDateTime;

/**
 * Created by dengzhiming on 2019/11/2
 */
@Component
public class Consumer1 {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    // 测试发送方事务回滚
    @JmsListener(destination = "queue",containerFactory = "queueListenerFactory")
    public void queue(String text) {
        log.info("consumer1收到queue信息:{}",text);
    }

    // 接收点对点发送的字符串信息
    @JmsListener(destination = "queue_string",containerFactory = "queueListenerFactory")
    public void receiveQueue(String text) {
        log.info("consumer1收到queue_string信息:{}",text);
    }

    // 接收点对点发送的对象信息
    @JmsListener(destination = "queue_user",containerFactory = "queueListenerFactory")
    public void receiveQueue(User user) {
        log.info("consumer1收到queue_user信息:{}",user.toString());
    }

    // 接收点对点发送的字符串信息,并返回一个消息到指定队列
    @JmsListener(destination = "queue_string_2way",containerFactory = "queueListenerFactory")
    @SendTo("queue_string_return")
    public String receive2WayQueue(String text) {
        log.info("consumer1收到queue_string_2way信息:{}",text);
        return "queue_string_2way已收到消息:" + text + ",请进行下一步操作";
    }

    // 接收点对点发送的消息,并手动会签且在出现异常时调用方法使消息重新被消费
    @JmsListener(destination = "queue_string_ack",containerFactory = "queueListenerACKFactory")
    public void receiveQueue(TextMessage message, Session session) throws JMSException {
        log.info("consumer1收到queue_string信息:{}",message.getText());
        try {
            int a = 1 / 0;
            //业务处理结束,手动ack通知队列收到消息,可以把消息从队列移除
            message.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
            /*
             * 通知队列重发,默认每秒重发1次,一共重发6次,
             * 若想自定义重试次数，重试间隔时间可以设置
             * ActiveMQConnectionFactory的RedeliveryPolicyMap的RedeliveryPolicy
             */
            session.recover();
        }
    }

    // 接收订阅模式下发送的字符串信息,和Consumer2一起测试
    @JmsListener(destination = "topic_string",containerFactory = "topicListenerFactory")
    public void receiveTopic(String text) {
        log.info("consumer1收到topic_string信息:{}",text);
    }

    // 接收订阅模式下发送的对象信息,和Consumer2一起测试
    @JmsListener(destination = "topic_user",containerFactory = "topicListenerFactory")
    public void receiveTopic(User user) {
        log.info("consumer1收到topic_user信息:{}",user.toString());
    }

    // 接收订阅模式下延迟发送的信息,和Consumer2一起测试
    @JmsListener(destination = "topic_delay_string",containerFactory = "topicListenerFactory")
    public void receiveDelayTopic(String text) {
        log.info("consumer1收到topic_delay_string延时信息:{},接收时间:{}",text, LocalDateTime.now());
    }

}
