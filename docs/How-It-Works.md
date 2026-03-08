# How It Works

The plugin generates an OpenAPI 3.0 document at build time, directly from compiled `.class` files. No Spring context is started and no HTTP server is required.

---

## Pipeline Overview

```
mvn process-classes
       │
       ▼
┌─────────────────────────┐
│  1. Classpath scanning  │  Discovers controller classes in configured base packages
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│  2. Type hierarchy walk │  Traverses superclasses and interfaces recursively
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│  3. Annotation harvest  │  Collects @Tag, @Operation, @ApiResponse, routing annotations
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│  4. Schema resolution   │  Builds component schemas from DTO classes via reflection
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│  5. Spec assembly       │  Merges everything into an OpenAPI 3.0 object model
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│  6. YAML serialisation  │  Writes the final document to the configured outputFile
└─────────────────────────┘
```

---

## Step 1 — Classpath Scanning

The plugin loads compiled classes from the Maven project's output directory (`target/classes`) using a `URLClassLoader`. It then scans every class in the configured `basePackages` and checks whether it qualifies as a controller.

A class qualifies if it is annotated (directly or meta-annotated) with `@RestController` or any annotation listed in the `controllerAnnotations` configuration parameter.

**Meta-annotation traversal** means the plugin does not just check `class.getAnnotation(RestController.class)`. It recursively inspects the annotations on each annotation, so a composed annotation like `@CustomRestController` — which is itself annotated with `@RestController` — is correctly detected.

---

## Step 2 — Type Hierarchy Walk

For each controller class, the plugin walks the full type hierarchy:

```
ConcreteController
  → superclass (e.g. AbstractCrudApi)
      → superclass of superclass
  → interface A (e.g. GenericVertexRestController)
  → interface B (e.g. AgentRestController)
      → interface B's superinterface
```

This is done recursively until `Object` is reached. The walk collects:

- `@Tag` annotations (for operation tagging)
- `@RequestMapping` on the class (for base path)
- All methods annotated with HTTP method mappings (`@GetMapping`, `@PostMapping`, etc.)

---

## Step 3 — Annotation Harvest

For each method discovered during the hierarchy walk, the plugin harvests:

| Annotation | Purpose |
|---|---|
| `@GetMapping` / `@PostMapping` / etc. | HTTP method and path |
| `@RequestMapping` | Fallback or class-level base path |
| `@Operation` | Summary and description |
| `@ApiResponse` / `@ApiResponses` | Response codes and descriptions |
| `@Parameter` | Path, query, and header parameter metadata |
| `@RequestBody` | Request body schema reference |
| `@Tag` | Operation grouping |

Annotations on the concrete class always take precedence over those on superclasses or interfaces (override semantics).

---

## Step 4 — Schema Resolution

Request body types and response types are resolved to OpenAPI schemas via reflection. The plugin inspects:

- Field names and types
- Swagger annotations: `@Schema`, `@NotNull`, `@NotBlank`, etc.
- Generic type parameters (e.g. `PagedResponse<UserDto>` is resolved to `PagedResponseUserDto`)

Each unique DTO type is registered once in `components/schemas` and referenced with `$ref` everywhere it is used.

---

## Step 5 — Spec Assembly

All collected operations, schemas, tags, servers, and security schemes are merged into a single `OpenAPI` object (using the `swagger-core` model library). Deduplication is applied:

- The same path from multiple inheritance sources is merged into one `PathItem`.
- Tags collected from multiple interfaces are unioned (not deduplicated away — see [Edge Cases](Edge-Cases#multi-tag-deduplication)).

---

## Step 6 — YAML Serialisation

The final `OpenAPI` object is serialised to YAML using `io.swagger.v3.core.util.Yaml` and written to the path configured in `outputFile`. The file is always overwritten on each build.
