package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

public class ClientExceptionsIOExceptionReaderInterceptor implements ReaderInterceptor {
   @Override
   public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
      throw new IOException("client io");
   }
}
