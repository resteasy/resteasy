package org.jboss.resteasy.spring.beanprocessor;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("foo/bar")
public class MyWriter implements MessageBodyWriter<Customer>
{
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
   {
      return Customer.class.isAssignableFrom(type);
   }

   public long getSize(Customer customer, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType)
   {
      return customer.getName().getBytes().length + 9;
   }

   public void writeTo(Customer customer, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
         OutputStream entityStream) throws IOException, WebApplicationException
   {
      entityStream.write(("customer=" + customer.getName()).getBytes());
   }
}
