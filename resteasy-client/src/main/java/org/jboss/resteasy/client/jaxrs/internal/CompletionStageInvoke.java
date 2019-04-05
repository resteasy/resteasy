package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

/**
 * An invoker for asynchronous invocation of HTTP methods that returns {@link CompletionStage}
 *
 * @author <a href="mailto:anilgursel@gmail.com">Anil Gursel</a>
 * @version $Revision: 1 $
 */
public class CompletionStageInvoke
{
   protected ClientInvocation invocation;

   public CompletionStageInvoke(final ClientInvocation invocation)
   {
      this.invocation = invocation;
   }

   public CompletionStage<Response> get()
   {
      return method(HttpMethod.GET);
   }

   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return method(HttpMethod.GET, responseType);
   }

   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return method(HttpMethod.GET, responseType);
   }

   public CompletionStage<Response> trace()
   {
      return method("TRACE");
   }

   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      return method("TRACE", responseType);
   }

   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return method("TRACE", responseType);
   }

   public CompletionStage<Response> put(Entity<?> entity)
   {
      return method(HttpMethod.PUT, entity);
   }

   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> responseType)
   {
      return method(HttpMethod.PUT, entity, responseType);
   }

   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> responseType)
   {
      return method(HttpMethod.PUT, entity, responseType);
   }

   public CompletionStage<Response> post(Entity<?> entity)
   {
      return method(HttpMethod.POST, entity);
   }

   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> responseType)
   {
      return method(HttpMethod.POST, entity, responseType);
   }

   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> responseType)
   {
      return method(HttpMethod.POST, entity, responseType);
   }

   public CompletionStage<Response> delete()
   {
      return method(HttpMethod.DELETE);
   }

   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return method(HttpMethod.DELETE, responseType);
   }

   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return method(HttpMethod.DELETE, responseType);
   }

   public CompletionStage<Response> head()
   {
      return method(HttpMethod.HEAD);
   }

   public CompletionStage<Response> options()
   {
      return method(HttpMethod.OPTIONS);
   }

   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return method(HttpMethod.OPTIONS, responseType);
   }

   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return method(HttpMethod.OPTIONS, responseType);
   }

   public CompletionStage<Response> method(String name)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
      return invocation.submitCF();
   }

   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
      return invocation.submitCF(responseType);
   }

   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
      return invocation.submitCF(responseType);
   }

   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submitCF();
   }

   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submitCF(responseType);
   }

   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submitCF(responseType);
   }

   public CompletionStage<Response> patch(Entity<?> entity) {
      throw new NotImplementedYetException();
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return method(HttpMethod.PATCH, entity, responseType);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return method(HttpMethod.PATCH, entity, responseType);
   }
}
