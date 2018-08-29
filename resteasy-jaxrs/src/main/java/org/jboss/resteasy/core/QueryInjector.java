package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.Constructor;

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
   public Object inject() {
      throw new IllegalStateException("You cannot inject outside the scope of an HTTP request");
   }

   @Override
   public Object inject(HttpRequest request, HttpResponse response) {
      Object target = constructorInjector.construct();
      propertyInjector.inject(request, response, target);
      return target;
   }
}
