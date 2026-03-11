package com.zenith.storeservice.dto;

import com.zenith.storeservice.entity.Sku;

import java.math.BigDecimal;
import java.time.Instant;

/** Response for a single SKU; includes productName when returned from get-by-sku/upc for order service. */
public class SkuResponse {

    private Long id;
    private Long productId;
    private Long storeId;
    private String sku;
    private String upc;
    private String productName;
    private BigDecimal price;
    private Instant createdAt;
    private Instant updatedAt;

    public SkuResponse() {
    }

    public static SkuResponse from(Sku s) {
        SkuResponse r = new SkuResponse();
        r.setId(s.getId());
        r.setProductId(s.getProduct().getId());
        r.setStoreId(s.getStoreId());
        r.setSku(s.getSkuCode());
        r.setUpc(s.getUpc());
        r.setProductName(s.getProduct().getName());
        r.setPrice(s.getPrice());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
