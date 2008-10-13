package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.ResourceMethodContext;
import org.jboss.resteasy.core.interception.ResourceMethodInterceptor;
import org.jboss.resteasy.specimpl.ResponseImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethod implements ResourceInvoker
{

   protected MediaType[] produces;
   protected MediaType[] consumes;
   protected List<WeightedMediaType> preferredProduces = new ArrayList<WeightedMediaType>();
   protected List<WeightedMediaType> preferredConsumes = new ArrayList<WeightedMediaType>();
   protected Set<String> httpMethods;
   protected MethodInjector methodInjector;
   protected InjectorFactory injector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected Class<?> resourceClass;
   protected ResourceMethodInterceptor[] interceptors;

   public ResourceMethod(Class<?> clazz, Method method, InjectorFactory injector, ResourceFactory resource, ResteasyProviderFactory providerFactory, Set<String> httpMethods)
   {
      this.injector = injector;
      this.resource = resource;
      this.providerFactory = providerFactory;
      this.httpMethods = httpMethods;
      this.resourceClass = clazz;
      this.method = method;
      this.methodInjector = injector.createMethodInjector(method);

      Produces p = method.getAnnotation(Produces.class);
      if (p == null) p = clazz.getAnnotation(Produces.class);
      Consumes c = method.getAnnotation(Consumes.class);
      if (c == null) c = clazz.getAnnotation(Consumes.class);

      if (p != null)
      {
         produces = new MediaType[p.value().length];
         int i = 0;
         for (String mediaType : p.value())
         {
            produces[i++] = MediaType.valueOf(mediaType);
            preferredProduces.add(WeightedMediaType.valueOf(mediaType));
         }
      }
      if (c != null)
      {
         consumes = new MediaType[c.value().length];
         int i = 0;
         for (String mediaType : c.value())
         {
            consumes[i++] = MediaType.valueOf(mediaType);
            preferredConsumes.add(WeightedMediaType.valueOf(mediaType));
         }
      }
      Collections.sort(preferredProduces);
      Collections.sort(preferredConsumes);
      interceptors = providerFactory.getInterceptorRegistry().bind(this);
      if (interceptors != null && interceptors.length == 0) interceptors = null;
   }

   public Class<?> getResourceClass()
   {
      return resourceClass;
   }

   /**
    * Presorted list of preferred types, 1st entry is most preferred
    *
    * @return
    */
   public List<WeightedMediaType> getPreferredProduces()
   {
      return preferredProduces;
   }

   /**
    * Presorted list of preferred types, 1st entry is most preferred
    *
    * @return
    */
   public List<WeightedMediaType> getPreferredConsumes()
   {
      return preferredConsumes;
   }

   public Method getMethod()
   {
      return method;
   }

   public Response invoke(HttpRequest request, HttpResponse response)
   {
      Object target = resource.createResource(request, response, injector);
      return invoke(request, response, target);
   }

   protected class ResourceContext implements ResourceMethodContext
   {
      protected HttpRequest request;
      protected HttpResponse response;
      protected Object target;
      protected int index;

      public ResourceContext(HttpRequest request, HttpResponse response, Object target)
      {
         this.request = request;
         this.response = response;
         this.target = target;
      }

      public HttpRequest getRequest()
      {
         return null;
      }

      public void setRequest(HttpRequest request)
      {
      }

      public Object getTarget()
      {
         return target;
      }

      public void setTarget(Object target)
      {
         this.target = target;
      }

      public ResourceMethod getMethod()
      {
         return ResourceMethod.this;
      }

      public Response proceed() throws Failure, WebApplicationException, ApplicationException
      {
         if (index >= interceptors.length) return invokeOnTarget(request, response, target);
         try
         {
            return interceptors[index++].invoke(this);
         }
         finally
         {
            index--;
         }
      }
   }

   public Response invoke(HttpRequest request, HttpResponse response, Object target)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      uriInfo.pushCurrentResource(target);
      try
      {
         if (interceptors == null || interceptors.length == 0)
            return invokeOnTarget(request, response, target);
         return new ResourceContext(request, response, target).proceed();

      }
      finally
      {
         uriInfo.popCurrentResource();
      }
   }

   protected Response invokeOnTarget(HttpRequest request, HttpResponse response, Object target)
   {
      Object rtn = methodInjector.invoke(request, response, target);
      if (request.isSuspended()) return null;
      if (method.getReturnType().equals(Response.class))
      {
         return (Response) rtn;
      }
      if (method.getReturnType().equals(void.class))
      {
         if (request.getHttpMethod().toUpperCase().equals("DELETE") || request.getHttpMethod().toUpperCase().equals("POST"))
            return Response.noContent().build();
         else return Response.ok().build();
      }
      Response.ResponseBuilder builder = null;
      if (rtn == null && (request.getHttpMethod().toUpperCase().equals("DELETE") || request.getHttpMethod().toUpperCase().equals("POST")))
      {
         builder = Response.status(HttpResponseCodes.SC_NO_CONTENT);
      }
      else
      {
         builder = Response.ok(rtn);
      }
      builder.type(resolveContentType(request));
      ResponseImpl jaxrsResponse = (ResponseImpl) builder.build();
      jaxrsResponse.setGenericType(method.getGenericReturnType());
      jaxrsResponse.setAnnotations(method.getAnnotations());
      return jaxrsResponse;
   }

   public boolean doesProduce(List<? extends MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0)
      {
         //System.out.println("**** no accepts " +" method: " + method);
         return true;
      }
      if (produces == null || produces.length == 0)
      {
         //System.out.println("**** no produces " +" method: " + method);
         return true;
      }

      for (MediaType accept : accepts)
      {
         for (MediaType type : preferredProduces)
         {
            if (type.isCompatible(accept))
            {
               return true;
            }
         }
      }
      return false;
   }

   public boolean doesConsume(MediaType contentType)
   {
      boolean matches = false;
      if (contentType == null)
      {
         matches = true;
      }
      else
      {
         if (consumes == null || consumes.length == 0)
         {
            matches = true;
         }
         else
         {
            for (MediaType type : preferredConsumes)
            {
               if (type.isCompatible(contentType))
               {
                  matches = true;
                  break;
               }
            }
         }
      }
      return matches;
   }

   protected MediaType resolveContentType(HttpRequest in)
   {
      MediaType responseContentType = matchByType(in.getHttpHeaders().getAcceptableMediaTypes());
      if (responseContentType == null)
      {
         responseContentType = MediaType.valueOf("*/*");
      }
      return responseContentType;
   }

   public MediaType matchByType(List<MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0)
      {
         if (produces == null) return MediaType.valueOf("*/*");
         else return produces[0];
      }

      if (produces == null || produces.length == 0) return accepts.get(0);

      for (MediaType accept : accepts)
      {
         for (MediaType type : produces)
         {
            if (type.isCompatible(accept)) return type;
         }
      }
      return null;
   }

   public Set<String> getHttpMethods()
   {
      return httpMethods;
   }

   public MediaType[] getProduces()
   {
      return produces;
   }

   public MediaType[] getConsumes()
   {
      return consumes;
   }
}
