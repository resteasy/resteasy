package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.EncoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

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
   private static class EndableGZIPOutputStream extends GZIPOutputStream
   {
      public EndableGZIPOutputStream(OutputStream os) throws IOException
      {
         super(os);
      }

      @Override
      public void finish() throws IOException
      {
         super.finish();
         def.end(); // make sure on finish the deflater's end() is called to release the native code pointer
      }
   }

   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      Object encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

      if (encoding != null && encoding.toString().equalsIgnoreCase("gzip"))
      {
         OutputStream old = context.getOutputStream();
         GZIPOutputStream gzipOutputStream = new EndableGZIPOutputStream(old);
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
