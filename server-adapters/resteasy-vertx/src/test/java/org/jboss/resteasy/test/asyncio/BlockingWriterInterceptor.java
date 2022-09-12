package org.jboss.resteasy.test.asyncio;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

public class BlockingWriterInterceptor implements WriterInterceptor
{

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      context.getHeaders().add("X-Writer", "blocking");
      context.proceed();
   }

}
