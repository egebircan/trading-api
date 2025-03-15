package com.example.demo.service.trade.strategy;

import static org.mockito.Mockito.*;

import com.example.demo.constant.Constants;
import com.example.demo.domain.model.Order;
import com.example.demo.service.trade.AssetService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellOrderStrategyTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private SellOrderStrategy sellOrderStrategy;

    private Order order;
    private static final Long CUSTOMER_ID = 1L;
    private static final String SYMBOL = "NVDA";
    private static final BigDecimal SIZE = BigDecimal.TEN;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal TOTAL_AMOUNT = SIZE.multiply(PRICE);

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setCustomerId(CUSTOMER_ID);
        order.setSymbol(SYMBOL);
        order.setSize(SIZE);
        order.setPrice(PRICE);
    }

    @Test
    void execute_SellOrder_Success() {
        doNothing().when(assetService).increaseSize(CUSTOMER_ID, TOTAL_AMOUNT, Constants.TRY_SYMBOL);
        doNothing().when(assetService).increaseUsableSize(CUSTOMER_ID, TOTAL_AMOUNT, Constants.TRY_SYMBOL);
        doNothing().when(assetService).closePosition(CUSTOMER_ID, SYMBOL, SIZE);

        sellOrderStrategy.execute(order);

        verify(assetService).increaseSize(CUSTOMER_ID, TOTAL_AMOUNT, Constants.TRY_SYMBOL);
        verify(assetService).increaseUsableSize(CUSTOMER_ID, TOTAL_AMOUNT, Constants.TRY_SYMBOL);
        verify(assetService).closePosition(CUSTOMER_ID, SYMBOL, SIZE);
    }
}
