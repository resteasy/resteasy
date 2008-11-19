package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnhandledException;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.LocaleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class SynchronousDispatcher implements Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected ResourceMethodRegistry registry;
   protected Map<String, MediaType> mediaTypeMappings;
   protected Map<String, String> languageMappings;

   // this should be overridable
   protected DispatcherUtilities dispatcherUtilities;

   private final static Logger logger = LoggerFactory.getLogger(SynchronousDispatcher.class);

   public SynchronousDispatcher(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
      dispatcherUtilities = new DispatcherUtilities(providerFactory, registry);
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
      this.mediaTypeMappings = mediaTypeMappings;
   }

   public void setLanguageMappings(Map<String, String> languageMappings)
   {
      this.languageMappings = languageMappings;
   }

   public Map<String, MediaType> getMediaTypeMappings()
   {
      return mediaTypeMappings;
   }

   public Map<String, String> getLanguageMappings()
   {
      return languageMappings;
   }

   protected void preprocess(HttpRequest in)
   {
      preprocessExtensions(in);
   }

   protected void preprocessExtensions(HttpRequest in)
   {

      List<PathSegment> segments = null;
      if (mediaTypeMappings != null || languageMappings != null)
      {

         String path = in.getUri().getPath(false);
         int lastSegment = path.lastIndexOf('/');
         if (lastSegment < 0) lastSegment = 0;
         int index = path.indexOf('.', lastSegment);
         if (index < 0) return;

         boolean preprocessed = false;

         String extension = path.substring(index + 1);
         String[] extensions = extension.split("\\.");

         String rebuilt = path.substring(0, index);
         for (String ext : extensions)
         {
            if (mediaTypeMappings != null)
            {
               MediaType match = mediaTypeMappings.get(ext);
               if (match != null)
               {
                  in.getHttpHeaders().getAcceptableMediaTypes().add(match);
                  preprocessed = true;
                  continue;
               }
            }
            if (languageMappings != null)
            {
               String match = languageMappings.get(ext);
               if (match != null)
               {
                  in.getHttpHeaders().getAcceptableLanguages().add(LocaleHelper.extractLocale(match));
                  preprocessed = true;
                  continue;
               }
            }
            rebuilt += "." + ext;
         }
         if (preprocessed) segments = PathSegmentImpl.parseSegments(rebuilt);
         else segments = in.getUri().getPathSegments(false);
      }
      else
      {
         segments = in.getUri().getPathSegments(false);
      }

      // finally strip out matrix parameters

      StringBuffer preprocessedPath = new StringBuffer();
      for (PathSegment pathSegment : segments)
      {
         preprocessedPath.append("/").append(pathSegment.getPath());
      }
      in.setPreprocessedPath(preprocessedPath.toString());
   }

   public void invoke(HttpRequest request, HttpResponse response)
   {
      logger.debug("PathInfo: " + request.getUri().getPath());
      if (!request.isInitial())
      {
         try
         {
            logger.error(request.getUri().getPath() + " is not initial request.  Its suspended and retried.  Aborting.");
            response.sendError(500, request.getUri().getPath() + " is not initial request.  Its suspended and retried.  Aborting.");
         }
         catch (IOException e)
         {
            throw new UnhandledException(e);
         }
         return;
      }
      preprocess(request);
      ResourceInvoker invoker = null;
      try
      {
         invoker = registry.getResourceInvoker(request, response);
      }
      catch (Failure e)
      {
         handleFailure(request, response, e);
         logger.info(e.getMessage());
         return;
      }
      if (invoker == null)
      {
         try
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
         catch (Exception e)
         {
            throw new UnhandledException(e);
         }
         logger.info("Could not match path: " + request.getUri().getPath());
         return;
      }
      invoke(request, response, invoker);
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
      if (e instanceof WebApplicationException)
      {
         handleWebApplicationException(response, (WebApplicationException) e);
      }
      else if (e instanceof ApplicationException)
      {
         handleApplicationException(response, (ApplicationException) e);
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

   public void handleFailure(HttpRequest request, HttpResponse response, Exception e)
   {
      Failure failure = (Failure) e;
      if (failure.getResponse() != null)
      {
         try
         {
            writeJaxrsResponse(response, failure.getResponse());
         }
         catch (Exception e1)
         {
            throw new UnhandledException(e1);
         }
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
      if (((Failure) e).isLoggable())
         logger.error("Failed executing " + request.getHttpMethod() + " " + request.getUri().getPath(), e);
      else logger.debug("Failed executing " + request.getHttpMethod() + " " + request.getUri().getPath(), e);
   }

   public void handleApplicationException(HttpResponse response, ApplicationException e)
   {
      if (e.getCause() instanceof WebApplicationException)
      {
         handleWebApplicationException(response, (WebApplicationException) e.getCause());
         return;
      }
      ExceptionMapper mapper = null;

      Class causeClass = e.getCause().getClass();
      while (mapper == null)
      {
         if (causeClass == null) break;
         mapper = providerFactory.getExceptionMapper(causeClass);
         if (mapper == null) causeClass = causeClass.getSuperclass();
      }
      if (mapper != null)
      {
         try
         {
            writeJaxrsResponse(response, mapper.toResponse(e.getCause()));
         }
         catch (WebApplicationException ex)
         {
            if (response.isCommitted())
               throw new UnhandledException("Request was committed couldn't handle exception", ex);
            // don't think I want to call writeJaxrsResponse infinately! so we'll just write the status
            response.reset();
            response.setStatus(ex.getResponse().getStatus());
            logger.error("Failed to write exception response", ex);

         }
         catch (Exception e1)
         {
            throw new UnhandledException(e1); // we're screwed, can't handle the exception
         }
      }
      else
      {
         throw new UnhandledException(e.getCause());
      }
   }

   public void handleWebApplicationException(HttpResponse response, WebApplicationException wae)
   {
      logger.error("failed to execute", wae);
      if (response.isCommitted()) throw new UnhandledException("Request was committed couldn't handle exception", wae);
      response.reset();
      try
      {
         writeJaxrsResponse(response, wae.getResponse());
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

   public void invoke(HttpRequest request, HttpResponse response, ResourceInvoker invoker)
   {
      try
      {
         getDispatcherUtilities().pushContextObjects(request, response);
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
               return; // we're handing response asynchronously
            }
         }
         catch (Exception e)
         {
            handleInvokerException(request, response, e);
         }

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
         ResteasyProviderFactory.clearContextData();
      }
   }

   public void asynchronousDelivery(HttpRequest request, HttpResponse response, Response jaxrsResponse)
   {
      try
      {
         getDispatcherUtilities().pushContextObjects(request, response);
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
         ResteasyProviderFactory.clearContextData();
      }
   }

   public void writeJaxrsResponse(HttpResponse response, Response jaxrsResponse)
           throws IOException, WebApplicationException
   {
      this.dispatcherUtilities.writeCookies(response, jaxrsResponse);
      ResponseInvoker responseInvoker = new ResponseInvoker(dispatcherUtilities, jaxrsResponse);

      if (jaxrsResponse.getEntity() == null)
      {
         response.setStatus(jaxrsResponse.getStatus());
         this.dispatcherUtilities.outputHeaders(response, jaxrsResponse);
      }
      if (jaxrsResponse.getEntity() != null)
      {
         if (responseInvoker.getWriter() == null)
         {
            throw new LoggableFailure(String.format(
                    "Could not find MessageBodyWriter for response object of type: %s of media type: %s",
                    responseInvoker.getType().getName(),
                    responseInvoker.getContentType()),
                    HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
         }

         response.setStatus(jaxrsResponse.getStatus());
         this.dispatcherUtilities.outputHeaders(response, jaxrsResponse);
         long size = responseInvoker.getResponseSize();
         response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(size));
         responseInvoker.writeTo(response);
      }
   }

   public DispatcherUtilities getDispatcherUtilities()
   {
      return dispatcherUtilities;
   }

   public void setDispatcherUtilities(DispatcherUtilities dispatcherUtilities)
   {
      this.dispatcherUtilities = dispatcherUtilities;
   }
}