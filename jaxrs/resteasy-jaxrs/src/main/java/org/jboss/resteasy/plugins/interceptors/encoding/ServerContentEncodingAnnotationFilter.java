package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.util.AcceptParser;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(ConstrainedTo.Type.SERVER)
@BindingPriority(BindingPriority.HEADER_DECORATOR)
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