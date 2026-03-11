package com.zenith.orderservice.repository;

import com.zenith.orderservice.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByStoreIdOrderByCreatedAtDesc(Long storeId);
}
