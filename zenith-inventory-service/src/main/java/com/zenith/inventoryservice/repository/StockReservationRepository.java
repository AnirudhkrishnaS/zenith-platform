package com.zenith.inventoryservice.repository;

import com.zenith.inventoryservice.enums.ReservationStatus;
import com.zenith.inventoryservice.entity.StockReservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByOrderIdAndStatus(String orderId, ReservationStatus status);
}
