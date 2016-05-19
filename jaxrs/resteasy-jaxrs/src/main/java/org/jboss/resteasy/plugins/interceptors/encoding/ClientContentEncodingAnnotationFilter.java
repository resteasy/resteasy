package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
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

   public ClientContentEncodingAnnotationFilter(String encoding)
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
