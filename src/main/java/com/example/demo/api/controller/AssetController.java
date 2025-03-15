package com.example.demo.api.controller;

import com.example.demo.api.response.AssetsResponse;
import com.example.demo.service.trade.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.user.id")
    public AssetsResponse listAssets(@RequestParam Long customerId) {
        return assetService.listAssets(customerId);
    }
}
