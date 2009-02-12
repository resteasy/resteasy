package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.core.interception.ClientInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyReaderContext;
import org.jboss.resteasy.core.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.core.interception.ServerInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ServerInterceptor
@ClientInterceptor
public class GZIPDecodingInterceptor implements MessageBodyReaderInterceptor
{
   public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException
   {

      Object encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

      if (encoding != null && encoding.toString().equalsIgnoreCase("gzip"))
      {
         InputStream old = context.getInputStream();
         context.setInputStream(new GZIPInputStream(old));
         try
         {
            return context.proceed();
         }
         finally
         {
            context.setInputStream(old);
         }
      }
      else
      {
         return context.proceed();
      }
   }
}
