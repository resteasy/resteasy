package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.resteasy.util.HttpResponseCodes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
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

   protected Object createResource(HttpRequest request, HttpResponse response, Object locator)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      index.populateUriInfoTemplateParams(request);
      Object[] args = methodInjector.injectArguments(request, response);
      try
      {
         Object subResource = method.invoke(locator, args);
         uriInfo.pushCurrentResource(locator);
         return subResource;

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

   public void setAncestorUri(UriInfoImpl uriInfo)
   {
      StringBuffer encoded = new StringBuffer();
      boolean first = true;
      for (int i = index.getOffset(); i < uriIndex + index.getOffset(); i++)
      {
         if (first) first = false;
         else
         {
            encoded.append("/");
         }
         PathSegmentImpl encodedSegment = (PathSegmentImpl) uriInfo.getPathSegments(false).get(i);
         encoded.append(encodedSegment.getOriginal());
      }
      String encodedUri = encoded.toString();
      try
      {
         String decodedUri = URLDecoder.decode(encodedUri, "UTF-8");
         uriInfo.pushAncestorURI(encodedUri, decodedUri);
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void invoke(HttpRequest request, HttpResponse response) throws IOException
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      try
      {
         Object target = createResource(request, response);
         setAncestorUri(uriInfo);
         invokeOnTargetObject(request, response, target);
      }
      finally
      {
         uriInfo.popCurrentResource();
         uriInfo.popAncestorURI();
      }
   }

   public void invoke(HttpRequest request, HttpResponse response, Object locator) throws IOException
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      try
      {
         Object target = createResource(request, response, locator);
         setAncestorUri(uriInfo);
         invokeOnTargetObject(request, response, target);
      }
      finally
      {
         uriInfo.popCurrentResource();
         uriInfo.popAncestorURI();
      }
   }

   protected void invokeOnTargetObject(HttpRequest request, HttpResponse response, Object target) throws IOException
   {
      ResourceMethodRegistry registry = cachedSubresources.get(target.getClass());
      if (registry == null)
      {
         registry = new ResourceMethodRegistry(providerFactory);
         Class subResourceClass = GetRestful.getSubResourceClass(target.getClass());
         if (subResourceClass == null)
         {
            String msg = "Subresource for target class has no jax-rs annotations.: " + target.getClass().getName();
            throw new Failure(msg, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
         }
         registry.addResourceFactory(null, null, subResourceClass, uriIndex + index.getOffset(), limited);
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
