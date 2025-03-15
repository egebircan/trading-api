package com.example.demo.api.dto;

import com.example.demo.domain.model.Asset;
import java.math.BigDecimal;

public record AssetDto(Long customerId, String symbol, BigDecimal size, BigDecimal usableSize) {
    public static AssetDto fromEntity(Asset asset) {
        return new AssetDto(asset.getCustomerId(), asset.getSymbol(), asset.getSize(), asset.getUsableSize());
    }
}
