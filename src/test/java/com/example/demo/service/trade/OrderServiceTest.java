package com.example.demo.service.trade;

import static com.example.demo.constant.Constants.TRY_SYMBOL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.api.dto.OrderDto;
import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.api.response.OrdersResponse;
import com.example.demo.domain.model.Order;
import com.example.demo.domain.repository.OrderRepository;
import com.example.demo.enums.OrderSide;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.OrderStatusException;
import com.example.demo.service.trade.factory.OrderFactory;
import com.example.demo.service.trade.strategy.BuyOrderStrategy;
import com.example.demo.service.trade.strategy.SellOrderStrategy;
import com.example.demo.service.trade.validation.OrderValidator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private OrderFactory orderFactory;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private BuyOrderStrategy buyOrderStrategy;

    @Mock
    private SellOrderStrategy sellOrderStrategy;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest buyOrderRequest;
    private CreateOrderRequest sellOrderRequest;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        buyOrderRequest = new CreateOrderRequest();
        buyOrderRequest.setCustomerId(1L);
        buyOrderRequest.setSymbol("BTC");
        buyOrderRequest.setSide(OrderSide.BUY);
        buyOrderRequest.setSize(BigDecimal.ONE);
        buyOrderRequest.setPrice(BigDecimal.valueOf(50000));

        sellOrderRequest = new CreateOrderRequest();
        sellOrderRequest.setCustomerId(1L);
        sellOrderRequest.setSymbol("BTC");
        sellOrderRequest.setSide(OrderSide.SELL);
        sellOrderRequest.setSize(BigDecimal.ONE);
        sellOrderRequest.setPrice(BigDecimal.valueOf(50000));

        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerId(1L);
        mockOrder.setSymbol("BTC");
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setSize(BigDecimal.ONE);
        mockOrder.setPrice(BigDecimal.valueOf(50000));
        mockOrder.setStatus(OrderStatus.PENDING);
    }

    @Test
    void createOrder_Buy_Success() {
        when(orderFactory.createOrder(any())).thenReturn(mockOrder);
        when(orderRepository.save(any())).thenReturn(mockOrder);

        OrderDto result = orderService.createOrder(buyOrderRequest);

        verify(orderValidator).validate(buyOrderRequest);
        verify(assetService).deductUsableSize(1L, BigDecimal.valueOf(50000), TRY_SYMBOL);
        verify(orderFactory).createOrder(buyOrderRequest);
        verify(orderRepository).save(mockOrder);

        assertNotNull(result);
    }

    @Test
    void createOrder_Sell_Success() {
        mockOrder.setSide(OrderSide.SELL);
        when(orderFactory.createOrder(any())).thenReturn(mockOrder);
        when(orderRepository.save(any())).thenReturn(mockOrder);

        OrderDto result = orderService.createOrder(sellOrderRequest);

        verify(orderValidator).validate(sellOrderRequest);
        verify(assetService).deductUsableSize(1L, BigDecimal.ONE, "BTC");
        verify(orderFactory).createOrder(sellOrderRequest);
        verify(orderRepository).save(mockOrder);

        assertNotNull(result);
    }

    @Test
    void listOrders_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        when(orderRepository.findByCustomerIdAndCreateDateBetween(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(mockOrder));

        OrdersResponse result = orderService.listOrders(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.orders().size());
        verify(orderRepository).findByCustomerIdAndCreateDateBetween(1L, startDate, endDate);
    }

    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        orderService.cancelOrder(1L);

        verify(assetService).increaseUsableSize(1L, BigDecimal.valueOf(50000), TRY_SYMBOL);
        verify(orderRepository).save(mockOrder);
        assertEquals(OrderStatus.CANCELED, mockOrder.getStatus());
    }

    @Test
    void cancelOrder_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void cancelOrder_InvalidStatus() {
        mockOrder.setStatus(OrderStatus.MATCHED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        assertThrows(OrderStatusException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void matchOrder_BuyOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);

        OrderDto result = orderService.matchOrder(1L);

        verify(buyOrderStrategy).execute(mockOrder);
        verify(orderRepository).save(mockOrder);
        assertEquals(OrderStatus.MATCHED, mockOrder.getStatus());
        assertNotNull(result);
    }

    @Test
    void matchOrder_SellOrder_Success() {
        mockOrder.setSide(OrderSide.SELL);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);

        OrderDto result = orderService.matchOrder(1L);

        verify(sellOrderStrategy).execute(mockOrder);
        verify(orderRepository).save(mockOrder);
        assertEquals(OrderStatus.MATCHED, mockOrder.getStatus());
        assertNotNull(result);
    }

    @Test
    void matchOrder_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.matchOrder(1L));
    }

    @Test
    void matchOrder_InvalidStatus() {
        mockOrder.setStatus(OrderStatus.CANCELED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        assertThrows(OrderStatusException.class, () -> orderService.matchOrder(1L));
    }

    @Test
    void getOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        Order result = orderService.getOrder(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrder_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(1L));
    }
}
