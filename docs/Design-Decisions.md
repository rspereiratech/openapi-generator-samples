# Design Decisions

This page documents the key architectural choices made in the plugin and the reasoning behind each one.

---

## Offline generation (no Spring context)

**Decision:** Generate the OpenAPI spec by scanning bytecode directly, without starting a Spring `ApplicationContext`.

**Why:** The standard approach — using `springdoc-openapi` at runtime — requires the application to boot. This is impractical in many CI pipelines (missing databases, external services, credentials) and adds significant build time. Offline generation produces the same document from the `.class` files alone, in a fraction of the time.

**Trade-off:** Some dynamic behaviour (e.g. conditionally registered beans, runtime request mappings) cannot be captured. For the vast majority of REST APIs with static routing this is not a limitation.

---

## Binding to `process-classes`, not `package`

**Decision:** The plugin executes during the `process-classes` Maven lifecycle phase rather than `package`.

**Why:** At `process-classes` the compiler has finished but no JAR has been assembled yet. This keeps the generation fast and means the spec is available before packaging — useful when other modules in a multi-module build depend on the generated YAML.

---

## Recursive meta-annotation traversal

**Decision:** Controller detection walks the annotation graph recursively, not just the immediate annotations on a class.

**Why:** Real codebases use composed annotations extensively. Requiring `@RestController` to be present directly on every controller class would break any project that uses a custom stereotype annotation (like `@CustomRestController` in this sample). Recursive traversal makes the plugin work naturally with any annotation composition depth.

---

## Full type hierarchy walk

**Decision:** The plugin traverses superclasses and interfaces recursively when collecting operations, rather than only inspecting the concrete class.

**Why:** Inherited operations are a first-class pattern in Spring MVC. Abstract base classes (`AbstractCrudApi`) and interfaces with default mappings are common in production codebases. Ignoring inherited annotations would produce an incomplete spec.

---

## Union semantics for `@Tag` collection

**Decision:** When the same interface is reachable through multiple paths in the type hierarchy, all `@Tag` annotations from all paths are collected and unioned.

**Why:** The alternative — `putIfAbsent` deduplication — silently drops tags that arrive via a second inheritance path. This was the bug that the `AgentController` scenario was written to expose. A controller that implements two interfaces, both extending a common tagged interface, should appear under both tags if each interface adds its own tag.

See [Edge Cases — Multi-tag deduplication](Edge-Cases#multi-tag-deduplication) for the full breakdown.

---

## Generic schema naming (`PagedResponseUserDto`)

**Decision:** Generic response wrappers are resolved to concrete schema names by concatenating the raw type name and the type argument name (e.g. `PagedResponse<UserDto>` → `PagedResponseUserDto`).

**Why:** OpenAPI 3.0 does not support generic schemas. Each concrete parameterisation must be its own named schema in `components/schemas`. Generating a predictable, readable name avoids collisions and makes the spec easier to consume by code generators downstream.

---

## Output file always overwritten

**Decision:** The plugin overwrites `outputFile` unconditionally on every build.

**Why:** Incremental or merge behaviour would risk leaving stale operations in the spec when endpoints are deleted or renamed. A full rewrite guarantees the output always reflects the current state of the codebase.
