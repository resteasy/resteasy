package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public interface AsyncMessageBodyWriter<T> extends MessageBodyWriter<T> {
    CompletionStage<Void> asyncWriteTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                                               MultivaluedMap<String, Object> httpHeaders,
                                               AsyncOutputStream entityStream);
}
