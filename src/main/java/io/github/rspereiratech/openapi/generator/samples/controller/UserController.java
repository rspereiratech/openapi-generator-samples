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

import io.github.rspereiratech.openapi.generator.samples.api.UserApi;
import io.github.rspereiratech.openapi.generator.samples.dto.CreateUserRequest;
import io.github.rspereiratech.openapi.generator.samples.dto.PagedResponse;
import io.github.rspereiratech.openapi.generator.samples.dto.UserDto;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * REST controller implementing {@link UserApi}.
 *
 * <p>All Spring MVC routing annotations ({@code @GetMapping}, {@code @PostMapping}, …)
 * and SpringDoc documentation annotations ({@code @Operation}, {@code @Tag},
 * {@code @Parameter}, …) are declared on the {@code UserApi} interface.
 * This class only needs {@code @RestController} – a clean separation between
 * the API contract and the implementation.
  *
 * @author ruispereira
 */
@RestController
public class UserController implements UserApi {

    @Override
    public PagedResponse<UserDto> listUsers(String name, String role, int page, int size, Locale locale) {
        return new PagedResponse<>(List.of(stubUser(1L), stubUser(2L)), page, size, 2);
    }

    @Override
    public UserDto getUserById(Long id) {
        return stubUser(id);
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        return new UserDto(99L, request.name(), request.email(), request.role(),
                true, LocalDateTime.now(), null);
    }

    @Override
    public UserDto updateUser(Long id, CreateUserRequest request) {
        UserDto original = stubUser(id);
        return new UserDto(id, request.name(), request.email(), request.role(),
                original.active(), original.createdAt(), LocalDateTime.now());
    }

    @Override
    public void deleteUser(Long id) {
        // no-op stub
    }

    @Override
    public PagedResponse<UserDto> searchUsers(String q, int page, int size) {
        return new PagedResponse<>(List.of(), page, size, 0);
    }

    // ------------------------------------------------------------------

    /**
     * Returns a hard-coded {@link io.github.rspereiratech.openapi.generator.samples.dto.UserDto}
     * for demonstration and testing purposes.
     *
     * @param id the user ID to embed in the stub
     * @return a pre-populated stub user
     */
    private static UserDto stubUser(Long id) {
        return new UserDto(id, "User " + id, "user" + id + "@example.com",
                "USER", true, LocalDateTime.of(2024, 1, 1, 0, 0), null);
    }
}
