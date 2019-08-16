package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ProviderOneArgConstructorStringHandlerBodyWriter
   implements MessageBodyWriter<ProviderOneArgConstructorStringHandler> {

   @SuppressWarnings("unused")
   public ProviderOneArgConstructorStringHandlerBodyWriter(@Context final Application application) {
   }

   @SuppressWarnings("unused")
   private ProviderOneArgConstructorStringHandlerBodyWriter() {
   }

   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType) {
      return ProviderOneArgConstructorStringHandler.class.equals(type);
   }

   @Override
   public void writeTo(ProviderOneArgConstructorStringHandler t, Class<?> type,
                       Type genericType, Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException {
      entityStream.write(t.getC().getBytes());
   }
}

