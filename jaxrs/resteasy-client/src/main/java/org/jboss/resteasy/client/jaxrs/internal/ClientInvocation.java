package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.core.filter.WriterInterceptorContextImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.MessageProcessingException;
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
   protected ClientHttpEngine httpEngine;
   protected ExecutorService executor;
   protected ClientRequestHeaders headers;
   protected String method;
   protected Object entity;
   protected Annotation[] entityAnnotations;
   protected ClientConfiguration configuration;
   protected URI uri;
   protected ResteasyProviderFactory providerFactory;
   protected Map<String, Object> properties = new HashMap<String, Object>();

   public ClientInvocation(URI uri, ClientRequestHeaders headers, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, ClientConfiguration configuration)
   {
      this.uri = uri;
      this.providerFactory = providerFactory;
      this.httpEngine = httpEngine;
      this.executor = executor;
      this.configuration = configuration;
      this.headers = headers;
      this.properties.putAll(configuration.getProperties());
   }

   public ClientInvocation clone()
   {
      ClientInvocation copy = new ClientInvocation(uri, headers.clone(), providerFactory, httpEngine, executor, configuration);
      copy.method = method;
      copy.entity = entity;
      copy.entityAnnotations = entityAnnotations;
      copy.configuration = configuration;
      return copy;
   }

   public URI getUri()
   {
      return uri;
   }

   public String getMethod()
   {
      return method;
   }

   public void setHttpEngine(ClientHttpEngine httpEngine)
   {
      this.httpEngine = httpEngine;
   }

   public void setExecutor(ExecutorService executor)
   {
      this.executor = executor;
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
      return providerFactory;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
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


      MessageBodyWriter writer = providerFactory
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
         WriterInterceptorContextImpl ctx = new WriterInterceptorContextImpl(entity,
                 type,
                 genericType,
                 entityAnnotations,
                 headers.getMediaType(),
                 headers.getHeaders(),
                 outputStream,
                 interceptors,
                 writer,
                 getProperties());
         ctx.proceed();
      }
   }

   protected WriterInterceptor[] getWriterInterceptors()
   {
      return providerFactory.getClientInterceptors().getWriterInterceptors().bind(null, null);
   }

   /*
   protected RequestFilter[] getRequestFilters()
   {
      return providerFactory.getClientInterceptors().getRequestFilters().bind(null, null);
   }

   protected ResponseFilter[] getResponseFilters()
   {
      return providerFactory.getClientInterceptors().getResponseFilters().bind(null, null);
   }
   */

   // Invocation methods


   @Override
   public Configuration configuration()
   {
      return configuration;
   }

   @Override
   public Response invoke() throws InvocationException
   {
      /*
      RequestFilter[] requestFilters = getRequestFilters();
      if (requestFilters != null && requestFilters.length > 0)
      {
         ClientFilterContext ctx = new ClientFilterContext(this);
         for (RequestFilter filter : requestFilters)
         {
            try
            {
               filter.preFilter(ctx);
               if (ctx.getResponse() != null)
               {
                  return ctx.getResponse();
               }
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      */
      ClientResponse response = httpEngine.invoke(this);
      response.setProperties(properties);

      /*
      ResponseFilter[] responseFilters = getResponseFilters();
      if (requestFilters != null && requestFilters.length > 0)
      {
         ClientFilterContext ctx = new ClientFilterContext(this);
         ctx.setResponse(response);
         for (ResponseFilter filter : responseFilters)
         {
            try
            {
               filter.postFilter(ctx);
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
         return ctx.getResponse();
      }
      */
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
      return executor.submit(new Callable<Response>()
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
         Future<Response> future = executor.submit(new Callable<Response>()
         {
            @Override
            public Response call() throws Exception
            {
               try
               {
                  Response res = invoke();
                  cb.completed((T)res);
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
         Future<T> future = executor.submit(new Callable<T>()
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
