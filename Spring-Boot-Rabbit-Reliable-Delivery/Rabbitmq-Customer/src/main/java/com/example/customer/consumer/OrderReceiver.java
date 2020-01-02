package com.example.customer.consumer;

import com.example.common.domain.Order;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public void onOrderMessage(@Payload Order order, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        try {
            logger.info("----收到消息，开始消费-----");
            logger.info("订单id："+order.getId());
            //消息确认,(deliveryTag,multiple:是否确认所有消息)
            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
            logger.info("--------消费完成--------");
        }catch (Exception e){
            //消息拒绝(deliveryTag,multiple,requeue:拒绝后是否重新回到队列)
            channel.basicNack((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false, false);
            // 拒绝一条
            // channel.basicReject();
            logger.info("--------消息消费异常--------");
        }

    }
}
