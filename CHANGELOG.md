# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased] — 1.1.0-SNAPSHOT

### Added

- `CreateProductRequest` — new DTO exercising all supported Jakarta Bean Validation constraints (`@NotBlank`, `@NotNull`, `@Size`, `@DecimalMin`, `@DecimalMax`, `@Min`, `@Max`, `@Pattern`) so every constraint-to-schema mapping has a concrete verifiable sample
- `jakarta.validation-api` dependency added to `pom.xml`
- Scenario 6 (Bean Validation Constraints) added to `docs/Scenarios.md`
- Three new edge-case entries in `docs/Edge-Cases.md`: `@Size` default suppression, multiple constraints on one field, and `@JsonProperty` name aliasing
- `UserApi.listUsers` now accepts a `java.util.Locale` parameter annotated with `@Parameter(schema = @Schema(type = "string"))` — demonstrates Scenario 7: ignored-type override via explicit schema annotation (Scenario 7 added to `docs/Scenarios.md`, edge case added to `docs/Edge-Cases.md`)
- `GenericVertexRestController.update` (PUT) and `.patch` (PATCH) already carried explicit `@ApiResponse` annotations; these now correctly appear in the spec after the `ResponseProcessorImpl` fix — the generated `PUT /api/v1/agents/{id}` and `PATCH /api/v1/agents/{id}` now emit all five explicit responses instead of a single default "OK" (Scenario 8 added to `docs/Scenarios.md`, edge case added to `docs/Edge-Cases.md`)
- `UserApi.updateUser` (PUT) already carried `@ApiResponse(200)`, `@ApiResponse(400)`, `@ApiResponse(404)` — these now appear correctly in the generated `PUT /api/v1/users/{id}` entry

### Changed

- `ProductController.createProduct` now accepts `CreateProductRequest` instead of `ProductDto`
- Bumped version to `1.1.0-SNAPSHOT` to open the next development cycle
- Regenerated `docs/swagger/openapi.yaml` to reflect current sample state — paths are now sorted alphabetically
- Moved `maven.deploy.skip` property below `description` in `pom.xml` for consistency
- Enabled `sortOutput=true` in the plugin configuration so the generated spec is byte-for-byte identical across machines and builds
- Regenerated `docs/swagger/openapi.yaml` after adding the `locale` parameter and fixing PUT/PATCH response processing — `GET /api/v1/users` now contains a `locale` query parameter; PUT and PATCH operations now list all explicit response codes

---

## [1.0.0] — 2026-03-09

### Added

- `OrderController` — sample for custom composed annotation (`@CustomRestController`) detection via meta-annotation traversal
- `ProductController` — sample for operation inheritance from abstract base class (`AbstractCrudApi`)
- `AgentController` — sample for multi-tag assignment via multiple interface inheritance paths (`GenericVertexRestController` + `AgentRestController`)
- `UserController` — sample for paginated response wrapper (`PagedResponse<UserDto>`) resolved to a concrete named schema
- `EmailNotificationController` — sample for merging operations from an abstract class and an interface onto a single controller path
- `@CustomRestController` composed annotation combining `@RestController`, `@RequestMapping`, and `@Tag`
- `AbstractCrudApi<T, ID>` — generic base class providing `GET /{id}`, `DELETE /{id}`, and `GET /` with inherited SpringDoc annotations
- `AbstractGenericVertexController<T, ID>` — generic base class for full CRUD + exists operations
- `AbstractNotificationController` — abstract base providing `POST /`, `GET /{id}`, `DELETE /{id}` for notification resources
- `GenericVertexRestController` interface tagged as `"Generic REST API"`
- `AgentRestController` interface tagged as `"Agents"`, extending `GenericVertexRestController`
- `NotificationApi` interface providing `GET /pending`, `GET /providers`, `GET /health`
- DTOs: `AgentDto`, `AgentGroupDto`, `CreateUserRequest`, `NotificationDto`, `NotificationProviderDto`, `OrderDto`, `PagedResponse`, `ProductDto`, `SendNotificationRequest`, `UserDto`
- Generated `docs/swagger/openapi.yaml` covering all sample endpoints
- Plugin configured with Bearer JWT security scheme and three server environments (production, staging, local)
