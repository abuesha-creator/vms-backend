package com.starkgroup.vms.controller;

import com.starkgroup.vms.dto.ApiResponse;
import com.starkgroup.vms.entity.MaintenanceRecord;
import com.starkgroup.vms.entity.Vehicle;
import com.starkgroup.vms.entity.VehicleDocument;
import com.starkgroup.vms.exception.ResourceNotFoundException;
import com.starkgroup.vms.repository.DriverRepository;
import com.starkgroup.vms.repository.MaintenanceRecordRepository;
import com.starkgroup.vms.repository.VehicleDocumentRepository;
import com.starkgroup.vms.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class FleetOperationsController {

    private final VehicleRepository vehicleRepository;
    private final VehicleDocumentRepository documentRepository;
    private final MaintenanceRecordRepository maintenanceRepository;
    private final DriverRepository driverRepository;

    public FleetOperationsController(VehicleRepository vehicleRepository,
            VehicleDocumentRepository documentRepository,
            MaintenanceRecordRepository maintenanceRepository,
            DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.documentRepository = documentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.driverRepository = driverRepository;
    }

    @PostMapping("/vehicles/{vehicleId}/documents")
    public ResponseEntity<ApiResponse<VehicleDocument>> uploadDocument(
            @PathVariable Long vehicleId,
            @RequestParam("documentType") String documentType,
            @RequestParam("expiryDate") String expiryDate,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        VehicleDocument doc = new VehicleDocument();
        doc.setVehicle(vehicle);
        doc.setDocumentType(documentType);
        doc.setExpiryDate(LocalDate.parse(expiryDate));

        if (file != null && !file.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
            String cleanName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), uploadDir.resolve(cleanName), StandardCopyOption.REPLACE_EXISTING);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileUrl("/uploads/" + cleanName);
        } else {
            doc.setFileName("record_only.pdf");
            doc.setFileUrl("");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Document saved successfully", documentRepository.save(doc)));
    }

    @GetMapping("/vehicles/{vehicleId}/documents")
    public ResponseEntity<ApiResponse<List<VehicleDocument>>> getVehicleDocuments(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(ApiResponse.ok("Documents retrieved", documentRepository.findByVehicleId(vehicleId)));
    }

    @PostMapping("/vehicles/{vehicleId}/maintenance")
    public ResponseEntity<ApiResponse<MaintenanceRecord>> addMaintenance(
            @PathVariable Long vehicleId,
            @RequestBody MaintenanceRecord record) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        record.setVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Maintenance recorded", maintenanceRepository.save(record)));
    }

    @GetMapping("/vehicles/{vehicleId}/maintenance")
    public ResponseEntity<ApiResponse<List<MaintenanceRecord>>> getMaintenanceHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(ApiResponse.ok("History retrieved",
                maintenanceRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId)));
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        long activeCount = allVehicles.stream().filter(v -> "ACTIVE".equalsIgnoreCase(v.getStatus())).count();
        long maintenanceCount = allVehicles.stream().filter(v -> "MAINTENANCE".equalsIgnoreCase(v.getStatus())).count();
        long inactiveCount = allVehicles.stream().filter(v -> "INACTIVE".equalsIgnoreCase(v.getStatus())).count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalVehicles", allVehicles.size());
        summary.put("activeVehicles", activeCount);
        summary.put("maintenanceVehicles", maintenanceCount);
        summary.put("inactiveVehicles", inactiveCount);
        summary.put("totalDrivers", driverRepository.count());
        summary.put("expiredDocuments", documentRepository.countByExpiryDateBefore(LocalDate.now()));
        summary.put("totalMaintenanceCost", maintenanceRepository.getTotalMaintenanceCost());

        return ResponseEntity.ok(ApiResponse.ok("Summary retrieved", summary));
    }
}
