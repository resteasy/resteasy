package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.specimpl.ResponseImpl;
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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SynchronousDispatcher implements Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected ResourceMethodRegistry registry;
   protected Map<String, MediaType> mediaTypeMappings;
   protected Map<String, String> languageMappings;
   private final static Logger logger = LoggerFactory.getLogger(SynchronousDispatcher.class);

   public SynchronousDispatcher(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
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

   public void invoke(HttpRequest in, HttpResponse response)
   {
      logger.debug("PathInfo: " + in.getUri().getPath());
      preprocess(in);
      ResourceInvoker invoker = null;
      try
      {
         invoker = registry.getResourceInvoker(in, response);
      }
      catch (Failure e)
      {
         handleFailure(in, response, e);
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
         logger.info("Could not match path: " + in.getUri().getPath());
         return;
      }
      invoke(in, response, invoker);
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

   protected void handleException(HttpRequest request, HttpResponse response, Exception e)
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

   protected void handleFailure(HttpRequest request, HttpResponse response, Exception e)
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

   protected void handleApplicationException(HttpResponse response, ApplicationException e)
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

   protected void handleWebApplicationException(HttpResponse response, WebApplicationException wae)
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
         ResteasyProviderFactory.pushContext(HttpRequest.class, request);
         ResteasyProviderFactory.pushContext(HttpResponse.class, response);
         ResteasyProviderFactory.pushContext(HttpHeaders.class, request.getHttpHeaders());
         ResteasyProviderFactory.pushContext(UriInfo.class, request.getUri());
         ResteasyProviderFactory.pushContext(Request.class, new RequestImpl(request));
         ResteasyProviderFactory.pushContext(Providers.class, providerFactory);
         Response jaxrsResponse = null;
         try
         {
            jaxrsResponse = invoker.invoke(request, response);
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

   protected void writeJaxrsResponse(HttpResponse response, Response jaxrsResponse)
           throws IOException, WebApplicationException
   {
      if (jaxrsResponse.getMetadata() != null)
      {
         List cookies = jaxrsResponse.getMetadata().get(HttpHeaderNames.SET_COOKIE);
         if (cookies != null)
         {
            Iterator it = cookies.iterator();
            while (it.hasNext())
            {
               Object next = it.next();
               if (next instanceof NewCookie)
               {
                  NewCookie cookie = (NewCookie) next;
                  response.addNewCookie(cookie);
                  it.remove();
               }
            }
            if (cookies.size() < 1) jaxrsResponse.getMetadata().remove(HttpHeaderNames.SET_COOKIE);
         }
      }

      if (jaxrsResponse.getEntity() == null)
      {
         response.setStatus(jaxrsResponse.getStatus());
         outputHeaders(response, jaxrsResponse);
      }
      if (jaxrsResponse.getEntity() != null)
      {
         MediaType responseContentType = resolveContentType(jaxrsResponse);
         Object entity = jaxrsResponse.getEntity();
         Class type = jaxrsResponse.getEntity().getClass();
         Type genericType = null;
         Annotation[] annotations = null;
         if (entity instanceof GenericEntity)
         {
            GenericEntity ge = (GenericEntity) entity;
            genericType = ge.getType();
            entity = ge.getEntity();
            type = entity.getClass();
         }
         if (jaxrsResponse instanceof ResponseImpl)
         {
            // if we haven't set it in GenericEntity processing...
            if (genericType == null) genericType = ((ResponseImpl) jaxrsResponse).getGenericType();

            annotations = ((ResponseImpl) jaxrsResponse).getAnnotations();
         }
         MessageBodyWriter writer = providerFactory.getMessageBodyWriter(type, genericType, annotations, responseContentType);
         if (writer == null)
         {
            throw new LoggableFailure("Could not find MessageBodyWriter for response object of type: " + type.getName() + " of media type: " + responseContentType, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
         }
         //System.out.println("MessageBodyWriter class is: " + writer.getClass().getName());
         //System.out.println("Response content type: " + responseContentType);
         long size = writer.getSize(entity, type, genericType, annotations, responseContentType);
         //System.out.println("Writer: " + writer.getClass().getName());
         //System.out.println("JAX-RS Content Size: " + size);
         response.setStatus(jaxrsResponse.getStatus());
         outputHeaders(response, jaxrsResponse);
         response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString((int) size));
         writer.writeTo(entity, type, genericType, annotations, responseContentType, response.getOutputHeaders(), response.getOutputStream());
      }
   }

   protected MediaType resolveContentType(Response jaxrsResponse)
   {
      MediaType responseContentType = null;
      Object type = jaxrsResponse.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
      if (type == null) return MediaType.valueOf("*/*");
      if (type instanceof MediaType)
      {
         responseContentType = (MediaType) type;
      }
      else
      {
         responseContentType = MediaType.valueOf(type.toString());
      }
      return responseContentType;
   }

   protected void outputHeaders(HttpResponse response, Response jaxrsResponse)
   {
      if (jaxrsResponse.getMetadata() != null && jaxrsResponse.getMetadata().size() > 0)
      {
         response.getOutputHeaders().putAll(jaxrsResponse.getMetadata());
      }
   }
}