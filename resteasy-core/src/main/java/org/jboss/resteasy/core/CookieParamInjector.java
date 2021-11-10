package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.core.Cookie;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamInjector extends StringParameterInjector implements ValueInjector
{

   public CookieParamInjector(final Class type, final Type genericType, final AccessibleObject target, final String cookieName, final String defaultValue, final Annotation[] annotations, final ResteasyProviderFactory factory)
   {
      if (type.equals(Cookie.class))
      {
         this.type = type;
         this.paramName = cookieName;
         this.paramType = CookieParam.class;
         this.defaultValue = defaultValue;

      }
      else
      {
         initialize(type, genericType, cookieName, CookieParam.class, defaultValue, target, annotations, factory);
      }
   }

   @Override
   public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
      if (type.equals(Cookie.class)) return cookie;

      if (cookie == null) return extractValues(null);
      List<String> values = new ArrayList<String>();
      values.add(cookie.getValue());
      return extractValues(values);
   }

   @Override
   public Object inject(boolean unwrapAsync)
   {
      throw new RuntimeException(Messages.MESSAGES.illegalToInjectCookieParam());
   }
}
