package org.jboss.resteasy.test.cdi.extensions.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes("application/test+xml")
public class CDIExtensionsTestReader implements MessageBodyReader<Object> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return false;
   }

   public Object readFrom(Class<Object> type, Type genericType,
                           Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException {
      return null;
   }
}
