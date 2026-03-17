package com.zenith.inventoryservice.service;

import com.zenith.inventoryservice.dto.InventoryResponse;
import com.zenith.inventoryservice.dto.ReserveRequest;
import com.zenith.inventoryservice.dto.RestockRequest;
import com.zenith.inventoryservice.entity.*;
import com.zenith.inventoryservice.enums.MovementType;
import com.zenith.inventoryservice.enums.ReservationStatus;
import com.zenith.inventoryservice.exception.InsufficientStockException;
import com.zenith.inventoryservice.exception.InventoryNotFoundException;
import com.zenith.inventoryservice.repository.InventoryRepository;
import com.zenith.inventoryservice.repository.StockMovementRepository;
import com.zenith.inventoryservice.repository.StockReservationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepo;
    private final StockReservationRepository reservationRepo;
    private final StockMovementRepository movementRepo;

    public InventoryService(InventoryRepository inventoryRepo,
                            StockReservationRepository reservationRepo,
                            StockMovementRepository movementRepo) {
        this.inventoryRepo = inventoryRepo;
        this.reservationRepo = reservationRepo;
        this.movementRepo = movementRepo;
    }

    /**
     * All-or-nothing reservation for an order.
     * If any item can't be reserved, the entire transaction rolls back --
     * no partial holds.
     */
    @Transactional
    public void reserve(ReserveRequest request) {
        Long storeId = request.getStoreId();
        Long orderId = request.getOrderId();

        for (ReserveRequest.Item item : request.getItems()) {
            int updated = inventoryRepo.reserve(item.getSkuId(), storeId, item.getQuantity());
            if (updated == 0) {
                int available = inventoryRepo.findBySkuIdAndStoreId(item.getSkuId(), storeId)
                        .map(InventoryRecord::getAvailableStock)
                        .orElse(0);
                throw new InsufficientStockException(item.getSkuId(), item.getQuantity(), available);
            }

            StockReservation reservation = new StockReservation();
            reservation.setOrderId(orderId);
            reservation.setSkuId(item.getSkuId());
            reservation.setStoreId(storeId);
            reservation.setQuantity(item.getQuantity());
            reservation.setStatus(ReservationStatus.RESERVED);
            reservationRepo.save(reservation);

            logMovement(item.getSkuId(), storeId, MovementType.RESERVATION, item.getQuantity(), "order:" + orderId);
        }

        log.info("Reserved stock for order {} ({} items)", orderId, request.getItems().size());
    }

    /**
     * Commit = items are physically picked from shelf. Physical stock goes down,
     * reserved stock goes down, reservation status -> COMMITTED.
     */
    @Transactional
    public void commitReservation(Long orderId) {
        List<StockReservation> reservations =
                reservationRepo.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        if (reservations.isEmpty()) {
            log.warn("No active reservations found for order {}", orderId);
            return;
        }

        for (StockReservation r : reservations) {
            int updated = inventoryRepo.commit(r.getSkuId(), r.getStoreId(), r.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException("Failed to commit SKU " + r.getSkuId() + " for order " + orderId);
            }
            r.setStatus(ReservationStatus.COMMITTED);
            reservationRepo.save(r);

            logMovement(r.getSkuId(), r.getStoreId(), MovementType.COMMIT, r.getQuantity(), "order:" + orderId);
        }

        log.info("Committed stock for order {}", orderId);
    }

    /**
     * Release = order cancelled before pick. Reserved stock returns to available pool.
     */
    @Transactional
    public void releaseReservation(Long orderId) {
        List<StockReservation> reservations =
                reservationRepo.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        if (reservations.isEmpty()) {
            log.warn("No active reservations to release for order {}", orderId);
            return;
        }

        for (StockReservation r : reservations) {
            int updated = inventoryRepo.release(r.getSkuId(), r.getStoreId(), r.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException("Failed to release SKU " + r.getSkuId() + " for order " + orderId);
            }
            r.setStatus(ReservationStatus.RELEASED);
            reservationRepo.save(r);

            logMovement(r.getSkuId(), r.getStoreId(), MovementType.RELEASE, r.getQuantity(), "order:" + orderId);
        }

        log.info("Released stock for order {}", orderId);
    }

    /**
     * Restock (inbound). Store owner adds stock after a new shipment.
     * Creates inventory record if this is the first time the SKU is stocked at this store.
     */
    @Transactional
    public InventoryResponse restock(RestockRequest request) {
        InventoryRecord record = inventoryRepo
                .findBySkuIdAndStoreId(request.getSkuId(), request.getStoreId())
                .orElseGet(() -> {
                    InventoryRecord fresh = new InventoryRecord();
                    fresh.setSkuId(request.getSkuId());
                    fresh.setStoreId(request.getStoreId());
                    return fresh;
                });

        record.setPhysicalStock(record.getPhysicalStock() + request.getQuantity());
        inventoryRepo.save(record);

        String ref = request.getReason() != null ? request.getReason() : "restock";
        logMovement(request.getSkuId(), request.getStoreId(), MovementType.INBOUND, request.getQuantity(), ref);
        log.info("Restocked SKU {} at store {} by {} units", request.getSkuId(), request.getStoreId(), request.getQuantity());
        return InventoryResponse.from(record);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStock(Long skuId, Long storeId) {
        InventoryRecord record = inventoryRepo.findBySkuIdAndStoreId(skuId, storeId)
                .orElseThrow(() -> new InventoryNotFoundException(skuId, storeId));
        return InventoryResponse.from(record);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getStoreStock(Long storeId) {
        return inventoryRepo.findByStoreId(storeId).stream()
                .map(InventoryResponse::from)
                .toList();
    }

    private void logMovement(Long skuId, Long storeId, MovementType type, int qty, String ref) {
        StockMovement m = new StockMovement();
        m.setSkuId(skuId);
        m.setStoreId(storeId);
        m.setMovementType(type);
        m.setQuantity(qty);
        m.setReferenceId(ref);
        movementRepo.save(m);
    }
}
