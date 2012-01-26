package org.jboss.resteasy.client.impl;

import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Target;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientTarget implements Target
{
   protected UriBuilder uriBuilder;
   protected ResteasyProviderFactory providerFactory;
   protected ClientHttpEngine httpEngine;
   protected ExecutorService executor;
   protected Configuration configuration;
   protected Map<String, Object> pathParams = new HashMap<String, Object>();

   protected ClientTarget(ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, Configuration configuration)
   {
      this.providerFactory = providerFactory;
      this.httpEngine = httpEngine;
      this.executor = executor;
      this.configuration = configuration;
   }

   public ClientTarget(String uri, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, Configuration configuration) throws IllegalArgumentException, NullPointerException
   {
      this(providerFactory, httpEngine, executor, configuration);
      uriBuilder = UriBuilder.fromUri(uri);
   }

   public ClientTarget(URI uri, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, Configuration configuration) throws NullPointerException
   {
      this(providerFactory, httpEngine, executor, configuration);
      uriBuilder = UriBuilder.fromUri(uri);
   }

   public ClientTarget(UriBuilder uriBuilder, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, Configuration configuration) throws NullPointerException
   {
      this(providerFactory, httpEngine, executor, configuration);
      this.uriBuilder = uriBuilder.clone();
   }


   @Override
   public URI getUri()
   {
      return uriBuilder.clone().build();
   }

   @Override
   public UriBuilder getUriBuilder()
   {
      return uriBuilder.clone();
   }

   @Override
   public Configuration configuration()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Target path(String path) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().path(path);
      return new ClientTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Target pathParam(String name, Object value) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      HashMap<String, Object> paramMap = new HashMap<String, Object>();
      paramMap.put(name, value);
      ClientTarget target = new ClientTarget(copy, providerFactory, httpEngine, executor, configuration);
      target.pathParams = paramMap;
      return target;
   }

   @Override
   public Target pathParams(Map<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      ClientTarget target = new ClientTarget(copy, providerFactory, httpEngine, executor, configuration);
      target.pathParams = parameters;
      return target;
   }

   @Override
   public Target matrixParam(String name, Object... values) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().matrixParam(name, values);
      return new ClientTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Target queryParam(String name, Object... values) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().queryParam(name, values);
      return new ClientTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Target queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      for (Map.Entry<String, List<Object>> entry : parameters.entrySet())
      {
         uriBuilder.queryParam(entry.getKey(), entry.getValue().toArray());
      }
      return new ClientTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Invocation.Builder request()
   {
      return new InvocationBuilder(uriBuilder.build(pathParams), providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Invocation.Builder request(String... acceptedResponseTypes)
   {
      InvocationBuilder builder = new InvocationBuilder(uriBuilder.build(pathParams), providerFactory, httpEngine, executor, configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }

   @Override
   public Invocation.Builder request(MediaType... acceptedResponseTypes)
   {
      InvocationBuilder builder = new InvocationBuilder(uriBuilder.build(pathParams), providerFactory, httpEngine, executor, configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }
}
