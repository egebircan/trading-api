package com.example.demo.api.dto;

import com.example.demo.domain.model.Order;
import com.example.demo.enums.OrderSide;
import com.example.demo.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(
        Long customerId,
        String symbol,
        OrderSide side,
        BigDecimal size,
        BigDecimal price,
        OrderStatus status,
        LocalDateTime createDate) {
    public static OrderDto fromEntity(Order order) {
        return new OrderDto(
                order.getCustomerId(),
                order.getSymbol(),
                order.getSide(),
                order.getSize(),
                order.getPrice(),
                order.getStatus(),
                order.getCreateDate());
    }
}
