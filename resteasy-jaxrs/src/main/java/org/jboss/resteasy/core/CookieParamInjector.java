package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamInjector extends StringParameterInjector implements ValueInjector
{

   public CookieParamInjector(Class type, Type genericType, AccessibleObject target, String cookieName, String defaultValue, Annotation[] annotations, ResteasyProviderFactory factory)
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
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
      if (type.equals(Cookie.class)) return CompletableFuture.completedFuture(cookie);

      if (cookie == null) return CompletableFuture.completedFuture(extractValues(null));
      List<String> values = new ArrayList<String>();
      values.add(cookie.getValue());
      return CompletableFuture.completedFuture(extractValues(values));
   }

   @Override
   public CompletionStage<Object> inject(boolean unwrapAsync)
   {
      throw new RuntimeException(Messages.MESSAGES.illegalToInjectCookieParam());
   }
}