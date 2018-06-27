package org.jboss.resteasy.client.jaxrs.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.client.jaxrs.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker;
import org.jboss.resteasy.core.interception.jaxrs.AbstractWriterInterceptorContext;
import org.jboss.resteasy.core.interception.jaxrs.ClientWriterInterceptorContext;
import org.jboss.resteasy.plugins.providers.sse.EventInput;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInvocation implements Invocation
{
   protected ResteasyClient client;

   protected ClientRequestHeaders headers;

   protected String method;

   protected Object entity;

   protected Type entityGenericType;

   protected Class<?> entityClass;

   protected Annotation[] entityAnnotations;

   protected ClientConfiguration configuration;

   protected URI uri;

   protected boolean chunked;
   
   protected ClientInvoker clientInvoker;

   // todo need a better solution for this.  Apache Http Client 4 does not let you obtain the OutputStream before executing this request.
   // That is problematic for wrapping the output stream in e.g. a RequestFilter for transparent compressing.
   protected DelegatingOutputStream delegatingOutputStream = new DelegatingOutputStream();

   protected OutputStream entityStream = delegatingOutputStream;

   public ClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers, ClientConfiguration parent)
   {
      this.uri = uri;
      this.client = client;
      this.configuration = new ClientConfiguration(parent);
      this.headers = headers;
   }

   protected ClientInvocation(ClientInvocation clientInvocation)
   {
      this.client = clientInvocation.client;
      this.configuration = new ClientConfiguration(clientInvocation.configuration);
      this.headers = new ClientRequestHeaders(this.configuration);
      MultivaluedTreeMap.copy(clientInvocation.headers.getHeaders(), this.headers.headers);
      this.method = clientInvocation.method;
      this.entity = clientInvocation.entity;
      this.entityGenericType = clientInvocation.entityGenericType;
      this.entityClass = clientInvocation.entityClass;
      this.entityAnnotations = clientInvocation.entityAnnotations;
      this.uri = clientInvocation.uri;
      this.chunked = clientInvocation.chunked;
   }

   /**
    * Extracts result from response throwing an appropriate exception if not a successful response.
    *
    * @param responseType generic type
    * @param response response entity
    * @param annotations array of annotations
    * @param <T> type
    * @return extracted result of type T
    */
   public static <T> T extractResult(GenericType<T> responseType, Response response, Annotation[] annotations)
   {
      int status = response.getStatus();
      if (status >= 200 && status < 300)
      {
         try
         {
            if (response.getMediaType() == null)
            {
               return null;
            }
            else
            {
               T rtn = response.readEntity(responseType, annotations);
               if (InputStream.class.isInstance(rtn) || Reader.class.isInstance(rtn)
                     || EventInput.class.isInstance(rtn))
               {
                  if (response instanceof ClientResponse)
                  {
                     ClientResponse clientResponse = (ClientResponse) response;
                     clientResponse.noReleaseConnection();
                  }
               }
               return rtn;

            }
         }
         catch (WebApplicationException wae)
         {
            try
            {
               response.close();
            }
            catch (Exception e)
            {

            }
            throw wae;
         }
         catch (Throwable throwable)
         {
            try
            {
               response.close();
            }
            catch (Exception e)
            {

            }
            throw new ResponseProcessingException(response, throwable);
         }
         finally
         {
            if (response.getMediaType() == null)
               response.close();
         }
      }
      try
      {
         // Buffer the entity for any exception thrown as the response may have any entity the user wants
         // We don't want to leave the connection open though.
         String s = String.class.cast(response.getHeaders().getFirst("resteasy.buffer.exception.entity"));
         if (s == null || Boolean.parseBoolean(s))
         {
            response.bufferEntity();
         }
         else
         {
            // close connection
            if (response instanceof ClientResponse)
            {
               try
               {
                  ClientResponse.class.cast(response).releaseConnection();
               }
               catch (IOException e)
               {
                  // Ignore
               }
            }
         }
         if (status >= 300 && status < 400)
            throw new RedirectionException(response);

         return handleErrorStatus(response);
      }
      finally
      {
         // close if no content
         if (response.getMediaType() == null)
            response.close();
      }

   }

   /**
    * Throw an exception.  Expecting a status of 400 or greater.
    *
    * @param response response entity
    * @param <T> type
    * @return unreachable
    */
   public static <T> T handleErrorStatus(Response response)
   {
      final int status = response.getStatus();
      switch (status)
      {
         case 400 :
            throw new BadRequestException(response);
         case 401 :
            throw new NotAuthorizedException(response);
         case 403 :
            throw new ForbiddenException(response);
         case 404 :
            throw new NotFoundException(response);
         case 405 :
            throw new NotAllowedException(response);
         case 406 :
            throw new NotAcceptableException(response);
         case 415 :
            throw new NotSupportedException(response);
         case 500 :
            throw new InternalServerErrorException(response);
         case 503 :
            throw new ServiceUnavailableException(response);
         default :
            break;
      }

      if (status >= 400 && status < 500)
         throw new ClientErrorException(response);
      if (status >= 500)
         throw new ServerErrorException(response);

      throw new WebApplicationException(response);
   }

   public ClientConfiguration getClientConfiguration()
   {
      return configuration;
   }

   public ResteasyClient getClient()
   {
      return client;
   }

   public DelegatingOutputStream getDelegatingOutputStream()
   {
      return delegatingOutputStream;
   }

   public void setDelegatingOutputStream(DelegatingOutputStream delegatingOutputStream)
   {
      this.delegatingOutputStream = delegatingOutputStream;
   }

   public OutputStream getEntityStream()
   {
      return entityStream;
   }

   public void setEntityStream(OutputStream entityStream)
   {
      this.entityStream = entityStream;
   }

   public URI getUri()
   {
      return uri;
   }

   public void setUri(URI uri)
   {
      this.uri = uri;
   }

   public Annotation[] getEntityAnnotations()
   {
      return entityAnnotations;
   }

   public void setEntityAnnotations(Annotation[] entityAnnotations)
   {
      this.entityAnnotations = entityAnnotations;
   }

   public String getMethod()
   {
      return method;
   }

   public void setMethod(String method)
   {
      this.method = method;
   }

   public void setHeaders(ClientRequestHeaders headers)
   {
      this.headers = headers;
   }

   public Map<String, Object> getMutableProperties()
   {
      return configuration.getMutableProperties();
   }

   public Object getEntity()
   {
      return entity;
   }

   public Type getEntityGenericType()
   {
      return entityGenericType;
   }

   public Class<?> getEntityClass()
   {
      return entityClass;
   }

   public ClientRequestHeaders getHeaders()
   {
      return headers;
   }

   public void setEntity(Entity<?> entity)
   {
      if (entity == null)
      {
         this.entity = null;
         this.entityAnnotations = null;
         this.entityClass = null;
         this.entityGenericType = null;
      }
      else
      {
         Object ent = entity.getEntity();
         setEntityObject(ent);
         this.entityAnnotations = entity.getAnnotations();
         Variant v = entity.getVariant();
         headers.setMediaType(v.getMediaType());
         headers.setLanguage(v.getLanguage());
         headers.header("Content-Encoding", null);
         headers.header("Content-Encoding", v.getEncoding());
      }

   }

   public void setEntityObject(Object ent)
   {
      if (ent instanceof GenericEntity)
      {
         GenericEntity<?> genericEntity = (GenericEntity<?>) ent;
         entityClass = genericEntity.getRawType();
         entityGenericType = genericEntity.getType();
         this.entity = genericEntity.getEntity();
      }
      else
      {
         if (ent == null)
         {
            this.entity = null;
            this.entityClass = null;
            this.entityGenericType = null;
         }
         else
         {
            this.entity = ent;
            this.entityClass = ent.getClass();
            this.entityGenericType = ent.getClass();
         }
      }
   }

   public void writeRequestBody(OutputStream outputStream) throws IOException
   {
      if (entity == null)
      {
         return;
      }

      WriterInterceptor[] interceptors = getWriterInterceptors();
      AbstractWriterInterceptorContext ctx = new ClientWriterInterceptorContext(interceptors,
            configuration.getProviderFactory(), entity, entityClass, entityGenericType, entityAnnotations,
            headers.getMediaType(), headers.getHeaders(), outputStream, getMutableProperties());
      ctx.proceed();
   }

   public WriterInterceptor[] getWriterInterceptors()
   {
      return configuration.getWriterInterceptors(null, null);
   }

   public ClientRequestFilter[] getRequestFilters()
   {
      return configuration.getRequestFilters(null, null);
   }

   public ClientResponseFilter[] getResponseFilters()
   {
      return configuration.getResponseFilters(null, null);
   }

   // Invocation methods

   public Configuration getConfiguration()
   {
      return configuration;
   }

   public boolean isChunked()
   {
      return chunked;
   }

   public void setChunked(boolean chunked)
   {
      this.chunked = chunked;
   }

   @Override
   public ClientResponse invoke()
   {
      Providers current = pushProvidersContext();
      try
      {
         ClientRequestContextImpl requestContext = new ClientRequestContextImpl(this);
         ClientResponse aborted = filterRequest(requestContext);

         // spec requires that aborted response go through filter/interceptor chains.
         ClientResponse response = (aborted != null) ? aborted : client.httpEngine().invoke(this);
         return filterResponse(requestContext, response);
      }
      catch (ResponseProcessingException e)
      {
         if (e.getResponse() != null)
         {
            e.getResponse().close();
         }
         throw e;
      }
      finally
      {
         popProvidersContext(current);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T invoke(Class<T> responseType)
   {
      Response response = invoke();
      if (Response.class.equals(responseType))
         return (T) response;
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T invoke(GenericType<T> responseType)
   {
      Response response = invoke();
      if (responseType.getRawType().equals(Response.class))
         return (T) response;
      return extractResult(responseType, response, null);
   }

   @Override
   public Future<Response> submit()
   {
      return doSubmit(false, null, new AsyncClientHttpEngine.ResultExtractor<Response>()
      {
         @Override
         public Response extractResult(ClientResponse response)
         {
            return response;
         }
      });
   }

   @Override
   public <T> Future<T> submit(final Class<T> responseType)
   {
      return doSubmit(false, null, new AsyncClientHttpEngine.ResultExtractor<T>()
      {
         @SuppressWarnings("unchecked")
         @Override
         public T extractResult(ClientResponse response)
         {
            if (Response.class.equals(responseType))
               return (T) response;
            return ClientInvocation.extractResult(new GenericType<T>(responseType), response, null);
         }
      });
   }

   @Override
   public <T> Future<T> submit(final GenericType<T> responseType)
   {
      return doSubmit(false, null, new AsyncClientHttpEngine.ResultExtractor<T>()
      {
         @SuppressWarnings("unchecked")
         @Override
         public T extractResult(ClientResponse response)
         {
            if (responseType.getRawType().equals(Response.class))
               return (T) response;
            return ClientInvocation.extractResult(responseType, response, null);
         }
      });
   }

   @SuppressWarnings(
   {"rawtypes", "unchecked"})
   @Override
   public <T> Future<T> submit(final InvocationCallback<T> callback)
   {
      GenericType<T> genericType = (GenericType<T>) new GenericType<Object>()
      {
      };
      Type[] typeInfo = Types.getActualTypeArgumentsOfAnInterface(callback.getClass(), InvocationCallback.class);
      if (typeInfo != null)
      {
         genericType = new GenericType(typeInfo[0]);
      }

      final GenericType<T> responseType = genericType;
      return doSubmit(true, callback, new AsyncClientHttpEngine.ResultExtractor<T>()
      {
         @Override
         public T extractResult(ClientResponse response)
         {
            if (responseType.getRawType().equals(Response.class))
               return (T) response;
            return ClientInvocation.extractResult(responseType, response, null);
         }
      });
   }

   @Override
   public Invocation property(String name, Object value)
   {
      configuration.property(name, value);
      return this;
   }
   
   public ClientInvoker getClientInvoker() {
      return clientInvoker;
   }

   public void setClientInvoker(ClientInvoker clientInvoker) {
      this.clientInvoker = clientInvoker;
   }
   // internals

   private Providers pushProvidersContext()
   {
      Providers current = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, configuration);
      return current;
   }

   private void popProvidersContext(Providers current)
   {
      ResteasyProviderFactory.popContextData(Providers.class);
      if (current != null)
         ResteasyProviderFactory.pushContext(Providers.class, current);
   }

   private ClientResponse filterRequest(ClientRequestContextImpl requestContext)
   {
      ClientRequestFilter[] requestFilters = getRequestFilters();
      ClientResponse aborted = null;
      if (requestFilters != null && requestFilters.length > 0)
      {
         for (ClientRequestFilter filter : requestFilters)
         {
            try
            {
               filter.filter(requestContext);
               if (requestContext.getAbortedWithResponse() != null)
               {
                  aborted = new AbortedResponse(configuration, requestContext.getAbortedWithResponse());
                  break;
               }
            }
            catch (ProcessingException e)
            {
               throw e;
            }
            catch (Throwable e)
            {
               throw new ProcessingException(e);
            }
         }
      }
      return aborted;
   }

   protected ClientResponse filterResponse(ClientRequestContextImpl requestContext, ClientResponse response)
   {
      response.setProperties(configuration.getMutableProperties());

      ClientResponseFilter[] responseFilters = getResponseFilters();
      if (responseFilters != null && responseFilters.length > 0)
      {
         ClientResponseContextImpl responseContext = new ClientResponseContextImpl(response);
         for (ClientResponseFilter filter : responseFilters)
         {
            try
            {
               filter.filter(requestContext, responseContext);
            }
            catch (ResponseProcessingException e)
            {
               throw e;
            }
            catch (Throwable e)
            {
               throw new ResponseProcessingException(response, e);
            }
         }
      }
      return response;
   }

   private <T> Future<T> doSubmit(boolean buffered, InvocationCallback<T> callback,
         AsyncClientHttpEngine.ResultExtractor<T> extractor)
   {
      ClientHttpEngine httpEngine = client.httpEngine();
      if (httpEngine instanceof AsyncClientHttpEngine)
      {
         return asyncSubmit((AsyncClientHttpEngine) httpEngine, buffered, callback, extractor);
      }
      else
      {
         // never buffered, but always blocks in a thread
         return executorSubmit(client.asyncInvocationExecutor(), callback, extractor);
      }
   }

   private <T> Future<T> asyncSubmit(AsyncClientHttpEngine asyncHttpEngine, boolean buffered,
         InvocationCallback<T> callback, final AsyncClientHttpEngine.ResultExtractor<T> extractor)
   {
      final ClientRequestContextImpl requestContext = new ClientRequestContextImpl(this);
      Providers current = pushProvidersContext();
      try
      {
         ClientResponse aborted = filterRequest(requestContext);
         if (aborted != null)
         {
            // spec requires that aborted response go through filter/interceptor chains.
            aborted = filterResponse(requestContext, aborted);
            T result = extractor.extractResult(aborted);
            callCompletedNoThrow(callback, result);
            return new CompletedFuture<T>(result, null);
         }
      }
      catch (Exception ex)
      {
         callFailedNoThrow(callback, ex);
         return new CompletedFuture<T>(null, new ExecutionException(ex));
      }
      finally
      {
         popProvidersContext(current);
      }

      return asyncHttpEngine.submit(this, buffered, callback, new AsyncClientHttpEngine.ResultExtractor<T>()
      {

         @Override
         public T extractResult(ClientResponse response)
         {
            Providers current = pushProvidersContext();
            try
            {
               return extractor.extractResult(filterResponse(requestContext, response));
            }
            finally
            {
               popProvidersContext(current);
            }
         }
      });
   }

   private <T> Future<T> executorSubmit(ExecutorService executor, final InvocationCallback<T> callback,
         final AsyncClientHttpEngine.ResultExtractor<T> extractor)
   {
      return executor.submit(new Callable<T>()
      {
         @Override
         public T call() throws Exception
         {
            // ensure the future and the callback see the same result
            T result = null;
            ClientResponse response = null;
            try
            {
               response = invoke(); // does filtering too
               result = extractor.extractResult(response);
               callCompletedNoThrow(callback, result);
               return result;
            }
            catch (Exception e)
            {
               callFailedNoThrow(callback, e);
               throw e;
            }
            finally
            {
               if (response != null && callback != null)
                  response.close();
            }
         }
      });
   }

   private <T> void callCompletedNoThrow(InvocationCallback<T> callback, T result)
   {
      if (callback != null)
      {
         try
         {
            callback.completed(result);
         }
         catch (Exception e)
         {
            //logger.error("ignoring exception in InvocationCallback", e);
         }
      }
   }

   private <T> void callFailedNoThrow(InvocationCallback<T> callback, Exception exception)
   {
      if (callback != null)
      {
         try
         {
            callback.failed(exception);
         }
         catch (Exception e)
         {
            //logger.error("ignoring exception in InvocationCallback", e);
         }
      }
   }

   private static class CompletedFuture<T> implements Future<T>
   {

      private final T result;

      private final ExecutionException ex;

      public CompletedFuture(T result, ExecutionException ex)
      {
         this.ex = ex;
         this.result = result;
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
         return false;
      }

      @Override
      public boolean isCancelled()
      {
         return false;
      }

      @Override
      public boolean isDone()
      {
         return true;
      }

      @Override
      public T get() throws InterruptedException, ExecutionException
      {
         if (ex != null)
            throw ex;
         return result;
      }

      @Override
      public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
      {
         return get();
      }
   }
}
