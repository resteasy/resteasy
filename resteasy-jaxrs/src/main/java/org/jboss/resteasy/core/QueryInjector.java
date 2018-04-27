package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.*;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletionStage;

/**
 * Created by Simon Ström on 7/17/14.
 */
@SuppressWarnings(value = "unchecked")
public class QueryInjector implements ValueInjector {

   private Class type;
   private ConstructorInjector constructorInjector;
   private PropertyInjector propertyInjector;

   public QueryInjector(Class type, ResteasyProviderFactory factory) {
      this.type = type;
      Constructor<?> constructor;

      try
      {
         constructor = type.getConstructor();
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException("Unable to instantiate @Query class. No no-arg constructor.");
      }

      constructorInjector = factory.getInjectorFactory().createConstructor(constructor, factory);
      propertyInjector = factory.getInjectorFactory().createPropertyInjector(type, factory);
   }

   @Override
   public CompletionStage<Object> inject(boolean unwrapAsync) {
      throw new IllegalStateException("You cannot inject outside the scope of an HTTP request");
   }

   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
      return constructorInjector.construct(unwrapAsync)
            .thenCompose(target -> propertyInjector.inject(request, response, target, unwrapAsync)
                                 .thenApply(v -> target));
   }
}
