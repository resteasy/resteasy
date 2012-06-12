package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInvocationBuilder implements Invocation.Builder
{
   protected ClientInvocation invocation;

   public ClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration)
   {
      invocation = new ClientInvocation(client, uri, new ClientRequestHeaders(client.getProviderFactory()), configuration);
   }

   public ClientInvocationBuilder(ClientInvocation invocation)
   {
      this.invocation = invocation.clone();

   }

   public ClientRequestHeaders getHeaders()
   {
      return invocation.headers;
   }

   @Override
   public Invocation.Builder acceptLanguage(Locale... locales)
   {
      getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder acceptLanguage(String... locales)
   {
      getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder cookie(Cookie cookie)
   {
      getHeaders().cookie(cookie);
      return this;
   }

   @Override
   public Invocation.Builder allow(String... methods)
   {
      getHeaders().allow(methods);
      return this;
   }

   @Override
   public Invocation.Builder allow(Set<String> methods)
   {
      getHeaders().allow(methods);
      return this;
   }

   @Override
   public Invocation.Builder cacheControl(CacheControl cacheControl)
   {
      getHeaders().cacheControl(cacheControl);
      return this;
   }

   @Override
   public Invocation.Builder header(String name, Object value)
   {
      getHeaders().header(name, value);
      return this;
   }

   @Override
   public Invocation.Builder headers(MultivaluedMap<String, Object> headers)
   {
      throw new NotImplementedYetException();
   }
   @Override
   public Invocation build(String method)
   {
      invocation.setMethod(method);
      return invocation;
   }

   @Override
   public Invocation build(String method, Entity<?> entity)
   {
      invocation.setMethod(method);
      invocation.setEntity(entity);
      return invocation;
   }

   @Override
   public Invocation buildGet()
   {
      return build(HttpMethod.GET);
   }

   @Override
   public Invocation buildDelete()
   {
      return build(HttpMethod.DELETE);
   }

   @Override
   public Invocation buildPost(Entity<?> entity)
   {
      return build(HttpMethod.POST, entity);
   }

   @Override
   public Invocation buildPut(Entity<?> entity)
   {
      return build(HttpMethod.PUT, entity);
   }

   @Override
   public AsyncInvoker async()
   {
      return new AsynchronousInvoke(invocation);
   }

   @Override
   public Configuration configuration()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response get() throws InvocationException
   {
      return buildGet().invoke();
   }

   @Override
   public <T> T get(Class<T> responseType) throws InvocationException
   {
      return get().readEntity(responseType);
   }

   @Override
   public <T> T get(GenericType<T> responseType) throws InvocationException
   {
      return get().readEntity(responseType);
   }

   @Override
   public Response put(Entity<?> entity) throws InvocationException
   {
      return buildPut(entity).invoke();
   }

   @Override
   public <T> T put(Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return put(entity).readEntity(responseType);
   }

   @Override
   public <T> T put(Entity<?> entity, GenericType<T> responseType) throws InvocationException
   {
      return put(entity).readEntity(responseType);
   }

   @Override
   public Response post(Entity<?> entity) throws InvocationException
   {
      return buildPost(entity).invoke();
   }

   @Override
   public <T> T post(Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return buildPost(entity).invoke().readEntity(responseType);
   }

   @Override
   public <T> T post(Entity<?> entity, GenericType<T> responseType) throws InvocationException
   {
      return buildPost(entity).invoke().readEntity(responseType);
   }

   @Override
   public Response delete() throws InvocationException
   {
      return buildDelete().invoke();
   }

   @Override
   public <T> T delete(Class<T> responseType) throws InvocationException
   {
      return buildDelete().invoke().readEntity(responseType);
   }

   @Override
   public <T> T delete(GenericType<T> responseType) throws InvocationException
   {
      return buildDelete().invoke().readEntity(responseType);
   }

   @Override
   public Response head() throws InvocationException
   {
      return build(HttpMethod.HEAD).invoke();
   }

   @Override
   public Response options() throws InvocationException
   {
      return build(HttpMethod.OPTIONS).invoke();
   }

   @Override
   public <T> T options(Class<T> responseType) throws InvocationException
   {
      return options().readEntity(responseType);
   }

   @Override
   public <T> T options(GenericType<T> responseType) throws InvocationException
   {
      return options().readEntity(responseType);
   }

   @Override
   public Response trace(Entity<?> entity) throws InvocationException
   {
      return build("TRACE", entity).invoke();
   }

   @Override
   public <T> T trace(Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return trace(entity).readEntity(responseType);
   }

   @Override
   public <T> T trace(Entity<?> entity, GenericType<T> responseType) throws InvocationException
   {
      return trace(entity).readEntity(responseType);
   }

   @Override
   public Response method(String name) throws InvocationException
   {
      return build(name).invoke();
   }

   @Override
   public <T> T method(String name, Class<T> responseType) throws InvocationException
   {
      return method(name).readEntity(responseType);
   }

   @Override
   public <T> T method(String name, GenericType<T> responseType) throws InvocationException
   {
      return method(name).readEntity(responseType);
   }

   @Override
   public Response method(String name, Entity<?> entity) throws InvocationException
   {
      return build(name, entity).invoke();
   }

   @Override
   public <T> T method(String name, Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return method(name, entity).readEntity(responseType);
   }

   @Override
   public <T> T method(String name, Entity<?> entity, GenericType<T> responseType) throws InvocationException
   {
      return method(name, entity).readEntity(responseType);
   }

}
