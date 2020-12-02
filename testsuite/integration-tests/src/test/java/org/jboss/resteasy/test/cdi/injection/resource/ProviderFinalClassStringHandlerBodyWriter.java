package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public final class ProviderFinalClassStringHandlerBodyWriter
   implements MessageBodyWriter<ProviderFinalClassStringHandler> {

   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType) {
      return ProviderFinalClassStringHandler.class.equals(type);
   }

   @Override
   public void writeTo(ProviderFinalClassStringHandler t, Class<?> type,
                       Type genericType, Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException {
      entityStream.write(t.getA().getBytes());
   }
}

