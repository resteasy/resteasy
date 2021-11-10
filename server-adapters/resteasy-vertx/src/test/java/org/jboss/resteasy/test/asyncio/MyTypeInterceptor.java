package org.jboss.resteasy.test.asyncio;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

@Provider
public class MyTypeInterceptor implements WriterInterceptor
{

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      if ("Hello".equals(context.getEntity())) {
        context.setEntity(new MyType());
        context.setMediaType(MediaType.TEXT_PLAIN_TYPE);
        context.setType(MyType.class);
      }
      context.proceed();
   }

}
