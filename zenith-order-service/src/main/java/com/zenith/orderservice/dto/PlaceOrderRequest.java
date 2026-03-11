package com.zenith.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PlaceOrderRequest {

    @NotNull(message = "Store id is required")
    private Long storeId;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<Item> items;

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item {

        private String sku;

        private String upc;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @AssertTrue(message = "Either sku or upc is required per item")
        public boolean isSkuOrUpcPresent() {
            return (sku != null && !sku.isBlank()) || (upc != null && !upc.isBlank());
        }

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getUpc() { return upc; }
        public void setUpc(String upc) { this.upc = upc; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
