package org.jboss.resteasy.tests.context;

import org.jboss.resteasy.core.interception.MessageBodyWriterContext;
import org.jboss.resteasy.core.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.core.interception.Precedence;
import org.jboss.resteasy.core.interception.ServerInterceptor;
import org.junit.Assert;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Precedence("AFTER_ENCODER")
@ServerInterceptor
public class AfterEncoderInterceptor implements MessageBodyWriterInterceptor
{
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      Assert.assertTrue(context.getHeaders().containsKey("before-encoder"));
      Assert.assertTrue(context.getHeaders().containsKey("encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("end"));
      context.getHeaders().add("after-encoder", "true");
      context.proceed();
   }
}