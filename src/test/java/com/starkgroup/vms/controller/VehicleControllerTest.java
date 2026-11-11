package com.starkgroup.vms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.starkgroup.vms.dto.vehicle.CreateVehicleRequest;
import com.starkgroup.vms.dto.vehicle.UpdateVehicleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link VehicleController}.
 *
 * Uses an H2 in-memory database (configured in src/test/resources/application.properties).
 * Tests are ordered so that the full CRUD lifecycle is exercised predictably.
 * {@link DirtiesContext} resets the Spring context (and therefore the H2 DB)
 * after the class so other test classes get a clean state.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VehicleControllerTest {

    private static final String BASE_URL = "/api/v1/vehicles";

    @Autowired
    private MockMvc mockMvc;

    // Shared ObjectMapper — mirrors JacksonConfig
    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    // =========================================================================
    // Helper
    // =========================================================================

    private CreateVehicleRequest buildCreateRequest(String vehicleNumber) {
        CreateVehicleRequest req = new CreateVehicleRequest();
        req.setVehicleNumber(vehicleNumber);
        req.setCompany("Stark Transport Ltd");
        req.setType("Truck");
        req.setBrand("Mercedes-Benz");
        req.setModel("Actros 1845");
        req.setStatus("ACTIVE");
        return req;
    }

    private UpdateVehicleRequest buildUpdateRequest() {
        UpdateVehicleRequest req = new UpdateVehicleRequest();
        req.setCompany("Updated Company");
        req.setType("Van");
        req.setBrand("Ford");
        req.setModel("Transit");
        req.setStatus("INACTIVE");
        return req;
    }

    // =========================================================================
    // POST /api/v1/vehicles  — create
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/vehicles → 201 Created with correct response body")
    void create_validRequest_returns201AndVehicle() throws Exception {
        CreateVehicleRequest req = buildCreateRequest("TRK-001");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Vehicle created successfully")))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.vehicleNumber", is("TRK-001")))
                .andExpect(jsonPath("$.data.company", is("Stark Transport Ltd")))
                .andExpect(jsonPath("$.data.type", is("Truck")))
                .andExpect(jsonPath("$.data.brand", is("Mercedes-Benz")))
                .andExpect(jsonPath("$.data.model", is("Actros 1845")))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/v1/vehicles → vehicleNumber normalised to uppercase")
    void create_lowercaseVehicleNumber_storedAsUppercase() throws Exception {
        CreateVehicleRequest req = buildCreateRequest("trk-002");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.vehicleNumber", is("TRK-002")));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/v1/vehicles → 409 Conflict for duplicate vehicleNumber")
    void create_duplicateVehicleNumber_returns409() throws Exception {
        // TRK-001 was already created in test @Order(1)
        CreateVehicleRequest req = buildCreateRequest("TRK-001");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Duplicate Vehicle Number")))
                .andExpect(jsonPath("$.message", containsString("TRK-001")));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/v1/vehicles → 409 Conflict — duplicate check is case-insensitive")
    void create_duplicateVehicleNumberDifferentCase_returns409() throws Exception {
        // TRK-001 (uppercase) already exists; submit lowercase variant
        CreateVehicleRequest req = buildCreateRequest("trk-001");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/v1/vehicles → 400 Bad Request when vehicleNumber is blank")
    void create_blankVehicleNumber_returns400WithValidationErrors() throws Exception {
        CreateVehicleRequest req = buildCreateRequest("TRK-VALID");
        req.setVehicleNumber(""); // violates @NotBlank

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors.vehicleNumber", notNullValue()));
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/v1/vehicles → 400 Bad Request when required fields are missing")
    void create_missingRequiredFields_returns400() throws Exception {
        // Send an empty JSON object — all @NotBlank constraints fire
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors", notNullValue()));
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/v1/vehicles → 400 Bad Request for malformed JSON")
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")));
    }

    // =========================================================================
    // GET /api/v1/vehicles  — list all
    // =========================================================================

    @Test
    @Order(10)
    @DisplayName("GET /api/v1/vehicles → 200 OK with list of vehicles")
    void getAll_returnsListOfVehicles() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", isA(java.util.List.class)))
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(2)));
    }

    // =========================================================================
    // GET /api/v1/vehicles/{id}  — get by ID
    // =========================================================================

    @Test
    @Order(20)
    @DisplayName("GET /api/v1/vehicles/{id} → 200 OK for existing vehicle")
    void getById_existingId_returns200() throws Exception {
        // First create a vehicle and capture its ID
        CreateVehicleRequest req = buildCreateRequest("TRK-GET");
        String responseJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Parse the returned ID
        Long id = objectMapper.readTree(responseJson)
                .path("data").path("id").asLong();

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(id.intValue())))
                .andExpect(jsonPath("$.data.vehicleNumber", is("TRK-GET")));
    }

    @Test
    @Order(21)
    @DisplayName("GET /api/v1/vehicles/{id} → 404 Not Found for non-existent ID")
    void getById_nonExistentId_returns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("999999")));
    }

    @Test
    @Order(22)
    @DisplayName("GET /api/v1/vehicles/{id} → 400 Bad Request for non-numeric ID")
    void getById_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL + "/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Type Mismatch")));
    }

    // =========================================================================
    // PUT /api/v1/vehicles/{id}  — update
    // =========================================================================

    @Test
    @Order(30)
    @DisplayName("PUT /api/v1/vehicles/{id} → 200 OK with updated fields")
    void update_existingId_returns200WithUpdatedData() throws Exception {
        // Create a vehicle
        CreateVehicleRequest create = buildCreateRequest("TRK-UPD");
        String createJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createJson).path("data").path("id").asLong();

        UpdateVehicleRequest update = buildUpdateRequest();

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Vehicle updated successfully")))
                // vehicleNumber is immutable — must still be the original value
                .andExpect(jsonPath("$.data.vehicleNumber", is("TRK-UPD")))
                .andExpect(jsonPath("$.data.company", is("Updated Company")))
                .andExpect(jsonPath("$.data.type", is("Van")))
                .andExpect(jsonPath("$.data.brand", is("Ford")))
                .andExpect(jsonPath("$.data.model", is("Transit")))
                .andExpect(jsonPath("$.data.status", is("INACTIVE")));
    }

    @Test
    @Order(31)
    @DisplayName("PUT /api/v1/vehicles/{id} → 404 Not Found for non-existent ID")
    void update_nonExistentId_returns404() throws Exception {
        mockMvc.perform(put(BASE_URL + "/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(32)
    @DisplayName("PUT /api/v1/vehicles/{id} → 400 Bad Request when update body fails validation")
    void update_invalidBody_returns400() throws Exception {
        // Create a vehicle first to get a valid ID
        CreateVehicleRequest create = buildCreateRequest("TRK-VUPD");
        String createJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createJson).path("data").path("id").asLong();

        // Send an empty update body
        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors", notNullValue()));
    }

    // =========================================================================
    // DELETE /api/v1/vehicles/{id}  — delete
    // =========================================================================

    @Test
    @Order(40)
    @DisplayName("DELETE /api/v1/vehicles/{id} → 204 No Content for existing vehicle")
    void delete_existingId_returns204() throws Exception {
        // Create a vehicle specifically for deletion
        CreateVehicleRequest create = buildCreateRequest("TRK-DEL");
        String createJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createJson).path("data").path("id").asLong();

        // Delete it
        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());

        // Verify it is gone
        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(41)
    @DisplayName("DELETE /api/v1/vehicles/{id} → 404 Not Found for non-existent ID")
    void delete_nonExistentId_returns404() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(42)
    @DisplayName("DELETE /api/v1/vehicles/{id} → vehicleNumber freed after deletion (can be reused)")
    void delete_vehicleNumberFreedAfterDeletion() throws Exception {
        // Create TRK-FREE, delete it, then recreate with same number — should succeed
        CreateVehicleRequest create = buildCreateRequest("TRK-FREE");

        String createJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createJson).path("data").path("id").asLong();

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());

        // Re-create with the same vehicle number — should now succeed (not 409)
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.vehicleNumber", is("TRK-FREE")));
    }
}

