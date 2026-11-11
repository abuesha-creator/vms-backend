package com.starkgroup.vms.service;

import com.starkgroup.vms.dto.vehicle.CreateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.UpdateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.VehicleResponse;

import java.util.List;

/**
 * Service contract for Vehicle business operations.
 * All business rules are enforced here; persistence is delegated to the repository.
 */
public interface VehicleService {

    /**
     * Create a new vehicle.
     * Throws {@link com.starkgroup.vms.exception.DuplicateVehicleNumberException}
     * if the vehicleNumber already exists (case-insensitive).
     */
    VehicleResponse create(CreateVehicleRequest request);

    /**
     * Retrieve a vehicle by its ID.
     * Throws {@link com.starkgroup.vms.exception.ResourceNotFoundException} when not found.
     */
    VehicleResponse getById(Long id);

    /**
     * Retrieve all vehicles.
     */
    List<VehicleResponse> getAll();

    /**
     * Update an existing vehicle's mutable fields.
     * vehicleNumber is immutable and cannot be changed via this operation.
     * Throws {@link com.starkgroup.vms.exception.ResourceNotFoundException} when not found.
     */
    VehicleResponse update(Long id, UpdateVehicleRequest request);

    /**
     * Delete a vehicle by its ID.
     * Throws {@link com.starkgroup.vms.exception.ResourceNotFoundException} when not found.
     */
    void delete(Long id);
}

