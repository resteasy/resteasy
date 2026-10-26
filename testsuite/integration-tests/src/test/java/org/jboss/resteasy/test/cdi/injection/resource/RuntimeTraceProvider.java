/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

/**
 * A reader and writer used by both the server and the client. Each invocation records the runtime its injected
 * context objects resolve to.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Provider
@ApplicationScoped
@Consumes(RuntimeTrace.MEDIA_TYPE)
@Produces(RuntimeTrace.MEDIA_TYPE)
public class RuntimeTraceProvider implements MessageBodyReader<RuntimeTrace>, MessageBodyWriter<RuntimeTrace> {

    @Inject
    @Context
    Configuration configuration;

    @Inject
    @Context
    Providers providers;

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return type == RuntimeTrace.class;
    }

    @Override
    public RuntimeTrace readFrom(final Class<RuntimeTrace> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
            throws IOException {
        final String content = new String(entityStream.readAllBytes(), StandardCharsets.UTF_8);
        final RuntimeTrace trace = new RuntimeTrace(content.isEmpty() ? List.of() : List.of(content.split("\n")));
        return trace.add("reader=%s".formatted(RuntimeTrace.describe(configuration, providers)));
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return type == RuntimeTrace.class;
    }

    @Override
    public void writeTo(final RuntimeTrace trace, final Class<?> type, final Type genericType,
            final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
            final OutputStream entityStream) throws IOException {
        final RuntimeTrace written = trace.add("writer=%s".formatted(RuntimeTrace.describe(configuration, providers)));
        entityStream.write(String.join("\n", written.entries()).getBytes(StandardCharsets.UTF_8));
    }
}
