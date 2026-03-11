package com.zenith.orderservice.dto;

import com.zenith.orderservice.entity.Order;
import com.zenith.orderservice.entity.OrderStatus;
import com.zenith.orderservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {

    private Long id;
    private Long customerId;
    private Long storeId;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal total;
    private List<OrderItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setCustomerId(order.getCustomerId());
        r.setStoreId(order.getStoreId());
        r.setStatus(order.getStatus());
        r.setPaymentStatus(order.getPaymentStatus());
        r.setTotal(order.getTotal());
        r.setItems(order.getItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList()));
        r.setCreatedAt(order.getCreatedAt());
        r.setUpdatedAt(order.getUpdatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
