package com.zenith.inventoryservice.dto;

import com.zenith.inventoryservice.entity.InventoryRecord;

public class InventoryResponse {

    private Long skuId;
    private Long storeId;
    private Integer physicalStock;
    private Integer reservedStock;
    private Integer availableStock;

    public static InventoryResponse from(InventoryRecord r) {
        InventoryResponse resp = new InventoryResponse();
        resp.setSkuId(r.getSkuId());
        resp.setStoreId(r.getStoreId());
        resp.setPhysicalStock(r.getPhysicalStock());
        resp.setReservedStock(r.getReservedStock());
        resp.setAvailableStock(r.getAvailableStock());
        return resp;
    }

    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Integer getPhysicalStock() { return physicalStock; }
    public void setPhysicalStock(Integer physicalStock) { this.physicalStock = physicalStock; }
    public Integer getReservedStock() { return reservedStock; }
    public void setReservedStock(Integer reservedStock) { this.reservedStock = reservedStock; }
    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
}
