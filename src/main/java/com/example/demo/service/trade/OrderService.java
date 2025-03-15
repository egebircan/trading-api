package com.example.demo.service.trade;

import static com.example.demo.constant.Constants.TRY_SYMBOL;

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
import com.example.demo.service.trade.strategy.OrderExecutionStrategy;
import com.example.demo.service.trade.strategy.SellOrderStrategy;
import com.example.demo.service.trade.validation.OrderValidator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final OrderFactory orderFactory;
    private final OrderValidator orderValidator;
    private final BuyOrderStrategy buyOrderStrategy;
    private final SellOrderStrategy sellOrderStrategy;

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);

        if (OrderSide.BUY.equals(request.getSide())) {
            BigDecimal totalAmount = request.getSize().multiply(request.getPrice());
            assetService.deductUsableSize(request.getCustomerId(), totalAmount, TRY_SYMBOL);
        } else if (OrderSide.SELL.equals(request.getSide())) {
            assetService.deductUsableSize(request.getCustomerId(), request.getSize(), request.getSymbol());
        }

        Order order = orderFactory.createOrder(request);

        return OrderDto.fromEntity(orderRepository.save(order));
    }

    // todo improve with jpa specification for filtering
    public OrdersResponse listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderDto> orders =
                orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate).stream()
                        .map(OrderDto::fromEntity)
                        .toList();

        return new OrdersResponse(orders);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new OrderStatusException("Only PENDING orders can be canceled");
        }

        if (OrderSide.BUY.equals(order.getSide())) {
            BigDecimal totalAmount = order.getTotalAmount();
            assetService.increaseUsableSize(order.getCustomerId(), totalAmount, TRY_SYMBOL);
        } else if (OrderSide.SELL.equals(order.getSide())) {
            assetService.increaseUsableSize(order.getCustomerId(), order.getSize(), order.getSymbol());
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Transactional
    public OrderDto matchOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new OrderStatusException("Only PENDING orders can be matched");
        }

        OrderExecutionStrategy strategy = OrderSide.BUY.equals(order.getSide()) ? buyOrderStrategy : sellOrderStrategy;
        strategy.execute(order);

        order.setStatus(OrderStatus.MATCHED);
        return OrderDto.fromEntity(orderRepository.save(order));
    }

    // Should only be used for Spring Security authorization check
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
