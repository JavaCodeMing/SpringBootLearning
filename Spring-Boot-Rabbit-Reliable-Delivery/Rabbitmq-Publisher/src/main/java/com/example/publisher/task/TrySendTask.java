package com.example.publisher.task;

import com.alibaba.fastjson.JSONObject;
import com.example.common.domain.BrokerMessageLogExample;
import com.example.common.domain.Order;
import com.example.publisher.constants.Constants;
import com.example.publisher.mapper.BrokerMessageLogMapper;
import com.example.publisher.producer.OrderSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TrySendTask {

    private final OrderSender orderSender;
    private final BrokerMessageLogMapper brokerMessageLogMapper;
    @Autowired
    public TrySendTask(OrderSender orderSender, BrokerMessageLogMapper brokerMessageLogMapper) {
        this.orderSender = orderSender;
        this.brokerMessageLogMapper = brokerMessageLogMapper;
    }

    /**
     * 系统启动后5秒开启定时任务 10秒执行一次
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void rabbitmqReSend() {
        BrokerMessageLogExample brokerMessageLogExample = new BrokerMessageLogExample();
        brokerMessageLogExample.createCriteria()
                .andStatusEqualTo(Constants.ORDER_SENDING)
                .andNext_retryLessThan(new Date());
        /*
         * 查询出下一次执行时间小于当前时间的日志记录并且状态为投递中，
         * 遍历结果集，判断重试次数是或大于3次，如果大于，将日志设置为投递失败，
         * 如果小于 则尝试重新投递，并改变数据库中日志的尝试次数
         */
        brokerMessageLogMapper.selectByExample(brokerMessageLogExample)
                .forEach(brokerMessageLog -> {
                    if (brokerMessageLog.getTryCount() >= 3) {
                        //投递失败的消息,只变更更新时间!
                        brokerMessageLog.setStatus(Constants.ORDER_SEND_FAILURE);
                        brokerMessageLog.setUpdateTime(new Date());
                        brokerMessageLogMapper.updateByPrimaryKeySelective(brokerMessageLog);
                    } else {
                        //发送中的消息,更新尝试次数和更新时间!
                        brokerMessageLog.setTryCount(brokerMessageLog.getTryCount() + 1);
                        brokerMessageLog.setUpdateTime(new Date());
                        brokerMessageLogMapper.updateByPrimaryKeySelective(brokerMessageLog);
                        try {
                            //发送中的消息,重新进行消息投递!
                            orderSender.send(JSONObject.parseObject(brokerMessageLog.getMessage(), Order.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
