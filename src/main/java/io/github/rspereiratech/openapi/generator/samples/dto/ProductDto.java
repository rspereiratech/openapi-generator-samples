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

/**
 * Represents a product in the catalogue.
  *
 * @author ruispereira
 */
@Schema(description = "Product data transfer object")
public record ProductDto(
        @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Product name", example = "Wireless Keyboard", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Detailed description", example = "Compact bluetooth keyboard")
        String description,

        @Schema(description = "Unit price in EUR", example = "49.99", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @Schema(description = "Product category", example = "ELECTRONICS",
                allowableValues = {"ELECTRONICS", "CLOTHING", "FOOD", "BOOKS"})
        String category,

        @Schema(description = "Available stock quantity", example = "150")
        int stock
) {}
