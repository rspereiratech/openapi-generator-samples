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
package io.github.rspereiratech.openapi.generator.samples.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body used to create or update a user.
  *
 * @author ruispereira
 */
@Schema(description = "Payload for creating or updating a user")
public record CreateUserRequest(
        @Schema(description = "Full name of the user", example = "João Costa", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Unique email address", example = "joao@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Plain-text password (will be hashed server-side)", example = "s3cr3t!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @Schema(description = "Role to assign to the user", example = "USER", allowableValues = {"USER", "ADMIN", "MANAGER"},
                defaultValue = "USER")
        String role
) {}
