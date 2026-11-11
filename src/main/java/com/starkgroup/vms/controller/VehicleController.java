package com.starkgroup.vms.controller;

import com.starkgroup.vms.dto.vehicle.CreateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.UpdateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.VehicleResponse;
import com.starkgroup.vms.dto.ApiResponse;
import com.starkgroup.vms.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Vehicle CRUD operations.
 * Base path: /api/v1/vehicles
 */
@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/vehicles → 201 Created
    // -------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> create(
            @Valid @RequestBody CreateVehicleRequest request) {

        VehicleResponse response = vehicleService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Vehicle created successfully", response));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/vehicles → 200 OK
    // -------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAll() {
        List<VehicleResponse> vehicles = vehicleService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Vehicles retrieved successfully", vehicles));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/vehicles/paged → 200 OK (Search, Filter, Sort, Pagination)
    // -------------------------------------------------------------------------

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<VehicleResponse>>> getPaged(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<VehicleResponse> pagedVehicles = vehicleService.getVehiclesPaged(
                search, status, type, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.ok("Vehicles retrieved successfully", pagedVehicles));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/vehicles/{id} → 200 OK
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle retrieved successfully", response));
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/vehicles/{id} → 200 OK
    // -------------------------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request) {

        VehicleResponse response = vehicleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle updated successfully", response));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/vehicles/{id} → 204 No Content
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
