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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RoleBasedSecurityFeature implements DynamicFeature
{

   @Override
   public void configure(ResourceInfo resourceInfo, Configurable configurable)
   {
      final Class declaring = resourceInfo.getResourceClass();
      final Method method = resourceInfo.getResourceMethod();

      if (declaring == null || method == null) return;

      String[] rolesAllowed = null;
      boolean denyAll;
      boolean permitAll;
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

      permitAll = (declaring.isAnnotationPresent(PermitAll.class) == true
              && method.isAnnotationPresent(RolesAllowed.class) == false
              && method.isAnnotationPresent(DenyAll.class) == false) || method.isAnnotationPresent(PermitAll.class);

      if (rolesAllowed != null || denyAll || permitAll)
      {
         RoleBasedSecurityFilter filter = new RoleBasedSecurityFilter(rolesAllowed, denyAll, permitAll);
         configurable.register(filter);
      }
   }
}
