package com.zenith.orderservice.event;

/**
 * Strategy interface for publishing order lifecycle events.
 * Implementations decide where events go -- console (local dev) or EventBridge (production).
 */
public interface OrderEventPublisher {

    void publish(OrderEvent event);
}
