package com.example.publisher.controller;

import com.example.common.domain.Order;
import com.example.publisher.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class IndexController {

    private final OrderService orderService;
    @Autowired
    public IndexController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(value = "send")
    public String send() throws Exception {
        //order对象必须实现序列化
        Order order = new Order();
        order.setName(UUID.randomUUID().toString());
        order.setMessageId(UUID.randomUUID().toString() + System.currentTimeMillis());
        orderService.createOrder(order);
        return "success";
    }

}
