# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] — 2026-03-08

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
