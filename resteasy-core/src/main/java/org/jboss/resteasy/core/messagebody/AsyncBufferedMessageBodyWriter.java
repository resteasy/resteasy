package org.jboss.resteasy.core.messagebody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;

public interface AsyncBufferedMessageBodyWriter<T> extends AsyncMessageBodyWriter<T>
{
   @Override
   default CompletionStage<Void> asyncWriteTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                                              MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream) {
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       try {
           writeTo(t, type, genericType, annotations, mediaType, httpHeaders, bos);
           return entityStream.asyncWrite(bos.toByteArray());
       } catch (WebApplicationException | IOException e) {
           return ProviderHelper.completedException(e);
       }
   }
}
