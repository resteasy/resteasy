package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ServerInterceptor
@Provider
@HeaderDecoratorPrecedence
public class ServerContentEncodingHeaderInterceptor extends ContentEncodingHeaderInterceptor implements AcceptedByMethod
{
   protected
   @Context
   HttpRequest request;

   public boolean accept(Class declaring, Method method)
   {
      return hasEncodingAnnotation(method.getAnnotations()) || hasEncodingAnnotation(declaring.getClass().getAnnotations());
   }

   @Override
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      List<String> acceptEncoding = request.getHttpHeaders().getRequestHeaders().get(HttpHeaders.ACCEPT_ENCODING);
      if (acceptEncoding != null)
      {
         for (String accept : acceptEncoding)
         {
            if (accept.contains(encoding))
            {
               super.write(context);
               return;
            }
         }
      }
      context.proceed();
   }
}