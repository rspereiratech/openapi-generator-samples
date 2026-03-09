# Adding a Sample

This guide explains how to contribute a new scenario to the sample suite.

Before writing code, identify **what plugin capability** your scenario exercises. A good scenario is one where a wrong implementation would produce a visibly incorrect or missing entry in `openapi.yaml`.

---

## Checklist

- [ ] Identify the plugin capability to exercise
- [ ] Create the DTO(s)
- [ ] Create the API interface or abstract class (if needed)
- [ ] Create the controller
- [ ] Regenerate `openapi.yaml` and verify the output
- [ ] Document the scenario in `docs/Scenarios.md`
- [ ] Document any edge case in `docs/Edge-Cases.md` (if applicable)

---

## 1. Identify the capability

Ask yourself: what would break in the generated spec if the plugin had a bug here?

Examples of good motivations:
- "The plugin should detect controllers annotated with a custom stereotype annotation."
- "The plugin should inherit `@Operation` from a default interface method."
- "The plugin should resolve `List<MyDto>` as an array schema of `MyDto`."

---

## 2. Create the DTO

Add a record or class under `src/main/java/.../dto/`. Annotate fields with `@Schema` for documentation:

```java
@Schema(description = "Widget data transfer object")
public record WidgetDto(

    @Schema(description = "Unique identifier", readOnly = true, example = "1")
    Long id,

    @Schema(description = "Widget name", example = "Sprocket")
    @NotBlank String name
) {}
```

Keep DTOs minimal — only the fields needed to illustrate the scenario.

---

## 3. Create the API contract (optional)

If your scenario involves inheritance, create an interface or abstract class under `src/main/java/.../api/`:

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

---

## 4. Create the controller

Add the controller under `src/main/java/.../controller/`. Use `@RestController` or `@CustomRestController`:

```java
@CustomRestController(
    value       = "/api/v1/widgets",
    name        = "widgets",
    description = "Widget management"
)
public class WidgetController implements WidgetApi {

    @Override
    public WidgetDto getById(@PathVariable Long id) {
        return new WidgetDto(id, "Sprocket");  // stub — runtime behaviour does not matter
    }
}
```

Keep the controller as a stub. The goal is to have the right annotations in bytecode, not to implement business logic.

---

## 5. Regenerate and verify

```bash
mvn process-classes
```

Open `docs/swagger/openapi.yaml` and confirm:

- The new paths exist under the expected base path.
- The correct HTTP methods are present.
- Schemas reference the correct DTOs.
- Tags are correct.
- Any edge case behaves as expected (see [Edge Cases](Edge-Cases)).

Commit both the source changes and the updated `openapi.yaml`.

---

## 6. Document the scenario

Add a new section to `docs/Scenarios.md` following the existing format:

```markdown
## N. Your Scenario Name

**Controller:** `YourController`
**Plugin capability tested:** One-line description

### What it does
...

### What the plugin must do
...

### Expected output
...
```

If the scenario covers a tricky annotation pattern, also add an entry to `docs/Edge-Cases.md`.
