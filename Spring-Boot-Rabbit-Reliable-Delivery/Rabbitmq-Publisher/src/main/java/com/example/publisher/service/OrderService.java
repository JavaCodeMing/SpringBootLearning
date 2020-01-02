package com.example.publisher.service;

import com.example.common.domain.Order;

public interface OrderService {
    void createOrder(Order order) throws Exception;
}
