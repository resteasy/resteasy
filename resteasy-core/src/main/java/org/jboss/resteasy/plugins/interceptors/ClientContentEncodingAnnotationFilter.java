package org.jboss.resteasy.plugins.interceptors;

import javax.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.CLIENT)
@Priority(Priorities.HEADER_DECORATOR)
public class ClientContentEncodingAnnotationFilter implements WriterInterceptor
{
   protected String encoding;

   public ClientContentEncodingAnnotationFilter(final String encoding)
   {
      this.encoding = encoding;
   }

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
      context.proceed();
   }
}
