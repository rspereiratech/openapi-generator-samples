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

---

## 6. Bean Validation Constraints

**DTO:** `dto/CreateProductRequest.java`
**Controller:** `ProductController` (`POST /api/v1/products`)
**Plugin capability tested:** Mapping Jakarta Bean Validation annotations to OpenAPI schema properties

### What it does

`CreateProductRequest` applies every supported Jakarta validation constraint so that each constraint type has a concrete, verifiable representation in the generated schema:

```java
@NotBlank
@Size(min = 2, max = 100)
String name,

@Size(max = 500)
String description,

@NotNull
@DecimalMin("0.01")
@DecimalMax("99999.99")
BigDecimal price,

@NotNull
@Pattern(regexp = "^[A-Z_]+$")
String category,

@Min(0)
@Max(9999)
int stock,

@NotEmpty
@Size(max = 50)
String sku,

@Email
String supplierEmail,

@PositiveOrZero
Integer weight
```

### What the plugin must do

After Swagger's `ModelConverters` generates the base schema for `CreateProductRequest`, the plugin walks all reachable fields, reads their annotations, and enriches the corresponding schema property:

| Annotation | Field | OpenAPI property set |
|---|---|---|
| `@NotBlank` | `name` | `nullable: false` |
| `@Size(min=2, max=100)` | `name` | `minLength: 2`, `maxLength: 100` |
| `@Size(max=500)` | `description` | `maxLength: 500` |
| `@NotNull` | `price` | `nullable: false` |
| `@DecimalMin("0.01")` | `price` | `minimum: 0.01` |
| `@DecimalMax("99999.99")` | `price` | `maximum: 99999.99` |
| `@NotNull` | `category` | `nullable: false` |
| `@Pattern(regexp="^[A-Z_]+$")` | `category` | `pattern: ^[A-Z_]+$` |
| `@Min(0)` | `stock` | `minimum: 0` |
| `@Max(9999)` | `stock` | `maximum: 9999` |
| `@NotEmpty` | `sku` | `nullable: false`, `minLength: 1` |
| `@Size(max=50)` | `sku` | `maxLength: 50` |
| `@Email` | `supplierEmail` | `format: email` |
| `@PositiveOrZero` | `weight` | `minimum: 0` |

### Expected output

`components/schemas/CreateProductRequest` contains:
- `name`: `nullable: false`, `minLength: 2`, `maxLength: 100`
- `description`: `maxLength: 500`
- `price`: `nullable: false`, `minimum: 0.01`, `maximum: 99999.99`
- `category`: `nullable: false`, `pattern: ^[A-Z_]+$`
- `stock`: `minimum: 0`, `maximum: 9999`
- `sku`: `nullable: false`, `minLength: 1`, `maxLength: 50`
- `supplierEmail`: `format: email`
- `weight`: `minimum: 0`

---

## 7. Ignored-Type Override via `@Parameter(schema = @Schema(type = "string"))`

**Controller:** `UserController`
**Interface:** `api/UserApi.java` (`listUsers`)
**Plugin capability tested:** Including an otherwise-ignored parameter type in the spec when an explicit `@Parameter(schema=…)` annotation is present

### What it does

`java.util.Locale` is part of the built-in ignored-type list (mirroring SpringDoc's defaults). It would normally be silently dropped from the generated parameter list. `UserApi.listUsers` annotates the `Locale` parameter with:

```java
@Parameter(
    description = "BCP-47 language tag for response localisation ...",
    schema      = @Schema(type = "string"),
    example     = "en-US"
)
@RequestParam(required = false) Locale locale
```

The explicit `schema = @Schema(type = "string")` signals to the plugin that the developer intentionally wants this parameter in the spec, even though `Locale` would otherwise be ignored.

### What the plugin must do

When a parameter's type is in the ignored set, check whether a `@Parameter(schema=@Schema(type=...))` or `@Parameter(schema=@Schema(implementation=...))` override is present. If it is, emit the parameter using the declared schema instead of skipping it entirely.

### Expected output

`GET /api/v1/users` includes a `locale` query parameter with:
```yaml
- name: locale
  in: query
  required: false
  schema:
    type: string
  example: en-US
```

---

## 8. PUT/PATCH with Explicit `@ApiResponse` Annotations

**Controllers:** `AgentController` (via `GenericVertexRestController`), `UserController` (via `UserApi`)
**Plugin capability tested:** PUT and PATCH operations correctly picking up explicit `@ApiResponse` annotations instead of falling back to a single default "OK" response

### What it does

`GenericVertexRestController` declares PUT and PATCH with explicit `@ApiResponses`:

```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Entity updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "404", description = "Entity not found"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
})
@PutMapping("/{id}")
T update(...);
```

Similarly, `UserApi.updateUser` declares `@ApiResponse(200)`, `@ApiResponse(400)`, and `@ApiResponse(404)`.

Before the `ResponseProcessorImpl` fix, PUT and PATCH bypassed explicit `@ApiResponse` annotations and always fell back to the HTTP-method inference (which produces a single "200 OK"). After the fix, explicit annotations are detected and emitted correctly for all HTTP methods including PUT and PATCH.

### What the plugin must do

`ResponseProcessorImpl.processResponses` must check for explicit `@ApiResponse`/`@ApiResponses` annotations before falling back to default HTTP-method inference, regardless of whether the method is GET, POST, PUT, PATCH, or DELETE.

### Expected output

`PUT /api/v1/agents/{id}` emits five responses: 200, 400, 401, 403, 404 — each with a meaningful description matching the annotation, not a generic "OK".

`PATCH /api/v1/agents/{id}` emits five responses: 200, 400, 401, 403, 404.

`PUT /api/v1/users/{id}` emits three responses: 200, 400, 404.

---

## 9. `@Schema` Annotation Enrichment on Records and POJOs

**DTOs:** `ProductDto`, `CreateProductRequest`
**Plugin capability tested:** `SchemaAnnotationEnricher` correctly propagating `@Schema` attributes — description, example, format, accessMode, allowableValues, defaultValue — for Java records where `ModelConverters` does not reliably pick them up

### What it does

`ProductDto` is a Java record annotated with `@Schema` at class and field level:

```java
@Schema(description = "Product data transfer object")
public record ProductDto(
    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    Long id,

    @Schema(description = "Product category", example = "ELECTRONICS",
            allowableValues = {"ELECTRONICS", "CLOTHING", "FOOD", "BOOKS"})
    String category,

    @Schema(description = "Unit price in EUR", example = "49.99")
    BigDecimal price,
    ...
)
```

`CreateProductRequest` combines Bean Validation constraints with `@Schema` metadata on the same fields, exercising the interaction between `ValidationSchemaEnricher` (which runs first) and `SchemaAnnotationEnricher` (which runs second with non-overwriting policy).

### What the plugin must do

After `ModelConverters` resolves the record's schema, `SchemaAnnotationEnricher` must:
1. Apply `description` at schema component level from the class-level `@Schema`
2. Apply `description`, `example`, `format`, `defaultValue`, `nullable`, `readOnly`/`writeOnly`/`accessMode`, `deprecated`, `pattern`, `minimum`/`maximum`, `minLength`/`maxLength`, `allowableValues` at property level
3. For `@Schema(hidden = true)` fields — remove the property from `properties` and `required`
4. Not overwrite any value already set by `ValidationSchemaEnricher` (non-overwriting policy)

### Expected output

`components/schemas/ProductDto`:
```yaml
ProductDto:
  type: object
  description: Product data transfer object
  properties:
    id:
      type: integer
      format: int64
      readOnly: true
      example: "1"
      description: Unique identifier
    category:
      type: string
      description: Product category
      example: ELECTRONICS
      enum:
        - ELECTRONICS
        - CLOTHING
        - FOOD
        - BOOKS
    price:
      type: number
      description: Unit price in EUR
      example: "49.99"
```

`components/schemas/CreateProductRequest` additionally reflects Bean Validation constraints alongside `@Schema` metadata:
```yaml
  properties:
    name:
      type: string
      description: Product name
      example: Wireless Keyboard
      nullable: false    # from @NotBlank
      minLength: 2       # from @Size(min=2)
      maxLength: 100     # from @Size(max=100)
    price:
      type: number
      description: Unit price in EUR
      nullable: false    # from @NotNull
      minimum: 0.01      # from @DecimalMin
      maximum: 99999.99  # from @DecimalMax
```

---

## 10. `@RequestHeader` Parameter

**Controller:** `OrderController` (`createOrder`)
**Plugin capability tested:** Mapping `@RequestHeader` to an OpenAPI `header` parameter

### What it does

`OrderController.createOrder` receives an optional `X-Idempotency-Key` HTTP request header:

```java
@PostMapping
public OrderDto createOrder(
        @RequestBody OrderDto order,
        @Parameter(description = "Optional client-generated idempotency key ...",
                   example = "550e8400-e29b-41d4-a716-446655440000")
        @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey)
```

### What the plugin must do

Recognise `@RequestHeader` and emit the parameter with `in: header`, using the parameter name from the annotation's `value` attribute, and `required: false` because the annotation sets `required = false`.

### Expected output

`POST /api/v1/orders` includes:
```yaml
- name: X-Idempotency-Key
  in: header
  required: false
  schema:
    type: string
  example: 550e8400-e29b-41d4-a716-446655440000
```

---

## 11. Named Examples via `@ExampleObject`

**Interface:** `api/NotificationApi.java` (`sendNotification`)
**Plugin capability tested:** `@Content(examples = { @ExampleObject(...) })` — named response examples with JSON-parsed `value`

### What it does

The `202 Accepted` response on `sendNotification` carries two named examples:

```java
@ApiResponse(
    responseCode = "202",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = NotificationDto.class),
        examples = {
            @ExampleObject(
                name = "email",
                summary = "Email notification queued",
                value = "{\"id\":1,\"type\":\"EMAIL\",\"recipient\":\"user@example.com\",\"status\":\"PENDING\"}"
            ),
            @ExampleObject(
                name = "sms",
                summary = "SMS notification queued",
                value = "{\"id\":2,\"type\":\"SMS\",\"recipient\":\"+351912345678\",\"status\":\"PENDING\"}"
            )
        }
    )
)
```

### What the plugin must do

1. Resolve each `@ExampleObject` by `name` into the response `examples` map.
2. Parse `value` as JSON and store it as a `JsonNode` so the YAML serialiser emits a proper mapping instead of a quoted escaped string.
3. Apply `summary` when non-blank.

### Expected output

`POST /api/v1/notifications` response `202`:
```yaml
'202':
  description: Notification accepted for delivery
  content:
    application/json:
      schema:
        $ref: '#/components/schemas/NotificationDto'
      examples:
        email:
          summary: Email notification queued
          value:
            id: 1
            type: EMAIL
            recipient: user@example.com
            status: PENDING
        sms:
          summary: SMS notification queued
          value:
            id: 2
            type: SMS
            recipient: "+351912345678"
            status: PENDING
```
