package com.zenith.storeservice.exception;

public class SkuNotFoundException extends RuntimeException {

    public SkuNotFoundException(Long skuId) {
        super("SKU not found: " + skuId);
    }

    public SkuNotFoundException(String message) {
        super(message);
    }
}
