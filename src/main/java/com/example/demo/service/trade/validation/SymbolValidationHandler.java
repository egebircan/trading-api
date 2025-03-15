package com.example.demo.service.trade.validation;

import static com.example.demo.constant.Constants.TRY_SYMBOL;

import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.exception.InvalidSymbolException;
import org.springframework.stereotype.Component;

@Component
public class SymbolValidationHandler implements ValidationHandler {
    private ValidationHandler nextHandler;

    @Override
    public void setNext(ValidationHandler handler) {
        this.nextHandler = handler;
    }

    @Override
    public void validate(CreateOrderRequest request) {
        if (request.getSymbol().equals(TRY_SYMBOL)) {
            throw new InvalidSymbolException("Cannot trade TRY symbol");
        }

        if (nextHandler != null) {
            nextHandler.validate(request);
        }
    }
}
