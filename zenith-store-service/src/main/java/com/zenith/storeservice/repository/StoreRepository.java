package com.zenith.storeservice.repository;

import com.zenith.storeservice.entity.Store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
