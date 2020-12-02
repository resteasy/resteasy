package org.jboss.resteasy.test.asyncio;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MyTypeWriter implements MessageBodyWriter<MyType>
{

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type == MyType.class;
   }

   @Override
   public void writeTo(MyType t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      entityStream.write("OK".getBytes(StandardCharsets.UTF_8));
      entityStream.flush();
   }

}
