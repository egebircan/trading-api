package com.example.demo.exception;

public class AssetNotFoundException extends BaseException {
    public AssetNotFoundException(String symbol, Long customerId) {
        super(String.format("Asset with symbol '%s' not found for customer: %d", symbol, customerId));
    }
}
