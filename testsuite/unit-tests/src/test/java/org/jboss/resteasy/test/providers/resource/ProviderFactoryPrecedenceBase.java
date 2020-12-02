package org.jboss.resteasy.test.providers.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ProviderFactoryPrecedenceBase<T> implements MessageBodyWriter<T> {
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   public long getSize(T integer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return 0;
   }

   public void writeTo(T integer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
   }
}
