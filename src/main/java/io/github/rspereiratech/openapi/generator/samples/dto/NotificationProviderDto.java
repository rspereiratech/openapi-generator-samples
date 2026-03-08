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
 * Describes an active notification delivery provider.
  *
 * @author ruispereira
 */
@Schema(description = "Notification provider descriptor")
public record NotificationProviderDto(
        @Schema(description = "Provider name", example = "sendgrid",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Channel type handled by this provider", example = "EMAIL",
                allowableValues = {"EMAIL", "SMS", "PUSH"})
        String type,

        @Schema(description = "Whether the provider is currently reachable", example = "true")
        boolean available
) {}
