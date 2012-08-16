package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistryListener;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.jboss.resteasy.core.registry.Segment;
import org.jboss.resteasy.plugins.providers.validation.ResteasyViolationExceptionExtension;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
public class ResourceMethod implements ResourceInvoker, JaxrsInterceptorRegistryListener
{

   protected MediaType[] produces;
   protected MediaType[] consumes;
   protected Consumes methodConsumes;

   protected List<WeightedMediaType> preferredProduces = new ArrayList<WeightedMediaType>();
   protected List<WeightedMediaType> preferredConsumes = new ArrayList<WeightedMediaType>();
   protected Set<String> httpMethods;
   protected MethodInjector methodInjector;
   protected InjectorFactory injector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected Class<?> resourceClass;
   protected ContainerRequestFilter[] requestFilters;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected ConcurrentHashMap<String, AtomicLong> stats = new ConcurrentHashMap<String, AtomicLong>();
   protected Type genericReturnType;
   protected GeneralValidator validator;
   protected ViolationsContainer<?> violationsContainer;


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
      if (p == null) p = method.getDeclaringClass().getAnnotation(Produces.class);
      Consumes c = methodConsumes = method.getAnnotation(Consumes.class);
      if (c == null) c = clazz.getAnnotation(Consumes.class);
      if(c == null) c = method.getDeclaringClass().getAnnotation(Consumes.class);

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

      requestFilters = providerFactory.getContainerRequestFilterRegistry().postMatch(resourceClass, method);
      responseFilters = providerFactory.getContainerResponseFilterRegistry().postMatch(resourceClass, method);
      writerInterceptors = providerFactory.getServerWriterInterceptorRegistry().postMatch(resourceClass, method);

      providerFactory.getContainerRequestFilterRegistry().getListeners().add(this);
      providerFactory.getContainerResponseFilterRegistry().getListeners().add(this);
      providerFactory.getServerWriterInterceptorRegistry().getListeners().add(this);
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
      ContextResolver<GeneralValidator> resolver = providerFactory.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE);
      if (resolver != null)
      {
         validator = providerFactory.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE).getContext(null);
      }
   }

   public void cleanup()
   {
      providerFactory.getContainerRequestFilterRegistry().getListeners().remove(this);
      providerFactory.getContainerResponseFilterRegistry().getListeners().remove(this);
      providerFactory.getServerWriterInterceptorRegistry().getListeners().remove(this);
      for (ValueInjector param : methodInjector.getParams())
      {
         if (param instanceof MessageBodyParameterInjector)
         {
            providerFactory.getServerReaderInterceptorRegistry().getListeners().remove(param);
         }
      }
   }

   public void registryUpdated(JaxrsInterceptorRegistry registry)
   {
      if (registry.getIntf().equals(WriterInterceptor.class))
      {
         writerInterceptors = providerFactory.getServerWriterInterceptorRegistry().postMatch(resourceClass, method);
      }
      else if (registry.getIntf().equals(ContainerRequestFilter.class))
      {
         requestFilters = providerFactory.getContainerRequestFilterRegistry().postMatch(resourceClass, method);
      }
      else if (registry.getIntf().equals(ContainerResponseFilter.class))
      {
         responseFilters = providerFactory.getContainerResponseFilterRegistry().postMatch(resourceClass, method);
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

   public ContainerRequestFilter[] getRequestFilters()
   {
      return requestFilters;
   }

   public ContainerResponseFilter[] getResponseFilters()
   {
      return responseFilters;
   }

   public WriterInterceptor[] getWriterInterceptors()
   {
      return writerInterceptors;
   }

   public Type getGenericReturnType()
   {
      return genericReturnType;
   }

   public Class<?> getResourceClass()
   {
      return resourceClass;
   }

   public Annotation[] getMethodAnnotations()
   {
      return method.getAnnotations();
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

   public BuiltResponse invoke(HttpRequest request, HttpResponse response)
   {
      Object target = resource.createResource(request, response, injector);
      return invoke(request, response, target);
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response, Object target)
   {
      request.setAttribute(ResourceMethod.class.getName(), this);
      incrementMethodCount(request.getHttpMethod());
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
      uriInfo.pushCurrentResource(target);
      try
      {
         BuiltResponse jaxrsResponse = invokeOnTarget(request, response, target);

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

   protected BuiltResponse invokeOnTarget(HttpRequest request, HttpResponse response, Object target)
   {
      if (validator != null)
      {
         violationsContainer = new ViolationsContainer<Object>(validator.validate(target));
         request.setAttribute(ViolationsContainer.class.getName(), violationsContainer);
         request.setAttribute(GeneralValidator.class.getName(), validator);
      }

      PostMatchContainerRequestContext requestContext = new PostMatchContainerRequestContext(request, this);
      for (ContainerRequestFilter filter : requestFilters)
      {
         try
         {
            filter.filter(requestContext);
         }
         catch (IOException e)
         {
            throw new ApplicationException(e);
         }
         BuiltResponse serverResponse = (BuiltResponse)requestContext.getResponseAbortedWith();
         if (serverResponse != null)
         {
            return serverResponse;
         }
      }

      Object rtn = methodInjector.invoke(request, response, target);

      if (violationsContainer != null && violationsContainer.size() > 0)
      {
         throw new ResteasyViolationExceptionExtension(violationsContainer);
      }

      if (request.getAsyncContext().isSuspended())
      {
         request.getAsyncContext().getAsyncResponse().setAnnotations(method.getAnnotations());
         request.getAsyncContext().getAsyncResponse().setWriterInterceptors(writerInterceptors);
         request.getAsyncContext().getAsyncResponse().setResponseFilters(responseFilters);
         request.getAsyncContext().getAsyncResponse().setMethod(this);
         return null;
      }
      if (rtn == null || method.getReturnType().equals(void.class))
      {
         BuiltResponse build = (BuiltResponse) Response.noContent().build();
         build.setAnnotations(method.getAnnotations());
         return build;
      }
      if (Response.class.isAssignableFrom(method.getReturnType()) || rtn instanceof Response)
      {
         BuiltResponse rtn1 = (BuiltResponse) rtn;
         rtn1.setAnnotations(method.getAnnotations());
         return rtn1;
      }

      Response.ResponseBuilder builder = Response.ok(rtn);
      builder.type(resolveContentType(request, rtn));
      BuiltResponse jaxrsResponse = (BuiltResponse)builder.build();
      jaxrsResponse.setGenericType(genericReturnType);
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
         // If there is no @Consumes annotation directly on method (i.e. a @GET or @DELETE) return true
         if (methodConsumes == null) return true;

         // Otherwise only accept if consumes is a wildcard type
         for (MediaType type : preferredConsumes)
         {
            if (type.equals(MediaType.WILDCARD_TYPE))
            {
               return true;
            }
         }
         return false;
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

   public MediaType resolveContentType(HttpRequest in, Object entity)
   {
      MediaType chosen = (MediaType)in.getAttribute(Segment.RESTEASY_CHOSEN_ACCEPT);
      if (chosen != null  && !chosen.equals(MediaType.WILDCARD_TYPE))
      {
         return chosen;
      }

      List<MediaType> accepts = in.getHttpHeaders().getAcceptableMediaTypes();

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
      Type type = this.genericReturnType;
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
