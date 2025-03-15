package com.example.demo.domain.model;

import com.example.demo.enums.OrderSide;
import com.example.demo.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long customerId;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private OrderSide side;

    private BigDecimal size;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createDate;

    public BigDecimal getTotalAmount() {
        return this.size.multiply(this.price);
    }
}
