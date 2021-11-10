package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamProcessor implements InvocationProcessor
{
   private String cookieName;
   protected Annotation[] annotations;
   protected Type type;

   public CookieParamProcessor(final String cookieName)
   {
      this.cookieName = cookieName;
   }

   public CookieParamProcessor(final String cookieName, final Type type, final Annotation[] annotations)
   {
      this.cookieName = cookieName;
      this.annotations = annotations;
      this.type = type;
   }

   public String getCookieName()
   {
      return cookieName;
   }

   @Override
   public void process(ClientInvocation invocation, Object object)
   {
      if (object == null) return;  // don't set a null value
      if (object instanceof Cookie)
      {
         Cookie cookie = (Cookie) object;
         invocation.getHeaders().cookie(cookie);
      }
      else
      {
         ClientConfiguration cc = invocation.getClientConfiguration();
         String s = (annotations != null && type != null) ? cc.toString(object, type, annotations) : cc.toString(object);
         invocation.getHeaders().cookie(new Cookie(cookieName, s));
      }
   }
}
