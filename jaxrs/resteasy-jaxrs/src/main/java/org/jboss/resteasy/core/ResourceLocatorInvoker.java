package org.jboss.resteasy.core;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.GetRestful;

import javax.ws.rs.NotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ResourceLocatorInvoker implements ResourceInvoker
{

   final static Logger logger = Logger.getLogger(ResourceLocatorInvoker.class);

   protected InjectorFactory injector;
   protected MethodInjector methodInjector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected ResourceLocator method;
   protected ConcurrentHashMap<Class, Registry> cachedSubresources = new ConcurrentHashMap<Class, Registry>();
   protected Pattern classRegex = null;

   public ResourceLocatorInvoker(ResourceFactory resource, InjectorFactory injector, ResteasyProviderFactory providerFactory, ResourceLocator locator)
   {
      this.resource = resource;
      this.injector = injector;
      this.providerFactory = providerFactory;
      this.method = locator;
      this.methodInjector = injector.createMethodInjector(locator, providerFactory);
      classRegex = ResourceMethodInvoker.setupClassRegex(method);
   }

   protected Object createResource(HttpRequest request, HttpResponse response)
   {
      Object resource = this.resource.createResource(request, response, providerFactory);
      return createResource(request, response, resource);

   }

   protected Object createResource(HttpRequest request, HttpResponse response, Object locator)
   {
      ResteasyUriInfo uriInfo = request.getUri();
      Object[] args = new Object[0];
      RuntimeException lastException = (RuntimeException)request.getAttribute(ResourceMethodRegistry.REGISTRY_MATCHING_EXCEPTION);
      try
      {
         args = methodInjector.injectArguments(request, response);
      }
      catch (NotFoundException failure)
      {
         if (lastException != null) throw lastException;
         throw failure;
      }
      try
      {
         ResourceMethodInvoker.pushMatchedUri(method, classRegex, uriInfo);
         uriInfo.pushCurrentResource(locator);
         Object subResource = method.getMethod().invoke(locator, args);
         return subResource;

      }
      catch (IllegalAccessException e)
      {
         throw new InternalServerErrorException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ApplicationException(e.getCause());
      }
   }

   public Method getMethod()
   {
      return method.getMethod();
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response)
   {
      Object target = createResource(request, response);
      return invokeOnTargetObject(request, response, target);
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response, Object locator)
   {
      Object target = createResource(request, response, locator);
      return invokeOnTargetObject(request, response, target);
   }

   protected BuiltResponse invokeOnTargetObject(HttpRequest request, HttpResponse response, Object target)
   {
      if (target == null)
      {
         NotFoundException notFound = new NotFoundException("Null subresource for path: " + request.getUri().getAbsolutePath());
         throw notFound;
      }
      Class<? extends Object> clazz = target.getClass();
      Registry registry = cachedSubresources.get(clazz);
      if (registry == null)
      {
         registry = new ResourceMethodRegistry(providerFactory);
         if (!GetRestful.isSubResourceClass(clazz))
         {
            String msg = "Subresource for target class has no jax-rs annotations.: " + clazz.getName();
            throw new InternalServerErrorException(msg);
         }
         if (Proxy.isProxyClass(clazz))
         {
            for (Class<?> intf : clazz.getInterfaces())
            {
               ResourceClass resourceClass = ResourceBuilder.locatorFromAnnotations(intf);
               registry.addResourceFactory(null, null, resourceClass);
            }
         }
         else
         {
            ResourceClass resourceClass = ResourceBuilder.locatorFromAnnotations(clazz);
            registry.addResourceFactory(null, null, resourceClass);
         }
         cachedSubresources.putIfAbsent(clazz, registry);
      }
      ResourceInvoker invoker = null;
      RuntimeException lastException = (RuntimeException)request.getAttribute(ResourceMethodRegistry.REGISTRY_MATCHING_EXCEPTION);
      try
      {
         invoker = registry.getResourceInvoker(request);
         if (invoker == null)
         {
            NotFoundException notFound = new NotFoundException("No path match in subresource for: " + request.getUri().getAbsolutePath());
            throw notFound;
         }
      }
      catch (NotFoundException e)
      {
         if (lastException != null) throw lastException;
         throw e;
      }
      if (invoker instanceof ResourceLocatorInvoker)
      {
         ResourceLocatorInvoker locator = (ResourceLocatorInvoker) invoker;
         return locator.invoke(request, response, target);
      }
      else
      {
         ResourceMethodInvoker method = (ResourceMethodInvoker) invoker;
         return method.invoke(request, response, target);
      }
   }
}
