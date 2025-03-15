package com.example.demo.service.trade.validation;

import static com.example.demo.constant.Constants.TRY_SYMBOL;

import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.domain.model.Asset;
import com.example.demo.enums.OrderSide;
import com.example.demo.exception.InsufficientAssetException;
import com.example.demo.service.trade.AssetService;
import com.example.demo.util.BigDecimalUtil;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetValidationHandler implements ValidationHandler {
    private final AssetService assetService;
    private ValidationHandler nextHandler;

    @Override
    public void setNext(ValidationHandler handler) {
        this.nextHandler = handler;
    }

    @Override
    public void validate(CreateOrderRequest request) {
        if (OrderSide.BUY.equals(request.getSide())) {
            validateBuyBalance(request);
        } else if (OrderSide.SELL.equals(request.getSide())) {
            validateSellBalance(request);
        }

        if (nextHandler != null) {
            nextHandler.validate(request);
        }
    }

    private void validateBuyBalance(CreateOrderRequest request) {
        Asset cash = assetService.findAsset(request.getCustomerId(), TRY_SYMBOL);
        BigDecimal totalAmount = request.getSize().multiply(request.getPrice());
        if (BigDecimalUtil.isLess(cash.getUsableSize(), totalAmount)) {
            throw new InsufficientAssetException(
                    request.getSymbol(), request.getCustomerId(), totalAmount, cash.getUsableSize());
        }
    }

    private void validateSellBalance(CreateOrderRequest request) {
        Asset asset = assetService.findAsset(request.getCustomerId(), request.getSymbol());
        if (BigDecimalUtil.isLess(asset.getUsableSize(), request.getSize())) {
            throw new InsufficientAssetException(
                    request.getSymbol(), request.getCustomerId(), request.getSize(), asset.getSize());
        }
    }
}
