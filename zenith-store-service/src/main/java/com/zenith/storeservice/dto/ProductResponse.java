package com.zenith.storeservice.dto;

import com.zenith.storeservice.entity.Product;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponse {

    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private List<SkuResponse> skus;
    private Instant createdAt;
    private Instant updatedAt;

    public ProductResponse() {
    }

    public static ProductResponse from(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setStoreId(p.getStoreId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setSkus(p.getSkus().stream().map(SkuResponse::from).collect(Collectors.toList()));
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }

    public static ProductResponse fromWithoutSkus(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setStoreId(p.getStoreId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setSkus(List.of());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<SkuResponse> getSkus() { return skus; }
    public void setSkus(List<SkuResponse> skus) { this.skus = skus; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
