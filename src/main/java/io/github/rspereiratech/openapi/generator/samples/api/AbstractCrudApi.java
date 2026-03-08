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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Abstract base "contract" for simple CRUD resources.
 *
 * <p>Subclasses inherit the common {@code GET /{id}} and {@code DELETE /{id}}
 * operations — both their Spring MVC routing annotations <em>and</em> their
 * SpringDoc documentation — without having to redeclare them.
 *
 * <p>This demonstrates that the generator walks the full type hierarchy:
 * concrete class → abstract superclass → interfaces.
 *
 * @param <T>  resource DTO type
 * @param <ID> identifier type
  *
 * @author ruispereira
 */
public abstract class AbstractCrudApi<T, ID> {

    @Operation(
            summary     = "Get by ID",
            description = "Returns a single resource identified by its primary key."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource found"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @GetMapping("/{id}")
    public abstract T getById(
            @Parameter(description = "Primary key of the resource", required = true)
            @PathVariable ID id
    );

    @Operation(
            summary     = "Delete by ID",
            description = "Permanently removes a resource."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deleted"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public abstract void deleteById(
            @Parameter(description = "Primary key of the resource to delete", required = true)
            @PathVariable ID id
    );

    /**
     * List all resources. Subclasses override this to add pagination or
     * filtering — this default declaration provides a baseline operation.
     */
    @Operation(summary = "List all", description = "Returns every resource (unfiltered).")
    @ApiResponse(responseCode = "200", description = "Full list returned")
    @GetMapping
    public abstract List<T> listAll();
}
