package org.jboss.resteasy.test.core.basic.resource;

import java.util.Collection;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.PUT;

public class AnnotationInheritanceGenericsAbstract<ENTITY_TYPE, ID_TYPE>
        implements AnnotationInheritanceGenericsInterface<ENTITY_TYPE, ID_TYPE> {

    @Override
    public Collection<ENTITY_TYPE> get() {
        throw new NotAllowedException((Throwable) null);
    }

    @Override
    public ENTITY_TYPE get(final ID_TYPE id) {
        throw new NotAllowedException((Throwable) null);
    }

    @Override
    public ENTITY_TYPE post(final ENTITY_TYPE entity) {
        throw new NotAllowedException((Throwable) null);
    }

    @Override
    public ENTITY_TYPE put(final ID_TYPE id, final ENTITY_TYPE entity) {
        throw new NotAllowedException((Throwable) null);
    }

    @PUT
    public ENTITY_TYPE put(final ENTITY_TYPE entity) {
        throw new NotAllowedException((Throwable) null);
    }

}
