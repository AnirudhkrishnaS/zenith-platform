package com.zenith.storeservice.service;

import com.zenith.storeservice.dto.CreateProductRequest;
import com.zenith.storeservice.dto.ProductResponse;
import com.zenith.storeservice.dto.UpdateProductRequest;
import com.zenith.storeservice.entity.Product;
import com.zenith.storeservice.entity.Store;
import com.zenith.storeservice.exception.ForbiddenException;
import com.zenith.storeservice.exception.ProductNotFoundException;
import com.zenith.storeservice.exception.StoreNotFoundException;
import com.zenith.storeservice.repository.ProductRepository;
import com.zenith.storeservice.repository.StoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public ProductService(ProductRepository productRepository, StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    private void ensureStoreOwner(Long storeId, Long ownerId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));
        if (!store.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Not the store owner");
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listByStoreId(Long storeId) {
        return productRepository.findByStoreIdOrderByName(storeId).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(Long storeId, Long ownerId, CreateProductRequest request) {
        ensureStoreOwner(storeId, ownerId);
        Product product = new Product();
        product.setStoreId(storeId);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        product.setPrice(request.getPrice());
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long productId, Long storeId, Long ownerId, UpdateProductRequest request) {
        ensureStoreOwner(storeId, ownerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (!product.getStoreId().equals(storeId)) {
            throw new ProductNotFoundException(productId);
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public void delete(Long productId, Long storeId, Long ownerId) {
        ensureStoreOwner(storeId, ownerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (!product.getStoreId().equals(storeId)) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.delete(product);
    }
}
