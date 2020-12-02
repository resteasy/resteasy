package org.jboss.resteasy.test.core.basic.resource;

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
@Produces("text/quoted")
public class ApplicationConfigQuotedTextWriter implements MessageBodyWriter<String> {
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.equals(String.class);
   }

   public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
         WebApplicationException {
      s = String.format("\"%s\"", s);
      entityStream.write(s.getBytes());
   }
}
