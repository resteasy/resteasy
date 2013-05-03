package org.jboss.resteasy.core;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.GetRestful;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

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

   public ResourceLocatorInvoker(ResourceFactory resource, InjectorFactory injector, ResteasyProviderFactory providerFactory, ResourceLocator locator)
   {
      this.resource = resource;
      this.injector = injector;
      this.providerFactory = providerFactory;
      this.method = locator;
      this.methodInjector = injector.createMethodInjector(locator, providerFactory);
   }

   protected Object createResource(HttpRequest request, HttpResponse response)
   {
      Object resource = this.resource.createResource(request, response, providerFactory);
      return createResource(request, response, resource);

   }

   protected Object createResource(HttpRequest request, HttpResponse response, Object locator)
   {
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
      Object[] args = methodInjector.injectArguments(request, response);
      try
      {
         uriInfo.pushCurrentResource(locator);
         Object subResource = method.getMethod().invoke(locator, args);
         warnIfJaxRSAnnotatedFields(subResource);

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
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
      try
      {
         Object target = createResource(request, response);
         return invokeOnTargetObject(request, response, target);
      }
      finally
      {
         uriInfo.popCurrentResource();
         uriInfo.popMatchedURI();
      }
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response, Object locator)
   {
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
      try
      {
         Object target = createResource(request, response, locator);
         return invokeOnTargetObject(request, response, target);
      }
      finally
      {
         uriInfo.popCurrentResource();
         uriInfo.popMatchedURI();
      }
   }

   protected BuiltResponse invokeOnTargetObject(HttpRequest request, HttpResponse response, Object target)
   {
      if (target == null)
      {
         NotFoundException notFound = new NotFoundException("Null subresource for path: " + request.getUri().getAbsolutePath());
         notFound.setLoggable(true);
         throw notFound;
      }
      Registry registry = cachedSubresources.get(target.getClass());
      if (registry == null)
      {
         registry = new ResourceMethodRegistry(providerFactory);
         if (!GetRestful.isSubResourceClass(target.getClass()))
         {
            String msg = "Subresource for target class has no jax-rs annotations.: " + target.getClass().getName();
            throw new InternalServerErrorException(msg);
         }
         registry.addResourceFactory(null, null, target.getClass());//subResourceClass);
         cachedSubresources.putIfAbsent(target.getClass(), registry);
      }
      ResourceInvoker invoker = registry.getResourceInvoker(request);
      if (invoker == null)
      {
         NotFoundException notFound = new NotFoundException("No path match in subresource for: " + request.getUri().getAbsolutePath());
         notFound.setLoggable(true);
         throw notFound;
      }
      else if (invoker instanceof ResourceLocatorInvoker)
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


   private void warnIfJaxRSAnnotatedFields(Object obj)
   {

      if (obj == null) return;

      Class<?> clazz = obj.getClass();

      while (clazz != Object.class)
      {

         Field[] fields = clazz.getDeclaredFields();

         for (Field field : fields)
         {

            Class<? extends Annotation>[] annotations =
                    FindAnnotation.findJaxRSAnnotations(field.getDeclaredAnnotations());

            if (annotations.length != 0)
            {
               logger.warn("Field {0} of subresource {1} will not be injected " +
                       "according to spec", field.getName(), obj.getClass().getName());
            }

         }

         clazz = clazz.getSuperclass();

      }

   }

}
