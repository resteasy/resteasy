package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext;
import org.jboss.resteasy.core.interception.ClientWriterInterceptorContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientException;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
   protected Type entityGenericType;
   protected Class entityClass;
   protected Annotation[] entityAnnotations;
   protected ClientConfiguration configuration;
   protected URI uri;

   // todo need a better solution for this.  Apache Http Client 4 does not let you obtain the OutputStream before executing
   // this request. is problematic for obtaining and setting
   // the output stream.  It also does not let you modify the request headers before the output stream is available
   // Since MessageBodyWriter allows you to modify headers, you're s
   protected DelegatingOutputStream delegatingOutputStream = new DelegatingOutputStream();
   protected OutputStream entityStream = delegatingOutputStream;

   public ClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers, ClientConfiguration parent)
   {
      this.uri = uri;
      this.client = client;
      this.configuration = new ClientConfiguration(parent);
      this.headers = headers;
   }

   public ClientConfiguration getConfiguration()
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

   public Class getEntityClass()
   {
      return entityClass;
   }

   public ClientRequestHeaders getHeaders()
   {
      return headers;
   }

   public void setEntity(Entity entity)
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
         Variant v = entity.getVariant();
         headers.setMediaType(v.getMediaType());
         headers.setLanguage(v.getLanguage());
         headers.header("Content-Encoding", v.getEncoding());
      }

   }

   public void setEntityObject(Object ent)
   {
      if (ent instanceof GenericEntity)
      {
         GenericEntity genericEntity = (GenericEntity) ent;
         entityClass = genericEntity.getRawType();
         entityGenericType = genericEntity.getType();
         this.entity = genericEntity.getEntity();
      }
      else
      {
         this.entity = ent;
         this.entityClass = ent.getClass();
      }
   }

   public void writeRequestBody(OutputStream outputStream) throws IOException
   {
      if (entity == null)
      {
         return;
      }

      MessageBodyWriter writer = getWriter();
      WriterInterceptor[] interceptors = getWriterInterceptors();
      if (interceptors == null || interceptors.length == 0)
      {
         writer.writeTo(entity, entityClass, entityGenericType, entityAnnotations, headers.getMediaType(), headers.getHeaders(), outputStream);
      }
      else
      {
         AbstractWriterInterceptorContext ctx = new ClientWriterInterceptorContext(interceptors, writer, entity, entityClass, entityGenericType, entityAnnotations, headers.getMediaType(), headers.getHeaders(), outputStream, getMutableProperties());
         ctx.proceed();
      }
   }

   public MessageBodyWriter getWriter()
   {
      MessageBodyWriter writer = configuration
              .getMessageBodyWriter(entityClass, entityGenericType,
                      entityAnnotations, this.getHeaders().getMediaType());
      if (writer == null)
      {
         throw new RuntimeException("could not find writer for content-type "
                 + this.getHeaders().getMediaType() + " type: " + entityClass.getName());
      }
      return writer;
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


   @Override
   public Configuration configuration()
   {
      return configuration;
   }

   @Override
   public Response invoke() throws ClientException
   {
      Providers current = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, configuration);
      try
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
                     if (requestContext.getAbortedWithResponse() instanceof ClientResponse) return requestContext.getAbortedWithResponse();
                     else return new AbortedResponse(configuration, requestContext.getAbortedWithResponse());
                  }
               }
               catch (Throwable e)
               {
                  throw new ClientException(e);
               }
            }
         }
         ClientResponse response = client.httpEngine().invoke(this);
         response.setProperties(configuration.getMutableProperties());

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
               catch (ClientException e)
               {
                  throw e;
               }
               catch (Throwable e)
               {
                  throw new ClientException(e);
               }
            }
         }
         return response;
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);
      }
   }

   @Override
   public <T> T invoke(Class<T> responseType) throws ClientException, WebApplicationException
   {
      Response response = invoke();
      return response.readEntity(responseType);
   }

   @Override
   public <T> T invoke(GenericType<T> responseType)throws ClientException, WebApplicationException
   {
      Response response = invoke();
      return response.readEntity(responseType);
   }

   @Override
   public Future<Response> submit()
   {
      return client.asyncInvocationExecutor().submit(new Callable<Response>()
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
         Future<Response> future = client.asyncInvocationExecutor().submit(new Callable<Response>()
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
               catch (ClientException e)
               {
                  cb.failed(e);
               }
               catch (Throwable e)
               {
                  cb.failed(new ClientException("MPE", e));
               }
               return null;
            }
         });
         return (Future<T>) future;

      }
      else
      {
         final Class<T> theType = type;
         final Type theGenericType = genericType;
         Future<T> future = client.asyncInvocationExecutor().submit(new Callable<T>()
         {
            @Override
            public T call() throws Exception
            {
               try
               {
                  Response res = invoke();
                  GenericType<T> gt = null;
                  if (theGenericType != null) gt = new GenericType<T>(theGenericType);
                  else gt = new GenericType<T>(theType);
                  T obj = res.readEntity(gt);
                  cb.completed(obj);
                  return obj;
               }
               catch (ClientException e)
               {
                  cb.failed(e);
               }
               catch (Throwable e)
               {
                  cb.failed(new ClientException("MPE", e));
               }
               return null;
            }
         });
         return future;
      }
   }

}
