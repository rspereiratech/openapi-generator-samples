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

import io.github.rspereiratech.openapi.generator.samples.api.AbstractNotificationController;
import io.github.rspereiratech.openapi.generator.samples.api.NotificationApi;
import io.github.rspereiratech.openapi.generator.samples.dto.NotificationDto;
import io.github.rspereiratech.openapi.generator.samples.dto.NotificationProviderDto;
import io.github.rspereiratech.openapi.generator.samples.dto.SendNotificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Email-channel notification controller.
 *
 * <p>This class brings together all three levels of the annotation-inheritance
 * hierarchy supported by the generator:
 *
 * <ol>
 *   <li><strong>Interface</strong> ({@link NotificationApi}) — the API contract:
 *       base path, tag, and the three core operations
 *       ({@code POST /}, {@code GET /{id}}, {@code DELETE /{id}}) with their
 *       full SpringDoc documentation.</li>
 *   <li><strong>Abstract superclass</strong> ({@link AbstractNotificationController}) —
 *       shared operations ({@code GET /providers}, {@code GET /health}) that every
 *       notification channel exposes, with routing and documentation declared
 *       only once.</li>
 *   <li><strong>This class</strong> — channel-specific operation
 *       ({@code GET /pending}) declared directly, plus {@code @RestController}
 *       as the only annotation needed here.</li>
 * </ol>
 *
 * <p>The generator must resolve the effective annotation set by walking the
 * full type hierarchy: concrete class → abstract superclass → interface.
  *
 * @author ruispereira
 */
@RestController
public class EmailNotificationController
        extends AbstractNotificationController
        implements NotificationApi {

    // ------------------------------------------------------------------
    // NotificationApi — routing and docs come entirely from the interface
    // ------------------------------------------------------------------

    @Override
    public NotificationDto sendNotification(SendNotificationRequest request) {
        return new NotificationDto(1L, request.type(), request.recipient(),
                request.subject(), "PENDING", LocalDateTime.now(), null);
    }

    @Override
    public NotificationDto getNotification(Long id) {
        return stubNotification(id);
    }

    @Override
    public void cancelNotification(Long id) {
        // no-op stub
    }

    // ------------------------------------------------------------------
    // AbstractNotificationController — routing and docs from the abstract class
    // ------------------------------------------------------------------

    @Override
    public List<NotificationProviderDto> listProviders() {
        return List.of(
                new NotificationProviderDto("sendgrid", "EMAIL", true),
                new NotificationProviderDto("twilio",   "SMS",   true),
                new NotificationProviderDto("firebase", "PUSH",  false)
        );
    }

    @Override
    public String healthCheck() {
        return "OK";
    }

    // ------------------------------------------------------------------
    // Channel-specific operation declared on this class
    // ------------------------------------------------------------------

    @Operation(
            summary     = "List pending notifications",
            description = "Returns all notifications that are queued but not yet delivered."
    )
    @ApiResponse(responseCode = "200", description = "Pending notifications returned")
    @GetMapping("/pending")
    public List<NotificationDto> listPending() {
        return List.of(stubNotification(1L), stubNotification(2L));
    }

    // ------------------------------------------------------------------

    /**
     * Returns a hard-coded {@link io.github.rspereiratech.openapi.generator.samples.dto.NotificationDto}
     * for demonstration and testing purposes.
     *
     * @param id the notification ID to embed in the stub
     * @return a pre-populated stub notification
     */
    private static NotificationDto stubNotification(Long id) {
        return new NotificationDto(id, "EMAIL", "user" + id + "@example.com",
                "Notification " + id, "SENT",
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 1, 0, 1));
    }
}
