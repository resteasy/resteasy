package org.jboss.resteasy.test.resource.generic;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/student")
public class StudentWriter implements MessageBodyWriter<Student>
{

   public long getSize(Student t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return t.getName().length();
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return true;
   }

   public void writeTo(Student t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      OutputStreamWriter writer = new OutputStreamWriter(entityStream);
      writer.write(t.getName());
      writer.flush();
   }
}
