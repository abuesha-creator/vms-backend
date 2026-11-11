package com.starkgroup.vms.dto.vehicle;

import com.starkgroup.vms.entity.Vehicle;

/**
 * Response DTO for a Vehicle resource.
 * Constructed from a {@link Vehicle} entity via the static factory method.
 */
public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private String company;
    private String type;
    private String brand;
    private String model;
    private String status;

    // -------------------------------------------------------------------------
    // Static factory
    // -------------------------------------------------------------------------

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.id            = vehicle.getId();
        response.vehicleNumber = vehicle.getVehicleNumber();
        response.company       = vehicle.getCompany();
        response.type          = vehicle.getType();
        response.brand         = vehicle.getBrand();
        response.model         = vehicle.getModel();
        response.status        = vehicle.getStatus();
        return response;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public VehicleResponse() {
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

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

