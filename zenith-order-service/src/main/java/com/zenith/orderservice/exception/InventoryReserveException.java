package com.zenith.orderservice.exception;

import org.springframework.http.HttpStatusCode;

public class InventoryReserveException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public InventoryReserveException(HttpStatusCode statusCode, String message) {
        super(message + " (inventory: " + statusCode + ")");
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
