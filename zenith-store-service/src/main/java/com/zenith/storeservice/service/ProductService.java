package com.zenith.storeservice.service;

import com.zenith.storeservice.dto.*;
import com.zenith.storeservice.entity.Product;
import com.zenith.storeservice.entity.Sku;
import com.zenith.storeservice.entity.Store;
import com.zenith.storeservice.exception.ForbiddenException;
import com.zenith.storeservice.exception.ProductNotFoundException;
import com.zenith.storeservice.exception.SkuNotFoundException;
import com.zenith.storeservice.exception.StoreNotFoundException;
import com.zenith.storeservice.repository.ProductRepository;
import com.zenith.storeservice.repository.SkuRepository;
import com.zenith.storeservice.repository.StoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    private final StoreRepository storeRepository;

    public ProductService(ProductRepository productRepository, SkuRepository skuRepository, StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
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
        return productRepository.findByStoreIdOrderByNameWithSkus(storeId).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findByIdWithSkus(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public SkuResponse getBySku(Long storeId, String sku) {
        Sku s = skuRepository.findByStoreIdAndSkuCode(storeId, sku)
                .orElseThrow(() -> new SkuNotFoundException("SKU not found: " + sku + " in store " + storeId));
        return SkuResponse.from(s);
    }

    @Transactional(readOnly = true)
    public SkuResponse getByUpc(Long storeId, String upc) {
        Sku s = skuRepository.findByStoreIdAndUpc(storeId, upc)
                .orElseThrow(() -> new SkuNotFoundException("SKU not found for upc: " + upc + " in store " + storeId));
        return SkuResponse.from(s);
    }

    @Transactional
    public ProductResponse create(Long storeId, Long ownerId, CreateProductRequest request) {
        ensureStoreOwner(storeId, ownerId);
        Product product = new Product();
        product.setStoreId(storeId);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public SkuResponse createSku(Long storeId, Long productId, Long ownerId, CreateSkuRequest request) {
        ensureStoreOwner(storeId, ownerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (!product.getStoreId().equals(storeId)) {
            throw new ProductNotFoundException(productId);
        }
        String skuCode = request.getSku() != null ? request.getSku().trim() : null;
        if (skuCode == null || skuCode.isBlank()) {
            throw new IllegalArgumentException("SKU code is required");
        }
        if (skuRepository.findByStoreIdAndSkuCode(storeId, skuCode).isPresent()) {
            throw new IllegalArgumentException("SKU '" + skuCode + "' already exists in this store");
        }
        String upc = request.getUpc() != null && !request.getUpc().isBlank() ? request.getUpc().trim() : null;
        if (upc != null && skuRepository.findByStoreIdAndUpc(storeId, upc).isPresent()) {
            throw new IllegalArgumentException("UPC '" + upc + "' already exists in this store");
        }
        Sku sku = new Sku();
        sku.setProduct(product);
        sku.setStoreId(storeId);
        sku.setSkuCode(skuCode);
        sku.setUpc(upc);
        sku.setPrice(request.getPrice());
        Sku saved = skuRepository.save(sku);
        return SkuResponse.from(saved);
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
        Product saved = productRepository.save(product);
        return ProductResponse.from(productRepository.findByIdWithSkus(saved.getId()).orElse(saved));
    }

    @Transactional
    public SkuResponse updateSku(Long storeId, Long productId, Long skuId, Long ownerId, UpdateSkuRequest request) {
        ensureStoreOwner(storeId, ownerId);
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new SkuNotFoundException(skuId));
        if (!sku.getStoreId().equals(storeId) || !sku.getProduct().getId().equals(productId)) {
            throw new SkuNotFoundException(skuId);
        }
        if (request.getSku() != null && !request.getSku().isBlank()) {
            String skuCode = request.getSku().trim();
            Optional<Sku> existing = skuRepository.findByStoreIdAndSkuCode(storeId, skuCode);
            if (existing.isPresent() && !existing.get().getId().equals(skuId)) {
                throw new IllegalArgumentException("SKU '" + skuCode + "' already exists in this store");
            }
            sku.setSkuCode(skuCode);
        }
        if (request.getUpc() != null) {
            String upc = request.getUpc().isBlank() ? null : request.getUpc().trim();
            if (upc != null) {
                Optional<Sku> existing = skuRepository.findByStoreIdAndUpc(storeId, upc);
                if (existing.isPresent() && !existing.get().getId().equals(skuId)) {
                    throw new IllegalArgumentException("UPC '" + upc + "' already exists in this store");
                }
            }
            sku.setUpc(upc);
        }
        if (request.getPrice() != null) {
            sku.setPrice(request.getPrice());
        }
        Sku saved = skuRepository.save(sku);
        return SkuResponse.from(saved);
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

    @Transactional
    public void deleteSku(Long storeId, Long productId, Long skuId, Long ownerId) {
        ensureStoreOwner(storeId, ownerId);
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new SkuNotFoundException(skuId));
        if (!sku.getStoreId().equals(storeId) || !sku.getProduct().getId().equals(productId)) {
            throw new SkuNotFoundException(skuId);
        }
        skuRepository.delete(sku);
    }
}
