package com.zenith.orderservice.repository;

import com.zenith.orderservice.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT nextval('order_number_seq')", nativeQuery = true)
    long getNextOrderNumber();

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByStoreIdOrderByCreatedAtDesc(Long storeId);
}
