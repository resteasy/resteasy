package org.jboss.resteasy.test.core.basic.resource;

import org.junit.Assert;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

@Provider
@Priority(30)
public class ContextAfterEncoderInterceptor implements WriterInterceptor {

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextAfterEncoderInterceptor don't have correct headers";
      Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("before-encoder"));
      Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("encoder"));
      Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("end"));
      context.getHeaders().add("after-encoder", "true");
      context.proceed();
   }
}
