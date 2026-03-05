package com.zenith.storeservice.controller;

import com.zenith.storeservice.dto.CreateProductRequest;
import com.zenith.storeservice.dto.ProductResponse;
import com.zenith.storeservice.dto.UpdateProductRequest;
import com.zenith.storeservice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/products")
@Tag(name = "Products")
public class ProductController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String BUSINESS_OWNER = "BUSINESS_OWNER";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List products of a store")
    public ResponseEntity<List<ProductResponse>> listProducts(@PathVariable Long storeId) {
        return ResponseEntity.ok(productService.listByStoreId(storeId));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by id")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }

    @PostMapping
    @Operation(summary = "Create product")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "401", description = "Missing user context")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> createProduct(
            @PathVariable Long storeId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType,
            @RequestBody @Valid CreateProductRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(java.util.Map.of("error", "Only business owners can add products"));
        }
        ProductResponse created = productService.create(storeId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestBody @Valid UpdateProductRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        return ResponseEntity.ok(productService.update(productId, storeId, userId, request));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        productService.delete(productId, storeId, userId);
        return ResponseEntity.noContent().build();
    }
}
