package com.starkgroup.vms.service;

import com.starkgroup.vms.dto.vehicle.CreateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.VehicleResponse;
import com.starkgroup.vms.entity.Vehicle;
import com.starkgroup.vms.exception.DuplicateVehicleNumberException;
import com.starkgroup.vms.repository.VehicleRepository;
import com.starkgroup.vms.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VehicleServiceImpl focused on the duplicate vehicleNumber business rule.
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private CreateVehicleRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateVehicleRequest();
        validRequest.setVehicleNumber("ABC-123");
        validRequest.setCompany("Stark Transport");
        validRequest.setType("Truck");
        validRequest.setBrand("Mercedes");
        validRequest.setModel("Actros");
        validRequest.setStatus("ACTIVE");
    }

    // -------------------------------------------------------------------------
    // create — duplicate vehicleNumber rule
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create: duplicate vehicleNumber throws DuplicateVehicleNumberException")
    void create_duplicateVehicleNumber_throwsDuplicateVehicleNumberException() {
        // Arrange: repository says the number already exists
        when(vehicleRepository.existsByVehicleNumberIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.create(validRequest))
                .isInstanceOf(DuplicateVehicleNumberException.class)
                .hasMessageContaining("ABC-123");

        // save must NEVER be called when a duplicate is detected
        verify(vehicleRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // create — happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create: unique vehicleNumber saves entity and returns response")
    void create_uniqueVehicleNumber_savesAndReturnsResponse() {
        // Arrange
        Vehicle savedVehicle = buildVehicle(1L, "ABC-123");
        when(vehicleRepository.existsByVehicleNumberIgnoreCase(anyString())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // Act
        VehicleResponse response = vehicleService.create(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVehicleNumber()).isEqualTo("ABC-123");
        assertThat(response.getCompany()).isEqualTo("Stark Transport");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    // -------------------------------------------------------------------------
    // create — vehicleNumber normalised to uppercase
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create: vehicleNumber is normalised to uppercase before persistence")
    void create_vehicleNumberNormalisedToUppercase() {
        // Arrange: lowercase input
        validRequest.setVehicleNumber("abc-123");

        Vehicle savedVehicle = buildVehicle(1L, "ABC-123");
        when(vehicleRepository.existsByVehicleNumberIgnoreCase("ABC-123")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // Act
        VehicleResponse response = vehicleService.create(validRequest);

        // Assert: the duplicate check and save used the uppercase form
        verify(vehicleRepository).existsByVehicleNumberIgnoreCase("ABC-123");
        assertThat(response.getVehicleNumber()).isEqualTo("ABC-123");
    }

    // -------------------------------------------------------------------------
    // getById — not found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getById: non-existent ID throws ResourceNotFoundException")
    void getById_notFound_throwsResourceNotFoundException() {
        when(vehicleRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> vehicleService.getById(99L))
                .isInstanceOf(com.starkgroup.vms.exception.ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Vehicle buildVehicle(Long id, String vehicleNumber) {
        Vehicle v = new Vehicle();
        v.setId(id);
        v.setVehicleNumber(vehicleNumber);
        v.setCompany("Stark Transport");
        v.setType("Truck");
        v.setBrand("Mercedes");
        v.setModel("Actros");
        v.setStatus("ACTIVE");
        return v;
    }
}

