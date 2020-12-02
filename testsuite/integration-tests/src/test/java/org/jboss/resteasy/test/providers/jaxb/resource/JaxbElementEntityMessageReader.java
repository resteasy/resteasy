package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class JaxbElementEntityMessageReader implements
      MessageBodyReader<JaxbElementReadableWritableEntity> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
      return JaxbElementReadableWritableEntity.class.isAssignableFrom(type);
   }

   @Override
   public JaxbElementReadableWritableEntity readFrom(Class<JaxbElementReadableWritableEntity> arg0,
                                                           Type arg1, Annotation[] annotations, MediaType mediaType,
                                                           MultivaluedMap<String, String> arg4, InputStream entityStream)
         throws IOException, WebApplicationException {
      String entity = readInputStream(entityStream);
      return JaxbElementReadableWritableEntity.fromString(entity);
   }

   String readInputStream(InputStream is) throws IOException {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      return br.readLine();
   }

}
