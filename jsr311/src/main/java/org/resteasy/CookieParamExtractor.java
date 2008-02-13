package org.resteasy;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamExtractor extends StringParameterExtractor implements ParameterExtractor
{

   public CookieParamExtractor(Method method, String cookieName, int index, String defaultValue)
   {
      if (method.getParameterTypes()[index].equals(Cookie.class))
      {
         this.type = method.getParameterTypes()[index];
         this.method = method;
         this.paramName = cookieName;
         this.paramType = "@" + CookieParam.class.getSimpleName();
         this.defaultValue = defaultValue;

      }
      else
      {
         initialize(index, method, cookieName, "@" + CookieParam.class.getSimpleName(), defaultValue);
      }
   }

   public Object extract(HttpInput request)
   {
      Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
      if (type.equals(Cookie.class)) return cookie;

      if (cookie == null) return extractValue(null);
      return extractValue(cookie.getValue());
   }
}