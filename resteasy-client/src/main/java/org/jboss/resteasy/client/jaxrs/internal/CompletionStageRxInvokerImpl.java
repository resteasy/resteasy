package org.jboss.resteasy.client.jaxrs.internal;

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
   private final ClientInvocationBuilder builder;

   private final ExecutorService executor;

   public CompletionStageRxInvokerImpl(final SyncInvoker builder)
   {
      this(builder, null);
   }

   public CompletionStageRxInvokerImpl(final SyncInvoker builder, final ExecutorService executor)
   {
      this.builder = (ClientInvocationBuilder)builder;
      this.executor = executor;
   }

   @Override
   public CompletionStage<Response> get()
   {
      return builder.asyncCS().get();
   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return builder.asyncCS().get(responseType);
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return builder.asyncCS().get(responseType);
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      return builder.asyncCS().put(entity);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      return builder.asyncCS().put(entity, clazz);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      return builder.asyncCS().put(entity, type);
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      return builder.asyncCS().post(entity);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {

      return builder.asyncCS().post(entity, clazz);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      return builder.asyncCS().post(entity, type);
   }

   @Override
   public CompletionStage<Response> delete()
   {
      return builder.asyncCS().delete();
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return builder.asyncCS().delete(responseType);
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return builder.asyncCS().delete(responseType);
   }

   @Override
   public CompletionStage<Response> head()
   {
      return builder.asyncCS().head();
   }

   @Override
   public CompletionStage<Response> options()
   {
      return builder.asyncCS().options();
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return builder.asyncCS().options(responseType);
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return builder.asyncCS().options(responseType);
   }

   @Override
   public CompletionStage<Response> trace()
   {

      return builder.asyncCS().trace();
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {

      return builder.asyncCS().trace(responseType);
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return builder.asyncCS().trace(responseType);
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      return builder.asyncCS().method(name);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      return builder.asyncCS().method(name, responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      return builder.asyncCS().method(name, responseType);
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      return builder.asyncCS().method(name, entity);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return builder.asyncCS().method(name, entity, responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return builder.asyncCS().method(name, entity, responseType);
   }

   public ExecutorService getExecutor()
   {
      return executor;
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      return builder.asyncCS().method(HttpMethod.PATCH, entity);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return builder.asyncCS().method(HttpMethod.PATCH, entity, responseType);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return builder.asyncCS().method(HttpMethod.PATCH, entity, responseType);
   }

}
