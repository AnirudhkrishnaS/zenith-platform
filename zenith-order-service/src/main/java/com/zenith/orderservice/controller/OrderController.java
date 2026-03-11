package com.zenith.orderservice.controller;

import com.zenith.orderservice.dto.OrderResponse;
import com.zenith.orderservice.dto.PlaceOrderRequest;
import com.zenith.orderservice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String CUSTOMER = "CUSTOMER";

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place order")
    @ApiResponse(responseCode = "201", description = "Order placed")
    @ApiResponse(responseCode = "400", description = "Invalid store or product")
    @ApiResponse(responseCode = "403", description = "Only customers can place orders")
    public ResponseEntity<?> placeOrder(
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType,
            @RequestBody @Valid PlaceOrderRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        if (!CUSTOMER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only customers can place orders"));
        }
        OrderResponse order = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
