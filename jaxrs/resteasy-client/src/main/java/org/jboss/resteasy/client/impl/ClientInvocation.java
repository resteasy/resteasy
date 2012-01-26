package org.jboss.resteasy.client.impl;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.RequestHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.TypeLiteral;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;
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
public class ClientInvocation implements Invocation, Request
{
   protected ClientHttpEngine httpEngine;
   protected ExecutorService executor;
   protected ClientRequestHeaders headers;
   protected String method;
   protected Entity entity;
   protected Annotation[] entityAnnotations;
   protected Configuration configuration;
   protected URI uri;
   protected ResteasyProviderFactory providerFactory;

   public ClientInvocation(URI uri, ClientRequestHeaders headers, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, Configuration configuration)
   {
      this.uri = uri;
      this.providerFactory = providerFactory;
      this.httpEngine = httpEngine;
      this.executor = executor;
      this.configuration = configuration;
      this.headers = headers;
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

   public void setEntity(Entity entity)
   {
      this.entity = entity;
      Variant v = entity.getVariant();
      headers.setMediaType(v.getMediaType());
      headers.setLanguage(v.getLanguage());
      headers.header("Content-Encoding", v.getEncoding());

   }

   public void setConfiguration(Configuration configuration)
   {
      this.configuration = configuration;
   }

   public void setUri(URI uri)
   {
      this.uri = uri;
   }

   public void setEntityAnnotations(Annotation[] entityAnnotations)
   {
      this.entityAnnotations = entityAnnotations;
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

      Object obj = entity.getEntity();
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
      // todo handle writer interceptors
      writer.writeTo(entity.getEntity(), type, genericType, entityAnnotations, entity.getMediaType(), headers.getHeaders(), outputStream);
   }


   // Invocation methods

   @Override
   public Response invoke() throws InvocationException
   {
      return httpEngine.invoke(this);
   }

   @Override
   public <T> T invoke(Class<T> responseType) throws InvocationException
   {
      Response response = httpEngine.invoke(this);
      return response.getEntity(responseType);
   }

   @Override
   public <T> T invoke(TypeLiteral<T> responseType) throws InvocationException
   {
      Response response = httpEngine.invoke(this);
      return response.getEntity(responseType);
   }

   @Override
   public Future<Response> submit()
   {
      return executor.submit(new Callable<Response>()
      {
         @Override
         public Response call() throws Exception
         {
            return httpEngine.invoke(ClientInvocation.this);
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
            return response.getEntity(type);
         }

         @Override
         public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException
         {
            Response response = future.get(l, timeUnit);
            return response.getEntity(type);
         }
      };
   }

   private static class TypeLiteralFuture<T> implements Future<T>
   {
      protected TypeLiteral<T> type;
      protected Future<Response> future;

      private TypeLiteralFuture(TypeLiteral<T> type, Future<Response> future)
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
            return response.getEntity(type);
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
            return response.getEntity(type);
         }
         catch (MessageProcessingException e)
         {
            throw new ExecutionException(e);
         }
      }
   }

   @Override
   public <T> Future<T> submit(TypeLiteral<T> responseType)
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

      if (type.equals(Response.class))
      {
         Future<Response> future = executor.submit(new Callable<Response>()
         {
            @Override
            public Response call() throws Exception
            {
               return httpEngine.invoke(ClientInvocation.this);
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
               Response response = httpEngine.invoke(ClientInvocation.this);
               return response.getEntity(theType);
            }
         });
         return future;
      }
   }

   // Request required methods

   @Override
   public Configuration configuration()
   {
      return configuration;
   }

   @Override
   public String getMethod()
   {
      return method;
   }

   @Override
   public RequestHeaders getHeaders()
   {
      return headers;
   }

   @Override
   public URI getUri()
   {
      return uri;
   }

   @Override
   public UriBuilder getUriBuilder()
   {
      return UriBuilder.fromUri(uri);
   }

   @Override
   public Object getEntity()
   {
      if (entity == null) return null;
      return entity.getEntity();
   }

   @Override
   public <T> T getEntity(Class<T> type) throws MessageProcessingException
   {
      return (T) entity.getEntity();
   }

   @Override
   public <T> T getEntity(TypeLiteral<T> entityType) throws MessageProcessingException
   {
      return (T) entity.getEntity();
   }

   @Override
   public boolean hasEntity()
   {
      return entity != null;
   }

   @Override
   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException
   {
      return null;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions(EntityTag eTag)
   {
      return null;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions(Date lastModified)
   {
      return null;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag)
   {
      return null;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions()
   {
      return null;
   }
}
