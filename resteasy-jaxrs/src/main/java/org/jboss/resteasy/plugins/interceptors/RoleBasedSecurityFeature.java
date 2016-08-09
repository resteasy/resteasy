package org.jboss.resteasy.plugins.interceptors;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RoleBasedSecurityFeature implements DynamicFeature
{
   @SuppressWarnings(value = "unchecked")
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
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
