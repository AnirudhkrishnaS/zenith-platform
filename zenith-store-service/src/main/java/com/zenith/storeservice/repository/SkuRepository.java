package com.zenith.storeservice.repository;

import com.zenith.storeservice.entity.Sku;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkuRepository extends JpaRepository<Sku, Long> {

    Optional<Sku> findByStoreIdAndSkuCode(Long storeId, String skuCode);

    Optional<Sku> findByStoreIdAndUpc(Long storeId, String upc);

    List<Sku> findByProductIdOrderBySkuCode(Long productId);
}
