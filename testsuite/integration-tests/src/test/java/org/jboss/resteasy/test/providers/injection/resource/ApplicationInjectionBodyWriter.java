package org.jboss.resteasy.test.providers.injection.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class ApplicationInjectionBodyWriter implements MessageBodyWriter<String> {
   
   @Context
   ApplicationInjectionApplicationParent application;

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   @Override
   public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
               throws IOException, WebApplicationException {
      
      entityStream.write((getClass() + ":" + application.getName()).getBytes());
      entityStream.write("|".getBytes());
      entityStream.write(t.getBytes());
   }

   @Override
   public long getSize(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return 0;
   }
}
