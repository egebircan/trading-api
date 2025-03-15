package com.example.demo.service.trade.factory;

import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.domain.model.Order;
import com.example.demo.enums.OrderStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFactory {

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setSymbol(request.getSymbol());
        order.setSide(request.getSide());
        order.setSize(request.getSize());
        order.setPrice(request.getPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        return order;
    }
}
