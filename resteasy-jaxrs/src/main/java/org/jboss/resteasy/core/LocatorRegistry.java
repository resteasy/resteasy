package org.jboss.resteasy.core;

import org.jboss.resteasy.core.registry.RootNode;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocatorRegistry
{
   protected RootNode root = new RootNode();
   protected ResteasyProviderFactory providerFactory;

   public LocatorRegistry(Class<?> clazz, ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      ResourceBuilder resourceBuilder = providerFactory.getResourceBuilder();
      if (Proxy.isProxyClass(clazz))
      {
         for (Class<?> intf : clazz.getInterfaces())
         {
            ResourceClass resourceClass = resourceBuilder.getLocatorFromAnnotations(intf);
            register(resourceClass);
         }
      }
      else
      {
         ResourceClass resourceClass = resourceBuilder.getLocatorFromAnnotations(clazz);
         register(resourceClass);
      }
   }

   public void register(ResourceClass resourceClass)
   {
      for (ResourceMethod method : resourceClass.getResourceMethods())
      {
         processMethod(method);
      }
      for (ResourceLocator method : resourceClass.getResourceLocators())
      {
         processMethod(method);
      }

   }

   protected void processMethod(ResourceLocator method)
   {
      String fullpath = method.getFullpath() == null ? "" : method.getFullpath();
      InjectorFactory injectorFactory = providerFactory.getInjectorFactory();
      if (method instanceof ResourceMethod)
      {
         ResourceMethodInvoker invoker = new ResourceMethodInvoker((ResourceMethod)method, injectorFactory, null, providerFactory);
         root.addInvoker(fullpath, invoker);
      }
      else
      {
         ResourceLocatorInvoker locator = new ResourceLocatorInvoker(null, injectorFactory, providerFactory, method);
         root.addInvoker(fullpath, locator);
      }
   }

   public ResourceInvoker getResourceInvoker(HttpRequest request)
   {
      try
      {
         String currentUri = request.getUri().getEncodedMatchedPaths().get(0);
         int startAt = currentUri.length();
         return root.match(request, startAt);
      }
      catch (RuntimeException e)
      {
         throw e;
      }
   }
}
