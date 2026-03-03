package com.zenith.storeservice.exception;

/**
 * Thrown when a store is requested by id but does not exist. Used for GET/PUT/DELETE
 * by id in later steps; mapped to 404 by GlobalExceptionHandler.
 */
public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(String message) {
        super(message);
    }

    public StoreNotFoundException(Long storeId) {
        super("Store not found: " + storeId);
    }
}
