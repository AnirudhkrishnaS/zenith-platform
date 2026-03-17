package com.zenith.orderservice.controller;

import com.zenith.orderservice.dto.CancelOrderRequest;
import com.zenith.orderservice.dto.OrderResponse;
import com.zenith.orderservice.dto.PlaceOrderRequest;
import com.zenith.orderservice.dto.RejectOrderRequest;
import com.zenith.orderservice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String CUSTOMER = "CUSTOMER";
    private static final String BUSINESS_OWNER = "BUSINESS_OWNER";

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

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "403", description = "Not your order")
    public ResponseEntity<?> getOrder(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        return ResponseEntity.ok(orderService.getById(id, userId, userType));
    }

    @GetMapping("/mine")
    @Operation(summary = "Get my orders (customer)")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<?> getMyOrders(
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get orders for a store (store owner)")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> getStoreOrders(
            @PathVariable Long storeId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only store owners can view store orders"));
        }
        return ResponseEntity.ok(orderService.getStoreOrders(storeId, userId));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accept order (store owner)")
    @ApiResponse(responseCode = "200", description = "Order accepted")
    @ApiResponse(responseCode = "409", description = "Invalid status transition")
    public ResponseEntity<?> acceptOrder(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only store owners can accept orders"));
        }
        return ResponseEntity.ok(orderService.acceptOrder(id, userId));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject order (store owner)")
    @ApiResponse(responseCode = "200", description = "Order rejected")
    @ApiResponse(responseCode = "409", description = "Invalid status transition")
    public ResponseEntity<?> rejectOrder(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType,
            @RequestBody @Valid RejectOrderRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only store owners can reject orders"));
        }
        return ResponseEntity.ok(orderService.rejectOrder(id, userId, request.getReason()));
    }

    @PutMapping("/{id}/prepare")
    @Operation(summary = "Start preparing order (store owner)")
    @ApiResponse(responseCode = "200", description = "Order is being prepared")
    @ApiResponse(responseCode = "409", description = "Invalid status transition")
    public ResponseEntity<?> prepareOrder(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only store owners can prepare orders"));
        }
        return ResponseEntity.ok(orderService.prepareOrder(id, userId));
    }

    @PutMapping("/{id}/ready")
    @Operation(summary = "Mark order ready for pickup (store owner)")
    @ApiResponse(responseCode = "200", description = "Order ready for pickup")
    @ApiResponse(responseCode = "409", description = "Invalid status transition")
    public ResponseEntity<?> readyOrder(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only store owners can mark orders ready"));
        }
        return ResponseEntity.ok(orderService.readyOrder(id, userId));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order (customer)")
    @ApiResponse(responseCode = "200", description = "Order cancelled")
    @ApiResponse(responseCode = "409", description = "Invalid status transition")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestBody(required = false) @Valid CancelOrderRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(orderService.cancelOrder(id, userId, reason));
    }
}
