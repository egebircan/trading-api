package com.example.demo.service.trade.validation;

import com.example.demo.api.request.CreateOrderRequest;

public interface ValidationHandler {
    void setNext(ValidationHandler handler);

    void validate(CreateOrderRequest request);
}
