package org.jboss.resteasy.test.client.resource;

import javax.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

@Priority(200)
public class ClientResponseFilterInterceptorReaderTwo implements ReaderInterceptor {
   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      throw new IOException("should be caught");
   }
}
