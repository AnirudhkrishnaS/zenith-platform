package com.zenith.storeservice.service;

import com.zenith.storeservice.dto.StoreResponse;
import com.zenith.storeservice.entity.Store;
import com.zenith.storeservice.repository.StoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for stores. List operations are read-only and safe for
 * concurrent use; write operations (to be added in later steps) will enforce
 * ownership and validation.
 */
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * Returns all stores. Used for browse (customers) and admin. In later steps
     * we will add filtering (e.g. by owner for "my stores", by location for "nearby").
     */
    @Transactional(readOnly = true)
    public List<StoreResponse> listAll() {
        return storeRepository.findAll().stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }
}
