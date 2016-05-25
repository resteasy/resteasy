package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.util.AcceptParser;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
@Priority(Priorities.HEADER_DECORATOR)
public class ServerContentEncodingAnnotationFilter implements WriterInterceptor
{
   protected
   @Context
   HttpRequest request;

   Set<String> encodings;

   public ServerContentEncodingAnnotationFilter(Set<String> encodings)
   {
      this.encodings = encodings;
   }

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      List<String> acceptEncoding = request.getHttpHeaders().getRequestHeaders().get(HttpHeaders.ACCEPT_ENCODING);
      if (acceptEncoding != null)
      {
         StringBuffer buf = new StringBuffer();
         for (String accept : acceptEncoding)
         {
            if (buf.length() > 0) buf.append(",");
            buf.append(accept);
         }
         List<String> accepts = AcceptParser.parseAcceptHeader(buf.toString());
         for (String encoding : accepts)
         {
            if (encodings.contains(encoding.toLowerCase()))
            {
               context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
               break;
            }
         }
      }
      context.proceed();
   }
}