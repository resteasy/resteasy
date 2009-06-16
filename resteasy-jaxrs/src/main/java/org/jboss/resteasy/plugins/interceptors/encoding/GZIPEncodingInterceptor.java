package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.core.interception.ClientInterceptor;
import org.jboss.resteasy.core.interception.EncoderPrecedence;
import org.jboss.resteasy.core.interception.MessageBodyWriterContext;
import org.jboss.resteasy.core.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.core.interception.ServerInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ServerInterceptor
@ClientInterceptor
@EncoderPrecedence
public class GZIPEncodingInterceptor implements MessageBodyWriterInterceptor
{
   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      Object encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

      if (encoding != null && encoding.toString().equalsIgnoreCase("gzip"))
      {
         OutputStream old = context.getOutputStream();
         GZIPOutputStream gzipOutputStream = new GZIPOutputStream(old);
         context.setOutputStream(gzipOutputStream);
         try
         {
            context.proceed();
         }
         finally
         {
            gzipOutputStream.finish();
            context.setOutputStream(old);
         }
         return;
      }
      else
      {
         context.proceed();
      }
   }
}
