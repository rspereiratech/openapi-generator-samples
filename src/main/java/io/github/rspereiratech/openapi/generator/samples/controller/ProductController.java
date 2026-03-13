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
package io.github.rspereiratech.openapi.generator.samples.controller;

import io.github.rspereiratech.openapi.generator.samples.api.AbstractCrudApi;
import io.github.rspereiratech.openapi.generator.samples.dto.CreateProductRequest;
import io.github.rspereiratech.openapi.generator.samples.dto.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for the {@code Product} resource.
 *
 * <p>Inherits {@code GET /{id}}, {@code DELETE /{id}} and {@code GET /}
 * — including their routing and documentation annotations — from
 * {@link AbstractCrudApi}. This controller only adds operations specific to products.
  *
 * @author ruispereira
 */
@RestController
@Tag(name = "products", description = "Product catalogue operations")
@RequestMapping("/api/v1/products")
public class ProductController extends AbstractCrudApi<ProductDto, Long> {

    @Override
    public ProductDto getById(@PathVariable Long id) {
        return stubProduct(id);
    }

    @Override
    public void deleteById(@PathVariable Long id) {
        // no-op stub
    }

    @Override
    public List<ProductDto> listAll() {
        return List.of(stubProduct(1L), stubProduct(2L));
    }

    @Operation(summary = "Create product", description = "Adds a new product to the catalogue.")
    @PostMapping
    public ProductDto createProduct(@RequestBody CreateProductRequest request) {
        return new ProductDto(100L, request.name(), request.description(),
                request.price(), request.category(), request.stock());
    }

    @Operation(summary = "List products by category",
               description = "Returns all products belonging to the given category.")
    @GetMapping("/category/{category}")
    public List<ProductDto> listByCategory(@PathVariable String category) {
        return List.of(stubProduct(1L));
    }

    /**
     * Demonstrates <em>virtual parameters</em>: the {@code Pageable} Spring parameter is
     * hidden from the spec via {@code @Parameter(hidden = true)}, and the individual
     * pagination query params are declared explicitly at method level using
     * {@code @Parameters}. The generator picks up the method-level annotations and emits
     * {@code page}, {@code size}, and {@code sort} as distinct query parameters — matching
     * SpringDoc's behaviour for this pattern.
     */
    @Operation(
            summary     = "Search products",
            description = "Returns a paginated, optionally filtered list of products. "
                        + "Pagination is controlled via the virtual page/size/sort parameters."
    )
    @ApiResponse(responseCode = "200", description = "Paginated product list")
    @Parameters({
            @Parameter(name = "page",     in = ParameterIn.QUERY, schema = @Schema(implementation = Integer.class), description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size",     in = ParameterIn.QUERY, schema = @Schema(implementation = Integer.class), description = "Number of records per page",    example = "20"),
            @Parameter(name = "sort",     in = ParameterIn.QUERY, schema = @Schema(implementation = String.class),  description = "Sort criteria: property(,asc|desc). Multiple values supported.", example = "name,asc"),
            @Parameter(name = "category", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class),  description = "Filter by category (optional)", example = "ELECTRONICS")
    })
    @GetMapping("/search")
    public Page<ProductDto> search(
            @RequestParam(required = false) String category,
            @Parameter(hidden = true) Pageable pageable) {
        return Page.empty();
    }

    // ------------------------------------------------------------------

    /**
     * Returns a hard-coded {@link io.github.rspereiratech.openapi.generator.samples.dto.ProductDto}
     * for demonstration and testing purposes.
     *
     * @param id the product ID to embed in the stub
     * @return a pre-populated stub product
     */
    private static ProductDto stubProduct(Long id) {
        return new ProductDto(id, "Product " + id, "Description of product " + id,
                BigDecimal.valueOf(9.99 * id), "ELECTRONICS", 50);
    }
}
