package org.jboss.resteasy.util;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * HttpClient4xUtils provides utility methods useful for changes
 * necessitated by switching from HttpClient 3.x to HttpClient 4.x.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class HttpClient4xUtils
{

   static public void consumeEntity(HttpResponse response)
   {
      try
      {
         EntityUtils.consume(response.getEntity());
      } catch (IOException e)
      {
         LogMessages.LOGGER.unableToCloseEntityStream(e);
      }
   }
   
   static public String updateQuery(String uriString, String query)
   {
      try
      {
         URI uri = new URI(uriString);
         return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment()).toString();
      } catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
   }
}
