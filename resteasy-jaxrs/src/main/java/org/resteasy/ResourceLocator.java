package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.MethodInjector;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocator implements ResourceFactory
{
   protected InjectorFactory injector;
   protected MethodInjector methodInjector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected PathParamIndex index;

   public ResourceLocator(ResourceFactory resource, InjectorFactory injector, ResteasyProviderFactory providerFactory, Method method, PathParamIndex index)
   {
      this.resource = resource;
      this.injector = injector;
      this.providerFactory = providerFactory;
      this.method = method;
      this.index = index;
      this.methodInjector = injector.createMethodInjector(method);
   }

   public void registered(InjectorFactory factory)
   {
   }

   public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
   {
      index.populateUriInfoTemplateParams(request);
      Object resource = this.resource.createResource(request, response, injector);
      Object[] args = methodInjector.injectArguments(request, response);
      try
      {
         return method.invoke(resource, args);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getCause());
      }
   }

   public void unregistered()
   {
   }

   public Class<?> getScannableClass()
   {
      return method.getReturnType();
   }
}
