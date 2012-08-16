package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientWebTarget implements ResteasyWebTarget
{
   protected ResteasyClient client;
   protected UriBuilder uriBuilder;
   protected ClientConfiguration configuration;
   protected Map<String, String> pathParams = new HashMap<String, String>();

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
   public ResteasyClient getResteasyClient()
   {
      return client;
   }

   @Override
   public <T> T proxy(Class<T> proxyInterface)
   {
      return ProxyBuilder.builder(proxyInterface, this).build();
   }

   @Override
   public <T> ProxyBuilder<T> proxyBuilder(Class<T> proxyInterface)
   {
      return ProxyBuilder.builder(proxyInterface, this);
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
      return configuration;
   }

   @Override
   public ResteasyWebTarget path(String path) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().path(path);
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget path(Class<?> resource) throws IllegalArgumentException
   {
      UriBuilder copy = uriBuilder.clone().path(resource);
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget path(Method method) throws IllegalArgumentException
   {
      UriBuilder copy = uriBuilder.clone().path(method);
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget pathParam(String name, Object value) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      HashMap<String, String> paramMap = new HashMap<String, String>();
      paramMap.putAll(pathParams);
      paramMap.put(name, configuration.toString(value));
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      target.pathParams = paramMap;
      return target;
   }

   @Override
   public ResteasyWebTarget pathParams(Map<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      HashMap<String, String> paramMap = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : parameters.entrySet())
      {
         paramMap.put(entry.getKey(), configuration.toString(entry.getValue()));
      }
      target.pathParams = paramMap;
      return target;
   }

   @Override
   public ResteasyWebTarget matrixParam(String name, Object... values) throws NullPointerException
   {
      String[] stringValues = toStringValues(values);
      UriBuilder copy = uriBuilder.clone().matrixParam(name, stringValues);
      return new ClientWebTarget(client, copy, configuration);
   }

   private String[] toStringValues(Object[] values)
   {
      String[] stringValues = new String[values.length];
      for (int i = 0; i < stringValues.length; i++)
      {
         stringValues[i] = configuration.toString(values[i]);
      }
      return stringValues;
   }

   @Override
   public ResteasyWebTarget queryParam(String name, Object... values) throws NullPointerException
   {
      String[] stringValues = toStringValues(values);
      UriBuilder copy = uriBuilder.clone().queryParam(name, stringValues);
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      for (Map.Entry<String, List<Object>> entry : parameters.entrySet())
      {
         String[] stringValues = toStringValues(entry.getValue().toArray());
         uriBuilder.queryParam(entry.getKey(), stringValues);
      }
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public Invocation.Builder request()
   {
      return new ClientInvocationBuilder(client, uriBuilder.buildFromMap(pathParams), configuration);
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
