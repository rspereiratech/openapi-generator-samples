/*
 *   ___                   _   ___ ___
 *  / _ \ _ __  ___ _ _   /_\ | _ \_ _|
 * | (_) | '_ \/ -_) ' \ / _ \|  _/| |
 *  \___/| .__/\___|_||_/_/ \_\_| |___|   Generator
 *       |_|
 *
 * MIT License - Copyright (c) 2026 Rui Pereira
 * See LICENSE in the project root for full license information.
 */
package io.github.rspereiratech.openapi.generator.samples.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Generic REST contract for domain entities stored as graph vertices.
 *
 * <p>Declares the standard CRUD surface — routing and documentation — that
 * every vertex resource inherits.  Specific resources extend this interface
 * (e.g. {@link AgentRestController}) and add their own operations and
 * {@code @Tag}, so that SpringDoc and the offline generator both emit
 * <em>two</em> tags per operation: this one ("Generic REST API") plus the
 * resource-specific one ("Agents", "Queues", …).
 *
 * <p>Implementing classes only need {@code @RestController} and
 * {@code @RequestMapping} to provide the base path.
 *
 * @param <T>  the domain DTO type
 * @param <ID> the identifier type
 * @author ruispereira
 */
@Tag(name = "Generic REST API", description = "Generic CRUD operations for domain entities")
@RequestMapping
public interface GenericVertexRestController<T, ID> {

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    @Operation(summary = "Get entity by ID",
               description = "Returns the entity with the given ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entity found"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    T getById(
            @Parameter(description = "Logical ID of the entity", required = true)
            @PathVariable ID id
    );

    @Operation(summary = "Get all entities",
               description = "Returns a paginated list of entities, with optional filtering and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entities fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    List<T> getAll(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "Check if entity exists",
               description = "Verifies whether an entity with the given ID exists")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entity exists"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}/exists")
    boolean exists(
            @Parameter(description = "Logical ID of the entity", required = true)
            @PathVariable ID id
    );

    // ------------------------------------------------------------------
    // WRITE
    // ------------------------------------------------------------------

    @Operation(summary = "Create new entity",
               description = "Creates a new instance of the entity")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entity created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    T create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Entity data", required = true)
            @RequestBody T dto
    );

    @Operation(summary = "Update existing entity",
               description = "Replaces the data of the entity with the given ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}")
    T update(
            @Parameter(description = "Logical ID of the entity", required = true)
            @PathVariable ID id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated entity data", required = true)
            @RequestBody T dto
    );

    @Operation(summary = "Partially update an existing entity",
               description = "Applies partial modifications to the entity identified by the given ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entity patched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patch data"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PatchMapping("/{id}")
    T patch(
            @Parameter(description = "Logical ID of the entity", required = true)
            @PathVariable ID id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Partial entity data", required = true)
            @RequestBody T dto
    );

    @Operation(summary = "Delete entity by ID",
               description = "Deletes the entity with the specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entity deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @Parameter(description = "Logical ID of the entity", required = true)
            @PathVariable ID id
    );
}
