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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Generic wrapper for paginated responses.
 *
 * @param <T> the type of items in this page
  *
 * @author ruispereira
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PagedResponse<T> {

    @Schema(description = "Items in the current page")
    private List<T> content;

    @Schema(description = "Current page number (0-based)", example = "0")
    private int page;

    @Schema(description = "Number of items per page", example = "20")
    private int size;

    @Schema(description = "Total number of items across all pages", example = "150")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;

    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;

    /**
     * Creates a page from a list of items and pagination metadata.
     *
     * <p>Automatically computes {@code totalPages} from {@code totalElements} and
     * {@code size}, and sets {@code last} to {@code true} when the current page
     * is the last one.
     *
     * @param content       the items in the current page
     * @param page          the current page number (0-based)
     * @param size          the maximum number of items per page
     * @param totalElements the total number of items across all pages
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content       = content;
        this.page          = page;
        this.size          = size;
        this.totalElements = totalElements;
        this.totalPages    = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.last          = (page + 1) >= this.totalPages;
    }

}
