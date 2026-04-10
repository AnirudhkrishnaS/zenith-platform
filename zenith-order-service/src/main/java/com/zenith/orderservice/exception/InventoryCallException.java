package com.zenith.orderservice.exception;

import org.springframework.http.HttpStatusCode;

public class InventoryCallException extends RuntimeException {

    public InventoryCallException(String operation, String orderId, HttpStatusCode statusCode) {
        super("Inventory " + operation + " failed for order " + orderId + ": " + statusCode);
    }
}
