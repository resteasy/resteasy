package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

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
@DecoderPrecedence
public class GZIPDecodingInterceptor implements MessageBodyReaderInterceptor
{
  public static class FinishableGZIPInputStream extends GZIPInputStream
   {
      public FinishableGZIPInputStream(final InputStream is) throws IOException
      {
         super(is);
      }

      public void finish()
      {
         inf.end(); // make sure on finish the inflater's end() is called to release the native code pointer
      }
   }

   public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException
   {

      Object encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

      if (encoding != null && encoding.toString().equalsIgnoreCase("gzip"))
      {
         InputStream old = context.getInputStream();
         FinishableGZIPInputStream is = new FinishableGZIPInputStream(old);
         context.setInputStream(is);
         try
         {
            return context.proceed();
         }
         finally
         {
            // Don't finish() an InputStream - TODO this still will require a garbage collect to finish the stream
            // see RESTEASY-554 for more details
            if (!context.getType().equals(InputStream.class)) is.finish();
            context.setInputStream(old);
         }
      }
      else
      {
         return context.proceed();
      }
   }}
