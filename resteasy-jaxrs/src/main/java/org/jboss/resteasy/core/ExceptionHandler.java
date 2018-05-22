package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnhandledException;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionHandler
{
   protected ResteasyProviderFactory providerFactory;
   protected Set<String> unwrappedExceptions = new HashSet<String>();
   protected boolean mapperExecuted;

   public ExceptionHandler(ResteasyProviderFactory providerFactory, Set<String> unwrappedExceptions)
   {
      this.providerFactory = providerFactory;
      this.unwrappedExceptions = unwrappedExceptions;
   }

   public boolean isMapperExecuted()
   {
      return mapperExecuted;
   }

   /**
    * If there exists an Exception mapper for exception, execute it, otherwise, do NOT recurse up class hierarchy
    * of exception.
    *
    * @param exception exception
    * @return response
    */
   @SuppressWarnings(value = "unchecked")
   public Response executeExactExceptionMapper(Throwable exception)
   {
      ExceptionMapper mapper = providerFactory.getExceptionMappers().get(exception.getClass());
      if (mapper == null) return null;
      mapperExecuted = true;
      return mapper.toResponse(exception);
   }

   @SuppressWarnings(value = "unchecked")
   public Response executeExceptionMapperForClass(Throwable exception, Class clazz)
   {
      ExceptionMapper mapper = providerFactory.getExceptionMappers().get(clazz);
      if (mapper == null) return null;
      mapperExecuted = true;
      return mapper.toResponse(exception);
   }

   protected Response handleApplicationException(HttpRequest request, ApplicationException e)
   {
      Response jaxrsResponse = null;
      // See if there is a mapper for ApplicationException
      if ((jaxrsResponse = executeExceptionMapperForClass(e, ApplicationException.class)) != null) {
         return jaxrsResponse;
      }
      jaxrsResponse = unwrapException(request, e);
      if (jaxrsResponse == null) {
         throw new UnhandledException(e.getCause());
      }
      return jaxrsResponse;
   }

   /**
    * Execute an ExceptionMapper if one exists for the given exception.  Recurse to base class if not found.
    *
    * @param exception exception
    * @return true if an ExceptionMapper was found and executed
    */
   @SuppressWarnings(value = "unchecked")
   public Response executeExceptionMapper(Throwable exception)
   {
      ExceptionMapper mapper = null;

      Class causeClass = exception.getClass();
      while (mapper == null) {
         if (causeClass == null) break;
         mapper = providerFactory.getExceptionMappers().get(causeClass);
         if (mapper == null) causeClass = causeClass.getSuperclass();
      }
      if (mapper != null) {
         mapperExecuted = true;
         Response jaxrsResponse = mapper.toResponse(exception);
         if (jaxrsResponse == null) {
            jaxrsResponse = Response.status(204).build();
         }
         return jaxrsResponse;
      }
      return null;
   }


   protected Response unwrapException(HttpRequest request, Throwable e)
   {
      Response jaxrsResponse = null;
      Throwable unwrappedException = e.getCause();

      /*
       * 					If the response property of the exception does not
       * 					contain an entity and an exception mapping provider
       * 					(see section 4.4) is available for
       * 					WebApplicationException an implementation MUST use the
       * 					provider to create a new Response instance, otherwise
       * 					the response property is used directly.
       */

      if (unwrappedException instanceof WebApplicationException) {
         WebApplicationException wae = (WebApplicationException) unwrappedException;
         if (wae.getResponse() != null && wae.getResponse().getEntity() != null) return wae.getResponse();
      }

      if ((jaxrsResponse = executeExceptionMapper(unwrappedException)) != null) {
         return jaxrsResponse;
      }
      if (unwrappedException instanceof WebApplicationException) {
         return handleWebApplicationException((WebApplicationException) unwrappedException);
      }
      else if (unwrappedException instanceof Failure) {
         return handleFailure(request, (Failure) unwrappedException);
      }
      else {
         if (unwrappedExceptions.contains(unwrappedException.getClass().getName()) && unwrappedException.getCause() != null) {
            return unwrapException(request, unwrappedException);
         }
         else {
            return null;
         }
      }
   }

   protected Response handleFailure(HttpRequest request, Failure failure)
   {
      if (failure.isLoggable())
         LogMessages.LOGGER.failedExecutingError(request.getHttpMethod(), request.getUri().getPath(), failure);
      else LogMessages.LOGGER.failedExecutingDebug(request.getHttpMethod(), request.getUri().getPath(), failure);

      if (failure.getResponse() != null) {
         return failure.getResponse();
      }
      else {
         Response.ResponseBuilder builder = Response.status(failure.getErrorCode());
         if (failure.getMessage() != null) builder.type(MediaType.TEXT_HTML).entity(failure.getMessage());
         return builder.build();
      }
   }

   protected Response handleWriterException(HttpRequest request, WriterException e)
   {
      Response jaxrsResponse = null;
      // See if there is a general mapper for WriterException
      if ((jaxrsResponse = executeExceptionMapperForClass(e, WriterException.class)) != null) {
         return jaxrsResponse;
      }
      if (e.getResponse() != null || e.getErrorCode() > -1) {
         return handleFailure(request, e);
      }
      else if (e.getCause() != null) {
         if ((jaxrsResponse = unwrapException(request, e)) != null) return jaxrsResponse;
      }
      e.setErrorCode(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      return handleFailure(request, e);
   }

   protected Response handleReaderException(HttpRequest request, ReaderException e)
   {
      Response jaxrsResponse = null;
      // See if there is a general mapper for ReaderException
      if ((jaxrsResponse = executeExceptionMapperForClass(e, ReaderException.class)) != null) {
         return jaxrsResponse;
      }
      if (e.getResponse() != null || e.getErrorCode() > -1) {
         return handleFailure(request, e);
      }
      else if (e.getCause() != null) {
         if ((jaxrsResponse = unwrapException(request, e)) != null) return jaxrsResponse;
      }
      e.setErrorCode(HttpResponseCodes.SC_BAD_REQUEST);
      return handleFailure(request, e);
   }

   protected Response handleWebApplicationException(WebApplicationException wae)
   {
      if (wae instanceof NotFoundException) 
      {
         LogMessages.LOGGER.failedToExecuteDebug(wae);
      }
      else if (!(wae instanceof NoLogWebApplicationException))
      {
         LogMessages.LOGGER.failedToExecute(wae);
      }
      return wae.getResponse();
   }


   public Response handleException(HttpRequest request, Throwable e)
   {
      Response jaxrsResponse = null;
      // See if there is an ExceptionMapper for the exact class of the exception instance being thrown
      if ((jaxrsResponse = executeExactExceptionMapper(e)) != null) return jaxrsResponse;

      // These are wrapper exceptions so they need to be processed first as they map e.getCause()
      if (e instanceof ApplicationException) {
         return handleApplicationException(request, (ApplicationException) e);
      }
      else if (e instanceof WriterException) {
         return handleWriterException(request, (WriterException) e);
      }
      else if (e instanceof ReaderException) {
         return handleReaderException(request, (ReaderException) e);
      }

      /*
       * 					If the response property of the exception does not
       * 					contain an entity and an exception mapping provider
       * 					(see section 4.4) is available for
       * 					WebApplicationException an implementation MUST use the
       * 					provider to create a new Response instance, otherwise
       * 					the response property is used directly.
       */
      if (e instanceof WebApplicationException) {
         WebApplicationException wae = (WebApplicationException) e;
         if (wae.getResponse() != null && wae.getResponse().getEntity() != null) return wae.getResponse();
      }

      // First try and handle it with a mapper
      if ((jaxrsResponse = executeExceptionMapper(e)) != null) {
         return jaxrsResponse;
      }
      // Otherwise do specific things
      else if (e instanceof WebApplicationException) {
         return handleWebApplicationException((WebApplicationException) e);
      }
      else if (e instanceof Failure) {
         return handleFailure(request, (Failure) e);
      }
      else {
         LogMessages.LOGGER.unknownException(request.getHttpMethod(), request.getUri().getPath(), e);
         throw new UnhandledException(e);
      }
   }
}
