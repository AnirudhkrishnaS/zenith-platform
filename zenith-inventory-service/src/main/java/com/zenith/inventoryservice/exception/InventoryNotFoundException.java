package com.zenith.inventoryservice.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(Long skuId, Long storeId) {
        super("No inventory record for SKU " + skuId + " in store " + storeId);
    }
}
