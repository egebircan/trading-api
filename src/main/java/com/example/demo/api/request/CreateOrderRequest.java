package com.example.demo.api.request;

import com.example.demo.enums.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long customerId;

    @NotBlank
    private String symbol;

    @NotNull
    private OrderSide side;

    @NotNull
    @Positive
    private BigDecimal size;

    @NotNull
    @Positive
    private BigDecimal price;
}
