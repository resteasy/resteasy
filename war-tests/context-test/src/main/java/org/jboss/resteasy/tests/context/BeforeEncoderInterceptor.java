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
@Precedence("BEFORE_ENCODER")
@ServerInterceptor
public class BeforeEncoderInterceptor implements MessageBodyWriterInterceptor
{
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {

      Assert.assertFalse(context.getHeaders().containsKey("after-encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("encoder"));
      Assert.assertFalse(context.getHeaders().containsKey("end"));
      context.getHeaders().add("before-encoder", "true");
      context.proceed();
   }
}
