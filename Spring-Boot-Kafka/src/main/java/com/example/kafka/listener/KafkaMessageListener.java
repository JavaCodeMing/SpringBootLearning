package com.example.kafka.listener;

import com.example.kafka.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created by dengzhiming on 2019/7/3
 */
@Component
public class KafkaMessageListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /*@KafkaListener(topics = "test",groupId = "test-consumer")
    public void listen(String message){
        logger.info("接受消息：{}",message);
    }*/
    @KafkaListener(topics = "test", groupId = "test-consumer")
    public void listen(Message message) {
        logger.info("接受消息：{}", message);
    }
}
