package org.jboss.resteasy.tests.context;

import org.jboss.resteasy.annotations.interception.EncoderPrecedence;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.junit.Assert;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@EncoderPrecedence
@ServerInterceptor
public class EncoderInterceptor implements MessageBodyWriterInterceptor
{
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      Assert.assertTrue(context.getHeaders().containsKey("before-encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("after-encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("end"));
      context.getHeaders().add("encoder", "true");
      context.proceed();
   }
}