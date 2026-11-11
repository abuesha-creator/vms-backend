package com.starkgroup.vms.controller;

import com.starkgroup.vms.dto.ApiResponse;
import com.starkgroup.vms.dto.ErrorResponse;
import com.starkgroup.vms.dto.auth.LoginRequest;
import com.starkgroup.vms.dto.auth.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @PostMapping({"/api/v1/auth/login", "/api/auth/login"})
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequest request) {
        if (request == null || request.getPassword() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Unauthorized", "Invalid username or password", "/api/v1/auth/login"));
        }

        String identifier = request.getUsername();
        if (identifier == null || identifier.isBlank()) {
            identifier = request.getEmail();
        }

        String password = request.getPassword();

        boolean isUserValid = identifier != null &&
                (identifier.equalsIgnoreCase("admin") ||
                 identifier.equalsIgnoreCase("admin@vms.com") ||
                 identifier.equalsIgnoreCase("admin@example.com"));

        boolean isPasswordValid = "password123".equals(password);

        if (isUserValid && isPasswordValid) {
            LoginResponse responseData = new LoginResponse(
                    "vms-jwt-token-admin-" + System.currentTimeMillis(),
                    "admin",
                    "admin@vms.com",
                    "ADMIN"
            );
            return ResponseEntity.ok(ApiResponse.ok("Login successful", responseData));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "Unauthorized", "Invalid username or password", "/api/v1/auth/login"));
    }
}

