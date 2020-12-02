package org.jboss.resteasy.util;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NoContentException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NoContent
{
   public static InputStream noContentCheck( MultivaluedMap<String, String> httpHeaders, InputStream is) throws NoContentException
   {
      contentLengthCheck(httpHeaders);
      NoContentInputStreamDelegate delegate = new NoContentInputStreamDelegate(is);
      return delegate;

   }

   public static boolean isContentLengthZero(MultivaluedMap<String, String> httpHeaders)
   {
      if (httpHeaders == null) return false;
      String contentLength = (String)httpHeaders.getFirst(HttpHeaders.CONTENT_LENGTH);
      if (contentLength != null)
      {
         long length = Long.parseLong(contentLength);
         if (length == 0) return true;
      }
      return false;
   }

   public static void contentLengthCheck(MultivaluedMap<String, String> httpHeaders) throws NoContentException
   {
      if (isContentLengthZero(httpHeaders)) throw new NoContentException(Messages.MESSAGES.noContentContentLength0());
   }
}
