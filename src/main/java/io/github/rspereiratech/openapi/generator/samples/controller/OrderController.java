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

import io.github.rspereiratech.openapi.generator.samples.annotation.CustomRestController;
import io.github.rspereiratech.openapi.generator.samples.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order controller demonstrating custom annotation support.
 *
 * <p>This class carries {@code @CustomRestController} instead of the usual
 * {@code @RestController + @RequestMapping + @Tag} triple. The OpenAPI generator
 * detects it as a controller through recursive meta-annotation traversal:
 * {@code @CustomRestController} → {@code @RestController} (FQN match).
 *
 * <p>The base path ({@code /api/v1/orders}) and OpenAPI tag ({@code orders})
 * are read from the composed annotation's own attributes, so no additional
 * annotations are needed on the class.
  *
 * @author ruispereira
 */
@CustomRestController(
        value       = "/api/v1/orders",
        name        = "orders",
        description = "Order management"
)
public class OrderController {

    @Operation(summary = "List all orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping
    public List<OrderDto> listOrders() {
        return List.of(stubOrder(1L), stubOrder(2L));
    }

    @Operation(summary = "Get order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public OrderDto getOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        return stubOrder(id);
    }

    @Operation(summary = "Place a new order", description = "Creates a new order for the given customer.")
    @ApiResponse(responseCode = "201", description = "Order placed successfully")
    @PostMapping
    public OrderDto createOrder(@RequestBody OrderDto order) {
        return new OrderDto(100L, order.customerId(), "PENDING", order.total(), LocalDateTime.now());
    }

    @Operation(summary = "Cancel an order")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public void cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true) @PathVariable Long id) {
        // no-op stub
    }

    // ------------------------------------------------------------------

    /**
     * Returns a hard-coded {@link io.github.rspereiratech.openapi.generator.samples.dto.OrderDto}
     * for demonstration and testing purposes.
     *
     * @param id the order ID to embed in the stub
     * @return a pre-populated stub order
     */
    private static OrderDto stubOrder(Long id) {
        return new OrderDto(id, 42L, "PENDING",
                BigDecimal.valueOf(149.99 * id), LocalDateTime.of(2024, 1, 1, 0, 0));
    }
}
