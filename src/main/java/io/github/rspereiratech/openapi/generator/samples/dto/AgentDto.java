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
 * Represents an agent in the contact centre.
 *
 * @author ruispereira
 */
@Schema(description = "Agent data transfer object")
public record AgentDto(

        @Schema(description = "Unique identifier of the agent", example = "agent-123",
                accessMode = Schema.AccessMode.READ_ONLY)
        String id,

        @Schema(description = "Display name of the agent", example = "Alice Smith",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "SSO identifier", example = "alice.smith@example.com")
        String ssoId,

        @Schema(description = "LDAP identifier", example = "cn=alice,ou=agents,dc=example,dc=com")
        String ldapId
) {}
