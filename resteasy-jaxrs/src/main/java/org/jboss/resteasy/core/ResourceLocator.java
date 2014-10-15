package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.UriInfoImpl;
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
public class ResourceLocator implements ResourceInvoker
{

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
         throw new InternalServerErrorException(e);
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

   public ServerResponse invoke(HttpRequest request, HttpResponse response)
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

   public ServerResponse invoke(HttpRequest request, HttpResponse response, Object locator)
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

   protected ServerResponse invokeOnTargetObject(HttpRequest request, HttpResponse response, Object target)
   {
      if (target == null)
      {
         NotFoundException notFound = new NotFoundException(Messages.MESSAGES.nullResource(request.getUri().getAbsolutePath()));
         notFound.setLoggable(true);
         throw notFound;
      }
      Registry registry = cachedSubresources.get(target.getClass());
      if (registry == null)
      {
         registry = new ResourceMethodRegistry(providerFactory);
         
         if (!GetRestful.isSubResourceClass(target.getClass()))
         {
            String msg = Messages.MESSAGES.subresourceHasNoJaxRsAnnotations(target.getClass().getName());
            throw new InternalServerErrorException(msg);
         }
         if (Proxy.isProxyClass(target.getClass()))
         {
            registry.addResourceFactory(null, null, GetRestful.getSubResourceClasses(target.getClass()));
         }
         else
         {
            registry.addResourceFactory(null, null, target.getClass());//subResourceClass);
         }
         cachedSubresources.putIfAbsent(target.getClass(), registry);
      }
      ResourceInvoker invoker = registry.getResourceInvoker(request);
      if (invoker == null)
      {
         NotFoundException notFound = new NotFoundException(Messages.MESSAGES.noPathMatchInSubresource(request.getUri().getAbsolutePath()));
         notFound.setLoggable(true);
         throw notFound;
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
               LogMessages.LOGGER.fieldOfSubesourceWillNotBeInjected(field.getName(), obj.getClass().getName());
            }

         }

         clazz = clazz.getSuperclass();

      }

   }

}
