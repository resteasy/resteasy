package org.jboss.resteasy.plugins.interceptors;

import org.jboss.resteasy.annotations.interception.SecurityPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BindingPriority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@BindingPriority(BindingPriority.AUTHORIZATION)
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
      if (denyAll) throw new UnauthorizedException();
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
            throw new UnauthorizedException();
         }
      }
      return;
   }
}
