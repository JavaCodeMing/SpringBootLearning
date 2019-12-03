package com.example.activemq.controller;

import com.example.activemq.bean.User;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by dengzhiming on 2019/11/2
 */
@Controller
public class ProducerController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Queue queueString;
    @Autowired
    private Queue queueUser;
    @Autowired
    private Queue queueString2Way;
    @Autowired
    private Topic topicString;
    @Autowired
    private Topic topicUser;
    @Autowired
    private Topic delayTopicString;
    @Autowired
    private Queue queueStringACK;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    // 点对点发送字符串,实现消息发送方的事务回滚
    @RequestMapping("sendQueueMsg")
    @ResponseBody
    public String sendQueueMsg(String msg) throws JMSException {
        log.info("product发送信息:{}到{}",msg,"queue");
        Session session = Objects.requireNonNull(this.jmsMessagingTemplate
                .getConnectionFactory())
                .createConnection()
                // true表示手动提交,false表示自动提交
                .createSession(true,Session.AUTO_ACKNOWLEDGE);
        Queue quene = session.createQueue("queue");
        MessageProducer producer = session.createProducer(quene);
        try {
            TextMessage textMessage=new ActiveMQTextMessage();
            textMessage.setText(msg);
            producer.send(textMessage);
            // 业务代码执行出现异常
            //int i = 1/0;
            // 消息发送的手动提交
            session.commit();
        }catch (Exception e){
            // 消息的回滚
            session.rollback();
            return "failure";
        }
        return "success";
    }

    // 点对点发送字符串
    @RequestMapping("sendQueueMsg1")
    @ResponseBody
    public String sendQueueMsg1(String msg) throws JMSException {
        log.info("product发送信息:{}到{}",msg,this.queueString.getQueueName());
        this.jmsMessagingTemplate.convertAndSend(this.queueString,msg);
        return "success";
    }

    // 点对点发送对象
    @RequestMapping("sendQueueMsg2")
    @ResponseBody
    public String sendQueueMsg2(User user) throws JMSException {
        log.info("product发送信息:{}到{}",user.toString(),this.queueUser.getQueueName());
        this.jmsMessagingTemplate.convertAndSend(this.queueUser,user);
        return "success";
    }

    // 点对点发送字符串,待消息消费后会返回
    @RequestMapping("send2WayQueueMsg")
    @ResponseBody
    public String send2WayQueueMsg(String msg) throws JMSException {
        log.info("product发送信息:{}到{}",msg,this.queueString2Way.getQueueName());
        this.jmsMessagingTemplate.convertAndSend(this.queueString2Way,msg);
        return "success";
    }
    // send2WayQueueMsg方法发送消息返回后的接收方法
    @JmsListener(destination = "queue_string_return",containerFactory = "queueListenerFactory")
    public void receiveQueue(String text) {
        log.info("product收到queue_string_return信息:{}",text);
    }

    // 点对点发送字符串,消费方手动会签
    @RequestMapping("sendACKQueueMsg")
    @ResponseBody
    public String sendACKQueueMsg(String msg) throws JMSException {
        log.info("product发送信息:{}到{}",msg,this.queueStringACK.getQueueName());
        this.jmsMessagingTemplate.convertAndSend(this.queueStringACK,msg);
        return "success";
    }

    // 订阅模式下发送字符串
    @RequestMapping("sendTopicMsg1")
    @ResponseBody
    public String sendTopicMsg1(String msg) throws JMSException {
        log.info("product发送信息:{}到{}",msg,this.topicString.getTopicName());
        this.jmsMessagingTemplate.convertAndSend(this.topicString,msg);
        return "success";
    }

    // 订阅模式下发送对象
    @RequestMapping("sendTopicMsg2")
    @ResponseBody
    public String sendTopicMsg2(User user) throws JMSException {
        log.info("product发送信息:{}到{}",user.toString(),this.topicUser.getTopicName());
        this.jmsMessagingTemplate.convertAndSend(this.topicUser,user);
        return "success";
    }

    //使用延时队列需要在ActiveMQ的配置文件activemq.xml中的<broker></broker>标签里添加schedulerSupport="true",如下:
    // <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}" schedulerSupport="true"></broker>
    @RequestMapping("sendDelayTopicMsg1")
    @ResponseBody
    public String sendDelayTopicMsg1(String msg) throws JMSException {
        log.info("product发送延时信息:{}到{},发送时间:{}",msg,this.delayTopicString.getTopicName(), LocalDateTime.now());
        this.jmsMessagingTemplate.getJmsTemplate().send(this.delayTopicString, session -> {
            TextMessage tx = session.createTextMessage(msg);
            tx.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,10 * 1000);
            return tx;
        });
        return "success";
    }
    @RequestMapping("sendDelayTopicMsg2")
    @ResponseBody
    public String sendDelayTopicMsg2(String msg,long time) throws JMSException {
        log.info("product发送延时信息:{}到{},发送时间:{}",msg,this.delayTopicString.getTopicName(), LocalDateTime.now());
        this.jmsMessagingTemplate.getJmsTemplate().send(this.delayTopicString, session -> {
            TextMessage tx = session.createTextMessage(msg);
            tx.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,time);
            return tx;
        });
        return "success";
    }
}
