package com.example.demo.exception;

import java.math.BigDecimal;

public class InsufficientAssetException extends BaseException {
    public InsufficientAssetException(String symbol, Long customerId, BigDecimal required, BigDecimal available) {
        super(String.format(
                "Insufficient %s asset for customer %d. Required: %s, Available: %s",
                symbol, customerId, required.toPlainString(), available.toPlainString()));
    }
}
