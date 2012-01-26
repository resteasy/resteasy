package org.jboss.resteasy.client.impl;

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
import javax.ws.rs.core.RequestHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.TypeLiteral;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InvocationBuilder implements Invocation.Builder
{
   protected ClientInvocation invocation;
   protected ClientRequestHeaders headers;

   protected URI uri;
   protected ResteasyProviderFactory providerFactory;
   protected ClientHttpEngine httpEngine;
   protected ExecutorService executor;
   protected Configuration configuration;

   public InvocationBuilder(URI uri, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, Configuration configuration)
   {
      this.uri = uri;
      this.providerFactory = providerFactory;
      this.httpEngine = httpEngine;
      this.executor = executor;
      this.configuration = configuration;

      headers = new ClientRequestHeaders(providerFactory);
      invocation = new ClientInvocation(uri, headers, providerFactory, httpEngine, executor, configuration);
   }

   public ClientRequestHeaders getHeaders()
   {
      return headers;
   }

   @Override
   public Invocation.Builder acceptLanguage(Locale... locales)
   {
      headers.acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder acceptLanguage(String... locales)
   {
      headers.acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder cookie(Cookie cookie)
   {
      headers.cookie(cookie);
      return this;
   }

   @Override
   public Invocation.Builder allow(String... methods)
   {
      headers.allow(methods);
      return this;
   }

   @Override
   public Invocation.Builder allow(Set<String> methods)
   {
      headers.allow(methods);
      return this;
   }

   @Override
   public Invocation.Builder cacheControl(CacheControl cacheControl)
   {
      headers.cacheControl(cacheControl);
      return this;
   }

   @Override
   public Invocation.Builder header(String name, Object value)
   {
      headers.header(name, value);
      return this;
   }

   @Override
   public Invocation.Builder headers(RequestHeaders headers)
   {
      if (true) throw new NotImplementedYetException();
      return this;
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
      return get().getEntity(responseType);
   }

   @Override
   public <T> T get(TypeLiteral<T> responseType) throws InvocationException
   {
      return get().getEntity(responseType);
   }

   @Override
   public Response put(Entity<?> entity) throws InvocationException
   {
      return buildPut(entity).invoke();
   }

   @Override
   public <T> T put(Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return put(entity).getEntity(responseType);
   }

   @Override
   public <T> T put(Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException
   {
      return put(entity).getEntity(responseType);
   }

   @Override
   public Response post(Entity<?> entity) throws InvocationException
   {
      return buildPost(entity).invoke();
   }

   @Override
   public <T> T post(Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return buildPost(entity).invoke().getEntity(responseType);
   }

   @Override
   public <T> T post(Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException
   {
      return buildPost(entity).invoke().getEntity(responseType);
   }

   @Override
   public Response delete() throws InvocationException
   {
      return buildDelete().invoke();
   }

   @Override
   public <T> T delete(Class<T> responseType) throws InvocationException
   {
      return buildDelete().invoke().getEntity(responseType);
   }

   @Override
   public <T> T delete(TypeLiteral<T> responseType) throws InvocationException
   {
      return buildDelete().invoke().getEntity(responseType);
   }

   @Override
   public Response head() throws InvocationException
   {
      return build(HttpMethod.HEAD).invoke();
   }

   @Override
   public <T> T head(Class<T> responseType) throws InvocationException
   {
      return head().getEntity(responseType);
   }

   @Override
   public <T> T head(TypeLiteral<T> responseType) throws InvocationException
   {
      return head().getEntity(responseType);
   }

   @Override
   public Response options() throws InvocationException
   {
      return build(HttpMethod.OPTIONS).invoke();
   }

   @Override
   public <T> T options(Class<T> responseType) throws InvocationException
   {
      return options().getEntity(responseType);
   }

   @Override
   public <T> T options(TypeLiteral<T> responseType) throws InvocationException
   {
      return options().getEntity(responseType);
   }

   @Override
   public Response trace(Entity<?> entity) throws InvocationException
   {
      return build("TRACE", entity).invoke();
   }

   @Override
   public <T> T trace(Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return trace(entity).getEntity(responseType);
   }

   @Override
   public <T> T trace(Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException
   {
      return trace(entity).getEntity(responseType);
   }

   @Override
   public Response method(String name) throws InvocationException
   {
      return build(name).invoke();
   }

   @Override
   public <T> T method(String name, Class<T> responseType) throws InvocationException
   {
      return method(name).getEntity(responseType);
   }

   @Override
   public <T> T method(String name, TypeLiteral<T> responseType) throws InvocationException
   {
      return method(name).getEntity(responseType);
   }

   @Override
   public Response method(String name, Entity<?> entity) throws InvocationException
   {
      return build(name, entity).invoke();
   }

   @Override
   public <T> T method(String name, Entity<?> entity, Class<T> responseType) throws InvocationException
   {
      return method(name, entity).getEntity(responseType);
   }

   @Override
   public <T> T method(String name, Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException
   {
      return method(name, entity).getEntity(responseType);
   }
}
