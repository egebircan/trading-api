package com.example.demo.api.controller;

import com.example.demo.api.dto.OrderDto;
import com.example.demo.api.request.CreateOrderRequest;
import com.example.demo.api.response.OrdersResponse;
import com.example.demo.service.trade.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or #request.customerId == authentication.principal.user.id")
    public OrderDto createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.user.id")
    public OrdersResponse listOrders(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return orderService.listOrders(customerId, startDate, endDate);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @orderService.getOrder(#orderId).customerId == authentication.principal.user.id")
    public void cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }

    @PatchMapping("/{orderId}/match")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto matchOrder(@PathVariable Long orderId) {
        return orderService.matchOrder(orderId);
    }
}
