package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;

import javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamMarshaller implements Marshaller
{
   private String cookieName;

   public CookieParamMarshaller(String cookieName)
   {
      this.cookieName = cookieName;
   }

   public String getCookieName()
   {
      return cookieName;
   }

   public void build(ClientRequest request, Object object)
   {
      if (object == null) return;  // don't set a null value
      if (object instanceof Cookie)
      {
         Cookie cookie = (Cookie) object;
         request.cookie(cookie);
      }
      else
      {
         request.cookie(cookieName, object);
      }
   }
}
