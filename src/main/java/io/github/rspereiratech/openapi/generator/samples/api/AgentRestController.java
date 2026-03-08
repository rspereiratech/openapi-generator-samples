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

import io.github.rspereiratech.openapi.generator.samples.dto.AgentDto;
import io.github.rspereiratech.openapi.generator.samples.dto.AgentGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * REST contract for the Agent resource.
 *
 * <p>Extends {@link GenericVertexRestController} with the concrete types
 * {@code AgentDto} / {@code String} and adds the agent-specific
 * {@code GET /{id}/group} operation.
 *
 * <p>Both {@code @Tag} annotations — "Generic REST API" (from
 * {@link GenericVertexRestController}) and "Agents" (from this interface) —
 * must appear on every operation generated for the implementing controller.
 * This is the scenario that validates the multi-tag collection fix in
 * {@code ControllerProcessorImpl.resolveTags()}.
 *
 * @author ruispereira
 */
@Tag(name = "Agents", description = "Agent management operations")
public interface AgentRestController extends GenericVertexRestController<AgentDto, String> {

    @Operation(summary = "Get Agent Group by Agent ID",
               description = "Returns the agent group associated with the given agent ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agent group found"),
            @ApiResponse(responseCode = "404", description = "Agent or group not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}/group")
    AgentGroupDto getAgentGroupByAgentId(
            @Parameter(description = "Logical agent ID", required = true, example = "agent-123")
            @PathVariable String id
    );
}
