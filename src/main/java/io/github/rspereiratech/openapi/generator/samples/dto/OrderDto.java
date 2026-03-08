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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an order in the system.
  *
 * @author ruispereira
 */
@Schema(description = "Order data transfer object")
public record OrderDto(
        @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "ID of the customer who placed the order", example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long customerId,

        @Schema(description = "Current order status", example = "PENDING",
                allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
        String status,

        @Schema(description = "Total order amount in EUR", example = "149.99",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal total,

        @Schema(description = "Timestamp when the order was placed", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt
) {}
