package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
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
