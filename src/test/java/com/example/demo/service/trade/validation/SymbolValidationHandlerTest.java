package com.example.demo.service.trade.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.constant.Constants;
import com.example.demo.exception.InvalidSymbolException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolValidationHandlerTest {

    @Mock
    private ValidationHandler nextHandler;

    @InjectMocks
    private SymbolValidationHandler validationHandler;

    private CreateOrderRequest request;
    private static final String VALID_SYMBOL = "NVDA";

    @BeforeEach
    void setUp() {
        validationHandler.setNext(nextHandler);

        request = new CreateOrderRequest();
        request.setSymbol(VALID_SYMBOL);
    }

    @Test
    void validate_ValidSymbol_Success() {
        validationHandler.validate(request);

        verify(nextHandler).validate(request);
    }

    @Test
    void validate_TrySymbol_ThrowsException() {
        request.setSymbol(Constants.TRY_SYMBOL);

        assertThrows(InvalidSymbolException.class, () -> validationHandler.validate(request));
        verify(nextHandler, never()).validate(any());
    }

    @Test
    void validate_ChainOfResponsibility_Success() {
        doNothing().when(nextHandler).validate(any());

        validationHandler.validate(request);

        verify(nextHandler).validate(request);
    }

    @Test
    void validate_NoNextHandler_Success() {
        validationHandler.setNext(null);

        assertDoesNotThrow(() -> validationHandler.validate(request));
    }
}
