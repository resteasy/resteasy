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
import org.jboss.resteasy.tracing.RESTEasyServerTracingEvent;
import org.jboss.resteasy.tracing.RESTEasyTracingEvent;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.tracing.RESTEasyTracingLoggerImpl;
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
    * @param exception
    * @param logger
    * @return response
    */
   @SuppressWarnings(value = "unchecked")
   protected Response executeExactExceptionMapper(Throwable exception, RESTEasyTracingLogger logger) {
       if (logger == null)
           logger = RESTEasyTracingLoggerImpl.empty();

       ExceptionMapper mapper = providerFactory.getExceptionMappers().get(exception.getClass());
      if (mapper == null) return null;
      mapperExecuted = true;
      long timestamp = logger.timestamp(RESTEasyServerTracingEvent.EXCEPTION_MAPPING);
      Response resp = mapper.toResponse(exception);
      logger.logDuration(RESTEasyServerTracingEvent.EXCEPTION_MAPPING, timestamp, mapper, exception, exception.getLocalizedMessage(), resp);
      return resp;
   }

   @Deprecated
   @SuppressWarnings(value = "unchecked")
   public Response executeExactExceptionMapper(Throwable exception) {
      return executeExactExceptionMapper(exception, null);
   }

   @SuppressWarnings(value = "unchecked")
   protected Response executeExceptionMapperForClass(Throwable exception, Class clazz, RESTEasyTracingLogger logger)
   {
      if (logger == null)
          logger = RESTEasyTracingLoggerImpl.empty();
      ExceptionMapper mapper = providerFactory.getExceptionMappers().get(clazz);
      if (mapper == null) return null;
      mapperExecuted = true;
      long timestamp = logger.timestamp(RESTEasyServerTracingEvent.EXCEPTION_MAPPING);
      Response resp = mapper.toResponse(exception);
      logger.logDuration(RESTEasyServerTracingEvent.EXCEPTION_MAPPING, timestamp, mapper, exception, exception.getLocalizedMessage(), resp);
      return resp;
   }

   @Deprecated
   @SuppressWarnings(value = "unchecked")
   public Response executeExceptionMapperForClass(Throwable exception, Class clazz)
   {
      return executeExceptionMapperForClass(exception, clazz, null);
   }

   protected Response handleApplicationException(HttpRequest request, ApplicationException e, RESTEasyTracingLogger logger)
   {
      Response jaxrsResponse = null;
      // See if there is a mapper for ApplicationException
      if ((jaxrsResponse = executeExceptionMapperForClass(e, ApplicationException.class, logger)) != null) {
         return jaxrsResponse;
      }
      jaxrsResponse = unwrapException(request, e, logger);
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
   protected Response executeExceptionMapper(Throwable exception, RESTEasyTracingLogger logger)
   {
       if (logger == null)
           logger = RESTEasyTracingLoggerImpl.empty();

       ExceptionMapper mapper = null;

      Class causeClass = exception.getClass();
      while (mapper == null) {
         if (causeClass == null) break;
         mapper = providerFactory.getExceptionMappers().get(causeClass);
         if (mapper == null) causeClass = causeClass.getSuperclass();
      }

      if (mapper != null) {
         mapperExecuted = true;

         final long timestamp = logger.timestamp(RESTEasyServerTracingEvent.EXCEPTION_MAPPING);
         Response jaxrsResponse = mapper.toResponse(exception);
         logger.logDuration(RESTEasyServerTracingEvent.EXCEPTION_MAPPING, timestamp, mapper, exception, exception.getLocalizedMessage(), jaxrsResponse);

         if (jaxrsResponse == null) {
            jaxrsResponse = Response.status(204).build();
         }
         return jaxrsResponse;
      }
      return null;
   }

   @Deprecated
   @SuppressWarnings(value = "unchecked")
   public Response executeExceptionMapper(Throwable exception)
   {
     return executeExactExceptionMapper(exception, null);
   }


   protected Response unwrapException(HttpRequest request, Throwable e, RESTEasyTracingLogger logger)
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

      jaxrsResponse = executeExceptionMapper(unwrappedException, logger);

      if (jaxrsResponse != null) {
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
            return unwrapException(request, unwrappedException, logger);
         }
         else {
            return null;
         }
      }
   }

   protected Response handleFailure(HttpRequest request, Failure failure) {
      if (failure.isLoggable())
         LogMessages.LOGGER.failedExecutingError(request.getHttpMethod(), request.getUri().getPath(), failure);
      else
         LogMessages.LOGGER.failedExecutingDebug(request.getHttpMethod(), request.getUri().getPath(), failure);

      Response response = failure.getResponse();

      if (response != null) {
         return response;
      } else {
         Response.ResponseBuilder builder = Response.status(failure.getErrorCode());
         if (failure.getMessage() != null)
            builder.type(MediaType.TEXT_HTML).entity(failure.getMessage());
         Response resp = builder.build();
         return resp;
      }
   }

   protected Response handleWriterException(HttpRequest request, WriterException e, RESTEasyTracingLogger logger)
   {
      Response jaxrsResponse = null;
      // See if there is a general mapper for WriterException
      if ((jaxrsResponse = executeExceptionMapperForClass(e, WriterException.class, logger)) != null) {
         return jaxrsResponse;
      }
      if (e.getResponse() != null || e.getErrorCode() > -1) {
         return handleFailure(request, e);
      }
      else if (e.getCause() != null) {
         if ((jaxrsResponse = unwrapException(request, e, logger)) != null) return jaxrsResponse;
      }
      e.setErrorCode(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      return handleFailure(request, e);
   }

   protected Response handleReaderException(HttpRequest request, ReaderException e, RESTEasyTracingLogger logger)
   {
      Response jaxrsResponse = null;
      // See if there is a general mapper for ReaderException
      if ((jaxrsResponse = executeExceptionMapperForClass(e, ReaderException.class, logger)) != null) {
         return jaxrsResponse;
      }
      if (e.getResponse() != null || e.getErrorCode() > -1) {
         return handleFailure(request, e);
      }
      else if (e.getCause() != null) {
         if ((jaxrsResponse = unwrapException(request, e, logger)) != null) return jaxrsResponse;
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
      Response response = wae.getResponse();
      return response;
   }


   public Response handleException(HttpRequest request, Throwable e) {
      Response jaxrsResponse = null;
      RESTEasyTracingLogger logger = RESTEasyTracingLogger.getInstance(request);

      // See if there is an ExceptionMapper for the exact class of the exception instance being thrown
      if ((jaxrsResponse = executeExactExceptionMapper(e, logger)) != null) return jaxrsResponse;

      // These are wrapper exceptions so they need to be processed first as they map e.getCause()
      if (e instanceof ApplicationException) {
         return handleApplicationException(request, (ApplicationException) e, logger);
      } else if (e instanceof WriterException) {
         return handleWriterException(request, (WriterException) e, logger);
      } else if (e instanceof ReaderException) {
         return handleReaderException(request, (ReaderException) e, logger);
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
         if (wae.getResponse() != null && wae.getResponse().getEntity() != null) {
            Response response =  wae.getResponse();
            return response;
         }
      }

      // First try and handle it with a mapper
      {
         jaxrsResponse = executeExceptionMapper(e, logger);
         if (jaxrsResponse != null) {
            return jaxrsResponse;
         }
      }

      // Otherwise do specific things
      if (e instanceof WebApplicationException) {
         return handleWebApplicationException((WebApplicationException) e);
      } else if (e instanceof Failure) {
         return handleFailure(request, (Failure) e);
      }

      LogMessages.LOGGER.unknownException(request.getHttpMethod(), request.getUri().getPath(), e);
      throw new UnhandledException(e);
   }
}
