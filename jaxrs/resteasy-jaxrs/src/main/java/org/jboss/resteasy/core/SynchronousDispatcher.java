package org.jboss.resteasy.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalDispatcher;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnhandledException;
import org.jboss.resteasy.spi.preprocessor.ExtensionHttpPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   protected ExtensionHttpPreprocessor extentionHttpPreprocessor;

   private final static Logger logger = LoggerFactory.getLogger(SynchronousDispatcher.class);

   public SynchronousDispatcher(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
      requestPreprocessors.add(extentionHttpPreprocessor = new ExtensionHttpPreprocessor());
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setMediaTypeMappings(Map<String, MediaType> mediaTypeMappings)
   {
      extentionHttpPreprocessor.mediaTypeMappings = mediaTypeMappings;
   }

   public void setLanguageMappings(Map<String, String> languageMappings)
   {
      extentionHttpPreprocessor.languageMappings = languageMappings;
   }

   public Map<String, MediaType> getMediaTypeMappings()
   {
      return extentionHttpPreprocessor.mediaTypeMappings;
   }

   public Map<String, String> getLanguageMappings()
   {
      return extentionHttpPreprocessor.languageMappings;
   }

   protected void preprocess(HttpRequest in)
   {
      preprocessExtensions(in);
   }

   protected void preprocessExtensions(HttpRequest in)
   {
      for (HttpRequestPreprocessor preprocessor : this.requestPreprocessors)
      {
         preprocessor.preProcess(in);
      }
   }

   public void invoke(HttpRequest request, HttpResponse response)
   {
      try
      {
         ResourceInvoker invoker = getInvoker(request, response);
         invoke(request, response, invoker);
      }
      catch (Failure e)
      {
         handleException(request, response, e);
         return;
      }
   }

   public ResourceInvoker getInvoker(HttpRequest request, HttpResponse response)
           throws Failure
   {
      logger.debug("PathInfo: " + request.getUri().getPath());
      if (!request.isInitial())
      {
         throw new InternalServerErrorException(request.getUri().getPath() + " is not initial request.  Its suspended and retried.  Aborting.");
      }
      preprocess(request);
      ResourceInvoker invoker = registry.getResourceInvoker(request, response);
      if (invoker == null)
      {
         throw new NotFoundException("Unable to find JAX-RS resource associated with path: " + request.getUri().getPath());
      }
      return invoker;
   }

   /**
    * Called if method invoke was unsuccessful
    *
    * @param request
    * @param response
    * @param e
    */
   public void handleInvokerException(HttpRequest request, HttpResponse response, Exception e)
   {
      handleException(request, response, e);
   }


   /**
    * Called if method invoke was successful, but writing the Response after was not.
    *
    * @param request
    * @param response
    * @param e
    */
   public void handleWriteResponseException(HttpRequest request, HttpResponse response, Exception e)
   {
      handleException(request, response, e);
   }

   public void handleException(HttpRequest request, HttpResponse response, Exception e)
   {
      // ApplicationException needs to come first as it does its own executeExceptionMapper() call
      if (e instanceof ApplicationException)
      {
         handleApplicationException(response, (ApplicationException) e);
         return;
      }

      if (executeExceptionMapper(response, e)) return;

      if (e instanceof WebApplicationException)
      {
         handleWebApplicationException(response, (WebApplicationException) e);
      }
      else if (e instanceof Failure)
      {
         handleFailure(request, response, e);
      }
      else
      {
         logger.error("Unknown exception while executing " + request.getHttpMethod() + " " + request.getUri().getPath(), e);
         throw new UnhandledException(e);
      }
   }

   protected void handleFailure(HttpRequest request, HttpResponse response, Exception e)
   {
      if (((Failure) e).isLoggable())
         logger.error("Failed executing " + request.getHttpMethod() + " " + request.getUri().getPath(), e);
      else logger.debug("Failed executing " + request.getHttpMethod() + " " + request.getUri().getPath(), e);

      Failure failure = (Failure) e;
      if (failure.getResponse() != null)
      {
         writeFailure(response, failure.getResponse());
      }
      else
      {
         try
         {
            if (failure.getMessage() != null)
            {
               response.sendError(failure.getErrorCode(), failure.getMessage());
            }
            else
            {
               response.sendError(failure.getErrorCode());
            }
         }
         catch (IOException e1)
         {
            throw new UnhandledException(e1);
         }
      }
   }

   /**
    * Execute an ExceptionMapper if one exists for the given exception
    *
    * @param response
    * @param exception
    * @return true if an ExceptionMapper was found and executed
    */
   public boolean executeExceptionMapper(HttpResponse response, Throwable exception)
   {
      ExceptionMapper mapper = null;

      Class causeClass = exception.getClass();
      while (mapper == null)
      {
         if (causeClass == null) break;
         mapper = providerFactory.getExceptionMapper(causeClass);
         if (mapper == null) causeClass = causeClass.getSuperclass();
      }
      if (mapper != null)
      {
         writeFailure(response, mapper.toResponse(exception));
         return true;
      }
      return false;
   }

   protected void handleApplicationException(HttpResponse response, ApplicationException e)
   {
      if (e.getCause() instanceof WebApplicationException)
      {
         handleWebApplicationException(response, (WebApplicationException) e.getCause());
         return;
      }

      if (!executeExceptionMapper(response, e.getCause()))
      {
         throw new UnhandledException(e.getCause());
      }
   }

   protected void writeFailure(HttpResponse response, Response jaxrsResponse)
   {
      response.reset();
      try
      {
         writeJaxrsResponse(response, jaxrsResponse);
      }
      catch (WebApplicationException ex)
      {
         if (response.isCommitted())
            throw new UnhandledException("Request was committed couldn't handle exception", ex);
         // don't think I want to call writeJaxrsResponse infinately! so we'll just write the status
         response.reset();
         response.setStatus(ex.getResponse().getStatus());

      }
      catch (Exception e1)
      {
         throw new UnhandledException(e1);  // we're screwed, can't handle the exception
      }
   }

   protected void handleWebApplicationException(HttpResponse response, WebApplicationException wae)
   {
      if (!(wae instanceof NoLogWebApplicationException)) logger.error("failed to execute", wae);
      if (response.isCommitted()) throw new UnhandledException("Request was committed couldn't handle exception", wae);

      writeFailure(response, wae.getResponse());
   }

   public void pushContextObjects(HttpRequest request, HttpResponse response)
   {
      ResteasyProviderFactory.pushContext(HttpRequest.class, request);
      ResteasyProviderFactory.pushContext(HttpResponse.class, response);
      ResteasyProviderFactory.pushContext(HttpHeaders.class, request.getHttpHeaders());
      ResteasyProviderFactory.pushContext(UriInfo.class, request.getUri());
      ResteasyProviderFactory.pushContext(Request.class, new RequestImpl(request));
      ResteasyProviderFactory.pushContext(Providers.class, providerFactory);
      ResteasyProviderFactory.pushContext(Registry.class, registry);
      ResteasyProviderFactory.pushContext(Dispatcher.class, this);
      ResteasyProviderFactory.pushContext(InternalDispatcher.class, InternalDispatcher.getInstance());
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
         ResourceInvoker invoker = getInvoker(request, response);
         if( invoker != null )
         {
            pushContextObjects(request, response);
            return getResponse(request, response, invoker);
         }  
         
         // this should never happen, since getInvoker should throw an exception
         // if invoker is null
         return null;
      }
      finally
      {
         ResteasyProviderFactory.removeContextDataLevel();
         if(pushedBody)
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

   public void invoke(HttpRequest request, HttpResponse response, ResourceInvoker invoker)
   {
      try
      {
         pushContextObjects(request, response);
         Response jaxrsResponse = getResponse(request, response, invoker);

         try
         {
            if (jaxrsResponse != null) writeJaxrsResponse(response, jaxrsResponse);
         }
         catch (Exception e)
         {
            handleWriteResponseException(request, response, e);
         }
      }
      finally
      {
         clearContextData();
      }
   }

   protected Response getResponse(HttpRequest request, HttpResponse response,
         ResourceInvoker invoker)
   {
      Response jaxrsResponse = null;
      try
      {
         jaxrsResponse = invoker.invoke(request, response);
         if (request.isSuspended())
         {
            /**
             * Callback by the initial calling thread.  This callback will probably do nothing in an asynchronous environment
             * but will be used to simulate AsynchronousResponse in vanilla Servlet containers that do not support
             * asychronous HTTP.
             *
             */
            request.initialRequestThreadFinished();
            jaxrsResponse = null; // we're handing response asynchronously
         }
      }
      catch (Exception e)
      {
         handleInvokerException(request, response, e);
      }
      return jaxrsResponse;
   }

   public void asynchronousDelivery(HttpRequest request, HttpResponse response, Response jaxrsResponse)
   {
      try
      {
         pushContextObjects(request, response);
         try
         {
            if (jaxrsResponse != null) writeJaxrsResponse(response, jaxrsResponse);
         }
         catch (Exception e)
         {
            handleWriteResponseException(request, response, e);
         }
      }
      finally
      {
         clearContextData();
      }
   }

   protected void writeJaxrsResponse(HttpResponse response, Response jaxrsResponse)
           throws IOException, WebApplicationException
   {
      ServerResponse serverResponse = (ServerResponse) jaxrsResponse;
      serverResponse.writeTo(response, providerFactory);
   }

}