/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.spi.model;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.spi.util.Types;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
// TODO (jrp) this is a nice-to-have, but do we need it?
public class MessageBodyWriterEntityProvider<T> extends AbstractEntityProvider<MessageBodyWriter<?>>
        implements MessageBodyWriter<T> {
    private final MessageBodyWriter<T> messageBodyWriter;

    public MessageBodyWriterEntityProvider(final MessageBodyWriter<T> provider, final int priority, final boolean builtIn) {
        super(provider, resolveGenericType(provider.getClass()), priority, builtIn);
        this.messageBodyWriter = provider;
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return messageBodyWriter.isWriteable(type, genericType, annotations, mediaType);
    }

    @Override
    public long getSize(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return messageBodyWriter.getSize(t, type, genericType, annotations, mediaType);
    }

    @Override
    public void writeTo(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
            throws IOException, WebApplicationException {
        messageBodyWriter.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

    private static Class<?> resolveGenericType(final Class<?> writerType) {
        final var genericTypes = Types.findParameterizedTypes(writerType, MessageBodyWriter.class);
        return genericTypes == null || genericTypes.length == 0 ? Object.class : Types.getRawType(genericTypes[0]);
    }
}
