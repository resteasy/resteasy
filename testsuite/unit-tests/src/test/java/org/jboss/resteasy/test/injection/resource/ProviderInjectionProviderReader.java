package org.jboss.resteasy.test.injection.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Consumes("not/real")
public class ProviderInjectionProviderReader implements MessageBodyReader {
   @Context
   public HttpHeaders headers;

   @Context
   public Providers workers;

   public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return false;
   }

   public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      return null;
   }
}
