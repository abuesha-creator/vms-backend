# Vehicle Management System (VMS) - API Contract

### 1. Get All Vehicles
- **Method:** `GET`
- **Endpoint:** `/api/v1/vehicles`
- **Response:** `200 OK` (List of Vehicles)

### 2. Create Vehicle
- **Method:** `POST`
- **Endpoint:** `/api/v1/vehicles`
- **Request Body:** JSON DTO (vehicleNumber, company, type, brand, model, status)
- **Response:** `201 Created`
- **Error Responses:** 
  - `400 Bad Request` (Validation error / Duplicate Vehicle Number)

### 3. Update Vehicle
- **Method:** `PUT`
- **Endpoint:** `/api/v1/vehicles/{id}`
- **Response:** `200 OK`

### 4. Delete Vehicle
- **Method:** `DELETE`
- **Endpoint:** `/api/v1/vehicles/{id}`
- **Response:** `204 No Content`
