package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.PreMatchContainerRequestContext;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalDispatcher;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnhandledException;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class SynchronousDispatcher implements Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected Registry registry;
   protected List<HttpRequestPreprocessor> requestPreprocessors = new ArrayList<HttpRequestPreprocessor>();
   protected Map<Class, Object> defaultContextObjects = new HashMap<Class, Object>();
   protected Set<String> unwrappedExceptions = new HashSet<String>();

   private final static Logger logger = Logger.getLogger(SynchronousDispatcher.class);

   public SynchronousDispatcher(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
      defaultContextObjects.put(Providers.class, providerFactory);
      defaultContextObjects.put(Registry.class, registry);
      defaultContextObjects.put(Dispatcher.class, this);
      defaultContextObjects.put(InternalDispatcher.class, InternalDispatcher.getInstance());
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public Map<Class, Object> getDefaultContextObjects()
   {
      return defaultContextObjects;
   }

   public Set<String> getUnwrappedExceptions()
   {
      return unwrappedExceptions;
   }

   public Response preprocess(HttpRequest request)
   {
      Response aborted = null;
      try
      {
         for (HttpRequestPreprocessor preprocessor : this.requestPreprocessors)
         {
            preprocessor.preProcess(request);
         }
         ContainerRequestFilter[] requestFilters = providerFactory.getContainerRequestFilterRegistry().preMatch();
         PreMatchContainerRequestContext requestContext = new PreMatchContainerRequestContext(request);
         for (ContainerRequestFilter filter : requestFilters)
         {
            filter.filter(requestContext);
            aborted = requestContext.getResponseAbortedWith();
            if (aborted != null) break;
         }
      }
      catch (Exception e)
      {
         aborted = new ExceptionHandler(providerFactory, unwrappedExceptions).handleException(request, e);
     }
      return aborted;
   }

   /**
    * Call pre-process ContainerRequestFilters
    *
    * @return true if request should continue
    */
   protected boolean preprocess(HttpRequest request, HttpResponse response)
   {
      Response aborted = null;
      try
      {
         for (HttpRequestPreprocessor preprocessor : this.requestPreprocessors)
         {
            preprocessor.preProcess(request);
         }
         ContainerRequestFilter[] requestFilters = providerFactory.getContainerRequestFilterRegistry().preMatch();
         PreMatchContainerRequestContext requestContext = new PreMatchContainerRequestContext(request);
         for (ContainerRequestFilter filter : requestFilters)
         {
            filter.filter(requestContext);
            aborted = requestContext.getResponseAbortedWith();
            if (aborted != null) break;
         }
      }
      catch (Exception e)
      {
         writeException(request, response, e);
         return false;
      }
      if (aborted != null)
      {
         writeResponse(request, response, aborted);
         return false;
      }
      return true;
   }

   public void writeException(HttpRequest request, HttpResponse response, Throwable e)
   {
      if (response.isCommitted()) throw new UnhandledException("Response is committed, can't handle exception", e);
      Response handledResponse = new ExceptionHandler(providerFactory, unwrappedExceptions).handleException(request, e);
      if (handledResponse == null) throw new UnhandledException(e);
      try
      {
         // if the content type is null and there is an entity, we'll set it to some default
         setDefaultContentType(request, handledResponse);
         ServerResponseWriter.writeNomapResponse(((BuiltResponse) handledResponse), request, response, providerFactory);
      }
      catch (Exception e1)
      {
         throw new UnhandledException(e1);
      }
   }


   public void invoke(HttpRequest request, HttpResponse response)
   {
      try
      {
         pushContextObjects(request, response);
         if (!preprocess(request, response)) return;
         ResourceInvoker invoker = null;
         try
         {
            invoker = getInvoker(request);
         }
         catch (Exception exception)
         {
            writeException(request, response, exception);
            return;
         }
         invoke(request, response, invoker);
      }
      finally
      {
         clearContextData();
      }
   }

   /**
    * Propagate NotFoundException.  This is used for Filters
    *
    * @param request
    * @param response
    */
   public void invokePropagateNotFound(HttpRequest request, HttpResponse response) throws NotFoundException
   {
      try
      {
         pushContextObjects(request, response);
         if (!preprocess(request, response)) return;
         ResourceInvoker invoker = null;
         try
         {
            invoker = getInvoker(request);
         }
         catch (Exception failure)
         {
            if (failure instanceof NotFoundException)
            {
               throw ((NotFoundException) failure);
            }
            else
            {
               writeException(request, response, failure);
               return;
            }
         }
         invoke(request, response, invoker);
      }
      finally
      {
         clearContextData();
      }

   }

   public ResourceInvoker getInvoker(HttpRequest request)
           throws Failure
   {
      logger.debug("PathInfo: " + request.getUri().getPath());
      if (!request.isInitial())
      {
         throw new InternalServerErrorException(request.getUri().getPath() + " is not initial request.  Its suspended and retried.  Aborting.");
      }
      ResourceInvoker invoker = registry.getResourceInvoker(request);
      if (invoker == null)
      {
         throw new NotFoundException("Unable to find JAX-RS resource associated with path: " + request.getUri().getPath());
      }
      return invoker;
   }

   public void pushContextObjects(HttpRequest request, HttpResponse response)
   {
      Map contextDataMap = ResteasyProviderFactory.getContextDataMap();
      contextDataMap.put(HttpRequest.class, request);
      contextDataMap.put(HttpResponse.class, response);
      contextDataMap.put(HttpHeaders.class, request.getHttpHeaders());
      contextDataMap.put(UriInfo.class, request.getUri());
      contextDataMap.put(Request.class, new RequestImpl(request));
      contextDataMap.put(ResteasyAsynchronousContext.class, request.getAsyncContext());

      contextDataMap.putAll(defaultContextObjects);
   }

   public Response internalInvocation(HttpRequest request, HttpResponse response, Object entity)
   {
      // be extra careful in the clean up process. Only pop if there was an
      // equivalent push.
      ResteasyProviderFactory.addContextDataLevel();
      boolean pushedBody = false;
      try
      {
         MessageBodyParameterInjector.pushBody(entity);
         pushedBody = true;
         ResourceInvoker invoker = getInvoker(request);
         if (invoker != null)
         {
            pushContextObjects(request, response);
            return execute(request, response, invoker);
         }

         // this should never happen, since getInvoker should throw an exception
         // if invoker is null
         return null;
      }
      finally
      {
         ResteasyProviderFactory.removeContextDataLevel();
         if (pushedBody)
         {
            MessageBodyParameterInjector.popBody();
         }
      }
   }

   public void clearContextData()
   {
      ResteasyProviderFactory.clearContextData();
      // just in case there were internalDispatches that need to be cleaned up
      MessageBodyParameterInjector.clearBodies();
   }

   /**
    * Return a response wither from an invoke or exception handling
    *
    * @param request
    * @param response
    * @param invoker
    * @return
    */
   public Response execute(HttpRequest request, HttpResponse response, ResourceInvoker invoker)
   {
      Response jaxrsResponse = null;
      try
      {
         jaxrsResponse = invoker.invoke(request, response);
         if (request.getAsyncContext().isSuspended())
         {
            /**
             * Callback by the initial calling thread.  This callback will probably do nothing in an asynchronous environment
             * but will be used to simulate AsynchronousResponse in vanilla Servlet containers that do not support
             * asychronous HTTP.
             *
             */
            request.getAsyncContext().getAsyncResponse().initialRequestThreadFinished();
            jaxrsResponse = null; // we're handing response asynchronously
         }
      }
      catch (Exception e)
      {
         jaxrsResponse = new ExceptionHandler(providerFactory, unwrappedExceptions).handleException(request, e);
         if (jaxrsResponse == null) throw new UnhandledException(e);
      }
      return jaxrsResponse;
   }

   /**
    * Invoke and write response
    *
    * @param request
    * @param response
    * @param invoker
    */
   public void invoke(HttpRequest request, HttpResponse response, ResourceInvoker invoker)
   {
      Response jaxrsResponse = null;
      try
      {
         jaxrsResponse = invoker.invoke(request, response);
         if (request.getAsyncContext().isSuspended())
         {
            /**
             * Callback by the initial calling thread.  This callback will probably do nothing in an asynchronous environment
             * but will be used to simulate AsynchronousResponse in vanilla Servlet containers that do not support
             * asychronous HTTP.
             *
             */
            request.getAsyncContext().getAsyncResponse().initialRequestThreadFinished();
            jaxrsResponse = null; // we're handing response asynchronously
         }
      }
      catch (Exception e)
      {
         writeException(request, response, e);
         return;
      }

      if (jaxrsResponse != null) writeResponse(request, response, jaxrsResponse);
   }

   public void asynchronousDelivery(HttpRequest request, HttpResponse response, Response jaxrsResponse) throws IOException
   {
      if (jaxrsResponse == null) return;
      try
      {
         pushContextObjects(request, response);

         setDefaultContentType(request, jaxrsResponse);
         ServerResponseWriter.writeNomapResponse((BuiltResponse) jaxrsResponse, request, response, providerFactory);
      }
      finally
      {
         clearContextData();
      }
   }

   public void asynchronousExceptionDelivery(HttpRequest request, HttpResponse response, Throwable exception)
   {
      try
      {
         pushContextObjects(request, response);
         writeException(request, response, exception);
      }
      finally
      {
         clearContextData();
      }
   }


   protected void writeResponse(HttpRequest request, HttpResponse response, Response jaxrsResponse)
   {
      setDefaultContentType(request, jaxrsResponse);

      try
      {
         ServerResponseWriter.writeNomapResponse((BuiltResponse) jaxrsResponse, request, response, providerFactory);
      }
      catch (Exception e)
      {
         writeException(request, response, e);
      }
   }

   protected void setDefaultContentType(HttpRequest request, Response jaxrsResponse)
   {
      Object type = jaxrsResponse.getMetadata().getFirst(
              HttpHeaderNames.CONTENT_TYPE);
      if (type == null && jaxrsResponse.getEntity() != null)
      {
         ResourceMethodInvoker method = (ResourceMethodInvoker) request.getAttribute(ResourceMethodInvoker.class.getName());
         if (method != null)
         {
            jaxrsResponse.getMetadata().putSingle(HttpHeaderNames.CONTENT_TYPE, method.resolveContentType(request, jaxrsResponse.getEntity()));
         }
         else
         {
            MediaType contentType = resolveContentTypeByAccept(request.getHttpHeaders().getAcceptableMediaTypes(), jaxrsResponse.getEntity());
            jaxrsResponse.getMetadata().putSingle(HttpHeaderNames.CONTENT_TYPE, contentType);
         }
      }
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
         if (providerFactory.getMessageBodyWriter(clazz, type, null, accept) != null)
         {
            return accept;
         }
      }
      return MediaType.WILDCARD_TYPE;
   }


   public void addHttpPreprocessor(HttpRequestPreprocessor httpPreprocessor)
   {
      requestPreprocessors.add(httpPreprocessor);
   }

}