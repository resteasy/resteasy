package org.jboss.resteasy.client.jaxrs.cache;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheRequestFilter implements ClientRequestFilter
{
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException
   {
      if (!requestContext.getMethod().equalsIgnoreCase("GET")) return;


   }
}
