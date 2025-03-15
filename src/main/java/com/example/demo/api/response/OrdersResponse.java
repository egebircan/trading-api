package com.example.demo.api.response;

import com.example.demo.api.dto.OrderDto;
import java.util.List;

public record OrdersResponse(List<OrderDto> orders) {}
