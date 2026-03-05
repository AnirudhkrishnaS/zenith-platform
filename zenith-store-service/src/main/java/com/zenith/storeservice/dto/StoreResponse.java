package com.zenith.storeservice.dto;

import com.zenith.storeservice.entity.Store;

import java.time.Instant;

public class StoreResponse {

    private Long id;
    private Long ownerId;
    private String name;
    private String address;
    private String phone;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public StoreResponse() {
    }

    public StoreResponse(Long id, Long ownerId, String name, String address, String phone, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getOwnerId(),
                store.getName(),
                store.getAddress(),
                store.getPhone(),
                store.getDescription(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
