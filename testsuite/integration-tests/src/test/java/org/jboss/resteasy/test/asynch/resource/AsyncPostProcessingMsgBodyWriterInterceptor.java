package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

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
