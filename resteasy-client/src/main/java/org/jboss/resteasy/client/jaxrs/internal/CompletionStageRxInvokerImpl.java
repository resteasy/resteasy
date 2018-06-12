package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * <p>
 * Date March 9, 2016
 */
public class CompletionStageRxInvokerImpl implements CompletionStageRxInvoker
{
   private final SyncInvoker builder;

   private final ExecutorService executor;

   public CompletionStageRxInvokerImpl(SyncInvoker builder)
   {
      this(builder, null);
   }

   public CompletionStageRxInvokerImpl(SyncInvoker builder, ExecutorService executor)
   {
      this.builder = builder;
      this.executor = executor;
   }

   @Override
   public CompletionStage<Response> get()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.get());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.get(), executor);
      }

   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.get(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.get(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.get(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.get(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.put(entity));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.put(entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.put(entity, clazz));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.put(entity, clazz), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.put(entity, type));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.put(entity, type), executor);
      }
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.post(entity));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.post(entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.post(entity, clazz));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.post(entity, clazz), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.post(entity, type));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.post(entity, type), executor);
      }
   }

   @Override
   public CompletionStage<Response> delete()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.delete());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.delete(), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.delete(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.delete(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.delete(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.delete(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> head()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.head());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.head(), executor);
      }
   }

   @Override
   public CompletionStage<Response> options()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.options());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.options(), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.options(responseType));
      }
      {
         return CompletableFuture.supplyAsync(() -> builder.options(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.options(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.options(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> trace()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.trace());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.trace(), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.trace(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.trace(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.trace(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.trace(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, entity));
      }
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, entity, responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(name, entity, responseType), executor);
      }
   }

   public ExecutorService getExecutor()
   {
      return executor;
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(HttpMethod.PATCH, entity));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(HttpMethod.PATCH, entity), executor);
      }
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(HttpMethod.PATCH, entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(HttpMethod.PATCH, entity, responseType), executor);
      }
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> builder.method(HttpMethod.PATCH, entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> builder.method(HttpMethod.PATCH, entity, responseType), executor);
      }
   }

}
