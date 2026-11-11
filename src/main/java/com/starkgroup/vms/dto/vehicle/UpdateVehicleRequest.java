package com.starkgroup.vms.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing Vehicle.
 * vehicleNumber is intentionally excluded — it is immutable after creation.
 */
public class UpdateVehicleRequest {

    @NotBlank(message = "Company must not be blank")
    @Size(max = 100, message = "Company must not exceed 100 characters")
    private String company;

    @NotBlank(message = "Type must not be blank")
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;

    @NotBlank(message = "Brand must not be blank")
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @NotBlank(message = "Model must not be blank")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;

    @NotBlank(message = "Status must not be blank")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public UpdateVehicleRequest() {
    }

    public UpdateVehicleRequest(String company, String type, String brand, String model, String status) {
        this.company = company;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.status = status;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

