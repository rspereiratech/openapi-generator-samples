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
 * Represents an agent group in the contact centre.
 *
 * @author ruispereira
 */
@Schema(description = "Agent group data transfer object")
public record AgentGroupDto(

        @Schema(description = "Unique identifier of the agent group", example = "group-001",
                accessMode = Schema.AccessMode.READ_ONLY)
        String id,

        @Schema(description = "Display name of the agent group", example = "Tier-1 Support",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {}
