package com.zenith.inventoryservice.controller;

import com.zenith.inventoryservice.dto.InventoryResponse;
import com.zenith.inventoryservice.dto.ReserveRequest;
import com.zenith.inventoryservice.dto.RestockRequest;
import com.zenith.inventoryservice.service.InventoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Stock queries

    @GetMapping("/{skuId}/store/{storeId}")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable Long skuId,
                                                      @PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.getStock(skuId, storeId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<InventoryResponse>> getStoreStock(@PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.getStoreStock(storeId));
    }

    //  Restock (store owner adds inventory)

    @PostMapping("/restock")
    public ResponseEntity<InventoryResponse> restock(@Valid @RequestBody RestockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.restock(request));
    }

    //  Reservation lifecycle (called by Order Service)

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, String>> reserve(@Valid @RequestBody ReserveRequest request) {
        inventoryService.reserve(request);
        return ResponseEntity.ok(Map.of("status", "reserved"));
    }

    @PostMapping("/commit/{orderId}")
    public ResponseEntity<Map<String, String>> commit(@PathVariable Long orderId) {
        inventoryService.commitReservation(orderId);
        return ResponseEntity.ok(Map.of("status", "committed"));
    }

    @PostMapping("/release/{orderId}")
    public ResponseEntity<Map<String, String>> release(@PathVariable Long orderId) {
        inventoryService.releaseReservation(orderId);
        return ResponseEntity.ok(Map.of("status", "released"));
    }
}
