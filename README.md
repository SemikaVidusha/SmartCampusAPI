# Smart Campus API

## Overview

The Smart Campus API is a RESTful web service developed using Java (JAX-RS with Jersey). It simulates a smart campus environment where rooms and sensors are managed, and real-time sensor readings can be recorded and retrieved.

The system follows a layered architecture with Resources, Services, and Models, ensuring separation of concerns and maintainability.

---

## Architecture Design

The API is structured into three main layers:

- **Resources (Controllers)** — Handle HTTP requests and responses using JAX-RS annotations.
- **Services (Business Logic)** — Contain the core logic such as validation, data handling, and business rules enforcement.
- **Models (Data Objects)** — Represent entities such as Room, Sensor, and SensorReading.

---

## RESTful Design

The API follows REST principles:

- Uses standard HTTP methods:
  - `GET` — Retrieve data
  - `POST` — Create data
  - `DELETE` — Remove data
- Uses JSON format for all request and response bodies
- Uses meaningful, resource-based endpoints:
  - `/api/v1/rooms`
  - `/api/v1/sensors`
  - `/api/v1/sensors/{id}/readings`
- Supports query parameters for filtering:
  - `/api/v1/sensors?type=CO2`

---

## Data Relationships

- A Room can contain multiple Sensors
- A Sensor belongs to one Room
- A Sensor can have multiple SensorReadings

Sensor readings are implemented as nested sub-resources:
/api/v1/sensors/{sensorId}/readings

---

## Features Implemented

- Room creation and deletion
- Sensor creation with room existence validation
- Filtering sensors by type using query parameters
- Adding and retrieving sensor readings per sensor
- Automatic update of a sensor's `currentValue` when a new reading is posted
- Business rules enforcement:
  - A room cannot be deleted if it still has sensors assigned to it
  - A sensor cannot be created with a non-existent room reference
  - A sensor under MAINTENANCE status cannot accept new readings

---

## Error Handling

Custom exceptions are implemented and mapped to appropriate HTTP status codes. The API never exposes raw Java stack traces.

| Scenario                          | Status Code                  |
|-----------------------------------|------------------------------|
| Room has active sensors on delete | 409 Conflict                 |
| Sensor references invalid room    | 422 Unprocessable Entity     |
| Sensor is under maintenance       | 403 Forbidden                |
| Unexpected server errors          | 500 Internal Server Error    |

All error responses are returned in JSON format:

```json
{
  "status": "403",
  "error": "Forbidden",
  "message": "Sensor S2 is currently under MAINTENANCE and cannot accept new readings."
}
```

---

## How to Build and Run

**Prerequisites:**
- Java JDK 8 or higher
- Apache Maven
- NetBeans IDE (or any Maven-compatible IDE)

**Steps:**

1. Clone the repository:
```bash
   git clone <your-repo-url>
   cd SmartCampusAPI
```

2. Build the project using Maven:
```bash
   mvn clean install
```

3. Run the main class `SmartCampusAPI.java` from your IDE, or from the terminal:
```bash
   mvn exec:java -Dexec.mainClass="com.semika.smartcampusapi.SmartCampusAPI"
```

4. The server will start at:
http://localhost:8081/api/v1/

---

## API Testing — curl Examples

**Create a Room**
```bash
curl -X POST http://localhost:8081/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"R1","name":"Room 1","capacity":20}'
```

**Create a Sensor**
```bash
curl -X POST http://localhost:8081/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"S1","type":"Temperature","status":"ACTIVE","currentValue":25,"roomId":"R1"}'
```

**Get All Sensors**
```bash
curl http://localhost:8081/api/v1/sensors
```

**Filter Sensors by Type**
```bash
curl "http://localhost:8081/api/v1/sensors?type=Temperature"
```

**Add a Sensor Reading**
```bash
curl -X POST http://localhost:8081/api/v1/sensors/S1/readings \
-H "Content-Type: application/json" \
-d '{"value":26.5}'
```

**Get All Readings for a Sensor**
```bash
curl http://localhost:8081/api/v1/sensors/S1/readings
```

**Delete a Room**
```bash
curl -X DELETE http://localhost:8081/api/v1/rooms/R1
```

## Report - Answers to Conceptual Questions

---

### Part 1.1 - JAX-RS Resource Lifecycle

By default, JAX-RS creates a new instance of a resource class for every incoming HTTP request. This is the per-request lifecycle. It means that instance variables on a resource class are not shared between requests and cannot be used to store shared state.

In this project, shared data (rooms, sensors, readings) is stored in static Maps inside the service classes (e.g., `private static Map<String, Sensor> sensors`). Using `static` ensures the data persists across requests regardless of how many resource instances are created. Without `static`, every request would see an empty map because a fresh service instance would be created each time.

This design decision also raises the concern of thread safety. Since multiple requests can arrive concurrently and modify the same static map, race conditions can occur - for example, concurrent write operations may lead to inconsistent state or data loss. To prevent this, `ConcurrentHashMap` should be used instead of `HashMap`, or synchronized blocks should wrap critical write operations.

---

### Part 1.2 — HATEOAS and Hypermedia in REST

HATEOAS (Hypermedia as the Engine of Application State) means that API responses include links to related actions and resources, allowing clients to navigate the API dynamically without hardcoding URLs.

For example, a response to `GET /rooms/R1` might include links such as `"sensors": "/api/v1/rooms/R1/sensors"` and `"delete": "/api/v1/rooms/R1"`, guiding the client on what it can do next.

This benefits client developers because they do not need to memorise or hardcode endpoint structures. If the API changes its URL structure, clients that follow hypermedia links automatically adapt without code changes. It also reduces the dependency on static external documentation, making the API more self-describing and resilient to change.

---

### Part 2.1 — Returning IDs vs Full Room Objects

Returning only IDs in a list response reduces the payload size significantly, which improves network performance especially when there are hundreds of rooms. However, it forces the client to make additional requests for each ID to retrieve the full details, increasing the number of round trips and adding latency.

Returning full room objects in the list means the client has all the data it needs in a single request, reducing round trips. The tradeoff is increased bandwidth usage per response. For most campus management use cases where the number of rooms is manageable, returning full objects is preferred for usability. For very large datasets, pagination with full objects or ID-only lists with lazy loading would be more appropriate.

---

### Part 2.2 — Idempotency of DELETE

In this implementation, DELETE is not fully idempotent because attempting to delete a non-existent room results in an error response.

According to REST principles, DELETE should be idempotent, meaning multiple identical requests should produce the same outcome. Whether the resource existed or not, the final state is the same — the room is absent.

To improve idempotency, the API should return a 404 Not Found or even a 204 No Content without error when the room does not exist, ensuring consistent behaviour across repeated requests.

---

### Part 3.1 — Effect of Mismatched Content-Type with @Consumes

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the endpoint only accepts requests with a `Content-Type` of `application/json`. If a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS will reject the request before it even reaches the method body.

Jersey will return an HTTP `415 Unsupported Media Type` response automatically. No custom code is needed to handle this — the framework enforces the contract declared by `@Consumes`. This protects the endpoint from receiving data it cannot deserialize and prevents unexpected runtime errors caused by incompatible input formats.

---

### Part 3.2 — @QueryParam vs Path-Based Filtering

Using `@QueryParam` for filtering (e.g., `GET /api/v1/sensors?type=CO2`) is considered superior to embedding the filter in the path (e.g., `/api/v1/sensors/type/CO2`) for several reasons.

Query parameters are semantically designed for filtering, searching, and sorting optional criteria on a collection. They make it clear that `type` is not a resource identifier but a filter applied to the collection. Path segments, by contrast, are meant to identify specific resources uniquely.

Query parameters are also more flexible — multiple filters can be combined easily (e.g., `?type=CO2&status=ACTIVE`) without redesigning the URL structure. A path-based approach would require new endpoint definitions for every combination of filters, making the API harder to maintain and less scalable.

---

### Part 4.1 — Benefits of the Sub-Resource Locator Pattern

The Sub-Resource Locator pattern delegates routing for nested paths to a separate dedicated class. In this project, `SensorResource` handles `/sensors` and locates `SensorReadingResource` to handle `/sensors/{id}/readings`.

The main benefit is separation of concerns. Each resource class is responsible for one entity, keeping the code focused and easier to maintain. As the API grows, adding new nested resources does not require modifying existing classes — a new locator and resource class can be added independently.

Compared to defining all paths in one large controller, the locator pattern avoids classes becoming bloated and difficult to navigate. It also improves testability since each sub-resource class can be unit tested in isolation. In large APIs with deep nesting or many sub-resources, this pattern is essential for keeping the codebase manageable.

---

### Part 5.2 — HTTP 422 vs 404 for Missing Reference in Payload

A 404 Not Found response implies that the requested URL or resource endpoint does not exist. In the case where a client POSTs a valid JSON body to a valid endpoint but includes a `roomId` that does not exist, the endpoint itself is valid and reachable - the problem is with the data inside the request body.

HTTP 422 Unprocessable Entity is more semantically accurate because it signals that the server understood the request format and the endpoint exists, but the content of the request is logically invalid and cannot be processed. It communicates that the issue is a business logic validation failure (a broken reference within the payload), not a missing route. This distinction helps client developers understand the exact nature of the error and correct their request data rather than questioning whether they are calling the wrong URL.

---

### Part 5.4 — Security Risks of Exposing Stack Traces

Exposing raw Java stack traces in API responses is a significant security risk for several reasons.

Stack traces reveal internal implementation details such as class names, method names, file paths, and line numbers. An attacker can use this information to identify the frameworks and libraries in use, look up known vulnerabilities for those specific versions, and craft targeted exploits.

They can also reveal the application's internal logic flow, helping an attacker understand how the system processes data, where validations occur, and which code paths exist. In some cases, stack traces include database query details or configuration values that further expose the system.

The global `ExceptionMapper<Throwable>` in this project addresses this by intercepting all unhandled exceptions and returning a generic 500 response with no internal detail, ensuring the API never leaks implementation information to external consumers.
