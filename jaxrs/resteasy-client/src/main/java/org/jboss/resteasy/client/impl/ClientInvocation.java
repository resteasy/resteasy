package org.jboss.resteasy.client.impl;

import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.TypeLiteral;
import java.lang.reflect.Type;
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
public class ClientInvocation extends ClientRequest implements Invocation
{
   protected ClientExecutor clientExecutor;
   protected ExecutorService executor;

   public ClientInvocation(ClientExecutor clientExecutor)
   {
      this.clientExecutor = clientExecutor;
   }

   @Override
   public RequestBuilder clone()
   {
      ClientInvocation copy = new ClientInvocation(clientExecutor);
      super.copy(copy);
      return copy;
   }

   @Override
   public Response invoke() throws InvocationException
   {
      return clientExecutor.invoke(this);
   }

   @Override
   public <T> T invoke(Class<T> responseType) throws InvocationException
   {
      Response response = clientExecutor.invoke(this);
      return response.getEntity(responseType);
   }

   @Override
   public <T> T invoke(TypeLiteral<T> responseType) throws InvocationException
   {
      Response response = clientExecutor.invoke(this);
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
            return clientExecutor.invoke(ClientInvocation.this);
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
      Class<T> type = null;
      Type genericType = null;

      Object[] typeInfo = Types.getInterfaceTemplateParameter(callback.getClass(), InvocationCallback.class);
      if (typeInfo != null)
      {
         type = (Class<T>)typeInfo[0];
         genericType = (Type)typeInfo[1];
      }



      Future<T> future = executor.submit(new Callable<Response>()
      {
         @Override
         public Response call() throws Exception
         {
            return clientExecutor.invoke(ClientInvocation.this);
         }
      });

      return null;
   }

   @Override
   public Configuration configuration()
   {
      return null;
   }
}
