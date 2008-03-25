package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.ConstructorInjector;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.PropertyInjector;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResourceReference;

import java.lang.reflect.Constructor;

/**
 * Allocates an instance of a class at each invocation
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class POJOResourceFactory implements ResourceReference
{
   private Class<?> scannableClass;

   public POJOResourceFactory(Class<?> scannableClass)
   {
      this.scannableClass = scannableClass;
   }

   public ResourceFactory getFactory(InjectorFactory factory)
   {
      if (scannableClass.getDeclaredConstructors().length > 1)
      {
         throw new RuntimeException("Your POJO must only have one constructor");
      }
      Constructor constructor = scannableClass.getDeclaredConstructors()[0];
      final ConstructorInjector constructorInjector = factory.createConstructor(constructor);
      final PropertyInjector propertyInjector = factory.createPropertyInjector(scannableClass);

      return new ResourceFactory()
      {
         public Object createResource(HttpRequest input, HttpResponse response)
         {
            Object obj = constructorInjector.construct(input, response);
            propertyInjector.inject(input, response, obj);
            return obj;
         }
      };
   }

   public Class<?> getScannableClass()
   {
      return scannableClass;
   }


}
