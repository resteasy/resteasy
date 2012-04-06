package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormInjector implements ValueInjector
{
   private Class type;
   private ConstructorInjector constructorInjector;
   private PropertyInjector propertyInjector;

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
         throw new RuntimeException("Unable to instantiate @Form class. No no-arg constructor.");
      }

      constructorInjector = factory.getInjectorFactory().createConstructor(constructor);
      propertyInjector = factory.getInjectorFactory().createPropertyInjector(type);

   }

   public Object inject()
   {
      throw new IllegalStateException("You cannot inject into a form outside the scope of an HTTP request");
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      Object target = constructorInjector.construct();
      propertyInjector.inject(request, response, target);
      return target;
   }
}
