package com.starkgroup.vms.service;

import com.starkgroup.vms.dto.vehicle.CreateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.UpdateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.VehicleResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service contract for vehicle business operations.
 * All business rules are enforced here; persistence is delegated to the
 * repository.
 */
public interface VehicleService {

    VehicleResponse create(CreateVehicleRequest request);

    VehicleResponse getById(Long id);

    List<VehicleResponse> getAll();

    Page<VehicleResponse> getVehiclesPaged(String search, String status, String type, int page, int size, String sortBy,
            String direction);

    VehicleResponse update(Long id, UpdateVehicleRequest request);

    void delete(Long id);
}
