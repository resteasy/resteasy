package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.core.interception.WriterInterceptorContextImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
   protected Annotation[] entityAnnotations;
   protected ClientConfiguration configuration;
   protected URI uri;
   protected Map<String, Object> properties = new HashMap<String, Object>();

   public ClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers, ClientConfiguration configuration)
   {
      this.uri = uri;
      this.client = client;
      this.configuration = configuration;
      this.headers = headers;
      this.properties.putAll(configuration.getProperties());
   }

   public ClientInvocation clone()
   {
      ClientInvocation copy = new ClientInvocation(client, uri, headers.clone(), configuration);
      copy.client = client;
      copy.method = method;
      copy.entity = entity;
      copy.entityAnnotations = entityAnnotations;
      copy.configuration = configuration;
      return copy;
   }

   public ClientConfiguration getConfiguration()
   {
      return configuration;
   }

   public ResteasyClient getClient()
   {
      return client;
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

   public void setEntity(Object entity)
   {
      this.entity = entity;
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

   public Map<String, Object> getProperties()
   {
      return properties;
   }

   public Object getEntity()
   {
      return entity;
   }

   public ClientRequestHeaders getHeaders()
   {
      return headers;
   }

   public void setEntity(Entity entity)
   {
      this.entity = entity.getEntity();
      Variant v = entity.getVariant();
      headers.setMediaType(v.getMediaType());
      headers.setLanguage(v.getLanguage());
      headers.header("Content-Encoding", v.getEncoding());

   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return client.getProviderFactory();
   }

   public void writeRequestBody(OutputStream outputStream) throws IOException
   {
      if (entity == null)
      {
         return;
      }

      Object obj = entity;
      Class type = obj.getClass();
      Type genericType = null;

      if (obj instanceof GenericEntity)
      {
         GenericEntity genericEntity = (GenericEntity) obj;
         type = genericEntity.getRawType();
         genericType = genericEntity.getType();
      }


      MessageBodyWriter writer = client.getProviderFactory()
              .getMessageBodyWriter(type, genericType,
                      entityAnnotations, this.getHeaders().getMediaType());
      if (writer == null)
      {
         throw new RuntimeException("could not find writer for content-type "
                 + this.getHeaders().getMediaType() + " type: " + type.getName());
      }
      WriterInterceptor[] interceptors = getWriterInterceptors();
      if (interceptors == null || interceptors.length == 0)
      {
         writer.writeTo(entity, type, genericType, entityAnnotations, headers.getMediaType(), headers.getHeaders(), outputStream);
      }
      else
      {
         WriterInterceptorContextImpl ctx = new WriterInterceptorContextImpl(interceptors, writer, entity, type, genericType, entityAnnotations, headers.getMediaType(), headers.getHeaders(), outputStream, getProperties());
         ctx.proceed();
      }
   }

   protected WriterInterceptor[] getWriterInterceptors()
   {
      return client.getProviderFactory().getClientWriterInterceptorRegistry().postMatch(null, null);
   }

   protected ClientRequestFilter[] getRequestFilters()
   {
      return client.getProviderFactory().getClientRequestFilters().postMatch(null, null);
   }

   protected ClientResponseFilter[] getResponseFilters()
   {
      return client.getProviderFactory().getClientResponseFilters().postMatch(null, null);
   }

   // Invocation methods


   @Override
   public Configuration configuration()
   {
      return configuration;
   }

   @Override
   public Response invoke() throws InvocationException
   {
      ClientRequestContextImpl requestContext = new ClientRequestContextImpl(this);
      ClientRequestFilter[] requestFilters = getRequestFilters();
      if (requestFilters != null && requestFilters.length > 0)
      {
         for (ClientRequestFilter filter : requestFilters)
         {
            try
            {
               filter.filter(requestContext);
               if (requestContext.getAbortedWithResponse() != null)
               {
                  return requestContext.getAbortedWithResponse();
               }
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      ClientResponse response = client.getHttpEngine().invoke(this);
      response.setProperties(properties);

      ClientResponseFilter[] responseFilters = getResponseFilters();
      if (requestFilters != null && requestFilters.length > 0)
      {
         ClientResponseContextImpl responseContext = new ClientResponseContextImpl(response);
         for (ClientResponseFilter filter : responseFilters)
         {
            try
            {
               filter.filter(requestContext, responseContext);
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      return response;
   }

   @Override
   public <T> T invoke(Class<T> responseType) throws InvocationException
   {
      Response response = invoke();
      return response.readEntity(responseType);
   }

   @Override
   public <T> T invoke(GenericType<T> responseType) throws InvocationException
   {
      Response response = invoke();
      return response.readEntity(responseType);
   }

   @Override
   public Future<Response> submit()
   {
      return client.getAsyncInvocationExecutor().submit(new Callable<Response>()
      {
         @Override
         public Response call() throws Exception
         {
            return invoke();
         }
      });
   }


   @Override
   public <T> Future<T> submit(Class<T> responseType)
   {
      final Future<Response> future = submit();
      final Class<T> type = responseType;
      return new Future<T>()
      {
         @Override
         public boolean cancel(boolean b)
         {
            return future.cancel(b);
         }

         @Override
         public boolean isCancelled()
         {
            return future.isCancelled();
         }

         @Override
         public boolean isDone()
         {
            return future.isDone();
         }

         @Override
         public T get() throws InterruptedException, ExecutionException
         {
            Response response = future.get();
            return response.readEntity(type);
         }

         @Override
         public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException
         {
            Response response = future.get(l, timeUnit);
            return response.readEntity(type);
         }
      };
   }

   private static class TypeLiteralFuture<T> implements Future<T>
   {
      protected GenericType<T> type;
      protected Future<Response> future;

      private TypeLiteralFuture(GenericType<T> type, Future<Response> future)
      {
         this.type = type;
         this.future = future;
      }

      @Override
      public boolean cancel(boolean b)
      {
         return future.cancel(b);
      }

      @Override
      public boolean isCancelled()
      {
         return future.isCancelled();
      }

      @Override
      public boolean isDone()
      {
         return future.isDone();
      }

      @Override
      public T get() throws InterruptedException, ExecutionException
      {
         Response response = future.get();
         try
         {
            return response.readEntity(type);
         }
         catch (MessageProcessingException e)
         {
            throw new ExecutionException(e);
         }
      }

      @Override
      public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException
      {
         Response response = future.get(l, timeUnit);
         try
         {
            return response.readEntity(type);
         }
         catch (MessageProcessingException e)
         {
            throw new ExecutionException(e);
         }
      }
   }

   @Override
   public <T> Future<T> submit(GenericType<T> responseType)
   {
      Future<Response> future = submit();
      return new TypeLiteralFuture<T>(responseType, future);
   }

   @Override
   public <T> Future<T> submit(InvocationCallback<T> callback)
   {
      Class type = Response.class;
      Type genericType = null;

      Type[] typeInfo = Types.getActualTypeArgumentsOfAnInterface(callback.getClass(), InvocationCallback.class);
      if (typeInfo != null)
      {
         type = (Class<T>) Types.getRawType(typeInfo[0]);
         genericType = typeInfo[0];
         if (type == null) type = Response.class;
      }

      final InvocationCallback<T> cb = callback;

      if (type.equals(Response.class))
      {
         Future<Response> future = client.getAsyncInvocationExecutor().submit(new Callable<Response>()
         {
            @Override
            public Response call() throws Exception
            {
               try
               {
                  Response res = invoke();
                  cb.completed((T) res);
                  return res;
               }
               catch (InvocationException e)
               {
                  cb.failed(e);
               }
               return null;
            }
         });
         return (Future<T>)future;

      }
      else
      {
         final Class<T> theType = type;
         final Type theGenericType = genericType;
         Future<T> future = client.getAsyncInvocationExecutor().submit(new Callable<T>()
         {
            @Override
            public T call() throws Exception
            {
               try
               {
                  Response res = invoke();
                  T obj = res.readEntity((GenericType<T>) GenericType.of(theType, theGenericType));
                  cb.completed(obj);
                  return obj;
               }
               catch (InvocationException e)
               {
                  cb.failed(e);
               }
               catch (MessageProcessingException e)
               {
                  cb.failed(new InvocationException("MPE", e));
               }
               return null;
            }
         });
         return future;
      }
   }

}
