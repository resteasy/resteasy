package org.jboss.resteasy.skeleton.key.server;

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
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@PreMatching
@Provider
public class TokenAuthFilter implements ContainerRequestFilter
{
   protected TokenService tokenService;

   public TokenAuthFilter(TokenService tokenService)
   {
      this.tokenService = tokenService;
   }

   @Context SecurityContext securityContext;

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      String tokenHeader = requestContext.getHeaderString("X-Auth-Token");
      if (tokenHeader == null) return;
      Access token = null;
      try
      {
         token = tokenService.get(tokenHeader);
      }
      catch (NotFoundException e)
      {
         return;  // do nothing
      }
      if (token == null) return; // do nothing
      if (token.getToken().expired()) return; // todo maybe throw 401 with an error stating token is expired?

      final UserPrincipal principal = new UserPrincipal(token.getUser());
      final Set<String> roleSet = new HashSet<String>();
      for (Role role : token.getUser().getRoles())
      {
         roleSet.add(role.getName());
      }
      SecurityContext ctx = new SecurityContext()
      {
         @Override
         public Principal getUserPrincipal()
         {
            return principal;
         }

         @Override
         public boolean isUserInRole(String role)
         {
            return roleSet.contains(role);
         }

         @Override
         public boolean isSecure()
         {
            return securityContext.isSecure();
         }

         @Override
         public String getAuthenticationScheme()
         {
            return securityContext.getAuthenticationScheme();
         }
      };
      requestContext.setSecurityContext(ctx);
   }
}
