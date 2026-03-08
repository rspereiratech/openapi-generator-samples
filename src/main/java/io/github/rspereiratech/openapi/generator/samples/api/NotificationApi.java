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

import io.github.rspereiratech.openapi.generator.samples.dto.NotificationDto;
import io.github.rspereiratech.openapi.generator.samples.dto.SendNotificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Contract for the Notification resource.
 *
 * <p>All routing ({@code @RequestMapping}, {@code @PostMapping}, …) and
 * documentation ({@code @Tag}, {@code @Operation}, {@code @Parameter}, …)
 * annotations live here.  Implementing controllers need only
 * {@code @RestController}.
 *
 * <p>Demonstrates <strong>interface-level annotation inheritance</strong>:
 * the generator discovers these annotations even when they are absent from
 * the concrete class that implements this interface.
  *
 * @author ruispereira
 */
@Tag(name = "notifications", description = "Notification delivery operations")
@RequestMapping("/api/v1/notifications")
public interface NotificationApi {

    @Operation(
            summary     = "Send notification",
            description = "Dispatches a notification to the given recipient via the requested channel."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Notification accepted for delivery"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "503", description = "No provider available for the requested channel")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    NotificationDto sendNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Notification to send", required = true)
            @RequestBody SendNotificationRequest request
    );

    @Operation(
            summary     = "Get notification",
            description = "Returns the current status and metadata of a single notification."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @GetMapping("/{id}")
    NotificationDto getNotification(
            @Parameter(description = "Notification ID", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(
            summary     = "Cancel notification",
            description = "Cancels a pending notification. Has no effect if already delivered."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification cancelled"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "409", description = "Notification already delivered or failed")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelNotification(
            @Parameter(description = "Notification ID to cancel", required = true, example = "1")
            @PathVariable Long id
    );
}
