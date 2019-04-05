package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

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
