package org.jboss.resteasy.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext;
import org.jboss.resteasy.plugins.server.servlet.Cleanable;
import org.jboss.resteasy.plugins.server.servlet.Cleanables;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalDispatcher;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnhandledException;

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
   protected boolean bufferExceptionEntityRead = false;
   protected boolean bufferExceptionEntity = true;
   
   public SynchronousDispatcher(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
      defaultContextObjects.put(Providers.class, providerFactory);
      defaultContextObjects.put(Registry.class, registry);
      defaultContextObjects.put(Dispatcher.class, this);
      defaultContextObjects.put(InternalDispatcher.class, InternalDispatcher.getInstance());
   }

   public SynchronousDispatcher(ResteasyProviderFactory providerFactory, ResourceMethodRegistry registry)
   {
      this(providerFactory);
      this.registry = registry;
      defaultContextObjects.put(Registry.class, registry);
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
         // FIXME: support async
         PreMatchContainerRequestContext requestContext = new PreMatchContainerRequestContext(request, requestFilters, null);
         aborted = requestContext.filter();
      }
      catch (Exception e)
      {
         //logger.error("Failed in preprocess, mapping exception", e);
         aborted = new ExceptionHandler(providerFactory, unwrappedExceptions).handleException(request, e);
     }
      return aborted;
   }

   /**
    * Call pre-process ContainerRequestFilters
    *
    * @return true if request should continue
    */
   protected void preprocess(HttpRequest request, HttpResponse response, Runnable continuation)
   {
      Response aborted = null;
      try
      {
         for (HttpRequestPreprocessor preprocessor : this.requestPreprocessors)
         {
            preprocessor.preProcess(request);
         }
         ContainerRequestFilter[] requestFilters = providerFactory.getContainerRequestFilterRegistry().preMatch();
         PreMatchContainerRequestContext requestContext = new PreMatchContainerRequestContext(request, requestFilters, continuation);
         aborted = requestContext.filter();
         System.out.println("Filter done: aborted: "+aborted);
         System.out.println("Filter done: suspended: "+requestContext.isSuspended());
         if(aborted == null)
         {
            if(requestContext.isSuspended())
               return;
         }
      }
      catch (Exception e)
      {
         //logger.error("Failed in preprocess, mapping exception", e);
         writeException(request, response, e);
         return;
      }
      if (aborted != null)
      {
         writeResponse(request, response, aborted);
         return;
      }
      continuation.run();
   }

   public void writeException(HttpRequest request, HttpResponse response, Throwable e)
   {
      if (!bufferExceptionEntityRead)
      {
         bufferExceptionEntityRead = true;
         ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
         if (context != null)
         {
            String s = context.getParameter("resteasy.buffer.exception.entity");
            if (s != null)
            {
               bufferExceptionEntity = Boolean.parseBoolean(s);
            }
         }
      }
      if (response.isCommitted())
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.responseIsCommitted());
         return;
      }
      Response handledResponse = new ExceptionHandler(providerFactory, unwrappedExceptions).handleException(request, e);
      if (handledResponse == null) throw new UnhandledException(e);
      if (!bufferExceptionEntity)
      {
         response.getOutputHeaders().add("resteasy.buffer.exception.entity", "false");
      }
      try
      {
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
         System.out.println("A");
         preprocess(request, response, () -> {
            System.out.println("In continuation");
            ResourceInvoker invoker = null;
            try
            {
               try
               {
                  invoker = getInvoker(request);
               }
               catch (Exception exception)
               {
                  //logger.error("getInvoker() failed mapping exception", exception);
                  writeException(request, response, exception);
                  return;
               }
               invoke(request, response, invoker);
            }
            finally
            {
               // we're probably clearing it twice but still required
               clearContextData();
            }
         });
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
         System.out.println("B");
         preprocess(request, response, () -> {
            ResourceInvoker invoker = null;
            try
            {
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
                     //logger.error("getInvoker() failed mapping exception", failure);
                     writeException(request, response, failure);
                     return;
                  }
               }
               invoke(request, response, invoker);
            }
            finally
            {
               // we're probably clearing it twice but still required
               clearContextData();
            }
         });
      }
      finally
      {
         clearContextData();
      }

   }

   public ResourceInvoker getInvoker(HttpRequest request)
           throws Failure
   {
      LogMessages.LOGGER.pathInfo(request.getUri().getPath());
      if (!request.isInitial())
      {
         throw new InternalServerErrorException(Messages.MESSAGES.isNotInitialRequest(request.getUri().getPath())); 
      }
      ResourceInvoker invoker = registry.getResourceInvoker(request);
      if (invoker == null)
      {
         throw new NotFoundException(Messages.MESSAGES.unableToFindJaxRsResource(request.getUri().getPath()));
      }
      return invoker;
   }

   public void pushContextObjects(final HttpRequest request, final HttpResponse response)
   {
      Map contextDataMap = ResteasyProviderFactory.getContextDataMap();
      contextDataMap.put(HttpRequest.class, request);
      contextDataMap.put(HttpResponse.class, response);
      contextDataMap.put(HttpHeaders.class, request.getHttpHeaders());
      contextDataMap.put(UriInfo.class, request.getUri());
      contextDataMap.put(Request.class, new RequestImpl(request, response));
      contextDataMap.put(ResteasyAsynchronousContext.class, request.getAsyncContext());
      ResourceContext resourceContext = new ResourceContext()
      {
         @Override
         public <T> T getResource(Class<T> resourceClass)
         {
            return providerFactory.injectedInstance(resourceClass, request, response);
         }

         @Override
         public <T> T initResource(T resource)
         {
            providerFactory.injectProperties(resource, request, response);
            return resource;
         }
      };
      contextDataMap.put(ResourceContext.class, resourceContext);

      contextDataMap.putAll(defaultContextObjects);
      contextDataMap.put(Cleanables.class, new Cleanables());
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
	  Cleanables cleanables = ResteasyProviderFactory.getContextData(Cleanables.class);
	  if (cleanables != null)
	  {
		  for (Iterator<Cleanable> it = cleanables.getCleanables().iterator(); it.hasNext(); )
		  {
			  try
			  {
				  it.next().clean();
			  }
			  catch(Exception e)
			  {
				// Empty
			  }
		  }
	  }
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
         //logger.error("invoke() failed mapping exception", e);
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
         //logger.error("invoke() failed mapping exception", e);
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
         ServerResponseWriter.writeNomapResponse((BuiltResponse) jaxrsResponse, request, response, providerFactory);
      }
      finally
      {
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }

   public void asynchronousExceptionDelivery(HttpRequest request, HttpResponse response, Throwable exception)
   {
      try
      {
         pushContextObjects(request, response);
         writeException(request, response, exception);
      }
      catch (Throwable ex)
      {
         LogMessages.LOGGER.unhandledAsynchronousException(ex);
         // unhandled exceptions need to be processed as they can't be thrown back to the servlet container
         if (!response.isCommitted()) {
            try
            {
               response.reset();
               response.sendError(500);
            }
            catch (IOException e)
            {

            }
         }
      }
      finally
      {
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }


   protected void writeResponse(HttpRequest request, HttpResponse response, Response jaxrsResponse)
   {
      try
      {
         ServerResponseWriter.writeNomapResponse((BuiltResponse) jaxrsResponse, request, response, providerFactory);
      }
      catch (Exception e)
      {
         //logger.error("writeResponse() failed mapping exception", e);
         writeException(request, response, e);
      }
   }

   public void addHttpPreprocessor(HttpRequestPreprocessor httpPreprocessor)
   {
      requestPreprocessors.add(httpPreprocessor);
   }

}