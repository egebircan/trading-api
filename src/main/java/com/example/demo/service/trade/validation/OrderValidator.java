package com.example.demo.service.trade.validation;

import com.example.demo.api.request.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final SymbolValidationHandler symbolValidationHandler;
    private final AssetValidationHandler assetValidationHandler;

    public void validate(CreateOrderRequest request) {
        symbolValidationHandler.setNext(assetValidationHandler);

        symbolValidationHandler.validate(request);
    }
}
