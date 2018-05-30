package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormInjector implements ValueInjector
{
   private Class type;
   private ConstructorInjector constructorInjector;
   private PropertyInjector propertyInjector;

   @SuppressWarnings(value = "unchecked")
   public FormInjector(Class type, ResteasyProviderFactory factory)
   {
      this.type = type;
      Constructor<?> constructor = null;

      try
      {
         constructor = type.getConstructor();
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException(Messages.MESSAGES.unableToInstantiateForm());
      }

      constructorInjector = factory.getInjectorFactory().createConstructor(constructor, factory);
      propertyInjector = factory.getInjectorFactory().createPropertyInjector(type, factory);

   }

   @Override
   public CompletionStage<Object> inject(boolean unwrapAsync)
   {
      throw new IllegalStateException(Messages.MESSAGES.cannotInjectIntoForm());
   }

   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      return constructorInjector.construct(unwrapAsync)
            .thenCompose(target -> propertyInjector.inject(request, response, target, unwrapAsync)
                                    .thenApply(v -> target));
   }
}
