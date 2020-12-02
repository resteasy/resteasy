package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class JaxbElementEntityMessageWriter implements
      MessageBodyWriter<JaxbElementReadableWritableEntity> {

   @Override
   public long getSize(JaxbElementReadableWritableEntity t, Class<?> type,
                  Type genericType, Annotation[] annotations, MediaType mediaType) {
      return t.toXmlString().length();
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
      return JaxbElementReadableWritableEntity.class.isAssignableFrom(type);
   }

   @Override
   public void writeTo(JaxbElementReadableWritableEntity t, Class<?> type,
                  Type genericType, Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, Object> httpHeaders,
                  OutputStream entityStream) throws IOException,
         WebApplicationException {
      entityStream.write(t.toXmlString().getBytes());
   }

}
