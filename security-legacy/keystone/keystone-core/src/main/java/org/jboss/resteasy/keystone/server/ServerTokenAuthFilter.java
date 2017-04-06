package org.jboss.resteasy.keystone.server;

import org.jboss.resteasy.keystone.core.AbstractTokenAuthFilter;
import org.jboss.resteasy.keystone.model.Access;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * Used only by skeleton key server server
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@PreMatching
@Provider
public class ServerTokenAuthFilter extends AbstractTokenAuthFilter
{
   protected TokenService tokenService;

   public ServerTokenAuthFilter(TokenService tokenService)
   {
      super(tokenService.getCertificate());
      this.tokenService = tokenService;
   }

   @Override
   protected Access getTokenFromServer(String header)
   {
      try
      {
         return tokenService.get(header);
      }
      catch (NotFoundException e)
      {
         return null;
      }
   }
}
