package com.zenith.storeservice.service;

import com.zenith.storeservice.dto.CreateStoreRequest;
import com.zenith.storeservice.dto.StoreResponse;
import com.zenith.storeservice.dto.UpdateStoreRequest;
import com.zenith.storeservice.entity.Store;
import com.zenith.storeservice.exception.ForbiddenException;
import com.zenith.storeservice.exception.StoreNotFoundException;
import com.zenith.storeservice.repository.StoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> listAll() {
        return storeRepository.findAll().stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> findByOwnerId(Long ownerId) {
        return storeRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreResponse create(Long ownerId, CreateStoreRequest request) {
        Store store = new Store();
        store.setOwnerId(ownerId);
        store.setName(request.getName().trim());
        store.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        store.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        store.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        Store saved = storeRepository.save(store);
        return StoreResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public StoreResponse getById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
        return StoreResponse.from(store);
    }

    @Transactional
    public StoreResponse update(Long id, Long ownerId, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
        if (!store.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Not the store owner");
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            store.setName(request.getName().trim());
        }
        if (request.getAddress() != null) {
            store.setAddress(request.getAddress().trim());
        }
        if (request.getPhone() != null) {
            store.setPhone(request.getPhone().trim());
        }
        if (request.getDescription() != null) {
            store.setDescription(request.getDescription().trim());
        }
        Store saved = storeRepository.save(store);
        return StoreResponse.from(saved);
    }
}
