package com.example.demo.exception;

public class OrderNotFoundException extends BaseException {
    private static final String ERROR_MESSAGE = "Order with id %d not found";

    public OrderNotFoundException(Long orderId) {
        super(String.format(ERROR_MESSAGE, orderId));
    }

    public OrderNotFoundException(Long orderId, Throwable cause) {
        super(String.format(ERROR_MESSAGE, orderId), cause);
    }
}
