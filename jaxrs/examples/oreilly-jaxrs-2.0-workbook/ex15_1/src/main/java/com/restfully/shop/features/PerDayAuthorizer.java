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
import java.lang.reflect.Method;
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



   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      SecurityContext sc = requestContext.getSecurityContext();
      if (sc == null) throw new ForbiddenException();
      Principal principal = sc.getUserPrincipal();
      if (principal == null) throw new ForbiddenException();

      String user = principal.getName();
      if (!authorized(user))
      {
         throw new ForbiddenException();
      }
   }

   protected static class UserMethodKey
   {
      String username;
      Method method;

      public UserMethodKey(String username, Method method)
      {
         this.username = username;
         this.method = method;
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         UserMethodKey that = (UserMethodKey) o;

         if (!method.equals(that.method)) return false;
         if (!username.equals(that.username)) return false;

         return true;
      }

      @Override
      public int hashCode()
      {
         int result = username.hashCode();
         result = 31 * result + method.hashCode();
         return result;
      }
   }

   protected Map<UserMethodKey, Integer> count = new HashMap<UserMethodKey, Integer>();

   protected long today = System.currentTimeMillis();

   protected synchronized boolean authorized(String user)
   {
      if (System.currentTimeMillis() > today + (24 * 60 * 60 * 1000))
      {
         today = System.currentTimeMillis();
         count.clear();
      }
      UserMethodKey key = new UserMethodKey(user, info.getResourceMethod());
      Integer counter = count.get(key);
      if (counter == null)
      {
         counter = 0;
      }

      AllowedPerDay allowed = info.getResourceMethod().getAnnotation(AllowedPerDay.class);
      if (allowed.value() > counter)
      {
         count.put(key, counter + 1);
         return true;
      }
      return false;
   }
}
