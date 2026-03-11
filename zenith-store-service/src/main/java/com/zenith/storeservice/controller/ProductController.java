package com.zenith.storeservice.controller;

import com.zenith.storeservice.dto.*;
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
@Tag(name = "Products & SKUs")
public class ProductController {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String BUSINESS_OWNER = "BUSINESS_OWNER";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List products of a store (with SKUs)")
    public ResponseEntity<List<ProductResponse>> listProducts(@PathVariable Long storeId) {
        return ResponseEntity.ok(productService.listByStoreId(storeId));
    }


    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get sellable by SKU (store-scoped); returns SKU with product name")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    public ResponseEntity<SkuResponse> getBySku(
            @PathVariable Long storeId,
            @PathVariable String sku) {
        return ResponseEntity.ok(productService.getBySku(storeId, sku));
    }

    @GetMapping("/upc/{upc}")
    @Operation(summary = "Get sellable by UPC / barcode (store-scoped)")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    public ResponseEntity<SkuResponse> getByUpc(
            @PathVariable Long storeId,
            @PathVariable String upc) {
        return ResponseEntity.ok(productService.getByUpc(storeId, upc));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by id (with SKUs)")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }

    @PostMapping
    @Operation(summary = "Create product (logical); add SKUs via POST .../skus")
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

    @PostMapping("/{productId}/skus")
    @Operation(summary = "Add a SKU (variant) to a product")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> createSku(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestHeader(value = HEADER_USER_TYPE, required = false) String userType,
            @RequestBody @Valid CreateSkuRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        if (!BUSINESS_OWNER.equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(java.util.Map.of("error", "Only business owners can add SKUs"));
        }
        SkuResponse created = productService.createSku(storeId, productId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product (name, description)")
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

    @PutMapping("/{productId}/skus/{skuId}")
    @Operation(summary = "Update a SKU")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> updateSku(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId,
            @RequestBody @Valid UpdateSkuRequest request) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        return ResponseEntity.ok(productService.updateSku(storeId, productId, skuId, userId, request));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product (and all its SKUs)")
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

    @DeleteMapping("/{productId}/skus/{skuId}")
    @Operation(summary = "Delete a SKU")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @ApiResponse(responseCode = "404", description = "SKU not found")
    @ApiResponse(responseCode = "403", description = "Not the store owner")
    public ResponseEntity<?> deleteSku(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Missing X-User-Id"));
        }
        productService.deleteSku(storeId, productId, skuId, userId);
        return ResponseEntity.noContent().build();
    }
}
