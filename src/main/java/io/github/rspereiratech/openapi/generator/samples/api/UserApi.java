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

import io.github.rspereiratech.openapi.generator.samples.dto.CreateUserRequest;
import io.github.rspereiratech.openapi.generator.samples.dto.PagedResponse;
import io.github.rspereiratech.openapi.generator.samples.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

/**
 * Contract for the User resource.
 *
 * <p>Spring MVC mapping annotations and SpringDoc/Swagger documentation
 * annotations are co-located here so that the interface acts as both the
 * routing contract and the API documentation source.  The implementing
 * {@code UserController} only needs {@code @RestController}.
 *
 * <p>The {@code listUsers} method also demonstrates that a normally-ignored
 * parameter type ({@link java.util.Locale}) is included in the spec when an
 * explicit {@code @Parameter(schema = @Schema(type = "string"))} override is
 * declared on it.
  *
 * @author ruispereira
 */
@Tag(name = "users", description = "User management operations")
@RequestMapping("/api/v1/users")
public interface UserApi {

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    @Operation(
            summary     = "List users",
            description = "Returns a paginated list of users, optionally filtered by name or role."
                        + " The optional {@code locale} parameter demonstrates that a normally-ignored"
                        + " type (java.util.Locale) is included in the spec when an explicit"
                        + " @Parameter(schema = @Schema(type = \"string\")) override is declared."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    @GetMapping
    PagedResponse<UserDto> listUsers(
            @Parameter(description = "Filter by name fragment (case-insensitive)", example = "Maria")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filter by role", example = "ADMIN")
            @RequestParam(required = false) String role,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20") int size,

            /**
             * Locale is normally an ignored type and would be silently dropped from the spec.
             * The explicit {@code @Parameter(schema = @Schema(type = "string"))} annotation
             * overrides the ignore rule and forces it to appear as a BCP-47 language tag.
             */
            @Parameter(
                    description = "BCP-47 language tag for response localisation (e.g. en-US, pt-BR)."
                                + " Demonstrates that ignored types (java.util.Locale) are included"
                                + " when an explicit @Parameter(schema=@Schema(type=\"string\")) override is present.",
                    schema = @Schema(type = "string"),
                    example = "en-US"
            )
            @RequestParam(required = false) Locale locale
    );

    @Operation(
            summary     = "Get user by ID",
            description = "Returns the full profile of a single user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    UserDto getUserById(
            @Parameter(description = "Unique identifier of the user", required = true, example = "42")
            @PathVariable Long id
    );

    // ------------------------------------------------------------------
    // WRITE
    // ------------------------------------------------------------------

    @Operation(
            summary     = "Create user",
            description = "Registers a new user account. The email must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for the new user",
                    required    = true
            )
            @RequestBody CreateUserRequest request
    );

    @Operation(
            summary     = "Update user",
            description = "Replaces all mutable fields of an existing user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    UserDto updateUser(
            @Parameter(description = "Unique identifier of the user to update", required = true, example = "42")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user data",
                    required    = true
            )
            @RequestBody CreateUserRequest request
    );

    @Operation(
            summary     = "Delete user",
            description = "Permanently removes a user account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(
            @Parameter(description = "Unique identifier of the user to delete", required = true, example = "42")
            @PathVariable Long id
    );

    // ------------------------------------------------------------------
    // SEARCH
    // ------------------------------------------------------------------

    @Operation(
            summary     = "Search users",
            description = "Full-text search across name and email fields."
    )
    @ApiResponse(responseCode = "200", description = "Search results")
    @GetMapping("/search")
    PagedResponse<UserDto> searchUsers(
            @Parameter(description = "Search query string", required = true, example = "silva")
            @RequestParam String q,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size
    );
}
