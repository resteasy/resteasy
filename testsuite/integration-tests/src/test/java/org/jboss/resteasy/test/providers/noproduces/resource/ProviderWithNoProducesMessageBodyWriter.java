package org.jboss.resteasy.test.providers.noproduces.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProviderWithNoProducesMessageBodyWriter implements MessageBodyWriter<Foo> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return "foo".equals(mediaType.getType()) && Foo.class.equals(type);
   }

   @Override
   public void writeTo(Foo t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
         OutputStream entityStream) throws IOException, WebApplicationException {
      entityStream.write("ProviderWithNoProducesMessageBodyWriter".getBytes());
   }
}
