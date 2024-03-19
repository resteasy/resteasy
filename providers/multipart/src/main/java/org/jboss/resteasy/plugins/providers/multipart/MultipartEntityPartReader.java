/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.spi.multipart.MultipartContent;
import org.jboss.resteasy.spi.util.Types;

/**
 * A {@link MessageBodyReader} for reading {@code multipart/form-data} into a collection of
 * {@linkplain EntityPart entity parts}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.1
 */
@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class MultipartEntityPartReader implements MessageBodyReader<List<EntityPart>> {

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return List.class.isAssignableFrom(type)
                && genericType instanceof ParameterizedType
                && Types.isGenericTypeInstanceOf(EntityPart.class, genericType);
    }

    @Override
    public List<EntityPart> readFrom(final Class<List<EntityPart>> type, final Type genericType,
            final Annotation[] annotations, final MediaType mediaType,
            final MultivaluedMap<String, String> httpHeaders,
            final InputStream entityStream)
            throws IOException, WebApplicationException {

        final String boundary = mediaType.getParameters().get("boundary");
        if (boundary == null)
            throw new IOException(Messages.MESSAGES.unableToGetBoundary());

        // Check if we've already parsed the entity parts
        final MultipartContent multipartContent = ResteasyContext.getContextData(MultipartContent.class);
        if (multipartContent != null) {
            return multipartContent.entityParts();
        }

        // On the returned EntityPart an injected (@Context Providers) doesn't work as it can't be found when
        // constructing this type. Therefore, the lookup here is required.
        final Providers providers = ResteasyContext.getRequiredContextData(Providers.class);
        MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(mediaType, providers);
        input.parse(entityStream);
        return input.toEntityParts();
    }
}
