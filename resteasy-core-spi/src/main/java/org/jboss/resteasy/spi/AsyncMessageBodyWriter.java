package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

/**
 * MessageBodyWriter which supports async IO.
 */
public interface AsyncMessageBodyWriter<T> extends MessageBodyWriter<T> {

    /**
     * Write a type to an HTTP message using async IO. The message header map is mutable
     * but any changes must be made before writing to the async output stream since
     * the headers will be flushed prior to writing the message body.
     *
     * @param t            the instance to write.
     * @param type         the class of instance that is to be written.
     * @param genericType  the type of instance to be written. {@link jakarta.ws.rs.core.GenericEntity}
     *                     provides a way to specify this information at runtime.
     * @param annotations  an array of the annotations attached to the message entity instance.
     * @param mediaType    the media type of the HTTP entity.
     * @param httpHeaders  a mutable map of the HTTP message headers.
     * @param entityStream the {@link AsyncOutputStream} for the HTTP entity. The
     *                     implementation should not close the output stream.
     * @return a {@link CompletionStage} indicating completion
     * @throws java.io.IOException if an IO error arises (in the returned {@link CompletionStage})
     * @throws jakarta.ws.rs.WebApplicationException
     *                             if a specific HTTP error response needs to be produced (in the returned {@link CompletionStage}).
     *                             Only effective if thrown prior to the message being committed.
     */
    CompletionStage<Void> asyncWriteTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                                       MultivaluedMap<String, Object> httpHeaders,
                                       AsyncOutputStream entityStream);
}
