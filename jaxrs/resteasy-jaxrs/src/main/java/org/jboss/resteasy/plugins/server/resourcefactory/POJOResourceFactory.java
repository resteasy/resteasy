package org.jboss.resteasy.plugins.server.resourcefactory;

import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.util.PickConstructor;

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
      Constructor constructor = PickConstructor.pickConstructor(scannableClass);
      if (constructor == null)
      {
         throw new RuntimeException("Unable to find a public constructor for class " + scannableClass.getName());
      }
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


   public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
   {
   }
}
