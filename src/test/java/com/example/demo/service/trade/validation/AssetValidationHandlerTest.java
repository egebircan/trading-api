package com.example.demo.service.trade.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.domain.model.Asset;
import com.example.demo.enums.OrderSide;
import com.example.demo.exception.InsufficientAssetException;
import com.example.demo.service.trade.AssetService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetValidationHandlerTest {

    @Mock
    private AssetService assetService;

    @Mock
    private ValidationHandler nextHandler;

    @InjectMocks
    private AssetValidationHandler validationHandler;

    private CreateOrderRequest buyRequest;
    private CreateOrderRequest sellRequest;
    private Asset tryAsset;
    private Asset stockAsset;
    private static final String STOCK_SYMBOL = "NVDA";
    private static final String TRY_SYMBOL = "TRY";
    private static final Long CUSTOMER_ID = 1L;

    @BeforeEach
    void setUp() {
        validationHandler.setNext(nextHandler);

        buyRequest = new CreateOrderRequest();
        buyRequest.setCustomerId(CUSTOMER_ID);
        buyRequest.setSymbol(STOCK_SYMBOL);
        buyRequest.setSide(OrderSide.BUY);
        buyRequest.setSize(BigDecimal.TEN);
        buyRequest.setPrice(BigDecimal.valueOf(100));

        sellRequest = new CreateOrderRequest();
        sellRequest.setCustomerId(CUSTOMER_ID);
        sellRequest.setSymbol(STOCK_SYMBOL);
        sellRequest.setSide(OrderSide.SELL);
        sellRequest.setSize(BigDecimal.TEN);

        tryAsset = new Asset();
        tryAsset.setSymbol(TRY_SYMBOL);
        tryAsset.setCustomerId(CUSTOMER_ID);
        tryAsset.setSize(BigDecimal.valueOf(2000));
        tryAsset.setUsableSize(BigDecimal.valueOf(2000));

        stockAsset = new Asset();
        stockAsset.setSymbol(STOCK_SYMBOL);
        stockAsset.setCustomerId(CUSTOMER_ID);
        stockAsset.setSize(BigDecimal.valueOf(20));
        stockAsset.setUsableSize(BigDecimal.valueOf(20));
    }

    @Test
    void validate_BuyOrder_SufficientBalance_Success() {
        when(assetService.findAsset(CUSTOMER_ID, TRY_SYMBOL)).thenReturn(tryAsset);

        validationHandler.validate(buyRequest);

        verify(nextHandler).validate(buyRequest);
        verify(assetService).findAsset(CUSTOMER_ID, TRY_SYMBOL);
    }

    @Test
    void validate_BuyOrder_InsufficientBalance_ThrowsException() {
        tryAsset.setUsableSize(BigDecimal.valueOf(500));
        when(assetService.findAsset(CUSTOMER_ID, TRY_SYMBOL)).thenReturn(tryAsset);

        assertThrows(InsufficientAssetException.class, () -> validationHandler.validate(buyRequest));
        verify(assetService).findAsset(CUSTOMER_ID, TRY_SYMBOL);
        verify(nextHandler, never()).validate(any());
    }

    @Test
    void validate_SellOrder_SufficientBalance_Success() {
        when(assetService.findAsset(CUSTOMER_ID, STOCK_SYMBOL)).thenReturn(stockAsset);

        validationHandler.validate(sellRequest);

        verify(nextHandler).validate(sellRequest);
        verify(assetService).findAsset(CUSTOMER_ID, STOCK_SYMBOL);
    }

    @Test
    void validate_SellOrder_InsufficientBalance_ThrowsException() {
        stockAsset.setUsableSize(BigDecimal.valueOf(5));
        when(assetService.findAsset(CUSTOMER_ID, STOCK_SYMBOL)).thenReturn(stockAsset);

        assertThrows(InsufficientAssetException.class, () -> validationHandler.validate(sellRequest));
        verify(assetService).findAsset(CUSTOMER_ID, STOCK_SYMBOL);
        verify(nextHandler, never()).validate(any());
    }

    @Test
    void validate_ChainOfResponsibility_Success() {
        when(assetService.findAsset(CUSTOMER_ID, TRY_SYMBOL)).thenReturn(tryAsset);
        doNothing().when(nextHandler).validate(any());

        validationHandler.validate(buyRequest);

        verify(nextHandler).validate(buyRequest);
    }
}
