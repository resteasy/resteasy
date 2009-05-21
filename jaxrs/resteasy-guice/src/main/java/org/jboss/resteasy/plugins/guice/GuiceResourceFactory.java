package org.jboss.resteasy.plugins.guice;

import com.google.inject.Provider;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResourceFactory;

public class GuiceResourceFactory implements ResourceFactory
{

   private final Provider provider;
   private final Class<?> scannableClass;

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
   }

   public Object createResource(final HttpRequest request, final HttpResponse response, final InjectorFactory factory)
   {
      return provider.get();
   }

   public void requestFinished(final HttpRequest request, final HttpResponse response, final Object resource)
   {
   }

   public void unregistered()
   {
   }
}
