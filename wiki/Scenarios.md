# Scenarios

Each scenario targets a specific plugin capability. Below is a detailed breakdown of every sample in the repository.

---

## 1. Custom Composed Annotation

**Controller:** `OrderController`
**Annotation:** `annotation/CustomRestController.java`
**Plugin capability tested:** Meta-annotation traversal for controller detection

### What it does

`@CustomRestController` is a composed annotation that bundles `@RestController`, `@RequestMapping`, and `@Tag` into a single declaration:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
@RequestMapping
@Tag(name = "")
public @interface CustomRestController {
    String[] value() default {};
    String name() default "";
    String description() default "";
}
```

`OrderController` is annotated with `@CustomRestController` only — `@RestController` is not present directly on the class.

```java
@CustomRestController(
    value       = "/api/v1/orders",
    name        = "orders",
    description = "Order management"
)
public class OrderController { ... }
```

### What the plugin must do

Detect `OrderController` as a controller by walking the annotation graph of `@CustomRestController` and finding `@RestController` as a meta-annotation. If the plugin only checks `class.getAnnotation(RestController.class)` it will miss this class entirely.

### Expected output

`/api/v1/orders` paths appear in the spec tagged under `orders`.

---

## 2. Abstract Base Class Inheritance

**Controller:** `ProductController`
**Base class:** `api/AbstractCrudApi.java`
**Plugin capability tested:** Operation inheritance from abstract superclasses

### What it does

`AbstractCrudApi<T, ID>` declares three operations — `GET /{id}`, `DELETE /{id}`, and `GET /` — with full Spring MVC routing annotations and SpringDoc documentation:

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

`ProductController` extends `AbstractCrudApi<ProductDto, Long>` and adds `POST /` and `GET /category/{category}` directly.

### What the plugin must do

Walk up the class hierarchy and collect operations defined on `AbstractCrudApi`, then merge them with the operations defined directly on `ProductController`. The final spec for `/api/v1/products` must include all five operations.

### Expected output

`/api/v1/products` exposes: `GET /`, `POST /`, `GET /{id}`, `DELETE /{id}`, `GET /category/{category}`.

---

## 3. Multi-Tag via Interface Hierarchy

**Controller:** `AgentController`
**Interfaces:** `api/AgentRestController.java`, `api/GenericVertexRestController.java`
**Base class:** `api/AbstractGenericVertexController.java`
**Plugin capability tested:** Collecting tags from multiple interface inheritance paths

### What it does

`AgentController` has the following type hierarchy:

```
AgentController
  extends AbstractGenericVertexController<AgentDto, String>
            implements GenericVertexRestController   →  @Tag("Generic REST API")
  implements AgentRestController
               extends GenericVertexRestController   →  @Tag("Generic REST API")
               @Tag("Agents")
```

`GenericVertexRestController` is reachable through **two separate paths**: via the superclass and via the direct interface. `AgentRestController` adds a second tag `"Agents"` on top.

### What the plugin must do

Collect tags from all paths in the hierarchy and union them. The result must be `["Generic REST API", "Agents"]` on every Agent operation. A naive `putIfAbsent` deduplication would silently discard one of the tags when the same interface is encountered twice.

### Expected output

All `/api/v1/agents` operations carry both tags: `Generic REST API` and `Agents`.

---

## 4. Paginated Response Wrapper

**Controller:** `UserController`
**DTO:** `dto/PagedResponse.java`, `dto/UserDto.java`
**Plugin capability tested:** Generic type resolution into concrete named schemas

### What it does

`UserController` returns `PagedResponse<UserDto>` from its list and search operations:

```java
@GetMapping
public PagedResponse<UserDto> listUsers(...) { ... }
```

`PagedResponse<T>` is a generic wrapper with pagination metadata fields (`page`, `size`, `totalElements`, `totalPages`, `last`, `content`).

### What the plugin must do

Resolve `PagedResponse<UserDto>` to a concrete schema named `PagedResponseUserDto` in `components/schemas`, with all fields from both the wrapper and `UserDto` correctly reflected. OpenAPI 3.0 does not support generics natively, so each parameterisation must become its own named schema.

### Expected output

`components/schemas/PagedResponseUserDto` exists with `content` as an array of `UserDto` references, plus all pagination metadata fields.

---

## 5. Notification Subsystem — Multiple Inheritance Sources

**Controller:** `EmailNotificationController`
**Base class:** `api/AbstractNotificationController.java`
**Interface:** `api/NotificationApi.java`
**Plugin capability tested:** Merging operations from an abstract class and an interface onto one controller path

### What it does

`EmailNotificationController` extends `AbstractNotificationController` (which provides `POST /`, `GET /{id}`, `DELETE /{id}`) and implements `NotificationApi` (which adds `GET /pending`, `GET /providers`, `GET /health`).

All six operations are mapped under `/api/v1/notifications` but come from different points in the type hierarchy.

### What the plugin must do

Walk both the superclass chain and the interface list, collect all six operations, and emit them under the same base path without duplication or loss.

### Expected output

`/api/v1/notifications` exposes: `POST /`, `GET /{id}`, `DELETE /{id}`, `GET /pending`, `GET /providers`, `GET /health`.
