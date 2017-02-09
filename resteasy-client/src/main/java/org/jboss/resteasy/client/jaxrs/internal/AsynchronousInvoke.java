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
      return method(HttpMethod.GET);
   }

   @Override
   public <T> Future<T> get(Class<T> responseType)
   {
      return method(HttpMethod.GET, responseType);
   }

   @Override
   public <T> Future<T> get(GenericType<T> responseType)
   {
      return method(HttpMethod.GET, responseType);
   }

   @Override
   public <T> Future<T> get(InvocationCallback<T> callback)
   {
      return method(HttpMethod.GET, callback);
   }

   @Override
   public Future<Response> trace()
   {
      return method("TRACE");
   }

   @Override
   public <T> Future<T> trace(Class<T> responseType)
   {
      return method("TRACE", responseType);
   }

   @Override
   public <T> Future<T> trace(GenericType<T> responseType)
   {
      return method("TRACE", responseType);
   }

   @Override
   public <T> Future<T> trace(InvocationCallback<T> callback)
   {
      return method("TRACE", callback);
   }

   @Override
   public Future<Response> put(Entity<?> entity)
   {
      return method(HttpMethod.PUT, entity);
   }

   @Override
   public <T> Future<T> put(Entity<?> entity, Class<T> responseType)
   {
      return method(HttpMethod.PUT, entity, responseType);
   }

   @Override
   public <T> Future<T> put(Entity<?> entity, GenericType<T> responseType)
   {
      return method(HttpMethod.PUT, entity, responseType);
   }

   @Override
   public <T> Future<T> put(Entity<?> entity, InvocationCallback<T> callback)
   {
      return method(HttpMethod.PUT, entity, callback);
   }

   @Override
   public Future<Response> post(Entity<?> entity)
   {
      return method(HttpMethod.POST, entity);
   }

   @Override
   public <T> Future<T> post(Entity<?> entity, Class<T> responseType)
   {
      return method(HttpMethod.POST, entity, responseType);
   }

   @Override
   public <T> Future<T> post(Entity<?> entity, GenericType<T> responseType)
   {
      return method(HttpMethod.POST, entity, responseType);
   }

   @Override
   public <T> Future<T> post(Entity<?> entity, InvocationCallback<T> callback)
   {
      return method(HttpMethod.POST, entity, callback);
   }

   @Override
   public Future<Response> delete()
   {
      return method(HttpMethod.DELETE);
   }

   @Override
   public <T> Future<T> delete(Class<T> responseType)
   {
      return method(HttpMethod.DELETE, responseType);
   }

   @Override
   public <T> Future<T> delete(GenericType<T> responseType)
   {
      return method(HttpMethod.DELETE, responseType);
   }

   @Override
   public <T> Future<T> delete(InvocationCallback<T> callback)
   {
      return method(HttpMethod.DELETE, callback);
   }

   @Override
   public Future<Response> head()
   {
      return method(HttpMethod.HEAD);
   }

   @Override
   public Future<Response> head(InvocationCallback<Response> callback)
   {
      return method(HttpMethod.HEAD,callback);
   }

   @Override
   public Future<Response> options()
   {
      return method(HttpMethod.OPTIONS);
   }

   @Override
   public <T> Future<T> options(Class<T> responseType)
   {
      return method(HttpMethod.OPTIONS, responseType);
   }

   @Override
   public <T> Future<T> options(GenericType<T> responseType)
   {
      return method(HttpMethod.OPTIONS, responseType);
   }

   @Override
   public <T> Future<T> options(InvocationCallback<T> callback)
   {
      return method(HttpMethod.OPTIONS, callback);
   }


   @Override
   public Future<Response> method(String name)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
      return invocation.submit();
   }

   @Override
   public <T> Future<T> method(String name, Class<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> method(String name, GenericType<T> responseType)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
      return invocation.submit(responseType);
   }

   @Override
   public <T> Future<T> method(String name, InvocationCallback<T> callback)
   {
      invocation.setMethod(name);
      invocation.setEntity(null);
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
