package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
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
   public ResteasyWebTarget clone()
   {
      UriBuilder copy = uriBuilder.clone();
      return new ClientWebTarget(client, copy, configuration);
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
   public Configuration getConfiguration()
   {
      return configuration;
   }

   @Override
   public ResteasyWebTarget path(String path) throws NullPointerException
   {
      UriBuilder copy = uriBuilder.clone().path(path);
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget path(Class<?> resource) throws IllegalArgumentException
   {
      UriBuilder copy = uriBuilder.clone().path(resource);
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget path(Method method) throws IllegalArgumentException
   {
      UriBuilder copy = uriBuilder.clone().path(method);
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget resolveTemplate(String name, Object value) throws NullPointerException
   {
      String val = configuration.toString(value);
      UriBuilder copy = uriBuilder.resolveTemplate(name, val);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplates(Map<String, Object> templateValues) throws NullPointerException
   {
      Map vals = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : templateValues.entrySet())
      {
         String val = configuration.toString(entry.getValue());
         vals.put(entry.getKey(), val);
      }
      UriBuilder copy = uriBuilder.resolveTemplates(vals);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath) throws NullPointerException
   {
      String val = configuration.toString(value);
      UriBuilder copy = uriBuilder.resolveTemplate(name, val, encodeSlashInPath);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplateFromEncoded(String name, Object value) throws NullPointerException
   {
      String val = configuration.toString(value);
      UriBuilder copy = uriBuilder.resolveTemplateFromEncoded(name, val);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) throws NullPointerException
   {
      Map vals = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : templateValues.entrySet())
      {
         String val = configuration.toString(entry.getValue());
         vals.put(entry.getKey(), val);
      }
      UriBuilder copy = uriBuilder.resolveTemplatesFromEncoded(vals) ;
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws NullPointerException
   {
      Map vals = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : templateValues.entrySet())
      {
         String val = configuration.toString(entry.getValue());
         vals.put(entry.getKey(), val);
      }
      UriBuilder copy = uriBuilder.resolveTemplates(vals, encodeSlashInPath) ;
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget matrixParam(String name, Object... values) throws NullPointerException
   {
      String[] stringValues = toStringValues(values);
      UriBuilder copy = uriBuilder.clone().matrixParam(name, stringValues);
      return  new ClientWebTarget(client, copy, configuration);
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
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilder copy = uriBuilder.clone();
      for (Map.Entry<String, List<Object>> entry : parameters.entrySet())
      {
         String[] stringValues = toStringValues(entry.getValue().toArray());
         copy.queryParam(entry.getKey(), stringValues);
      }
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget queryParamNoTemplate(String name, Object... values) throws NullPointerException
   {
      String[] stringValues = toStringValues(values);
      UriBuilderImpl copy = (UriBuilderImpl)uriBuilder.clone();
      for (String obj : stringValues)
      {
         copy.clientQueryParam(name, obj);
      }
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget queryParamsNoTemplate(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      UriBuilderImpl copy = (UriBuilderImpl)uriBuilder.clone();
      for (Map.Entry<String, List<Object>> entry : parameters.entrySet())
      {
         String[] stringValues = toStringValues(entry.getValue().toArray());
         for (String val : stringValues)
         {
            copy.clientQueryParam(entry.getKey(), val);
         }
      }
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public Invocation.Builder request()
   {
      return new ClientInvocationBuilder(client, uriBuilder.build(), configuration);
   }

   @Override
   public Invocation.Builder request(String... acceptedResponseTypes)
   {
      ClientInvocationBuilder builder = new ClientInvocationBuilder(client, uriBuilder.build(), configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }

   @Override
   public Invocation.Builder request(MediaType... acceptedResponseTypes)
   {
      ClientInvocationBuilder builder = new ClientInvocationBuilder(client, uriBuilder.build(), configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      return builder;
   }

   @Override
   public ResteasyWebTarget setProperty(String name, Object value)
   {
      configuration.setProperty(name, value);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass)
   {
      configuration.register(componentClass);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass, int priority)
   {
      configuration.register(componentClass, priority);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass, Class<?>... contracts)
   {
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component)
   {
      configuration.register(component);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component, int priority)
   {
      configuration.register(component, priority);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component, Class<?>... contracts)
   {
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component, Map<Class<?>, Integer> contracts)
   {
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget replaceWith(Configuration config)
   {
      configuration.replaceWith(config);
      return this;
   }
}
