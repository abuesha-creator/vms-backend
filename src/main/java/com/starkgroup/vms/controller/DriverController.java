package com.starkgroup.vms.controller;

import com.starkgroup.vms.dto.ApiResponse;
import com.starkgroup.vms.entity.Driver;
import com.starkgroup.vms.entity.Vehicle;
import com.starkgroup.vms.exception.ResourceNotFoundException;
import com.starkgroup.vms.repository.DriverRepository;
import com.starkgroup.vms.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public DriverController(DriverRepository driverRepository, VehicleRepository vehicleRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        return ResponseEntity.ok(ApiResponse.ok("Drivers retrieved successfully", driverRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Driver>> createDriver(@Valid @RequestBody Driver driver) {
        if (driverRepository.existsByLicenseNumberIgnoreCase(driver.getLicenseNumber().trim())) {
            throw new IllegalArgumentException("Driver with this license number already exists!");
        }
        driver.setLicenseNumber(driver.getLicenseNumber().trim().toUpperCase());
        Driver saved = driverRepository.save(driver);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Driver created successfully", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> updateDriver(@PathVariable Long id, @Valid @RequestBody Driver updated) {
        Driver existing = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setStatus(updated.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Driver updated successfully", driverRepository.save(existing)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        Driver existing = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        driverRepository.delete(existing);
        return ResponseEntity.noContent().build();
    }

    // Assign a Vehicle to a Driver
    @PutMapping("/{driverId}/assign/{vehicleId}")
    public ResponseEntity<ApiResponse<Driver>> assignVehicle(
            @PathVariable Long driverId, @PathVariable Long vehicleId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (driverRepository.existsByAssignedVehicleId(vehicleId)) {
            throw new IllegalArgumentException("This vehicle is already assigned to another driver!");
        }

        driver.setAssignedVehicle(vehicle);
        driver.setStatus("ASSIGNED");
        return ResponseEntity
                .ok(ApiResponse.ok("Vehicle assigned to driver successfully", driverRepository.save(driver)));
    }

    // Unassign Vehicle from Driver
    @PutMapping("/{driverId}/unassign")
    public ResponseEntity<ApiResponse<Driver>> unassignVehicle(@PathVariable Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        driver.setAssignedVehicle(null);
        driver.setStatus("AVAILABLE");
        return ResponseEntity.ok(ApiResponse.ok("Vehicle unassigned successfully", driverRepository.save(driver)));
    }
}
