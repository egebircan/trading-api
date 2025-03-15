package com.example.demo.api.response;

import com.example.demo.api.dto.AssetDto;
import java.util.List;

public record AssetsResponse(List<AssetDto> assets) {}
