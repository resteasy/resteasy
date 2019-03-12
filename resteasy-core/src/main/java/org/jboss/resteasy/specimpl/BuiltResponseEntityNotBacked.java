package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class BuiltResponseEntityNotBacked extends BuiltResponse {

    public BuiltResponseEntityNotBacked() {

    }

    public BuiltResponseEntityNotBacked(final int status,
                                        final Headers<Object> metadata,
                                        final Object entity,
                                        final Annotation[] entityAnnotations) {
        super(status, null, metadata, entity, entityAnnotations);
    }

    public BuiltResponseEntityNotBacked(final int status, final String reason,
                                        final Headers<Object> metadata,
                                        final Object entity,
                                        final Annotation[] entityAnnotations) {
        super(status, reason, metadata, entity, entityAnnotations);
    }

    @Override
    public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns)
    {
        throw new IllegalStateException(Messages.MESSAGES.entityNotBackedByInputStream());
    }
}
