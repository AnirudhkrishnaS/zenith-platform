package com.zenith.storeservice.repository;

import com.zenith.storeservice.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStoreIdOrderByName(Long storeId);
}
