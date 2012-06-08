package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
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
public class ClientWebTarget implements WebTarget
{
   protected UriBuilder uriBuilder;
   protected ResteasyProviderFactory providerFactory;
   protected ClientHttpEngine httpEngine;
   protected ExecutorService executor;
   protected ClientConfiguration configuration;
   protected Map<String, Object> pathParams = new HashMap<String, Object>();

   protected ClientWebTarget(ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, ClientConfiguration configuration)
   {
      this.providerFactory = providerFactory;
      this.httpEngine = httpEngine;
      this.executor = executor;
      this.configuration = new ClientConfiguration(configuration);
   }

   public ClientWebTarget(String uri, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, ClientConfiguration configuration) throws IllegalArgumentException, NullPointerException
   {
      this(providerFactory, httpEngine, executor, configuration);
      uriBuilder = UriBuilder.fromUri(uri);
   }

   public ClientWebTarget(URI uri, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, ClientConfiguration configuration) throws NullPointerException
   {
      this(providerFactory, httpEngine, executor, configuration);
      uriBuilder = UriBuilder.fromUri(uri);
   }

   public ClientWebTarget(UriBuilder uriBuilder, ResteasyProviderFactory providerFactory, ClientHttpEngine httpEngine, ExecutorService executor, ClientConfiguration configuration) throws NullPointerException
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
   public WebTarget path(String path) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().path(path);
      return new ClientWebTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public WebTarget pathParam(String name, Object value) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      HashMap<String, Object> paramMap = new HashMap<String, Object>();
      paramMap.put(name, value);
      ClientWebTarget target = new ClientWebTarget(copy, providerFactory, httpEngine, executor, configuration);
      target.pathParams = paramMap;
      return target;
   }

   @Override
   public WebTarget pathParams(Map<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      ClientWebTarget target = new ClientWebTarget(copy, providerFactory, httpEngine, executor, configuration);
      target.pathParams = parameters;
      return target;
   }

   @Override
   public WebTarget matrixParam(String name, Object... values) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().matrixParam(name, values);
      return new ClientWebTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public WebTarget queryParam(String name, Object... values) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().queryParam(name, values);
      return new ClientWebTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public WebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      for (Map.Entry<String, List<Object>> entry : parameters.entrySet())
      {
         uriBuilder.queryParam(entry.getKey(), entry.getValue().toArray());
      }
      return new ClientWebTarget(copy, providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Invocation.Builder request()
   {
      return new ClientInvocationBuilder(uriBuilder.build(pathParams), providerFactory, httpEngine, executor, configuration);
   }

   @Override
   public Invocation.Builder request(String... acceptedResponseTypes)
   {
      ClientInvocationBuilder builder = new ClientInvocationBuilder(uriBuilder.build(pathParams), providerFactory, httpEngine, executor, configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }

   @Override
   public Invocation.Builder request(MediaType... acceptedResponseTypes)
   {
      ClientInvocationBuilder builder = new ClientInvocationBuilder(uriBuilder.build(pathParams), providerFactory, httpEngine, executor, configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }
}
