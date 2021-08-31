package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

public class DuplicateProviderRegistrationInterceptor implements ReaderInterceptor {
   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      return null;
   }
}
