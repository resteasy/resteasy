package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ResourceLocator implements ResourceInvoker
{

   final static Logger logger = LoggerFactory.getLogger(ResourceLocator.class);

   protected InjectorFactory injector;
   protected MethodInjector methodInjector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected ConcurrentHashMap<Class, Registry> cachedSubresources = new ConcurrentHashMap<Class, Registry>();

   public ResourceLocator(ResourceFactory resource, InjectorFactory injector, ResteasyProviderFactory providerFactory, Class root, Method method)
   {
      this.resource = resource;
      this.injector = injector;
      this.providerFactory = providerFactory;
      this.method = method;
      this.methodInjector = injector.createMethodInjector(root, method);
   }

   protected Object createResource(HttpRequest request, HttpResponse response)
   {
      Object resource = this.resource.createResource(request, response, injector);
      return createResource(request, response, resource);

   }

   protected Object createResource(HttpRequest request, HttpResponse response, Object locator)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      Object[] args = methodInjector.injectArguments(request, response);
      try
      {
         uriInfo.pushCurrentResource(locator);
         Object subResource = method.invoke(locator, args);
         warnIfJaxRSAnnotatedFields(subResource);

         return subResource;

      }
      catch (IllegalAccessException e)
      {
         throw new LoggableFailure(e);
      }
       catch (InvocationTargetException e)
      {
         throw new ApplicationException(e.getCause());
      }
   }

   public Method getMethod()
   {
      return method;
   }

   public Response invoke(HttpRequest request, HttpResponse response)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
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

   public Response invoke(HttpRequest request, HttpResponse response, Object locator)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
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

   protected Response invokeOnTargetObject(HttpRequest request, HttpResponse response, Object target)
   {
      if (target == null)
      {
         throw new Failure("Null subresource for path: " + request.getUri().getAbsolutePath(), HttpResponseCodes.SC_NOT_FOUND);
      }
      Registry registry = cachedSubresources.get(target.getClass());
      if (registry == null)
      {
         registry = new ResourceMethodRegistry(providerFactory);
         Class subResourceClass = GetRestful.getSubResourceClass(target.getClass());
         if (subResourceClass == null)
         {
            String msg = "Subresource for target class has no jax-rs annotations.: " + target.getClass().getName();
            throw new LoggableFailure(msg, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
         }
         registry.addResourceFactory(null, null, subResourceClass);
         cachedSubresources.putIfAbsent(target.getClass(), registry);
      }
      ResourceInvoker invoker = registry.getResourceInvoker(request, response);
      if (invoker == null)
      {
         throw new Failure("No path match in subresource for: " + request.getUri().getAbsolutePath(), HttpResponseCodes.SC_NOT_FOUND);
      }
      else if (invoker instanceof ResourceLocator)
      {
         ResourceLocator locator = (ResourceLocator) invoker;
         return locator.invoke(request, response, target);
      }
      else
      {
         ResourceMethod method = (ResourceMethod) invoker;
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
               logger.warn("Field '{}' of subresource '{}' will not be injected " +
                       "according to spec", field.getName(), obj.getClass().getName());
            }

         }

         clazz = clazz.getSuperclass();

      }

   }

}
