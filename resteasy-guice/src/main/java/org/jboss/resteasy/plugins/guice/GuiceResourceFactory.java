package org.jboss.resteasy.plugins.guice;

import com.google.inject.Provider;
import org.jboss.resteasy.spi.*;

public class GuiceResourceFactory implements ResourceFactory
{

   private final Provider provider;
   private final Class<?> scannableClass;
   private PropertyInjector propertyInjector;

   public GuiceResourceFactory(final Provider provider, final Class<?> scannableClass)
   {
      this.provider = provider;
      this.scannableClass = scannableClass;
   }

   public Class<?> getScannableClass()
   {
      return scannableClass;
   }

   public void registered(final InjectorFactory factory)
   {
      propertyInjector = factory.createPropertyInjector(scannableClass);
   }

   public Object createResource(final HttpRequest request, final HttpResponse response, final InjectorFactory factory)
   {
      final Object resource = provider.get();
      propertyInjector.inject(request, response, resource);
      return resource;
   }

   public void requestFinished(final HttpRequest request, final HttpResponse response, final Object resource)
   {
   }

   public void unregistered()
   {
   }
}
