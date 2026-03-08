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

import java.time.LocalDateTime;

/**
 * Represents a user returned by the API.
  *
 * @author ruispereira
 */
@Schema(description = "User data transfer object")
public record UserDto(
        @Schema(description = "Unique identifier of the user", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Full name of the user", example = "Maria Silva", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Unique email address", example = "maria@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Role assigned to the user", example = "ADMIN", allowableValues = {"USER", "ADMIN", "MANAGER"})
        String role,

        @Schema(description = "Whether the account is active", example = "true")
        boolean active,

        @Schema(description = "Timestamp when the account was created", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt,

        @Schema(description = "Timestamp of the last modification", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime updatedAt
) {}
