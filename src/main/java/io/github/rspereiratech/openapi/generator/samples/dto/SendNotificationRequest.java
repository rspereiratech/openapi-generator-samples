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
 * Request body for sending a notification.
  *
 * @author ruispereira
 */
@Schema(description = "Payload for dispatching a notification")
public record SendNotificationRequest(
        @Schema(description = "Delivery channel", example = "EMAIL",
                allowableValues = {"EMAIL", "SMS", "PUSH"}, requiredMode = Schema.RequiredMode.REQUIRED)
        String type,

        @Schema(description = "Recipient address (email, phone number, or device token)",
                example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String recipient,

        @Schema(description = "Message subject or title", example = "Your order has shipped",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String subject,

        @Schema(description = "Full message body", example = "Your order #42 is on its way!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String body,

        @Schema(description = "Delivery priority", example = "NORMAL",
                allowableValues = {"LOW", "NORMAL", "HIGH"}, defaultValue = "NORMAL")
        String priority
) {}
