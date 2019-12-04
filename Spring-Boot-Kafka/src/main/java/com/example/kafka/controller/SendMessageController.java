package com.example.kafka.controller;

import com.example.kafka.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dengzhiming on 2019/7/3
 */
@RestController
public class SendMessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    //private KafkaTemplate<String,String> kafkaTemplate;
    private KafkaTemplate<String, Message> kafkaTemplate;

    @GetMapping("send/{message}")
    public void send(@PathVariable String message) {
        // test为Topic的名称,message为要发送的消息
        //this.kafkaTemplate.send("test",message);

        //异步回调反馈
        /*ListenableFuture<SendResult<String,String>> future =
                this.kafkaTemplate.send("test",message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.info("消息: {} 发送失败，原因：{}",message,throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
                logger.error("成功发送消息：{}，offset=[{}]",message,
                        stringStringSendResult.getRecordMetadata().offset());
            }
        });*/
        this.kafkaTemplate.send("test", new Message("kimi", message));
    }


}
