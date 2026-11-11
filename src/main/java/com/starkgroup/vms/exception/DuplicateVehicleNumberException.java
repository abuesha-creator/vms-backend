package com.starkgroup.vms.exception;

/**
 * Thrown when an attempt is made to register a vehicle whose vehicleNumber
 * already exists in the system.  Maps to HTTP 409 Conflict via GlobalExceptionHandler.
 */
public class DuplicateVehicleNumberException extends RuntimeException {

    private final String vehicleNumber;

    public DuplicateVehicleNumberException(String vehicleNumber) {
        super("Vehicle with number '" + vehicleNumber + "' already exists");
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}

