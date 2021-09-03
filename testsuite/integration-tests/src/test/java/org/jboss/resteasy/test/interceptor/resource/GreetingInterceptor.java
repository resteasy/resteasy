package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

public class GreetingInterceptor implements WriterInterceptor {

   @Override
   public void aroundWriteTo(WriterInterceptorContext context)
         throws IOException, WebApplicationException {
      String entity = (String) context.getEntity();
      if (entity != null) {
         context.setEntity("Hello " + entity + " !");
      }
      context.proceed();
   }

}
