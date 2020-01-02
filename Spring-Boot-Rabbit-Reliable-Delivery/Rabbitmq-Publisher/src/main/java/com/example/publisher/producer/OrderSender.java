package com.example.publisher.producer;

import com.example.common.domain.BrokerMessageLog;
import com.example.common.domain.BrokerMessageLogExample;
import com.example.common.domain.Order;
import com.example.publisher.constants.Constants;
import com.example.publisher.mapper.BrokerMessageLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RabbitTemplate rabbitTemplate;
    private final BrokerMessageLogMapper brokerMessageLogMapper;
    @Autowired
    public OrderSender(RabbitTemplate rabbitTemplate, BrokerMessageLogMapper brokerMessageLogMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.brokerMessageLogMapper = brokerMessageLogMapper;
    }

    private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         * @param correlationData 唯一标识,有了这个唯一标识,我们就知道可以确认(失败)哪一条消息了
         * @param ack 是否投递成功
         * @param cause 失败原因
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            String messageId = correlationData.getId();
            BrokerMessageLogExample brokerMessageLogExample = new BrokerMessageLogExample();
            brokerMessageLogExample.createCriteria().andMessage_idEqualTo(messageId);
            BrokerMessageLog brokerMessageLog;
            try {
                brokerMessageLog = brokerMessageLogMapper.selectByExample(brokerMessageLogExample).get(0);
            } catch (IndexOutOfBoundsException e) {
                logger.error("不存在messageId:{}的日志记录", messageId);
                return;
            }

            //返回成功,表示消息被正常投递
            if (ack) {
                brokerMessageLog.setStatus(Constants.ORDER_SEND_SUCCESS);
                brokerMessageLog.setUpdateTime(new Date());
                brokerMessageLogMapper.updateByPrimaryKeySelective(brokerMessageLog);
                logger.info("信息投递成功，messageId:{}", brokerMessageLog.getMessageId());
            } else {
                logger.error("消费信息失败，messageId:{} 原因:{}", brokerMessageLog.getMessageId(), cause);
            }
        }
    };

    /**
     * 信息投递的方法
     *
     * @param order 订单
     */
    public void send(Order order) {
        //设置投递回调
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationData = new CorrelationData(order.getMessageId());
        rabbitTemplate.convertAndSend("order-exchange", "order.abcd", order, correlationData);
    }

}
