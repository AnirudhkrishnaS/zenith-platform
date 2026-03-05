package com.zenith.storeservice.exception;

public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(String message) {
        super(message);
    }

    public StoreNotFoundException(Long storeId) {
        super("Store not found: " + storeId);
    }
}
