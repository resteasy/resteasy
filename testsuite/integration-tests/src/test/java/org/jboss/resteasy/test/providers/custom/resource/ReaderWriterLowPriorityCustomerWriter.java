package org.jboss.resteasy.test.providers.custom.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("application/xml")
@Priority(Priorities.USER + 100)
public class ReaderWriterLowPriorityCustomerWriter extends ReaderWriterCustomerWriter implements MessageBodyWriter<ReaderWriterCustomer>
{
   @Override
   public void writeTo(ReaderWriterCustomer customer, Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      String out = "<customer><name>low priority</name></customer>";
      entityStream.write(out.getBytes());
   }
}
