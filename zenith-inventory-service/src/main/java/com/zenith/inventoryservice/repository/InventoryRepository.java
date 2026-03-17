package com.zenith.inventoryservice.repository;

import com.zenith.inventoryservice.entity.InventoryRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryRecord, Long> {

    Optional<InventoryRecord> findBySkuIdAndStoreId(Long skuId, Long storeId);

    List<InventoryRecord> findByStoreId(Long storeId);

    /**
     * Atomic reserve: increments reserved_stock ONLY if enough available.
     * Returns number of rows updated (1 = success, 0 = insufficient stock).
     * This is a single SQL statement -- no race condition possible.
     */
    @Modifying
    @Query("UPDATE InventoryRecord i SET i.reservedStock = i.reservedStock + :qty, i.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE i.skuId = :skuId AND i.storeId = :storeId " +
           "AND (i.physicalStock - i.reservedStock) >= :qty")
    int reserve(@Param("skuId") Long skuId, @Param("storeId") Long storeId, @Param("qty") int qty);

    /**
     * Atomic commit: decreases both physical and reserved (items picked from shelf).
     */
    @Modifying
    @Query("UPDATE InventoryRecord i SET i.physicalStock = i.physicalStock - :qty, " +
           "i.reservedStock = i.reservedStock - :qty, i.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE i.skuId = :skuId AND i.storeId = :storeId AND i.reservedStock >= :qty")
    int commit(@Param("skuId") Long skuId, @Param("storeId") Long storeId, @Param("qty") int qty);

    /**
     * Atomic release: decreases reserved (order cancelled before pick).
     */
    @Modifying
    @Query("UPDATE InventoryRecord i SET i.reservedStock = i.reservedStock - :qty, i.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE i.skuId = :skuId AND i.storeId = :storeId AND i.reservedStock >= :qty")
    int release(@Param("skuId") Long skuId, @Param("storeId") Long storeId, @Param("qty") int qty);
}
