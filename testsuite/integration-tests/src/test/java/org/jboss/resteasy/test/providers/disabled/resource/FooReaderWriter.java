package org.jboss.resteasy.test.providers.disabled.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Consumes("application/foo")
@Produces("application/foo")
@Provider
public class FooReaderWriter implements MessageBodyReader<Foo>, MessageBodyWriter<Foo> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Foo.class.equals(type);
   }

   @Override
   public Foo readFrom(Class<Foo> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException {
      byte[] b = new byte[100];
      int n = entityStream.read(b);
      return new Foo(new String(b, 0, n));
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Foo.class.equals(type);
   }

   @Override
   public void writeTo(Foo t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException {
      entityStream.write(t.getS().getBytes());
   }
}
