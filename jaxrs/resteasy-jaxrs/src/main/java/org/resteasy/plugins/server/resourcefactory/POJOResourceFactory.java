package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.ConstructorInjector;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.PropertyInjector;
import org.resteasy.spi.ResourceFactory;

import java.lang.reflect.Constructor;

/**
 * Allocates an instance of a class at each invocation
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class POJOResourceFactory implements ResourceFactory
{
   private Class<?> scannableClass;
   private ConstructorInjector constructorInjector;
   private PropertyInjector propertyInjector;

   public POJOResourceFactory(Class<?> scannableClass)
   {
      this.scannableClass = scannableClass;
   }

   public void registered(InjectorFactory factory)
   {
      Constructor constructor = scannableClass.getDeclaredConstructors()[0];
      this.constructorInjector = factory.createConstructor(constructor);
      this.propertyInjector = factory.createPropertyInjector(scannableClass);
   }

   public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
   {
      Object obj = constructorInjector.construct(request, response);
      propertyInjector.inject(request, response, obj);
      return obj;
   }

   public void unregistered()
   {
   }

   public Class<?> getScannableClass()
   {
      return scannableClass;
   }


}
