package org.jboss.resteasy.test.stream.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
public class StreamRawCharArrayMessageBodyReaderWriter implements MessageBodyReader<Character[]>, MessageBodyWriter<Character[]> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return char[].class.equals(type) || Character[].class.equals(type);
   }

   @Override
   public Character[] readFrom(Class<Character[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      List<Character> chars = new ArrayList<Character>(); 
      int c = entityStream.read();
      while (c != -1) {
         chars.add((char) c);
         c = entityStream.read();
      }
      return chars.toArray(new Character[chars.size()]);
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return char[].class.equals(type) || Character[].class.equals(type);
   }

   @Override
   public void writeTo(Character[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      for (Character c : t) {
         entityStream.write(Character.toString(c).getBytes());
      }
   }  
}
