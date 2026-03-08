# Contributing

Thank you for your interest in contributing to `openapi-generator-samples`.

This module is the reference sample suite for the [`openapi-generator-maven-plugin`](https://github.com/rspereiratech/openapi-generator-maven-plugin). Each scenario here validates a specific plugin capability. Contributions that cover new edge cases or annotation patterns are especially welcome.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Adding a New Sample Scenario](#adding-a-new-sample-scenario)
- [Regenerating the OpenAPI Spec](#regenerating-the-openapi-spec)
- [Validating Your Changes](#validating-your-changes)
- [Code Style](#code-style)
- [Submitting a Pull Request](#submitting-a-pull-request)

---

## Prerequisites

- Java 21+
- Maven 3.9+
- `openapi-generator-parent` installed locally (see [README](README.md#prerequisites))

---

## Adding a New Sample Scenario

A scenario typically consists of three layers: a **DTO**, an **API interface or abstract class**, and a **controller**. Follow the structure below.

### 1. Create the DTO

Add a new record or class under `src/main/java/.../dto/`. Use Swagger annotations to document each field:

```java
@Schema(description = "Widget data transfer object")
public record WidgetDto(

    @Schema(description = "Unique identifier", readOnly = true, example = "1")
    Long id,

    @Schema(description = "Widget name", example = "Sprocket")
    @NotBlank String name
) {}
```

### 2. Define the API contract (optional)

If the scenario involves inherited operations, create an interface or abstract class under `src/main/java/.../api/`:

```java
@Tag(name = "widgets", description = "Widget management")
public interface WidgetApi {

    @Operation(summary = "Get widget by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Widget found"),
        @ApiResponse(responseCode = "404", description = "Widget not found")
    })
    @GetMapping("/{id}")
    WidgetDto getById(@PathVariable Long id);
}
```

### 3. Create the controller

Add the controller under `src/main/java/.../controller/`. Use either `@RestController` or the composed `@CustomRestController`:

```java
@CustomRestController(
    value       = "/api/v1/widgets",
    name        = "widgets",
    description = "Widget management"
)
public class WidgetController implements WidgetApi {

    @Override
    public WidgetDto getById(@PathVariable Long id) {
        return new WidgetDto(id, "Sprocket");
    }
}
```

### 4. Document the scenario in the README

Add a new subsection under [Scenarios Covered](README.md#scenarios-covered) describing what plugin behaviour the scenario exercises and why it matters.

---

## Regenerating the OpenAPI Spec

After making changes, regenerate `docs/swagger/openapi.yaml` by running:

```bash
mvn process-classes
```

The plugin binds to the `process-classes` phase and overwrites the output file automatically.

Always **commit the updated `openapi.yaml`** together with your source changes so reviewers can see the exact diff in the generated output.

---

## Validating Your Changes

Before opening a PR, verify that:

- [ ] `mvn process-classes` completes without errors.
- [ ] The generated `openapi.yaml` contains the new paths and schemas you expect.
- [ ] No existing paths or schemas were unintentionally removed or modified.
- [ ] The scenario is documented in the README.

---

## Code Style

- Follow the existing package and naming conventions.
- Keep controllers as stubs — they exist only to be scanned, not to run.
- Add Javadoc to any new API interface or abstract class, following the pattern in `AbstractCrudApi`.
- Do not add production logic, persistence, or Spring Boot bootstrap code.

---

## Submitting a Pull Request

1. Fork the repository and create a branch from `master`.
2. Implement your scenario following the steps above.
3. Regenerate and commit `docs/swagger/openapi.yaml`.
4. Open a pull request with a clear description of what plugin behaviour the scenario covers.
