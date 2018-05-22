package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.util.BasicAuthHelper;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * Client filter that will do basic authentication.  You must allocate it and then register it with the Client or WebTarget
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthentication implements ClientRequestFilter
{
   private final String authHeader;

   /**
    *
    * @param username user name
    * @param password password
    */
   public BasicAuthentication(String username, String password)
   {
      authHeader = BasicAuthHelper.createHeader(username, password);
   }

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException
   {
      requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, authHeader);
   }
}
