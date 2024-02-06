package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.util.Types;

/**
 * A {@link jakarta.ws.rs.ext.MessageBodyWriter} for write {@code multipart/form-data} as a list of
 * {@linkplain EntityPart entity parts}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.1
 */
@Provider
@Produces(MediaType.MULTIPART_FORM_DATA)
public class MultipartEntityPartWriter extends AbstractMultipartFormDataWriter
        implements AsyncMessageBodyWriter<List<EntityPart>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(type)
                && genericType instanceof ParameterizedType
                && Types.isGenericTypeInstanceOf(EntityPart.class, genericType);
    }

    @Override
    public void writeTo(final List<EntityPart> entityParts, final Class<?> type, final Type genericType,
            final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
            final OutputStream entityStream)
            throws IOException, WebApplicationException {
        write(create(entityParts), mediaType, httpHeaders, entityStream, annotations);
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(final List<EntityPart> entityParts, final Class<?> type,
            final Type genericType,
            final Annotation[] annotations, final MediaType mediaType,
            final MultivaluedMap<String, Object> httpHeaders,
            final AsyncOutputStream entityStream) {
        return CompletableFuture.supplyAsync(() -> create(entityParts), ContextualExecutors.threadPool())
                .thenCompose((out) -> asyncWrite(out, mediaType, httpHeaders, entityStream, annotations));
    }

    private static MultipartFormDataOutput create(final List<EntityPart> entityParts) {
        final MultipartFormDataOutput output = new MultipartFormDataOutput();
        for (EntityPart entityPart : entityParts) {
            final OutputPart part = output.addFormData(entityPart.getName(), entityPart.getContent(), entityPart.getMediaType(),
                    entityPart.getFileName()
                            .orElse(null));
            entityPart.getHeaders().forEach((name, value) -> part.getHeaders().addAll(name, value.toArray()));
        }
        return output;
    }
}
