package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.ResourceMethodContext;
import org.jboss.resteasy.core.interception.ResourceMethodInterceptor;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethodSecurityInterceptor implements ResourceMethodInterceptor
{
   protected String[] rolesAllowed;
   protected boolean denyAll;

   public boolean accepted(ResourceMethod resourceMethod)
   {
      RolesAllowed allowed = resourceMethod.getResourceClass().getAnnotation(RolesAllowed.class);
      RolesAllowed methodAllowed = resourceMethod.getMethod().getAnnotation(RolesAllowed.class);
      if (methodAllowed != null) allowed = methodAllowed;
      if (allowed != null)
      {
         rolesAllowed = allowed.value();
      }

      denyAll = (resourceMethod.getResourceClass().isAnnotationPresent(DenyAll.class) && resourceMethod.getMethod().isAnnotationPresent(RolesAllowed.class) == false && resourceMethod.getMethod().isAnnotationPresent(PermitAll.class) == false) || resourceMethod.getMethod().isAnnotationPresent(DenyAll.class);


      return rolesAllowed != null || denyAll;
   }

   public ServerResponse invoke(ResourceMethodContext ctx) throws Failure, ApplicationException, WebApplicationException
   {
      if (denyAll) throw new Failure(HttpResponseCodes.SC_UNAUTHORIZED);
      if (rolesAllowed != null)
      {
         SecurityContext context = ResteasyProviderFactory.getContextData(SecurityContext.class);
         if (context != null)
         {
            for (String role : rolesAllowed)
            {
               if (context.isUserInRole(role)) return ctx.proceed();
            }
            throw new Failure(HttpResponseCodes.SC_UNAUTHORIZED);
         }
      }
      return ctx.proceed();
   }
}
