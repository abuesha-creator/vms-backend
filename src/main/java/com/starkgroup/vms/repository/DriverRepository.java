package com.starkgroup.vms.repository;

import com.starkgroup.vms.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByLicenseNumberIgnoreCase(String licenseNumber);

    boolean existsByAssignedVehicleId(Long vehicleId);
}
