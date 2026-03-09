# Edge Cases

This page documents the tricky annotation patterns covered by the samples and the exact behaviour the plugin is expected to produce for each one.

---

## Multi-tag deduplication

**Sample:** `AgentController`

When a class reaches the same interface through two separate inheritance paths, the plugin must not deduplicate the tags from that interface before collecting the ones from the second path.

**Problematic behaviour (before fix):** Using `putIfAbsent` when registering collected tags meant that if `GenericVertexRestController` was encountered first via the superclass, its tag `"Generic REST API"` was stored. When the same interface was encountered again via `AgentRestController`, `putIfAbsent` skipped it — and the `"Agents"` tag from `AgentRestController` was also skipped because tag collection stopped at the already-seen interface.

**Correct behaviour:** All paths in the hierarchy are fully walked. Tags from `AgentRestController` (`"Agents"`) are collected independently of the fact that `GenericVertexRestController` was already visited via another path.

**Verification:** Every operation under `/api/v1/agents` in `openapi.yaml` must carry both `Generic REST API` and `Agents`.

---

## Composed annotation with attribute aliasing

**Sample:** `OrderController` + `@CustomRestController`

`@CustomRestController` maps its `value()` attribute to `@RequestMapping.value()` and its `name()` / `description()` attributes to `@Tag.name()` / `@Tag.description()`. The plugin must resolve these aliased attribute values correctly, not read the defaults from the meta-annotation declarations.

**Verification:** `OrderController` configured with `value = "/api/v1/orders"` and `name = "orders"` must produce paths under `/api/v1/orders` tagged as `orders`.

---

## Generic type resolution — same wrapper, different type arguments

**Sample:** `UserController` (`PagedResponse<UserDto>`)

If a second controller were added that returns `PagedResponse<ProductDto>`, the plugin must generate two separate schemas: `PagedResponseUserDto` and `PagedResponseProductDto`. The two must not be merged or one must not be silently reused for the other.

**Verification:** Each parameterisation of `PagedResponse<T>` appears as a distinct named schema in `components/schemas`.

---

## Operations declared only on an interface (no override in concrete class)

**Sample:** `NotificationApi` methods implemented by `EmailNotificationController`

Some methods (e.g. `GET /health`) are declared and annotated only on the interface. The concrete controller provides the implementation body but no annotations. The plugin must still emit the operation using the annotations from the interface.

**Verification:** `GET /api/v1/notifications/health` appears in the spec with the summary and description from `NotificationApi`, even though `EmailNotificationController` does not redeclare those annotations.

---

## Overridden annotations — concrete class wins

**Sample:** Any controller that redeclares `@Operation` on an overridden method

When a concrete controller overrides a method and adds its own `@Operation`, the concrete annotation takes precedence over the one on the superclass or interface.

**Verification:** The summary and description in the spec match the annotation on the most-derived class, not the base.

---

## Abstract class with generic type parameters

**Sample:** `AbstractCrudApi<T, ID>`, `AbstractGenericVertexController<T, ID>`

The abstract class declares methods with generic parameter types (`T`, `ID`). The plugin must resolve these to the concrete types used by the subclass (`ProductDto`, `Long` for `ProductController`; `AgentDto`, `String` for `AgentController`) when generating the schema references.

**Verification:** `GET /api/v1/products/{id}` references `ProductDto`, not a raw or unresolved generic type.

---

## @Size default values are not emitted

**Sample:** `CreateProductRequest` (`description` field annotated with `@Size(max=500)`)

`@Size` has defaults `min=0` and `max=Integer.MAX_VALUE`. Emitting these defaults as-is would produce `minLength: 0` (redundant) and `maxLength: 2147483647` (misleading). The plugin must suppress both:

- `min=0` → do **not** set `minLength`
- `max=Integer.MAX_VALUE` → do **not** set `maxLength`

**Verification:** The `description` property in `components/schemas/CreateProductRequest` has `maxLength: 500` but no `minLength` entry.

---

## Multiple constraints on the same field

**Sample:** `CreateProductRequest` (`name` annotated with both `@NotBlank` and `@Size(min=2, max=100)`)

Both constraints must be applied independently to the same schema property. The plugin must not short-circuit after the first matching annotation.

**Verification:** The `name` property carries `nullable: false` (from `@NotBlank`), `minLength: 2` and `maxLength: 100` (from `@Size`) simultaneously.

---

## @JsonProperty name aliasing with validation constraints

**Sample:** Any DTO field that combines `@JsonProperty` with a validation annotation

When a field is serialised under a different name (via `@JsonProperty`), the plugin must apply the constraint to the aliased property name in the schema, not the raw Java field name.

**Verification:** If a field `private String internalCode` is annotated `@JsonProperty("code")` and `@NotBlank`, the constraint must appear on the `code` property in `components/schemas`, not on `internalCode`.
