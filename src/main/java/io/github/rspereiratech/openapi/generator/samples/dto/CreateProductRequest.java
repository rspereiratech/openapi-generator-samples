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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request payload for creating a new product.
 *
 * <p>This DTO is the primary sample for Bean Validation constraint propagation.
 * Every supported Jakarta validation annotation is exercised here so that the
 * generated OpenAPI schema reflects the correct numeric bounds, string lengths,
 * nullability flags and regex patterns.
 *
 * <p>Constraint → OpenAPI schema mapping:
 * <ul>
 *   <li>{@link NotBlank} / {@link NotNull} → {@code nullable: false}</li>
 *   <li>{@link Size} → {@code minLength} / {@code maxLength}</li>
 *   <li>{@link DecimalMin} / {@link DecimalMax} → {@code minimum} / {@code maximum}</li>
 *   <li>{@link Min} / {@link Max} → {@code minimum} / {@code maximum}</li>
 *   <li>{@link Pattern} → {@code pattern}</li>
 * </ul>
 *
 * @author ruispereira
 */
@Schema(description = "Payload for creating a new product")
public record CreateProductRequest(

        @NotBlank
        @Size(min = 2, max = 100)
        @Schema(description = "Product name", example = "Wireless Keyboard",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Size(max = 500)
        @Schema(description = "Detailed description", example = "Compact bluetooth keyboard")
        String description,

        @NotNull
        @DecimalMin("0.01")
        @DecimalMax("99999.99")
        @Schema(description = "Unit price in EUR", example = "49.99",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @NotNull
        @Pattern(regexp = "^[A-Z_]+$")
        @Schema(description = "Product category code", example = "ELECTRONICS",
                allowableValues = {"ELECTRONICS", "CLOTHING", "FOOD", "BOOKS"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        String category,

        @Min(0)
        @Max(9999)
        @Schema(description = "Initial stock quantity", example = "100")
        int stock

) {}
