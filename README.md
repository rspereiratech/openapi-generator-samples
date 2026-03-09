# openapi-generator-samples

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-blue?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring](https://img.shields.io/badge/Spring-6DB33F?logo=spring&logoColor=white)](https://spring.io)
[![OpenAPI 3.0](https://img.shields.io/badge/OpenAPI-3.0-green?logo=openapiinitiative)](docs/swagger/openapi.yaml)
![REST API](https://img.shields.io/badge/REST-API-blue)

Sample Spring MVC application used to validate and demonstrate the capabilities of the [`openapi-generator-maven-plugin`](https://github.com/rspereiratech/openapi-generator-maven-plugin).

The project exercises a wide range of real-world annotation patterns so that every feature of the plugin has a concrete, runnable test case to be verified against.

---

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Scenarios Covered](#scenarios-covered)
  - [Custom Composed Annotation](#1-custom-composed-annotation)
  - [Abstract Base Class Inheritance](#2-abstract-base-class-inheritance)
  - [Multi-Tag via Interface Hierarchy](#3-multi-tag-via-interface-hierarchy)
  - [Paginated Responses](#4-paginated-responses)
  - [Notification Subsystem](#5-notification-subsystem)
  - [Bean Validation Constraints](#6-bean-validation-constraints)
- [Generated OpenAPI Specification](#generated-openapi-specification)
- [Plugin Configuration Reference](#plugin-configuration-reference)
- [Building](#building)
- [License](#license)

---

## Overview

The plugin generates an OpenAPI 3.0 document **at build time**, directly from compiled Spring MVC classes — no running server required. This module is the reference sample that proves the generator handles each of the following correctly:

| Feature | Covered by |
|---|---|
| `@RestController` detection | All controllers |
| Custom composed annotation (meta-annotated with `@RestController`) | `@CustomRestController` |
| Inherited operations from abstract base classes | `AbstractCrudApi`, `AbstractGenericVertexController` |
| Inherited operations from interfaces | `GenericVertexRestController`, `AgentRestController` |
| Multi-tag assignment via multiple interface paths | `AgentController` |
| Paginated response wrapper | `UserController` |
| Security scheme (Bearer JWT) | Global |
| Multiple server environments | pom.xml configuration |
| Bean Validation constraints (`@NotBlank`, `@Size`, `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax`, `@Pattern`) | `CreateProductRequest` |

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| `openapi-generator-parent` | latest (must be installed locally) |

> The parent POM manages all dependency versions. Install it before building this project.

---

## Project Structure

```
openapi-generator-samples/
├── docs/
│   └── swagger/
│       └── openapi.yaml              # Generated output (committed for reference)
├── src/main/java/.../
│   ├── annotation/
│   │   └── CustomRestController.java # Composed annotation sample
│   ├── api/
│   │   ├── AbstractCrudApi.java      # Generic CRUD base (GET/DELETE/list)
│   │   ├── AbstractGenericVertexController.java
│   │   ├── AbstractNotificationController.java
│   │   ├── AgentRestController.java  # Interface with @Tag("Agents")
│   │   ├── GenericVertexRestController.java  # Interface with @Tag("Generic REST API")
│   │   ├── NotificationApi.java
│   │   └── UserApi.java
│   ├── controller/
│   │   ├── AgentController.java      # Multi-tag scenario
│   │   ├── EmailNotificationController.java
│   │   ├── OrderController.java
│   │   ├── ProductController.java
│   │   └── UserController.java
│   └── dto/
│       ├── AgentDto.java
│       ├── AgentGroupDto.java
│       ├── CreateProductRequest.java  # Bean Validation constraint sample
│       ├── CreateUserRequest.java
│       ├── NotificationDto.java
│       ├── NotificationProviderDto.java
│       ├── OrderDto.java
│       ├── PagedResponse.java
│       ├── ProductDto.java
│       ├── SendNotificationRequest.java
│       └── UserDto.java
└── pom.xml
```

---

## How It Works

The `openapi-generator-maven-plugin` binds to the `process-classes` Maven lifecycle phase. After the Java compiler produces `.class` files, the plugin:

1. Scans the configured base packages for controller classes (using reflective annotation traversal).
2. Walks the full type hierarchy of each controller: concrete class → abstract superclasses → interfaces.
3. Collects all Spring MVC routing annotations (`@RequestMapping`, `@GetMapping`, etc.) and SpringDoc annotations (`@Operation`, `@ApiResponse`, `@Tag`, etc.).
4. Merges them into an OpenAPI 3.0 document and writes it to the configured output file.

The key point is that **no Spring context is started** — the document is built purely from bytecode.

---

## Scenarios Covered

### 1. Custom Composed Annotation

**File:** `annotation/CustomRestController.java`

`@CustomRestController` is a composed annotation that combines `@RestController`, `@RequestMapping`, and `@Tag` into a single declaration:

```java
@CustomRestController(
    value       = "/api/v1/orders",
    name        = "orders",
    description = "Order management"
)
public class OrderController { ... }
```

The generator detects this class as a controller through **recursive meta-annotation traversal**: it sees that `@CustomRestController` is itself annotated with `@RestController`, even though `@RestController` is not present directly on the controller class.

---

### 2. Abstract Base Class Inheritance

**File:** `api/AbstractCrudApi.java`

`AbstractCrudApi<T, ID>` declares the common `GET /{id}`, `DELETE /{id}`, and `GET /` operations — including their Spring MVC routing annotations and SpringDoc documentation — in one place:

```java
public abstract class AbstractCrudApi<T, ID> {

    @Operation(summary = "Get by ID", ...)
    @GetMapping("/{id}")
    public abstract T getById(@PathVariable ID id);

    @Operation(summary = "Delete by ID", ...)
    @DeleteMapping("/{id}")
    public abstract void deleteById(@PathVariable ID id);

    @Operation(summary = "List all", ...)
    @GetMapping
    public abstract List<T> listAll();
}
```

Concrete subclasses (e.g. `ProductController`) inherit both the routing and the documentation without redeclaring them.

---

### 3. Multi-Tag via Interface Hierarchy

**Files:** `controller/AgentController.java`, `api/AgentRestController.java`, `api/GenericVertexRestController.java`

`AgentController` extends `AbstractGenericVertexController` and implements `AgentRestController`. Both paths in the type hierarchy eventually reach `GenericVertexRestController`, which carries `@Tag("Generic REST API")`. `AgentRestController` adds a second `@Tag("Agents")`.

```
AgentController
  extends AbstractGenericVertexController<AgentDto, String>
            implements GenericVertexRestController  →  @Tag("Generic REST API")
  implements AgentRestController
               extends GenericVertexRestController  →  @Tag("Generic REST API")
               @Tag("Agents")
```

The generator collects **both** tags so every Agent operation is published under `["Generic REST API", "Agents"]`.

---

### 4. Paginated Responses

**Files:** `controller/UserController.java`, `dto/PagedResponse.java`

`UserController` demonstrates how a generic `PagedResponse<T>` wrapper is resolved to a concrete schema (`PagedResponseUserDto`) in the generated spec, with all pagination metadata fields (`page`, `size`, `totalElements`, `totalPages`, `last`) correctly reflected.

---

### 5. Notification Subsystem

**Files:** `controller/EmailNotificationController.java`, `api/AbstractNotificationController.java`, `api/NotificationApi.java`

This group shows how operations defined across an abstract class and an interface are merged onto a single controller path (`/api/v1/notifications`), covering: send, get, cancel, list pending, list providers, and health check.

### 6. Bean Validation Constraints

**File:** `dto/CreateProductRequest.java`

`CreateProductRequest` is the primary sample for Bean Validation constraint propagation. Every supported Jakarta constraint annotation is applied to at least one field:

```java
@NotBlank @Size(min = 2, max = 100)  String name,       // nullable:false, minLength:2, maxLength:100
@Size(max = 500)                      String description, // maxLength:500
@NotNull @DecimalMin("0.01")
         @DecimalMax("99999.99")      BigDecimal price,   // nullable:false, minimum:0.01, maximum:99999.99
@NotNull @Pattern(regexp="^[A-Z_]+$") String category,   // nullable:false, pattern:^[A-Z_]+$
@Min(0)  @Max(9999)                   int stock           // minimum:0, maximum:9999
```

The generator reads the annotations directly from the compiled `.class` file and applies them to the corresponding property in `components/schemas/CreateProductRequest`. No Spring context or runtime is required.

---

## Generated OpenAPI Specification

The output file is committed at [`docs/swagger/openapi.yaml`](docs/swagger/openapi.yaml) so you can inspect the result without running the build.

**API Groups**

| Tag | Base Path | Operations |
|---|---|---|
| `orders` | `/api/v1/orders` | list, create, get, cancel |
| `products` | `/api/v1/products` | list, create, get, delete, list by category |
| `users` | `/api/v1/users` | list, create, get, update, delete, search |
| `notifications` | `/api/v1/notifications` | send, get, cancel, list pending, list providers, health |
| `Agents` + `Generic REST API` | `/api/v1/agents` | CRUD + exists + get group |

**Security**

All endpoints require a Bearer JWT token:

```
Authorization: Bearer <token>
```

---

## Plugin Configuration Reference

The plugin is configured in `pom.xml`. The key parameters used in this sample are:

| Parameter | Description | Value in this sample |
|---|---|---|
| `basePackages` | Root packages to scan for controllers | `io.github.rspereiratech.openapi.generator.samples` |
| `outputFile` | Path where the YAML is written | `docs/swagger/openapi.yaml` |
| `contextPath` | Servlet context path prepended to all server URLs | `sample-api` |
| `title` | API title in the `info` block | `Sample API` |
| `version` | API version | `1.1.0-SNAPSHOT` |
| `servers` | List of server environments | Production, Staging, Local |
| `controllerAnnotations` | Additional annotations treated as controller markers | `@CustomRestController` |
| `securitySchemes` | Security schemes added to the `components` block | `bearerAuth` (HTTP Bearer JWT) |

---

## Building

```bash
# Install the parent POM and plugin first (from the parent repository)
mvn install -f ../openapi-generator-parent/pom.xml

# Build this project and regenerate openapi.yaml
mvn process-classes
```

The generated file will be written to `docs/swagger/openapi.yaml`.

---

## License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
