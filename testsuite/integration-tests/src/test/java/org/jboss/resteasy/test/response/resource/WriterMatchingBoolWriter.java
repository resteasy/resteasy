package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces(MediaType.WILDCARD)
public class WriterMatchingBoolWriter implements MessageBodyWriter<Object> {
   @Override
   public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.equals(Boolean.class);
   }

   @Override
   public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      Boolean b = (Boolean) o;
      if (b.booleanValue()) {
         entityStream.write("YES".getBytes());
      } else {
         entityStream.write("NO".getBytes());
      }
   }
}
