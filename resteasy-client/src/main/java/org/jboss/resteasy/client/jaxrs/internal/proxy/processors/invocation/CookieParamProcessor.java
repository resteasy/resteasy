package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

import javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamProcessor implements InvocationProcessor
{
   private String cookieName;

   public CookieParamProcessor(String cookieName)
   {
      this.cookieName = cookieName;
   }

   public String getCookieName()
   {
      return cookieName;
   }

   @Override
   public void process(ClientInvocationBuilder request, Object object)
   {
      if (object == null) return;  // don't set a null value
      if (object instanceof Cookie)
      {
         Cookie cookie = (Cookie) object;
         request.cookie(cookie);
      }
      else
      {
         request.cookie(new Cookie(cookieName, request.getInvocation().getClientConfiguration().toString(object)));
      }
   }
}
