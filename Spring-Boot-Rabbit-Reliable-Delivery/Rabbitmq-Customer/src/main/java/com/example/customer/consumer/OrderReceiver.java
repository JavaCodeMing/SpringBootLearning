package com.example.customer.consumer;

import com.example.common.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderReceiver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RabbitListener(
            containerFactory = "jsonListenerFactory",
            bindings = @QueueBinding(
                    //数据是否持久化
                    value = @Queue(value = "order-queue",durable = "true"),
                    exchange = @Exchange(name = "order-exchange",type = "topic"),
                    key="order.*"
            )
    )
    public void onOrderMessage(@Payload Order order) throws Exception {
        logger.info("----收到消息，开始消费-----");
        logger.info("订单id："+order.getId());
        logger.info("--------消费完成--------");
    }
}
