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

package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.Cleanables;
import org.jboss.resteasy.spi.EntityOutputStream;
import org.jboss.resteasy.spi.multipart.MultipartContent;
import org.jboss.resteasy.spi.util.Types;

/**
 * Checks the method found to see if it as a {@link FormParam} or {@link org.jboss.resteasy.annotations.jaxrs.FormParam}
 * annotation on an {@link EntityPart} or {@link List List<EntityPart>} parameter. If so, the multipart parts are
 * parsed and a {@link MultipartContent} is created and placed on the context to be used in other readers and parameter
 * injector.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.2.8.Final
 */
@Provider
public class EntityPartFilter implements ContainerRequestFilter {

    @Context
    private Providers providers;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final MediaType mediaType = requestContext.getMediaType();
        // Ensure the media type is multipart/form-data and a MultipartContent type is not already in our context
        if (MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(mediaType)
                && !ResteasyContext.hasContextData(MultipartContent.class)) {
            final String boundary = mediaType.getParameters().get("boundary");
            // If we don't have a boundary, we will just skip the processing
            if (boundary != null) {
                // Get the methods and check that we need to get the entity parts
                final Method method = resourceInfo.getResourceMethod();
                final Parameter[] parameters = method.getParameters();
                if (hasEntityPartParameter(parameters)) {
                    final MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(requestContext.getMediaType(),
                            providers);
                    // Copy the input stream as it's being parsed. This will allow us to reset the entity stream for
                    // further reads.
                    final CopyInputStream copyInputStream = new CopyInputStream(requestContext.getEntityStream());
                    input.parse(copyInputStream);
                    // Set the entity stream to the copied content for cases where another read might be required
                    requestContext.setEntityStream(copyInputStream.entity.toInputStream());
                    final List<EntityPart> parts = List.copyOf(input.toEntityParts());
                    final MultipartContent multipartParts = () -> parts;
                    ResteasyContext.pushContext(MultipartContent.class, multipartParts);
                    final Cleanables cleanables = ResteasyContext.getContextData(Cleanables.class);
                    if (cleanables != null) {
                        cleanables.addCleanable(() -> ResteasyContext.popContextData(MultipartContent.class));
                    }
                }
            }
        }
    }

    private static boolean hasEntityPartParameter(final Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(FormParam.class)
                    || parameter.isAnnotationPresent(org.jboss.resteasy.annotations.jaxrs.FormParam.class)) {
                if (parameter.getType().isAssignableFrom(EntityPart.class)) {
                    return true;
                } else if (parameter.getType().isAssignableFrom(List.class)
                        && Types.isGenericTypeInstanceOf(EntityPart.class, parameter.getParameterizedType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static class CopyInputStream extends InputStream {
        private final InputStream delegate;
        private final EntityOutputStream entity;

        private CopyInputStream(final InputStream delegate) {
            this.delegate = delegate;
            entity = new EntityOutputStream();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            final int read = delegate.read(b);
            write(b, 0, read);
            return read;
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int read = delegate.read(b, off, len);
            write(b, off, read);
            return read;
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            final byte[] read = delegate.readAllBytes();
            if (read.length > 0) {
                entity.write(read);
            }
            return read;
        }

        @Override
        public byte[] readNBytes(final int len) throws IOException {
            final byte[] read = delegate.readNBytes(len);
            if (read.length > 0) {
                entity.write(read);
            }
            return read;
        }

        @Override
        public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
            final int read = delegate.readNBytes(b, off, len);
            write(b, off, read);
            return read;
        }

        @Override
        public long skip(final long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void close() throws IOException {
            try {
                delegate.close();
            } finally {
                entity.close();
            }
        }

        @Override
        public void mark(final int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override
        public long transferTo(final OutputStream out) throws IOException {
            return delegate.transferTo(out);
        }

        @Override
        public int read() throws IOException {
            final int read = delegate.read();
            if (read != -1) {
                entity.write(read);
            }
            return read;
        }

        private void write(final byte[] b, final int off, final int read) throws IOException {
            if (read > 0) {
                final int writeLen = (read - off);
                if (writeLen > 0) {
                    entity.write(b, off, writeLen);
                }
            }
        }
    }
}
