package com.zenith.inventoryservice.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long skuId, int requested, int available) {
        super("Insufficient stock for SKU " + skuId + ": requested=" + requested + ", available=" + available);
    }
}
