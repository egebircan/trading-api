package com.example.demo.service.trade.strategy;

import com.example.demo.domain.model.Order;

public interface OrderExecutionStrategy {
    void execute(Order order);
}
