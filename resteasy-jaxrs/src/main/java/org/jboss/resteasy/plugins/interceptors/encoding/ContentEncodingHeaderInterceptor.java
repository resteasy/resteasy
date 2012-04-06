package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.ContentEncoding;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Sets the content-encoding header based on applied @ContentEncoding meta-annotation
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ContentEncodingHeaderInterceptor implements MessageBodyWriterInterceptor
{
   protected String encoding;

   protected boolean hasEncodingAnnotation(Annotation[] annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType().isAnnotationPresent(ContentEncoding.class))
         {
            encoding = annotation.annotationType().getAnnotation(ContentEncoding.class).value();
            return true;
         }
      }
      return false;
   }

   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
      context.proceed();
   }
}
