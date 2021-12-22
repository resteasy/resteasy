/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * An asynchronous writer for the {@link Greeter}. This should only use
 * {@link #asyncWriteTo(Greeter, Class, Type, Annotation[], MediaType, MultivaluedMap, AsyncOutputStream)}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Provider
@Produces(MediaType.WILDCARD)
public class GreeterAsyncMessageBodyWriter implements AsyncMessageBodyWriter<Greeter> {
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                               final MediaType mediaType) {
        return Greeter.class.isAssignableFrom(type) && FindAnnotation.findAnnotation(annotations, GreetAsync.class) != null;
    }

    @Override
    public void writeTo(final Greeter greeter, final Class<?> type, final Type genericType,
                        final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
            throws IOException, WebApplicationException {
        throw new WebApplicationException("Only asyncWriteTo should be invoked");
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(final Greeter greeter, final Class<?> type, final Type genericType,
                                              final Annotation[] annotations, final MediaType mediaType,
                                              final MultivaluedMap<String, Object> httpHeaders,
                                              final AsyncOutputStream entityStream) {
        return entityStream.asyncWrite(greeter.greet("Async"));
    }
}
