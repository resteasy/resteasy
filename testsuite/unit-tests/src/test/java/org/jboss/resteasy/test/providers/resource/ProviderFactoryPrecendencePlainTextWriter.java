package org.jboss.resteasy.test.providers.resource;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("text/plain")
public class ProviderFactoryPrecendencePlainTextWriter implements MessageBodyWriter {
   public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return 0;
   }

   public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
   }
}
