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
@Precedence("END")
@ServerInterceptor
public class EndInterceptor implements MessageBodyWriterInterceptor
{
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      Assert.assertTrue(context.getHeaders().containsKey("before-encoder"));
      Assert.assertTrue(context.getHeaders().containsKey("after-encoder"));
      Assert.assertTrue(context.getHeaders().containsKey("encoder"));
      context.getHeaders().add("end", "true");
      context.proceed();
   }
}