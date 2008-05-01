package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.MethodInjector;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocator implements ResourceInvoker
{
   protected InjectorFactory injector;
   protected MethodInjector methodInjector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected ConcurrentHashMap<Class, ResourceMethodRegistry> cachedSubresources = new ConcurrentHashMap<Class, ResourceMethodRegistry>();
   protected int uriIndex;
   protected PathParamIndex index;
   protected boolean limited;

   public ResourceLocator(ResourceFactory resource, InjectorFactory injector, ResteasyProviderFactory providerFactory, Method method, PathParamIndex index, boolean limited)
   {
      this.resource = resource;
      this.injector = injector;
      this.providerFactory = providerFactory;
      this.method = method;
      this.methodInjector = injector.createMethodInjector(method);
      this.index = index;
      this.limited = limited;
   }

   public void setUriIndex(int uriIndex)
   {
      this.uriIndex = uriIndex;
   }

   protected Object createResource(HttpRequest request, HttpResponse response)
   {
      Object resource = this.resource.createResource(request, response, injector);
      return createResource(request, response, resource);

   }

   protected Object createResource(HttpRequest request, HttpResponse response, Object resource)
   {
      index.populateUriInfoTemplateParams(request);
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

   public void invoke(HttpRequest request, HttpResponse response) throws IOException
   {
      Object target = createResource(request, response);
      invokeOnTargetObject(request, response, target);
   }

   public void invoke(HttpRequest request, HttpResponse response, Object locator) throws IOException
   {
      Object target = createResource(request, response, locator);
      invokeOnTargetObject(request, response, target);

   }

   protected void invokeOnTargetObject(HttpRequest request, HttpResponse response, Object target) throws IOException
   {
      ResourceMethodRegistry registry = cachedSubresources.get(target.getClass());
      if (registry == null)
      {
         registry = new ResourceMethodRegistry(providerFactory);
         registry.addResourceFactory(null, null, target.getClass(), uriIndex + index.getOffset(), limited);
         cachedSubresources.putIfAbsent(target.getClass(), registry);
      }
      ResourceInvoker invoker = registry.getResourceInvoker(request, response, uriIndex + index.getOffset(), limited);
      if (invoker instanceof ResourceLocator)
      {
         ResourceLocator locator = (ResourceLocator) invoker;
         locator.invoke(request, response, target);
      }
      else
      {
         ResourceMethod method = (ResourceMethod) invoker;
         method.invoke(request, response, target);
      }
   }
}
