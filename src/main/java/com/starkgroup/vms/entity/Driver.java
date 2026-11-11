package com.starkgroup.vms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Driver name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "License number is required")
    @Column(unique = true, nullable = false)
    private String licenseNumber;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phone;

    private String status = "AVAILABLE"; // AVAILABLE, ASSIGNED, INACTIVE

    @OneToOne
    @JoinColumn(name = "assigned_vehicle_id", unique = true)
    private Vehicle assignedVehicle;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(Vehicle assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }
}
