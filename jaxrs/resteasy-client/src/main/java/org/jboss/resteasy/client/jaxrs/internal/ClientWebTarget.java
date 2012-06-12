package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
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
   protected ResteasyClient client;
   protected UriBuilder uriBuilder;
   protected ClientConfiguration configuration;
   protected Map<String, Object> pathParams = new HashMap<String, Object>();

   protected ClientWebTarget(ResteasyClient client, ClientConfiguration configuration)
   {
      this.configuration = new ClientConfiguration(configuration);
      this.client = client;
   }

   public ClientWebTarget(ResteasyClient client, String uri, ClientConfiguration configuration) throws IllegalArgumentException, NullPointerException
   {
      this(client, configuration);
      uriBuilder = UriBuilder.fromUri(uri);
   }

   public ClientWebTarget(ResteasyClient client, URI uri, ClientConfiguration configuration) throws NullPointerException
   {
      this(client, configuration);
      uriBuilder = UriBuilder.fromUri(uri);
   }

   public ClientWebTarget(ResteasyClient client, UriBuilder uriBuilder, ClientConfiguration configuration) throws NullPointerException
   {
      this(client, configuration);
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
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public WebTarget pathParam(String name, Object value) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      HashMap<String, Object> paramMap = new HashMap<String, Object>();
      paramMap.put(name, value);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      target.pathParams = paramMap;
      return target;
   }

   @Override
   public WebTarget pathParams(Map<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      target.pathParams = parameters;
      return target;
   }

   @Override
   public WebTarget matrixParam(String name, Object... values) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().matrixParam(name, values);
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public WebTarget queryParam(String name, Object... values) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().queryParam(name, values);
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public WebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      for (Map.Entry<String, List<Object>> entry : parameters.entrySet())
      {
         uriBuilder.queryParam(entry.getKey(), entry.getValue().toArray());
      }
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public Invocation.Builder request()
   {
      return new ClientInvocationBuilder(client, uriBuilder.build(pathParams), configuration);
   }

   @Override
   public Invocation.Builder request(String... acceptedResponseTypes)
   {
      ClientInvocationBuilder builder = new ClientInvocationBuilder(client, uriBuilder.build(pathParams), configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }

   @Override
   public Invocation.Builder request(MediaType... acceptedResponseTypes)
   {
      ClientInvocationBuilder builder = new ClientInvocationBuilder(client, uriBuilder.build(pathParams), configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }
}
