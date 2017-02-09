package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventOutput;

@Provider
public class SseEventOutputProvider implements MessageBodyWriter<SseEventOutput>
{
   @Override
   public boolean isWriteable(Class<?> cls, Type type, Annotation[] anns, MediaType mt)
   {
      return SseEventOutput.class.isAssignableFrom(cls);
   }

   @Override
   public long getSize(final SseEventOutput output, final Class<?> type, final Type genericType,
         final Annotation[] annotations, final MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(final SseEventOutput output, final Class<?> type, final Type genericType,
         final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
         final OutputStream entityStream) throws IOException, WebApplicationException
   {
      //no operation
   }
}