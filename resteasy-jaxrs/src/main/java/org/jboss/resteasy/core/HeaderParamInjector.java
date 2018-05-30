package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.HeaderParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamInjector extends StringParameterInjector implements ValueInjector
{

   public HeaderParamInjector(Class type, Type genericType, AccessibleObject target, String header, String defaultValue, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      super(type, genericType, header, HeaderParam.class, defaultValue, target, annotations, factory);
   }

   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
      return CompletableFuture.completedFuture(extractValues(list));
   }

   @Override
   public CompletionStage<Object> inject(boolean unwrapAsync)
   {
      throw new RuntimeException(Messages.MESSAGES.illegalToInjectHeaderParam());
   }
}
