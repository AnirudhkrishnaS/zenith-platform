package com.zenith.orderservice.dto;

import com.zenith.orderservice.entity.OrderItem;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long productId;
    private String sku;
    private String upc;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse r = new OrderItemResponse();
        r.setProductId(item.getProductId());
        r.setSku(item.getSku());
        r.setUpc(item.getUpc());
        r.setProductName(item.getProductName());
        r.setQuantity(item.getQuantity());
        r.setPrice(item.getPrice());
        return r;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
