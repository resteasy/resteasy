package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;

import javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 *  
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
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
