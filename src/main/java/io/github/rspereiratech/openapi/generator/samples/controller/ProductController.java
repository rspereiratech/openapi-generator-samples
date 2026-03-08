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
import io.github.rspereiratech.openapi.generator.samples.dto.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ProductDto createProduct(@RequestBody ProductDto product) {
        return new ProductDto(100L, product.name(), product.description(),
                product.price(), product.category(), product.stock());
    }

    @Operation(summary = "List products by category",
               description = "Returns all products belonging to the given category.")
    @GetMapping("/category/{category}")
    public List<ProductDto> listByCategory(@PathVariable String category) {
        return List.of(stubProduct(1L));
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
