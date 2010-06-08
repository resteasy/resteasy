package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistryListener;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethod implements ResourceInvoker, InterceptorRegistryListener
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
   protected PreProcessInterceptor[] preProcessInterceptors;
   protected PostProcessInterceptor[] postProcessInterceptors;
   protected MessageBodyWriterInterceptor[] writerInterceptors;
   protected ConcurrentHashMap<String, AtomicLong> stats = new ConcurrentHashMap<String, AtomicLong>();
   protected Type genericReturnType;


   public ResourceMethod(Class<?> clazz, Method method, InjectorFactory injector, ResourceFactory resource, ResteasyProviderFactory providerFactory, Set<String> httpMethods)
   {
      this.injector = injector;
      this.resource = resource;
      this.providerFactory = providerFactory;
      this.httpMethods = httpMethods;
      this.resourceClass = clazz;
      this.method = method;
      this.methodInjector = injector.createMethodInjector(clazz, method);

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

      preProcessInterceptors = providerFactory.getServerPreProcessInterceptorRegistry().bind(resourceClass, method);
      postProcessInterceptors = providerFactory.getServerPostProcessInterceptorRegistry().bind(resourceClass, method);
      writerInterceptors = providerFactory.getServerMessageBodyWriterInterceptorRegistry().bind(resourceClass, method);

      providerFactory.getServerPreProcessInterceptorRegistry().getListeners().add(this);
      providerFactory.getServerPostProcessInterceptorRegistry().getListeners().add(this);
      providerFactory.getServerMessageBodyWriterInterceptorRegistry().getListeners().add(this);
      /*
          We get the genericReturnType for the case of:
          
          interface Foo<T> {
             @GET
             List<T> get();
          }

          public class FooImpl implements Foo<Customer> {
              public List<Customer> get() {...}
          }
       */
      genericReturnType = Types.getGenericReturnTypeOfGenericInterfaceMethod(clazz, method);
   }

   public void cleanup()
   {
      providerFactory.getServerPreProcessInterceptorRegistry().getListeners().remove(this);
      providerFactory.getServerPostProcessInterceptorRegistry().getListeners().remove(this);
      providerFactory.getServerMessageBodyWriterInterceptorRegistry().getListeners().remove(this);
      for (ValueInjector param : methodInjector.getParams())
      {
         if (param instanceof MessageBodyParameterInjector)
         {
            providerFactory.getServerMessageBodyReaderInterceptorRegistry().getListeners().remove(param);
         }
      }
   }

   public void registryUpdated(InterceptorRegistry registry)
   {
      if (registry.getIntf().equals(MessageBodyWriterInterceptor.class))
      {
         writerInterceptors = providerFactory.getServerMessageBodyWriterInterceptorRegistry().bind(resourceClass, method);
      }
      else if (registry.getIntf().equals(PreProcessInterceptor.class))
      {
         preProcessInterceptors = providerFactory.getServerPreProcessInterceptorRegistry().bind(resourceClass, method);
      }
      else if (registry.getIntf().equals(PostProcessInterceptor.class))
      {
         postProcessInterceptors = providerFactory.getServerPostProcessInterceptorRegistry().bind(resourceClass, method);
      }
   }


   protected void incrementMethodCount(String httpMethod)
   {
      AtomicLong stat = stats.get(httpMethod);
      if (stat == null)
      {
         stat = new AtomicLong();
         AtomicLong old = stats.putIfAbsent(httpMethod, stat);
         if (old != null) stat = old;
      }
      stat.incrementAndGet();
   }

   /**
    * Key is httpMethod called
    *
    * @return
    */
   public Map<String, AtomicLong> getStats()
   {
      return stats;
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

   public ServerResponse invoke(HttpRequest request, HttpResponse response)
   {
      Object target = resource.createResource(request, response, injector);
      return invoke(request, response, target);
   }

   public ServerResponse invoke(HttpRequest request, HttpResponse response, Object target)
   {
      request.setAttribute(ResourceMethod.class.getName(), this);
      incrementMethodCount(request.getHttpMethod());
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      uriInfo.pushCurrentResource(target);
      try
      {
         ServerResponse jaxrsResponse = invokeOnTarget(request, response, target);

         if (jaxrsResponse != null && jaxrsResponse.getEntity() != null)
         {
            // if the content type isn't set, then set it to be either most desired type from the Accept header
            // or the first media type in the @Produces annotation
            // See RESTEASY-144
            Object type = jaxrsResponse.getMetadata().getFirst(
                    HttpHeaderNames.CONTENT_TYPE);
            if (type == null)
               jaxrsResponse.getMetadata().putSingle(HttpHeaderNames.CONTENT_TYPE, resolveContentType(request, jaxrsResponse.getEntity()));
         }
         return jaxrsResponse;

      }
      finally
      {
         uriInfo.popCurrentResource();
      }
   }

   protected ServerResponse invokeOnTarget(HttpRequest request, HttpResponse response, Object target)
   {
      for (PreProcessInterceptor preInterceptor : preProcessInterceptors)
      {
         ServerResponse serverResponse = preInterceptor.preProcess(request, this);
         if (serverResponse != null)
         {
            return prepareResponse(serverResponse);
         }
      }

      Object rtn = methodInjector.invoke(request, response, target);
      if (request.isSuspended())
      {
         AbstractAsynchronousResponse asyncResponse = (AbstractAsynchronousResponse) request.getAsynchronousResponse();
         if (asyncResponse == null) return null;
         asyncResponse.setAnnotations(method.getAnnotations());
         asyncResponse.setMessageBodyWriterInterceptors(writerInterceptors);
         asyncResponse.setPostProcessInterceptors(postProcessInterceptors);
         return null;
      }
      if (rtn == null || method.getReturnType().equals(void.class))
      {
         return (ServerResponse) Response.noContent().build();
      }
      if (Response.class.isAssignableFrom(method.getReturnType()) || rtn instanceof Response)
      {
         return prepareResponse(ServerResponse.copyIfNotServerResponse((Response) rtn));
      }

      Response.ResponseBuilder builder = Response.ok(rtn);
      builder.type(resolveContentType(request, rtn));
      ServerResponse jaxrsResponse = (ServerResponse) builder.build();
      jaxrsResponse.setGenericType(genericReturnType);
      return prepareResponse(jaxrsResponse);
   }

   protected ServerResponse prepareResponse(ServerResponse serverResponse)
   {
      serverResponse.setAnnotations(method.getAnnotations());
      serverResponse.setMessageBodyWriterInterceptors(writerInterceptors);
      serverResponse.setPostProcessInterceptors(postProcessInterceptors);
      return serverResponse;
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

   protected MediaType resolveContentType(HttpRequest in, Object entity)
   {
      MediaType responseContentType = matchByType(in.getHttpHeaders().getAcceptableMediaTypes(), entity);
      if (responseContentType == null)
      {
         responseContentType = MediaType.WILDCARD_TYPE;
      }
      //NOTE: This should be the real behavior, but the stupid spec says it should default to */*
      /*
      if (responseContentType == null || responseContentType.isWildcardType())
      {
         throw new LoggableFailure("There is no Content-Type set.  Annotate your method with @Produces or set the content type in the Response object for method: " + method, 500);
      }
      */
      return responseContentType;
   }

   public MediaType matchByType(List<MediaType> accepts, Object entity)
   {
      if (accepts == null || accepts.size() == 0)
      {
         if (produces == null) return MediaType.WILDCARD_TYPE;
         else return produces[0];
      }

      if (produces == null || produces.length == 0)
      {
         return resolveContentTypeByAccept(accepts, entity);
      }

      for (MediaType accept : accepts)
      {
         for (MediaType type : produces)
         {
            if (type.isCompatible(accept)) return type;
         }
      }
      return MediaType.WILDCARD_TYPE;
   }

   protected MediaType resolveContentTypeByAccept(List<MediaType> accepts, Object entity)
   {
      if (accepts == null || accepts.size() == 0 || entity == null)
      {
         return MediaType.WILDCARD_TYPE;
      }
      Class clazz = entity.getClass();
      Type type = null;
      if (entity instanceof GenericEntity)
      {
         GenericEntity gen = (GenericEntity) entity;
         clazz = gen.getRawType();
         type = gen.getType();
      }
      for (MediaType accept : accepts)
      {
         if (providerFactory.getMessageBodyWriter(clazz, type, method.getAnnotations(), accept) != null)
         {
            return accept;
         }
      }
      return MediaType.WILDCARD_TYPE;
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
