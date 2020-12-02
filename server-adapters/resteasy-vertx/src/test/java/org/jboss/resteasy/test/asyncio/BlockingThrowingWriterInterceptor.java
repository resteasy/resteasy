package org.jboss.resteasy.test.asyncio;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

public class BlockingThrowingWriterInterceptor implements WriterInterceptor
{

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      // do not throw when we're serialising the exception
      if("KO".equals(context.getEntity()))
         throw new WebApplicationException(Response.ok("this is fine").build());
      context.proceed();
   }

}
