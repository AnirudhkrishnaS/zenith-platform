package com.zenith.storeservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateSkuRequest {

    @NotBlank(message = "SKU code is required")
    @Size(max = 64)
    private String sku;

    @Size(max = 32)
    private String upc;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal price;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
