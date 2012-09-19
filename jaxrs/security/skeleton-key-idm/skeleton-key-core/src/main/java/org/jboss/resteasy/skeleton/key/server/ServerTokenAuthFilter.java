package org.jboss.resteasy.skeleton.key.server;

import org.jboss.resteasy.skeleton.key.core.AbstractTokenAuthFilter;
import org.jboss.resteasy.skeleton.key.core.UserPrincipal;
import org.jboss.resteasy.skeleton.key.keystone.model.Access;
import org.jboss.resteasy.skeleton.key.keystone.model.Role;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

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
