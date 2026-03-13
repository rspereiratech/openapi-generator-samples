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
package org.springframework.data.domain;

import java.util.List;

/**
 * Minimal stub for {@code org.springframework.data.domain.Page}.
 *
 * <p>Provided so the samples module compiles without a full Spring Data Commons dependency.
 * The openapi-generator-maven-plugin recognises this type by its fully-qualified class name
 * and generates a {@code PageXxxDTO} component schema with the standard Spring Data page
 * structure.</p>
 *
 * @param <T> the type of elements in this page
 * @author ruispereira
 */
public interface Page<T> {

    List<T> getContent();

    int getTotalPages();

    long getTotalElements();

    boolean isFirst();

    boolean isLast();

    boolean hasNext();

    boolean hasPrevious();

    static <T> Page<T> empty() {
        return new Page<>() {
            @Override public List<T> getContent()      { return List.of(); }
            @Override public int     getTotalPages()    { return 0; }
            @Override public long    getTotalElements() { return 0L; }
            @Override public boolean isFirst()          { return true; }
            @Override public boolean isLast()           { return true; }
            @Override public boolean hasNext()          { return false; }
            @Override public boolean hasPrevious()      { return false; }
        };
    }
}
