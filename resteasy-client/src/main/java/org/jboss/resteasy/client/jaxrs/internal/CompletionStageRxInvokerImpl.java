package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

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

   private ClientInvocation createClientInvocation(String method, Entity<?> entity)
   {
      ClientInvocation invocation = builder.createClientInvocation(builder.invocation);
      invocation.setMethod(method);
      invocation.setEntity(entity);
      return invocation;
   }

   @Override
   public CompletionStage<Response> get()
   {
      return createClientInvocation(HttpMethod.GET, null).submitCF();
   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.GET, null).submitCF(responseType);
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.GET, null).submitCF(responseType);
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      return createClientInvocation(HttpMethod.PUT, entity).submitCF();
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      return createClientInvocation(HttpMethod.PUT, entity).submitCF(clazz);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      return createClientInvocation(HttpMethod.PUT, entity).submitCF(type);
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      return createClientInvocation(HttpMethod.POST, entity).submitCF();
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      return createClientInvocation(HttpMethod.POST, entity).submitCF(clazz);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      return createClientInvocation(HttpMethod.POST, entity).submitCF(type);
   }

   @Override
   public CompletionStage<Response> delete()
   {
      return createClientInvocation(HttpMethod.DELETE, null).submitCF();
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.DELETE, null).submitCF(responseType);
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.DELETE, null).submitCF(responseType);
   }

   @Override
   public CompletionStage<Response> head()
   {
      return createClientInvocation(HttpMethod.HEAD, null).submitCF();
   }

   @Override
   public CompletionStage<Response> options()
   {
      return createClientInvocation(HttpMethod.OPTIONS, null).submitCF();
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.OPTIONS, null).submitCF(responseType);
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.OPTIONS, null).submitCF(responseType);
   }

   @Override
   public CompletionStage<Response> trace()
   {
      return createClientInvocation("TRACE", null).submitCF();
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      return createClientInvocation("TRACE", null).submitCF(responseType);
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return createClientInvocation("TRACE", null).submitCF(responseType);
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      return createClientInvocation(name, null).submitCF();
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      return createClientInvocation(name, null).submitCF(responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      return createClientInvocation(name, null).submitCF(responseType);
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      return createClientInvocation(name, entity).submitCF();
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return createClientInvocation(name, entity).submitCF(responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return createClientInvocation(name, entity).submitCF(responseType);
   }

   public ExecutorService getExecutor()
   {
      return executor;
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      return createClientInvocation(HttpMethod.PATCH, entity).submitCF();
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return createClientInvocation(HttpMethod.PATCH, entity).submitCF(responseType);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return createClientInvocation(HttpMethod.PATCH, entity).submitCF(responseType);
   }

}
