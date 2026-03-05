package com.zenith.storeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateStoreRequest {

    @NotBlank(message = "Store name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String address;

    @Size(max = 50)
    private String phone;

    @Size(max = 2000)
    private String description;

    public CreateStoreRequest() {
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
}
