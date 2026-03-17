package com.zenith.inventoryservice.repository;

import com.zenith.inventoryservice.entity.StockMovement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findBySkuIdAndStoreIdOrderByCreatedAtDesc(Long skuId, Long storeId);
}
