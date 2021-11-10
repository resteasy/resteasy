package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

@Provider
public class AsyncPostProcessingMsgBodyWriterInterceptor implements WriterInterceptor {
   public static volatile boolean called;

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      called = true;
      context.proceed();
   }
}
