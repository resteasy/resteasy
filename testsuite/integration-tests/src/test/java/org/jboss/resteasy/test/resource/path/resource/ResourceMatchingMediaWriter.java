package org.jboss.resteasy.test.resource.path.resource;

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
import java.util.List;

@Provider
@Produces(MediaType.APPLICATION_SVG_XML)
public class ResourceMatchingMediaWriter implements MessageBodyWriter<List<?>> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
      return List.class.isAssignableFrom(type);
   }

   @Override
   public long getSize(List<?> t, Class<?> type, Type genericType,
                  Annotation[] annotations, MediaType mediaType) {
      return List.class.getSimpleName().length();
   }

   @Override
   public void writeTo(List<?> t, Class<?> type, Type genericType,
                  Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, Object> httpHeaders,
                  OutputStream entityStream) throws IOException,
         WebApplicationException {
      entityStream.write(List.class.getSimpleName().getBytes());
   }

}
