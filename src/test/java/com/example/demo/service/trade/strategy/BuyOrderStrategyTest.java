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
class BuyOrderStrategyTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private BuyOrderStrategy buyOrderStrategy;

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
    void execute_BuyOrder_Success() {
        doNothing().when(assetService).deductSize(CUSTOMER_ID, TOTAL_AMOUNT, Constants.TRY_SYMBOL);
        doNothing().when(assetService).openPosition(CUSTOMER_ID, SYMBOL, SIZE);

        buyOrderStrategy.execute(order);

        verify(assetService).deductSize(CUSTOMER_ID, TOTAL_AMOUNT, Constants.TRY_SYMBOL);
        verify(assetService).openPosition(CUSTOMER_ID, SYMBOL, SIZE);
    }
}
