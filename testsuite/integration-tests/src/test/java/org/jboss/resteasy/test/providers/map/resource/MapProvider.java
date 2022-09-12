package org.jboss.resteasy.test.providers.map.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;

@Provider
@Produces(MediaType.APPLICATION_FORM_URLENCODED)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class MapProvider extends MapProviderAbstractProvider implements
      MessageBodyReader<MultivaluedMap<String, String>>,
      MessageBodyWriter<MultivaluedMap<String, String>> {

   @Override
   public long getSize(MultivaluedMap<String, String> t, Class<?> type,
                  Type genericType, Annotation[] annotations, MediaType mediaType) {
      return getLength();
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
      return MultivaluedMap.class.isAssignableFrom(type);
   }

   @Override
   public void writeTo(MultivaluedMap<String, String> t, Class<?> type,
                  Type genericType, Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, Object> httpHeaders,
                  OutputStream entityStream) throws IOException,
         WebApplicationException {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      entityStream.write(t.getFirst(getClass().getSimpleName()).getBytes());
      entityStream.write(getWriterName().getBytes());
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
      return isWriteable(type, genericType, annotations, mediaType);
   }

   @Override
   public MultivaluedMap<String, String> readFrom(
         Class<MultivaluedMap<String, String>> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add(getClass().getSimpleName(), getReaderName());
      return map;
   }

}
