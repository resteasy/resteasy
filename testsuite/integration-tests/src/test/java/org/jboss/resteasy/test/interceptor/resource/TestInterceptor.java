package org.jboss.resteasy.test.interceptor.resource;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class TestInterceptor implements WriterInterceptor
{
   public static volatile AtomicInteger closeCounter = new AtomicInteger(0);

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      OutputStream outputStream = new FilterOutputStream(context.getOutputStream())
      {
         @Override
         public void close() throws IOException
         {
            closeCounter.incrementAndGet();
            super.close();
         }
      };
      context.setOutputStream(outputStream);
      context.proceed();
   }

}
