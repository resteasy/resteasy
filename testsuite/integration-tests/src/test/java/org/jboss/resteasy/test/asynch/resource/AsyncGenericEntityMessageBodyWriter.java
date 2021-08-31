package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AsyncGenericEntityMessageBodyWriter implements MessageBodyWriter<List<String>> {

   private static final Type stringListType;

   static {
      List<String> list = new ArrayList<String>();
      GenericEntity<List<String>> entity = new GenericEntity<List<String>>(list) {};
      stringListType = entity.getType();
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return stringListType.equals(genericType);
   }

   @Override
   public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
               throws IOException, WebApplicationException {
      entityStream.write("ok".getBytes());
   }
}
