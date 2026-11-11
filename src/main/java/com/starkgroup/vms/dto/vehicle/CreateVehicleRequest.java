package com.starkgroup.vms.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateVehicleRequest {

    @NotBlank(message = "Vehicle number is required")
    @Size(max = 50, message = "Vehicle number must not exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "Vehicle number may only contain letters, digits, and hyphens")
    private String vehicleNumber;

    @NotBlank(message = "Company is required")
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String company;

    @NotBlank(message = "Type is required")
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @NotBlank(message = "Status is required")
    @Size(max = 30, message = "Status must not exceed 30 characters")
    private String status;

    public CreateVehicleRequest() {
    }

    public CreateVehicleRequest(String vehicleNumber, String company, String type,
                                String brand, String model, String status) {
        this.vehicleNumber = vehicleNumber;
        this.company = company;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.status = status;
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

