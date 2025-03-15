package com.example.demo.service.trade;

import com.example.demo.api.dto.AssetDto;
import com.example.demo.api.response.AssetsResponse;
import com.example.demo.domain.model.Asset;
import com.example.demo.domain.repository.AssetRepository;
import com.example.demo.exception.AssetNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository assetRepository;

    public AssetsResponse listAssets(Long customerId) {
        List<AssetDto> assets = assetRepository.findByCustomerId(customerId).stream()
                .map(AssetDto::fromEntity)
                .toList();
        return new AssetsResponse(assets);
    }

    public Asset findAsset(Long customerId, String symbol) {
        return assetRepository
                .findByCustomerIdAndSymbol(customerId, symbol)
                .orElseThrow(() -> new AssetNotFoundException(symbol, customerId));
    }

    public void openPosition(Long customerId, String symbol, BigDecimal size) {
        try {
            Asset asset = findAsset(customerId, symbol);
            asset.setSize(asset.getSize().add(size));
            asset.setUsableSize(asset.getUsableSize().add(size));
            assetRepository.save(asset);
        } catch (AssetNotFoundException e) {
            Asset newAsset = new Asset();
            newAsset.setCustomerId(customerId);
            newAsset.setSymbol(symbol);
            newAsset.setSize(size);
            newAsset.setUsableSize(size);
            assetRepository.save(newAsset);
        }
    }

    public void closePosition(Long customerId, String symbol, BigDecimal size) {
        Asset asset = findAsset(customerId, symbol);
        asset.setSize(asset.getSize().subtract(size));
        assetRepository.save(asset);
    }

    public void deductUsableSize(Long customerId, BigDecimal size, String symbol) {
        Asset asset = findAsset(customerId, symbol);
        asset.setUsableSize(asset.getUsableSize().subtract(size));
        assetRepository.save(asset);
    }

    public void increaseUsableSize(Long customerId, BigDecimal size, String symbol) {
        Asset asset = findAsset(customerId, symbol);
        asset.setUsableSize(asset.getUsableSize().add(size));
        assetRepository.save(asset);
    }

    public void deductSize(Long customerId, BigDecimal size, String symbol) {
        Asset asset = findAsset(customerId, symbol);
        asset.setSize(asset.getSize().subtract(size));
        assetRepository.save(asset);
    }

    public void increaseSize(Long customerId, BigDecimal size, String symbol) {
        Asset asset = findAsset(customerId, symbol);
        asset.setSize(asset.getSize().add(size));
        assetRepository.save(asset);
    }
}
