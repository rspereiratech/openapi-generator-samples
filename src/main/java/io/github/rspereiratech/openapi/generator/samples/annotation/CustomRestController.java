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
package io.github.rspereiratech.openapi.generator.samples.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composed annotation that combines {@code @RestController}, {@code @RequestMapping},
 * and {@code @Tag} in a single declaration.
 *
 * <p>Controllers annotated with {@code @CustomRestController} are detected by the
 * OpenAPI generator through recursive meta-annotation traversal: the generator
 * sees that {@code @CustomRestController} is meta-annotated with {@code @RestController}
 * and therefore treats the class as a controller, even though {@code @RestController}
 * is not present directly on the class.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * @CustomRestController(
 *     value       = "/api/v1/orders",
 *     name        = "orders",
 *     description = "Order management"
 * )
 * public class OrderController { ... }
 * }</pre>
  *
 * @author ruispereira
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
@RequestMapping
@Tag(name = "")
public @interface CustomRestController {

    /** Base path for all endpoints in this controller (maps to {@code @RequestMapping.value()}). */
    String[] value() default {};

    /** OpenAPI tag name for this controller's operations (maps to {@code @Tag.name()}). */
    String name() default "";

    /** OpenAPI tag description (maps to {@code @Tag.description()}). */
    String description() default "";
}
