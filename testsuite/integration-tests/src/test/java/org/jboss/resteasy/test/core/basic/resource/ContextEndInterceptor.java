package org.jboss.resteasy.test.core.basic.resource;

import org.junit.Assert;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

@Provider
@Priority(40)
public class ContextEndInterceptor implements WriterInterceptor {

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextEndInterceptor don't have correct headers";
      Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("before-encoder"));
      Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("after-encoder"));
      Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("encoder"));
      context.getHeaders().add("end", "true");
      context.proceed();
   }
}
