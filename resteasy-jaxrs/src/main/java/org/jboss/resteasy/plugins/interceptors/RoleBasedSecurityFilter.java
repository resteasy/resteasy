package org.jboss.resteasy.plugins.interceptors;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Priority(Priorities.AUTHORIZATION)
public class RoleBasedSecurityFilter implements ContainerRequestFilter
{
   protected String[] rolesAllowed;
   protected boolean denyAll;
   protected boolean permitAll;

   public RoleBasedSecurityFilter(String[] rolesAllowed, boolean denyAll, boolean permitAll)
   {
      this.rolesAllowed = rolesAllowed;
      this.denyAll = denyAll;
      this.permitAll = permitAll;
   }

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      if (denyAll) 
      {
         throw new ForbiddenException(Response.status(403).entity("Access forbidden: role not allowed").type("text/html;charset=UTF-8").build());
      }
      if (permitAll) return;
      if (rolesAllowed != null)
      {
         SecurityContext context = ResteasyProviderFactory.getContextData(SecurityContext.class);
         if (context != null)
         {
            for (String role : rolesAllowed)
            {
               if (context.isUserInRole(role)) return;
            }
            throw new ForbiddenException(Response.status(403).entity("Access forbidden: role not allowed").type("text/html;charset=UTF-8").build());
         }
      }
      return;
   }
}
