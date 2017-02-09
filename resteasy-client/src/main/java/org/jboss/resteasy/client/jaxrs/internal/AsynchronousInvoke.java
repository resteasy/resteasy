package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.NotImplementedYetException;

import java.util.concurrent.Future;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchronousInvoke implements AsyncInvoker
{
   protected ClientInvocation invocation;

   public AsynchronousInvoke(ClientInvocation invocation)
   {
      this.invocation = invocation;
   }

   @Override
   public Future<Response> get()
   {
      invocation.setMethod(HttpMethod.GET);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> get(Class<T> responseType)
   {
      invocation.setMethod(HttpMethod.GET);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> get(GenericType<T> responseType)
   {
      invocation.setMethod(HttpMethod.GET);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> get(InvocationCallback<T> callback)
   {
      invocation.setMethod(HttpMethod.GET);
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> trace()
   {
      invocation.setMethod("TRACE");
      return invocation.submit();
   }

   @Override
   public <T> Future<T> trace(Class<T> responseType)
   {
      invocation.setMethod("TRACE");
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> trace(GenericType<T> responseType)
   {
      invocation.setMethod("TRACE");
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> trace(InvocationCallback<T> callback)
   {
      invocation.setMethod("TRACE");
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> put(Entity<?> entity)
   {
      invocation.setMethod(HttpMethod.PUT);
      invocation.setEntity(entity);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> put(Entity<?> entity, Class<T> responseType)
   {
      invocation.setMethod(HttpMethod.PUT);
      invocation.setEntity(entity);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> put(Entity<?> entity, GenericType<T> responseType)
   {
      invocation.setMethod(HttpMethod.PUT);
      invocation.setEntity(entity);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> put(Entity<?> entity, InvocationCallback<T> callback)
   {
      invocation.setMethod(HttpMethod.PUT);
      invocation.setEntity(entity);
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> post(Entity<?> entity)
   {
      invocation.setMethod(HttpMethod.POST);
      invocation.setEntity(entity);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> post(Entity<?> entity, Class<T> responseType)
   {
      invocation.setMethod(HttpMethod.POST);
      invocation.setEntity(entity);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> post(Entity<?> entity, GenericType<T> responseType)
   {
      invocation.setMethod(HttpMethod.POST);
      invocation.setEntity(entity);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> post(Entity<?> entity, InvocationCallback<T> callback)
   {
      invocation.setMethod(HttpMethod.POST);
      invocation.setEntity(entity);
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> delete()
   {
      invocation.setMethod(HttpMethod.DELETE);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> delete(Class<T> responseType)
   {
      invocation.setMethod(HttpMethod.DELETE);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> delete(GenericType<T> responseType)
   {
      invocation.setMethod(HttpMethod.DELETE);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> delete(InvocationCallback<T> callback)
   {
      invocation.setMethod(HttpMethod.DELETE);
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> head()
   {
      invocation.setMethod(HttpMethod.HEAD);
      return invocation.submit();
   }

   @Override
   public Future<Response> head(InvocationCallback<Response> callback)
   {
      invocation.setMethod(HttpMethod.HEAD);
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> options()
   {
      invocation.setMethod(HttpMethod.OPTIONS);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> options(Class<T> responseType)
   {
      invocation.setMethod(HttpMethod.OPTIONS);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> options(GenericType<T> responseType)
   {
      invocation.setMethod(HttpMethod.OPTIONS);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> options(InvocationCallback<T> callback)
   {
      invocation.setMethod(HttpMethod.OPTIONS);
      return invocation.submit(callback);
   }


   @Override
   public Future<Response> method(String name)
   {
      invocation.setMethod(name);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> method(String name, Class<T> responseType)
   {
      invocation.setMethod(name);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> method(String name, GenericType<T> responseType)
   {
      invocation.setMethod(name);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> method(String name, InvocationCallback<T> callback)
   {
      invocation.setMethod(name);
      return invocation.submit(callback);
   }

   @Override
   public Future<Response> method(String name, Entity<?> entity)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> method(String name, Entity<?> entity, InvocationCallback<T> callback)
   {
      invocation.setMethod(name);
      invocation.setEntity(entity);
      return invocation.submit(callback);
   }

   public Future<Response> patch(Entity<?> entity) {
      throw new NotImplementedYetException();
   }

   public <T> Future<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return method(HttpMethod.PATCH, entity, responseType);
   }

   public <T> Future<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return method(HttpMethod.PATCH, entity, responseType);
   }

   public <T> Future<T> patch(Entity<?> entity, InvocationCallback<T> callback)
   {
      return method(HttpMethod.PATCH, entity, callback);
   }


}
