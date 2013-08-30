package com.restfully.shop.features;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@AllowedPerDay(0)
@Priority(Priorities.AUTHORIZATION)
public class PerDayAuthorizer implements ContainerRequestFilter
{
   @Context
   ResourceInfo info;

   protected long today = System.currentTimeMillis();

   Map<String, Integer> count = new HashMap<String, Integer>();

   protected synchronized boolean authorized(String user, AllowedPerDay allowed)
   {
      if (System.currentTimeMillis() > today + (24 * 60 * 60 * 1000))
      {
         count.clear();
      }
      Integer counter = count.get(user);
      if (counter == null)
      {
         counter = 0;
      }

      if (allowed.value() > counter)
      {
         count.put(user, counter + 1);
         return true;
      }
      return false;
   }

   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      AllowedPerDay allowed = info.getResourceMethod().getAnnotation(AllowedPerDay.class);
      SecurityContext sc = requestContext.getSecurityContext();
      if (sc == null) throw new ForbiddenException();
      Principal principal = sc.getUserPrincipal();
      if (principal == null) throw new ForbiddenException();
      String user = principal.getName();
      if (!authorized(user, allowed))
      {
         throw new ForbiddenException();
      }
   }
}
