package org.jboss.resteasy.test.core.basic.resource;

import org.junit.Assert;

import javax.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

@Provider
@Priority(20)
public class ContextEncoderInterceptor implements WriterInterceptor {

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextEncoderInterceptor don't have correct headers";
      Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("before-encoder"));
      Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("after-encoder"));
      Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("end"));
      context.getHeaders().add("encoder", "true");
      context.proceed();
   }
}
