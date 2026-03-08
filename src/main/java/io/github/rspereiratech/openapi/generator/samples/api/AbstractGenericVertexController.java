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

import java.util.List;

/**
 * Abstract base implementation of {@link GenericVertexRestController}.
 *
 * <p>Provides stub implementations of all generic CRUD operations so that
 * concrete subclasses (e.g. {@code AgentController}) only need to override
 * the methods they actually handle, or delegate to a service layer.
 *
 * <p>This class sits between the generic interface and the concrete controller
 * in the type hierarchy — exactly the role played by
 * {@code GenericVertexRestControllerImpl} in the real service.  Its presence
 * means that {@link GenericVertexRestController} is reachable via the
 * <em>superclass chain</em> of any concrete controller, while the
 * resource-specific interface (e.g. {@link AgentRestController}) is reachable
 * via the concrete class's <em>direct interface list</em>.  This is the
 * scenario that exercises the multi-tag collection fix.
 *
 * @param <T>  the domain DTO type
 * @param <ID> the identifier type
 * @author ruispereira
 */
public abstract class AbstractGenericVertexController<T, ID>
        implements GenericVertexRestController<T, ID> {

    @Override
    public T getById(ID id) {
        throw new UnsupportedOperationException("getById not implemented");
    }

    @Override
    public List<T> getAll(int page, int size) {
        throw new UnsupportedOperationException("getAll not implemented");
    }

    @Override
    public boolean exists(ID id) {
        throw new UnsupportedOperationException("exists not implemented");
    }

    @Override
    public T create(T dto) {
        throw new UnsupportedOperationException("create not implemented");
    }

    @Override
    public T update(ID id, T dto) {
        throw new UnsupportedOperationException("update not implemented");
    }

    @Override
    public T patch(ID id, T dto) {
        throw new UnsupportedOperationException("patch not implemented");
    }

    @Override
    public void delete(ID id) {
        throw new UnsupportedOperationException("delete not implemented");
    }
}
