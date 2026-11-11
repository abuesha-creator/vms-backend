package com.starkgroup.vms.repository;

import com.starkgroup.vms.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByVehicleIdOrderByServiceDateDesc(Long vehicleId);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM MaintenanceRecord m")
    Double getTotalMaintenanceCost();
}
