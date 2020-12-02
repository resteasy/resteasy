package org.jboss.resteasy.test.response.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("abc/xyz")
public class CompletionStageResponseMessageBodyWriter implements MessageBodyWriter<CompletionStageResponseTestClass> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return CompletionStageResponseTestClass.class.isAssignableFrom(type);
   }

   @Override
   public void writeTo(CompletionStageResponseTestClass t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
               throws IOException, WebApplicationException
   {
      entityStream.write(t.s.getBytes(StandardCharsets.UTF_8));
   }
}
