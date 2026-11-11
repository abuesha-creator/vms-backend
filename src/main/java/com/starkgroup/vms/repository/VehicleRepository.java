package com.starkgroup.vms.repository;

import com.starkgroup.vms.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByVehicleNumberIgnoreCase(String vehicleNumber);

    boolean existsByVehicleNumberIgnoreCaseAndIdNot(String vehicleNumber, Long id);

    Optional<Vehicle> findByVehicleNumberIgnoreCase(String vehicleNumber);

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(v.vehicleNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.company) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.model) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR :status = '' OR UPPER(v.status) = UPPER(:status)) AND " +
            "(:type IS NULL OR :type = '' OR LOWER(v.type) = LOWER(:type))")
    Page<Vehicle> searchAndFilterVehicles(
            @Param("search") String search,
            @Param("status") String status,
            @Param("type") String type,
            Pageable pageable);
}
