package com.zenith.orderservice.enums;

import java.util.Set;

public enum OrderStatus {

    PLACED,
    ACCEPTED,
    REJECTED,
    PREPARING,
    READY_FOR_PICKUP,
    RIDER_ASSIGNED,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELIVERY_FAILED,
    RETURNED,
    CANCELLED,
    COMPLETED,
    PAYMENT_FAILED;

    private static final Set<OrderStatus> TERMINAL = Set.of(
            REJECTED, CANCELLED, COMPLETED, PAYMENT_FAILED, RETURNED
    );

    public boolean isTerminal() {
        return TERMINAL.contains(this);
    }

    public boolean canTransitionTo(OrderStatus target) {
        return allowedTransitions().contains(target);
    }

    private Set<OrderStatus> allowedTransitions() {
        return switch (this) {
            case PLACED -> Set.of(ACCEPTED, REJECTED, CANCELLED, PAYMENT_FAILED);
            case ACCEPTED -> Set.of(PREPARING, CANCELLED);
            case PREPARING -> Set.of(READY_FOR_PICKUP);
            case READY_FOR_PICKUP -> Set.of(RIDER_ASSIGNED);
            case RIDER_ASSIGNED -> Set.of(PICKED_UP);
            case PICKED_UP -> Set.of(OUT_FOR_DELIVERY);
            case OUT_FOR_DELIVERY -> Set.of(DELIVERED, DELIVERY_FAILED);
            case DELIVERED -> Set.of(COMPLETED);
            case DELIVERY_FAILED -> Set.of(RETURNED, OUT_FOR_DELIVERY);
            default -> Set.of();
        };
    }
}
