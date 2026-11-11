package com.starkgroup.vms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());

        Map<String, Object> dbHealth = checkDatabaseConnectivity();
        response.put("database", dbHealth);

        boolean dbUp = "UP".equals(dbHealth.get("status"));
        HttpStatus status = dbUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity.status(status).body(response);
    }

    private Map<String, Object> checkDatabaseConnectivity() {
        Map<String, Object> dbInfo = new LinkedHashMap<>();
        long start = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(2);
            long latencyMs = System.currentTimeMillis() - start;

            if (isValid) {
                DatabaseMetaData metaData = connection.getMetaData();
                dbInfo.put("status", "UP");
                dbInfo.put("product", metaData.getDatabaseProductName());
                dbInfo.put("version", metaData.getDatabaseProductVersion());
                dbInfo.put("latencyMs", latencyMs);
            } else {
                dbInfo.put("status", "DOWN");
                dbInfo.put("error", "Database connection validation returned false");
            }
        } catch (Exception e) {
            log.warn("Database connectivity check failed: {}", e.getMessage());
            dbInfo.put("status", "DOWN");
            dbInfo.put("error", e.getMessage());
        }

        return dbInfo;
    }
}

