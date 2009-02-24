package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.AcceptedByMethod;
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
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethodSecurityInterceptor implements ResourceMethodInterceptor, AcceptedByMethod
{
   protected String[] rolesAllowed;
   protected boolean denyAll;

   public boolean accept(Class declaring, Method method)
   {
      if (declaring == null || method == null) return false;
      RolesAllowed allowed = (RolesAllowed) declaring.getAnnotation(RolesAllowed.class);
      RolesAllowed methodAllowed = method.getAnnotation(RolesAllowed.class);
      if (methodAllowed != null) allowed = methodAllowed;
      if (allowed != null)
      {
         rolesAllowed = allowed.value();
      }

      denyAll = (declaring.isAnnotationPresent(DenyAll.class)
              && method.isAnnotationPresent(RolesAllowed.class) == false
              && method.isAnnotationPresent(PermitAll.class) == false) || method.isAnnotationPresent(DenyAll.class);


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
