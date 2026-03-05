package com.zenith.storeservice.controller;

import com.zenith.storeservice.dto.CreateStoreRequest;
import com.zenith.storeservice.dto.StoreResponse;
import com.zenith.storeservice.dto.UpdateStoreRequest;
import com.zenith.storeservice.service.StoreService;

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
@RequestMapping("/api/stores")
@Tag(name = "Stores")
public class StoreController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String BUSINESS_OWNER = "BUSINESS_OWNER";

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    @Operation(summary = "List all stores")
    public ResponseEntity<List<StoreResponse>> listStores() {
        return ResponseEntity.ok(storeService.listAll());
    }

    @GetMapping("/mine")
    @Operation(summary = "Get my stores")
    @ApiResponse(responseCode = "401", description = "Missing or invalid user context")
    public ResponseEntity<?> getMyStores(
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        return ResponseEntity.ok(storeService.findByOwnerId(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store by id")
    @ApiResponse(responseCode = "404", description = "Store not found")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create store")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "401", description = "Missing user context")
    @ApiResponse(responseCode = "403", description = "Only business owners can create stores")
    public ResponseEntity<?> createStore(
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType,
            @RequestBody @Valid CreateStoreRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(java.util.Map.of("error", "Only business owners can create stores"));
        }
        StoreResponse created = storeService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update store")
    @ApiResponse(responseCode = "404", description = "Store not found")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> updateStore(
            @PathVariable Long id,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestBody @Valid UpdateStoreRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing X-User-Id"));
        }
        return ResponseEntity.ok(storeService.update(id, userId, request));
    }
}
