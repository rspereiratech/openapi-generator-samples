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
 * Represents a notification entry.
  *
 * @author ruispereira
 */
@Schema(description = "Notification data transfer object")
public record NotificationDto(
        @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Delivery channel", example = "EMAIL",
                allowableValues = {"EMAIL", "SMS", "PUSH"}, requiredMode = Schema.RequiredMode.REQUIRED)
        String type,

        @Schema(description = "Recipient address (email, phone number, or device token)",
                example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String recipient,

        @Schema(description = "Message subject or title", example = "Your order has shipped")
        String subject,

        @Schema(description = "Current delivery status", example = "SENT",
                allowableValues = {"PENDING", "SENT", "FAILED", "CANCELLED"})
        String status,

        @Schema(description = "When the notification was created", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt,

        @Schema(description = "When the notification was successfully delivered; null if not yet sent",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime sentAt
) {}
