package org.jboss.resteasy.test.client.resource;

import javax.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

@Priority(100)
public class ClientResponseFilterInterceptorReaderOne implements ReaderInterceptor {
   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      try {
         return context.proceed();
      } catch (IOException e) {
         return "OK";
      }
   }
}
