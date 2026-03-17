package com.zenith.orderservice.exception;

import com.zenith.orderservice.enums.OrderStatus;

public class IllegalStatusTransitionException extends RuntimeException {

    public IllegalStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Cannot transition from " + from + " to " + to);
    }
}
