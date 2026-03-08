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

import io.github.rspereiratech.openapi.generator.samples.api.AbstractGenericVertexController;
import io.github.rspereiratech.openapi.generator.samples.api.AgentRestController;
import io.github.rspereiratech.openapi.generator.samples.dto.AgentDto;
import io.github.rspereiratech.openapi.generator.samples.dto.AgentGroupDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the Agent resource.
 *
 * <p>This class is the concrete implementation that the classpath scanner will
 * discover.  Its type hierarchy is the key scenario for multi-tag generation:
 *
 * <pre>
 *   AgentController
 *     extends AbstractGenericVertexController&lt;AgentDto, String&gt;
 *               implements GenericVertexRestController&lt;AgentDto, String&gt;
 *                            @Tag("Generic REST API")
 *     implements AgentRestController
 *                  extends GenericVertexRestController&lt;AgentDto, String&gt;
 *                  @Tag("Agents")
 * </pre>
 *
 * <p>{@link io.github.rspereiratech.openapi.generator.samples.api.GenericVertexRestController}
 * is reachable through <em>both</em> the superclass chain (via
 * {@link AbstractGenericVertexController}) and the direct interface list
 * (via {@link AgentRestController}).  Before the fix, the generator would
 * only pick up the first {@code @Tag} it encountered — "Generic REST API" —
 * because of {@code putIfAbsent} deduplication.  After the fix both tags are
 * collected and every operation carries {@code ["Generic REST API", "Agents"]}.
 *
 * @author ruispereira
 */
@RestController
@RequestMapping("/api/v1/agents")
public class AgentController
        extends AbstractGenericVertexController<AgentDto, String>
        implements AgentRestController {

    @Override
    public AgentDto getById(@PathVariable String id) {
        return stubAgent(id);
    }

    @Override
    public List<AgentDto> getAll(int page, int size) {
        return List.of(stubAgent("agent-1"), stubAgent("agent-2"));
    }

    @Override
    public boolean exists(@PathVariable String id) {
        return true;
    }

    @Override
    public AgentDto create(AgentDto dto) {
        return dto;
    }

    @Override
    public AgentDto update(@PathVariable String id, AgentDto dto) {
        return new AgentDto(id, dto.name(), dto.ssoId(), dto.ldapId());
    }

    @Override
    public AgentDto patch(@PathVariable String id, AgentDto dto) {
        return new AgentDto(id, dto.name(), dto.ssoId(), dto.ldapId());
    }

    @Override
    public void delete(@PathVariable String id) {
        // no-op stub
    }

    @Override
    public AgentGroupDto getAgentGroupByAgentId(@PathVariable String id) {
        return new AgentGroupDto("group-001", "Tier-1 Support");
    }

    // ------------------------------------------------------------------

    private static AgentDto stubAgent(String id) {
        return new AgentDto(id, "Agent " + id, id + "@example.com", "cn=" + id);
    }
}
