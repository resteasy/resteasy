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
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.ResteasyViolationException;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.FeatureContextDelegate;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.PathHelper;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethodInvoker implements ResourceInvoker, JaxrsInterceptorRegistryListener
{
   protected List<WeightedMediaType> preferredProduces = new ArrayList<WeightedMediaType>();
   protected List<WeightedMediaType> preferredConsumes = new ArrayList<WeightedMediaType>();
   protected MethodInjector methodInjector;
   protected InjectorFactory injector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory parentProviderFactory;
   protected ResteasyProviderFactory resourceMethodProviderFactory;
   protected ResourceMethod method;
   protected ContainerRequestFilter[] requestFilters;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected ConcurrentHashMap<String, AtomicLong> stats = new ConcurrentHashMap<String, AtomicLong>();
   protected GeneralValidator validator;
   protected ViolationsContainer<?> violationsContainer;
   protected ResourceInfo resourceInfo;

   protected Pattern classRegex = null;

   protected boolean methodConsumes;



   public ResourceMethodInvoker(ResourceMethod method, InjectorFactory injector, ResourceFactory resource, ResteasyProviderFactory providerFactory)
   {
      this.injector = injector;
      this.resource = resource;
      this.parentProviderFactory = providerFactory;
      this.method = method;

       resourceInfo = new ResourceInfo()
      {
         @Override
         public Method getResourceMethod()
         {
            return ResourceMethodInvoker.this.method.getAnnotatedMethod();
         }

         @Override
         public Class<?> getResourceClass()
         {
            return ResourceMethodInvoker.this.method.getResourceClass().getClazz();
         }
      };

      classRegex = setupClassRegex(method);

      this.resourceMethodProviderFactory = new ResteasyProviderFactory(providerFactory);
      for (DynamicFeature feature : providerFactory.getServerDynamicFeatures())
      {
         feature.configure(resourceInfo, new FeatureContextDelegate(resourceMethodProviderFactory));
      }

      this.methodInjector = injector.createMethodInjector(method, resourceMethodProviderFactory);

      for (MediaType mediaType : method.getProduces())
      {
         preferredProduces.add(WeightedMediaType.parse(mediaType));

      }

      // hack for when message contentType == null
      // todo this needs review on why it is here and what do we need it for
      methodConsumes = method.getAnnotatedMethod().isAnnotationPresent(Consumes.class);

      for (MediaType mediaType : method.getConsumes())
      {
         preferredConsumes.add(WeightedMediaType.parse(mediaType));

      }

      Collections.sort(preferredProduces);
      Collections.sort(preferredConsumes);

      requestFilters = resourceMethodProviderFactory.getContainerRequestFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      responseFilters = resourceMethodProviderFactory.getContainerResponseFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      writerInterceptors = resourceMethodProviderFactory.getServerWriterInterceptorRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());


      // we register with parent to lisen for redeploy evens
      providerFactory.getContainerRequestFilterRegistry().getListeners().add(this);
      providerFactory.getContainerResponseFilterRegistry().getListeners().add(this);
      providerFactory.getServerWriterInterceptorRegistry().getListeners().add(this);
      ContextResolver<GeneralValidator> resolver = providerFactory.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE);
      if (resolver != null)
      {
         validator = providerFactory.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE).getContext(null);
      }
   }

   public void cleanup()
   {
      parentProviderFactory.getContainerRequestFilterRegistry().getListeners().remove(this);
      parentProviderFactory.getContainerResponseFilterRegistry().getListeners().remove(this);
      parentProviderFactory.getServerWriterInterceptorRegistry().getListeners().remove(this);
      for (ValueInjector param : methodInjector.getParams())
      {
         if (param instanceof MessageBodyParameterInjector)
         {
            parentProviderFactory.getServerReaderInterceptorRegistry().getListeners().remove(param);
         }
      }
   }

   public void registryUpdated(JaxrsInterceptorRegistry registry)
   {
      this.resourceMethodProviderFactory = new ResteasyProviderFactory(parentProviderFactory);
      for (DynamicFeature feature : parentProviderFactory.getServerDynamicFeatures())
      {
         feature.configure(resourceInfo, new FeatureContextDelegate(resourceMethodProviderFactory));
      }
      if (registry.getIntf().equals(WriterInterceptor.class))
      {
         writerInterceptors = resourceMethodProviderFactory.getServerWriterInterceptorRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      }
      else if (registry.getIntf().equals(ContainerRequestFilter.class))
      {
         requestFilters = resourceMethodProviderFactory.getContainerRequestFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      }
      else if (registry.getIntf().equals(ContainerResponseFilter.class))
      {
         responseFilters = resourceMethodProviderFactory.getContainerResponseFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
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
      return method.getGenericReturnType();
   }

   public Class<?> getResourceClass()
   {
      return method.getResourceClass().getClazz();
   }

   public Annotation[] getMethodAnnotations()
   {
      return method.getAnnotatedMethod().getAnnotations();
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
      return method.getMethod();
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response)
   {
      Object target = resource.createResource(request, response, resourceMethodProviderFactory);
      return invoke(request, response, target);
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response, Object target)
   {
      request.setAttribute(ResourceMethodInvoker.class.getName(), this);
      incrementMethodCount(request.getHttpMethod());
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();

      pushMatchedUri(method, classRegex, uriInfo);
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
         //uriInfo.popCurrentResource();
      }
   }

   public static Pattern setupClassRegex(ResourceLocator method)
   {
      String segment = "";
      if (method.getResourceClass().getClazz().isAnnotationPresent(Path.class))
      {
         segment = method.getResourceClass().getClazz().getAnnotation(Path.class).value();
         if (segment.startsWith("/")) segment = segment.substring(1);
         if ("".equals(segment)) return null;
      }
      else
      {
         return null;
      }
      return getClassRegexPattern(segment);
   }

   public static Pattern getClassRegexPattern(String segment)
   {
      String replacedCurlySegment = PathHelper.replaceEnclosedCurlyBraces(segment);

      String[] split = PathHelper.URI_PARAM_PATTERN.split(replacedCurlySegment);
      Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment);
      int i = 0;
      StringBuffer buffer = new StringBuffer();
      if (i < split.length) buffer.append(Pattern.quote(split[i++]));

      while (withPathParam.find())
      {
         buffer.append("(");
         if (withPathParam.group(3) == null)
         {
            buffer.append("[^/]+");
         }
         else
         {
            String expr = withPathParam.group(3);
            expr = PathHelper.recoverEnclosedCurlyBraces(expr);
            buffer.append(expr);
         }
         buffer.append(")");
         if (i < split.length) buffer.append(Pattern.quote(split[i++]));
      }
      return Pattern.compile(buffer.toString());
   }

   public static void pushMatchedUri(ResourceLocator method, Pattern classRegex, ResteasyUriInfo uriInfo)
   {
      // we're at the top so setup matched uri for class
      if (uriInfo.getMatchedResources().size() == 0)
      {
         String encoded = uriInfo.getEncodedMatchedPaths().get(0);
         if (encoded.startsWith("/")) encoded = encoded.substring(1);
         String decoded = Encode.decode(encoded);
         if (method.getPath() != null && !method.getPath().equals(""))
         {
            if (classRegex == null)
            {
               uriInfo.pushMatchedURI("", "");
               uriInfo.pushMatchedURI(encoded, decoded);
            }
            else
            {
               Matcher match = classRegex.matcher(encoded);
               if (match.find())
               {
                  int end = match.end();
                  String classMatch = encoded.substring(0, end);
                  uriInfo.pushMatchedURI(classMatch, Encode.decode(classMatch));
                  uriInfo.pushMatchedURI(encoded, decoded);

               }
               else
               {
                  // fuckit
               }
            }
         }
         else
         {
            uriInfo.pushMatchedURI(encoded, decoded);
         }
      }
      else if (method.getPath() != null && !method.getPath().equals(""))
      {
         String encoded = uriInfo.getEncodedMatchedPaths().get(0);
         if (encoded.startsWith("/")) encoded = encoded.substring(1);
         String decoded = Encode.decode(encoded);
         uriInfo.pushMatchedURI(encoded, decoded);
      }
   }

   protected BuiltResponse invokeOnTarget(HttpRequest request, HttpResponse response, Object target)
   {
      ResteasyProviderFactory.pushContext(ResourceInfo.class, resourceInfo);  // we don't pop so writer interceptors can get at this
      if (validator != null)
      {
         violationsContainer = new ViolationsContainer<Object>(validator.validate(target));
         request.setAttribute(ViolationsContainer.class.getName(), violationsContainer);
         request.setAttribute(Validator.class.getName(), validator);
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
         throw new ResteasyViolationException(violationsContainer);
      }

      if (request.getAsyncContext().isSuspended())
      {
         request.getAsyncContext().getAsyncResponse().setAnnotations(method.getAnnotatedMethod().getAnnotations());
         request.getAsyncContext().getAsyncResponse().setWriterInterceptors(writerInterceptors);
         request.getAsyncContext().getAsyncResponse().setResponseFilters(responseFilters);
         request.getAsyncContext().getAsyncResponse().setMethod(this);
         return null;
      }
      if (rtn == null || method.getReturnType().equals(void.class))
      {
         BuiltResponse build = (BuiltResponse) Response.noContent().build();
         build.setAnnotations(method.getAnnotatedMethod().getAnnotations());
         return build;
      }
      if (Response.class.isAssignableFrom(method.getReturnType()) || rtn instanceof Response)
      {
         BuiltResponse rtn1 = (BuiltResponse) rtn;
         if (rtn1.getAnnotations() == null) rtn1.setAnnotations(method.getAnnotatedMethod().getAnnotations());
         if (rtn1.getGenericType() == null)
         {
            if (getMethod().getReturnType().equals(Response.class))
            {
               rtn1.setGenericType(rtn1.getEntityClass());
            }
            else
            {
               rtn1.setGenericType(method.getGenericReturnType());
            }
         }
         return rtn1;
      }

      Response.ResponseBuilder builder = Response.ok(rtn);
      builder.type(resolveContentType(request, rtn));
      BuiltResponse jaxrsResponse = (BuiltResponse)builder.build();
      if (jaxrsResponse.getGenericType() == null)
      {
         if (getMethod().getReturnType().equals(Response.class))
         {
            jaxrsResponse.setGenericType(jaxrsResponse.getEntityClass());
         }
         else
         {
            jaxrsResponse.setGenericType(method.getGenericReturnType());
         }
      }
      if (jaxrsResponse.getAnnotations() == null) jaxrsResponse.setAnnotations(method.getAnnotatedMethod().getAnnotations());
      return jaxrsResponse;
   }

   public boolean doesProduce(List<? extends MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0)
      {
         //System.out.println("**** no accepts " +" method: " + method);
         return true;
      }
      if (preferredProduces.size() == 0)
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
         return true;
         /*
         // If there is no @Consumes annotation directly on method (i.e. a @GET or @DELETE) return true
         // this is a hack to determine if this is a @GET or @DELETE.  Because we can create new HTTP methods ad hoc
         // there is no way to determine if it is an HTTP method that doesn't require content (like PUT, POST)
         // So, we just check to see if there is an annotation directly on the method.
         if (!methodConsumes) return true;

         // Otherwise only accept if consumes is a wildcard type
         for (MediaType type : preferredConsumes)
         {
            if (type.equals(MediaType.WILDCARD_TYPE))
            {
               return true;
            }
         }
         return false;
         */
      }
      else
      {
         if (preferredConsumes.size() == 0) return true;
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
         if (preferredProduces.size() == 0) return MediaType.WILDCARD_TYPE;
         else return preferredProduces.get(0);
      }

      if (preferredProduces.size() == 0)
      {
         return resolveContentTypeByAccept(accepts, entity);
      }

      for (MediaType accept : accepts)
      {
         for (MediaType type : preferredProduces)
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
      Type type = this.method.getGenericReturnType();
      if (entity instanceof GenericEntity)
      {
         GenericEntity gen = (GenericEntity) entity;
         clazz = gen.getRawType();
         type = gen.getType();
      }
      for (MediaType accept : accepts)
      {
         if (resourceMethodProviderFactory.getMessageBodyWriter(clazz, type, method.getAnnotatedMethod().getAnnotations(), accept) != null)
         {
            return accept;
         }
      }
      return MediaType.WILDCARD_TYPE;
   }

   public Set<String> getHttpMethods()
   {
      return method.getHttpMethods();
   }

   public MediaType[] getProduces()
   {
      return method.getProduces();
   }

   public MediaType[] getConsumes()
   {
      return method.getConsumes();
   }
}
