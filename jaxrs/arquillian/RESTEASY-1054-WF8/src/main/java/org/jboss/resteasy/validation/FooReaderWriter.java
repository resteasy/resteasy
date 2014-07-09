package org.jboss.resteasy.validation;

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

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 18, 2013
 */
@Provider
@Produces("application/foo")
@Consumes("application/foo")
public class FooReaderWriter implements MessageBodyReader<Foo>, MessageBodyWriter<Foo>
{
   public boolean isWriteable(Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType)
   {
      return Foo.class.equals(type);
   }

   public long getSize(Foo t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Foo t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      byte[] b = t.s.getBytes();
      entityStream.write(b.length);
      entityStream.write(t.s.getBytes());
      entityStream.flush();
   }

   public boolean isReadable(Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType)
   {
      return Foo.class.equals(type);
   }

   public Foo readFrom(Class<Foo> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      int length = entityStream.read();
      byte[] b = new byte[length];
      entityStream.read(b);
      String s = new String(b);
      return new Foo(s);
   }
}

