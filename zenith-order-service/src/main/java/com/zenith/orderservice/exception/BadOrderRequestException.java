package com.zenith.orderservice.exception;

public class BadOrderRequestException extends RuntimeException {

    public BadOrderRequestException(String message) {
        super(message);
    }
}
