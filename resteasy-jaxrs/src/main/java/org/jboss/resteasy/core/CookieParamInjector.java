package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamInjector extends StringParameterInjector implements ValueInjector
{

   public CookieParamInjector(Class type, Type genericType, AccessibleObject target, String cookieName, String defaultValue)
   {
      if (type.equals(Cookie.class))
      {
         this.type = type;
         this.paramName = cookieName;
         this.paramType = "@" + CookieParam.class.getSimpleName();
         this.defaultValue = defaultValue;

      }
      else
      {
         initialize(type, genericType, cookieName, "@" + CookieParam.class.getSimpleName(), defaultValue, target);
      }
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
      if (type.equals(Cookie.class)) return cookie;

      if (cookie == null) return extractValue(null);
      return extractValue(cookie.getValue());
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @CookieParam into a singleton");
   }
}