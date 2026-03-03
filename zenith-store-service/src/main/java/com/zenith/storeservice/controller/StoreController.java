package com.zenith.storeservice.controller;

import com.zenith.storeservice.dto.StoreResponse;
import com.zenith.storeservice.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for stores. All requests are intended to go via the API Gateway
 * (e.g. /api/stores/**). Authentication and authorization will be enforced
 * at the gateway and via headers in later steps.
 */
@RestController
@RequestMapping("/api/stores")
@Tag(name = "Stores", description = "Store (business) listing and management")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    @Operation(summary = "List all stores", description = "Returns all registered stores. Used for browse and discovery. No auth required for this step.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = StoreResponse.class)))
    })
    public ResponseEntity<List<StoreResponse>> listStores() {
        List<StoreResponse> stores = storeService.listAll();
        return ResponseEntity.ok(stores);
    }
}
