package com.starkgroup.vms.service.impl;

import com.starkgroup.vms.dto.vehicle.CreateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.UpdateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.VehicleResponse;
import com.starkgroup.vms.entity.Vehicle;
import com.starkgroup.vms.exception.DuplicateVehicleNumberException;
import com.starkgroup.vms.exception.ResourceNotFoundException;
import com.starkgroup.vms.repository.VehicleRepository;
import com.starkgroup.vms.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link VehicleService}.
 * All business rules are enforced here; persistence is delegated to {@link VehicleRepository}.
 */
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Override
    public VehicleResponse create(CreateVehicleRequest request) {
        String normalizedNumber = request.getVehicleNumber().trim().toUpperCase();

        if (vehicleRepository.existsByVehicleNumberIgnoreCase(normalizedNumber)) {
            throw new DuplicateVehicleNumberException(normalizedNumber);
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(normalizedNumber);
        vehicle.setCompany(request.getCompany());
        vehicle.setType(request.getType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setStatus(request.getStatus());

        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(saved);
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        Vehicle vehicle = findByIdOrThrow(id);
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Override
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = findByIdOrThrow(id);

        vehicle.setCompany(request.getCompany());
        vehicle.setType(request.getType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setStatus(request.getStatus());

        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(saved);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    @Override
    public void delete(Long id) {
        Vehicle vehicle = findByIdOrThrow(id);
        vehicleRepository.delete(vehicle);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Vehicle findByIdOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle with id " + id + " not found"));
    }
}

