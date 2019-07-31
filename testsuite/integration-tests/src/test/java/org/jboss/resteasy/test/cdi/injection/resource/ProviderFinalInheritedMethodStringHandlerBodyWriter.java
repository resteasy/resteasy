package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

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

