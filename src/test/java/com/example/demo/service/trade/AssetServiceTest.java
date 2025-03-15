package com.example.demo.service.trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.api.dto.AssetDto;
import com.example.demo.api.response.AssetsResponse;
import com.example.demo.domain.model.Asset;
import com.example.demo.domain.repository.AssetRepository;
import com.example.demo.exception.AssetNotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset mockAsset;
    private final Long customerId = 1L;
    private final String symbol = "NVDA";
    private final BigDecimal initialSize = BigDecimal.valueOf(10);

    @BeforeEach
    void setUp() {
        mockAsset = new Asset();
        mockAsset.setCustomerId(customerId);
        mockAsset.setSymbol(symbol);
        mockAsset.setSize(initialSize);
        mockAsset.setUsableSize(initialSize);
    }

    @Test
    void listAssets_Success() {
        when(assetRepository.findByCustomerId(customerId)).thenReturn(Collections.singletonList(mockAsset));

        AssetsResponse response = assetService.listAssets(customerId);

        assertNotNull(response);
        assertEquals(1, response.assets().size());
        AssetDto assetDto = response.assets().get(0);
        assertEquals(customerId, assetDto.customerId());
        assertEquals(symbol, assetDto.symbol());
        assertEquals(initialSize, assetDto.size());
        assertEquals(initialSize, assetDto.usableSize());
    }

    @Test
    void findAsset_Success() {
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));

        Asset result = assetService.findAsset(customerId, symbol);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(symbol, result.getSymbol());
        assertEquals(initialSize, result.getSize());
    }

    @Test
    void findAsset_NotFound() {
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.empty());

        assertThrows(AssetNotFoundException.class, () -> assetService.findAsset(customerId, symbol));
    }

    @Test
    void openPosition_ExistingAsset() {
        BigDecimal additionalSize = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));
        when(assetRepository.save(any())).thenReturn(mockAsset);

        assetService.openPosition(customerId, symbol, additionalSize);

        verify(assetRepository).save(mockAsset);
        assertEquals(initialSize.add(additionalSize), mockAsset.getSize());
        assertEquals(initialSize.add(additionalSize), mockAsset.getUsableSize());
    }

    @Test
    void openPosition_NewAsset() {
        BigDecimal size = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.empty());

        assetService.openPosition(customerId, symbol, size);

        verify(assetRepository).save(any());
    }

    @Test
    void closePosition_Success() {
        BigDecimal closeSize = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));
        when(assetRepository.save(any())).thenReturn(mockAsset);

        assetService.closePosition(customerId, symbol, closeSize);

        verify(assetRepository).save(mockAsset);
        assertEquals(initialSize.subtract(closeSize), mockAsset.getSize());
    }

    @Test
    void deductUsableSize_Success() {
        BigDecimal deductSize = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));
        when(assetRepository.save(any())).thenReturn(mockAsset);

        assetService.deductUsableSize(customerId, deductSize, symbol);

        verify(assetRepository).save(mockAsset);
        assertEquals(initialSize.subtract(deductSize), mockAsset.getUsableSize());
    }

    @Test
    void increaseUsableSize_Success() {
        BigDecimal increaseSize = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));
        when(assetRepository.save(any())).thenReturn(mockAsset);

        assetService.increaseUsableSize(customerId, increaseSize, symbol);

        verify(assetRepository).save(mockAsset);
        assertEquals(initialSize.add(increaseSize), mockAsset.getUsableSize());
    }

    @Test
    void deductSize_Success() {
        BigDecimal deductSize = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));
        when(assetRepository.save(any())).thenReturn(mockAsset);

        assetService.deductSize(customerId, deductSize, symbol);

        verify(assetRepository).save(mockAsset);
        assertEquals(initialSize.subtract(deductSize), mockAsset.getSize());
    }

    @Test
    void increaseSize_Success() {
        BigDecimal increaseSize = BigDecimal.valueOf(5);
        when(assetRepository.findByCustomerIdAndSymbol(customerId, symbol)).thenReturn(Optional.of(mockAsset));
        when(assetRepository.save(any())).thenReturn(mockAsset);

        assetService.increaseSize(customerId, increaseSize, symbol);

        verify(assetRepository).save(mockAsset);
        assertEquals(initialSize.add(increaseSize), mockAsset.getSize());
    }
}
