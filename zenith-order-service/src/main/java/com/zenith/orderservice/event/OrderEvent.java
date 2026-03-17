package com.zenith.orderservice.event;

import com.zenith.orderservice.entity.Order;
import com.zenith.orderservice.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable snapshot of an order state change, published to the event bus
 * after every status transition. Consumers (notification, inventory, analytics)
 * use this without needing to call back to the Order Service.
 */
public record OrderEvent(
        String eventType,
        Long orderId,
        Long customerId,
        Long storeId,
        OrderStatus status,
        OrderStatus previousStatus,
        BigDecimal total,
        Instant timestamp
) {

    public static OrderEvent from(String eventType, Order order, OrderStatus previousStatus) {
        return new OrderEvent(
                eventType,
                order.getId(),
                order.getCustomerId(),
                order.getStoreId(),
                order.getStatus(),
                previousStatus,
                order.getTotal(),
                Instant.now()
        );
    }
}
