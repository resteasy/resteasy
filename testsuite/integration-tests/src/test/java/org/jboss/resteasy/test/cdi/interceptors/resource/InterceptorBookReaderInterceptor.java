package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class InterceptorBookReaderInterceptor implements ReaderInterceptor {
   @Inject
   private Logger log;

   @Override
   @InterceptorReaderBinding
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      log.info("*** Intercepting call in InterceptorBookReaderInterceptor.aroundReadFrom()");
      InterceptorVisitList.add(this);
      Object result = context.proceed();
      log.info("*** Back from intercepting call in InterceptorBookReaderInterceptor.aroundReadFrom()");
      return result;
   }
}
