package org.jboss.resteasy.test.stream.resource;

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

@Consumes("*/*")
@Produces("*/*")
@Provider
public class StreamRawByteMessageBodyReaderWriter implements MessageBodyReader<Byte>, MessageBodyWriter<Byte> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return byte.class.equals(type) || Byte.class.equals(type);
   }

   @Override
   public void writeTo(Byte t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      entityStream.write(t);
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return byte.class.equals(type) || Byte.class.equals(type);
   }

   @Override
   public Byte readFrom(Class<Byte> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      return (byte) entityStream.read();
   }
}
