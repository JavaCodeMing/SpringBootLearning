package com.example.publisher.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.common.domain.BrokerMessageLog;
import com.example.common.domain.Order;
import com.example.publisher.constants.Constants;
import com.example.publisher.mapper.BrokerMessageLogMapper;
import com.example.publisher.mapper.OrderMapper;
import com.example.publisher.producer.OrderSender;
import com.example.publisher.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.example.publisher.constants.Constants.ORDER_TIMEOUT;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final BrokerMessageLogMapper brokerMessageLogMapper;
    private final OrderSender orderSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, BrokerMessageLogMapper brokerMessageLogMapper, OrderSender orderSender) {
        this.orderMapper = orderMapper;
        this.brokerMessageLogMapper = brokerMessageLogMapper;
        this.orderSender = orderSender;
    }

    @Override
    @Transactional  // 确保数据库操作失败数据回滚
    public void createOrder(Order order) {

        // 插入业务数据
        logger.info("插入业务数据!");
        orderMapper.insertSelective(order);
        // 插入消息记录表数据
        BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
        // 消息唯一ID
        brokerMessageLog.setMessageId(order.getMessageId());
        // 保存消息整体 转为JSON 格式存储入库
        brokerMessageLog.setMessage(JSONObject.toJSONString(order));
        // 设置消息状态为0 表示发送中
        brokerMessageLog.setStatus(Constants.ORDER_SENDING);
        // 消息记录插入时间
        brokerMessageLog.setCreateTime(new Date());
        // 消息记录更新时间
        brokerMessageLog.setUpdateTime(new Date());
        // 下一次投递时间
        brokerMessageLog.setNextRetry(new Date(new Date().getTime() + ORDER_TIMEOUT * 60000));
        brokerMessageLog.setTryCount(0);
        logger.info("插入消息数据!");
        brokerMessageLogMapper.insertSelective(brokerMessageLog);

        // 发送订单消息供其他系统消费
        logger.info("发送订单消息!");
        orderSender.send(order);
    }
}
