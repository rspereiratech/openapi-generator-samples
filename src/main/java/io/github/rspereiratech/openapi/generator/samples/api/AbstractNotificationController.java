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
package io.github.rspereiratech.openapi.generator.samples.api;

import io.github.rspereiratech.openapi.generator.samples.dto.NotificationProviderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Abstract base for notification controllers.
 *
 * <p>Provides two operations that are common to every notification channel
 * ({@code GET /providers} and {@code GET /health}), annotated with their
 * routing and documentation metadata.  Concrete subclasses inherit both
 * operations without redeclaring any annotations.
 *
 * <p>Demonstrates <strong>abstract-class annotation inheritance</strong>:
 * the generator walks the superclass chain to discover these operations
 * in addition to those declared on the concrete class and its interfaces.
  *
 * @author ruispereira
 */
public abstract class AbstractNotificationController {

    @Operation(
            summary     = "List notification providers",
            description = "Returns all configured delivery providers and their current availability."
    )
    @ApiResponse(responseCode = "200", description = "Provider list returned")
    @GetMapping("/providers")
    public abstract List<NotificationProviderDto> listProviders();

    @Operation(
            summary     = "Health check",
            description = "Returns a simple status string indicating whether the notification " +
                          "subsystem is operational."
    )
    @ApiResponse(responseCode = "200", description = "Subsystem is healthy")
    @GetMapping("/health")
    public abstract String healthCheck();
}
