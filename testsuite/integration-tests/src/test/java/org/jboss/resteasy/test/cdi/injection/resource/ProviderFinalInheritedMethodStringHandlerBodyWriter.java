package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProviderFinalInheritedMethodStringHandlerBodyWriter
   extends FinalMethodSuperclass
   implements MessageBodyWriter<ProviderFinalInheritedMethodStringHandler> {

   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType) {
      return ProviderFinalInheritedMethodStringHandler.class.equals(type);
   }

   @Override
   public void writeTo(ProviderFinalInheritedMethodStringHandler t, Class<?> type,
                       Type genericType, Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException {
      write(t, entityStream);
   }
}

