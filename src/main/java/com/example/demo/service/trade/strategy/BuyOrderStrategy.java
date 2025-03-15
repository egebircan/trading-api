package com.example.demo.service.trade.strategy;

import static com.example.demo.constant.Constants.TRY_SYMBOL;

import com.example.demo.domain.model.Order;
import com.example.demo.service.trade.AssetService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyOrderStrategy implements OrderExecutionStrategy {
    private final AssetService assetService;

    @Override
    public void execute(Order order) {
        BigDecimal totalAmount = order.getTotalAmount();
        assetService.deductSize(order.getCustomerId(), totalAmount, TRY_SYMBOL);
        assetService.openPosition(order.getCustomerId(), order.getSymbol(), order.getSize());
    }
}
