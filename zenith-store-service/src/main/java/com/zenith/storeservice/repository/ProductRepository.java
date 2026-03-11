package com.zenith.storeservice.repository;

import com.zenith.storeservice.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStoreIdOrderByName(Long storeId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.skus WHERE p.storeId = :storeId ORDER BY p.name")
    List<Product> findByStoreIdOrderByNameWithSkus(@Param("storeId") Long storeId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.skus WHERE p.id = :id")
    Optional<Product> findByIdWithSkus(@Param("id") Long id);
}
