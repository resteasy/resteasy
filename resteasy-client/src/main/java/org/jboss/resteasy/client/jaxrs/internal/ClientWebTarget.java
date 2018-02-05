package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.Invocation;
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
   protected boolean chunked = false;

   protected ClientWebTarget(ResteasyClient client, ClientConfiguration configuration)
   {
      this.configuration = new ClientConfiguration(configuration);
      this.client = client;
   }

   public ClientWebTarget(ResteasyClient client, String uri, ClientConfiguration configuration) throws IllegalArgumentException, NullPointerException
   {
      this(client, configuration);
      uriBuilder = uriBuilderFromUri(uri);
   }

   public ClientWebTarget(ResteasyClient client, URI uri, ClientConfiguration configuration) throws NullPointerException
   {
      this(client, configuration);
      uriBuilder = uriBuilderFromUri(uri);
   }

   public ClientWebTarget(ResteasyClient client, UriBuilder uriBuilder, ClientConfiguration configuration) throws NullPointerException
   {
      this(client, configuration);
      this.uriBuilder = uriBuilder.clone();
   }
   
   /**
    * Get a new UriBuilder explicitly using RESTEasy implementation
    * (instead of running UriBuilder.fromUri(uri) which relies on
    * current registered JAX-RS implementation)
    * 
    * @param uri
    * @return
    */
   private static UriBuilder uriBuilderFromUri(URI uri)
   {
       return new ResteasyUriBuilder().uri(uri);
   }
   
   private static UriBuilder uriBuilderFromUri(String uri)
   {
       return new ResteasyUriBuilder().uri(uri);
   }
   
   @Override
   public ResteasyWebTarget clone()
   {
      client.abortIfClosed();
      UriBuilder copy = uriBuilder.clone();
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyClient getResteasyClient()
   {
      client.abortIfClosed();
      return client;
   }

   @Override
   public <T> T proxy(Class<T> proxyInterface)
   {
      client.abortIfClosed();
      return ProxyBuilder.builder(proxyInterface, this).build();
   }

   @Override
   public <T> ProxyBuilder<T> proxyBuilder(Class<T> proxyInterface)
   {
      client.abortIfClosed();
      if (proxyInterface == null) throw new NullPointerException(Messages.MESSAGES.proxyInterfaceWasNull());
      return ProxyBuilder.builder(proxyInterface, this);
   }

   @Override
   public URI getUri()
   {
      client.abortIfClosed();
      return uriBuilder.clone().build();
   }

   @Override
   public UriBuilder getUriBuilder()
   {
      client.abortIfClosed();
      return uriBuilder.clone();
   }

   @Override
   public Configuration getConfiguration()
   {
      client.abortIfClosed();
      return configuration;
   }

   @Override
   public ResteasyWebTarget path(String path) throws NullPointerException
   {
      client.abortIfClosed();
      if (path == null) throw new NullPointerException(Messages.MESSAGES.pathWasNull());
      UriBuilder copy = uriBuilder.clone().path(path);
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget path(Class<?> resource) throws IllegalArgumentException
   {
      client.abortIfClosed();
      if (resource == null) throw new NullPointerException(Messages.MESSAGES.resourceWasNull());
      UriBuilder copy = uriBuilder.clone().path(resource);
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget path(Method method) throws IllegalArgumentException
   {
      client.abortIfClosed();
      if (method == null) throw new NullPointerException(Messages.MESSAGES.methodWasNull());
      UriBuilder copy = uriBuilder.clone().path(method);
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget resolveTemplate(String name, Object value) throws NullPointerException
   {
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      if (value == null) throw new NullPointerException(Messages.MESSAGES.valueWasNull());
      String val = configuration.toString(value);
      UriBuilder copy = uriBuilder.resolveTemplate(name, val);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplates(Map<String, Object> templateValues) throws NullPointerException
   {
      client.abortIfClosed();
      if (templateValues == null) throw new NullPointerException(Messages.MESSAGES.templateValuesWasNull());
      if (templateValues.isEmpty()) return this;
      Map vals = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : templateValues.entrySet())
      {
         if (entry.getKey() == null || entry.getValue() == null) throw new NullPointerException(Messages.MESSAGES.templateValuesEntryWasNull());
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
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      if (value == null) throw new NullPointerException(Messages.MESSAGES.valueWasNull());
      String val = configuration.toString(value);
      UriBuilder copy = uriBuilder.resolveTemplate(name, val, encodeSlashInPath);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplateFromEncoded(String name, Object value) throws NullPointerException
   {
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      if (value == null) throw new NullPointerException(Messages.MESSAGES.valueWasNull());
      String val = configuration.toString(value);
      UriBuilder copy = uriBuilder.resolveTemplateFromEncoded(name, val);
      ClientWebTarget target = new ClientWebTarget(client, copy, configuration);
      return target;
   }

   @Override
   public ResteasyWebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) throws NullPointerException
   {
      client.abortIfClosed();
      if (templateValues == null) throw new NullPointerException(Messages.MESSAGES.templateValuesWasNull());
      if (templateValues.isEmpty()) return this;
      Map vals = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : templateValues.entrySet())
      {
         if (entry.getKey() == null || entry.getValue() == null) throw new NullPointerException(Messages.MESSAGES.templateValuesEntryWasNull());
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
      client.abortIfClosed();
      if (templateValues == null) throw new NullPointerException(Messages.MESSAGES.templateValuesWasNull());
      if (templateValues.isEmpty()) return this;
      Map vals = new HashMap<String, String>();
      for (Map.Entry<String, Object> entry : templateValues.entrySet())
      {
         if (entry.getKey() == null || entry.getValue() == null) throw new NullPointerException(Messages.MESSAGES.templateValuesEntryWasNull());
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
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      UriBuilder copy = uriBuilder.clone();
      if (values.length == 1 && values[0] == null)
      {
         copy.replaceMatrixParam(name, null);
      }
      else
      {
         String[] stringValues = toStringValues(values);
         copy = uriBuilder.clone().matrixParam(name, stringValues);
      }
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
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      UriBuilder copy = uriBuilder.clone();
      if (values == null || (values.length == 1 && values[0] == null))
      {
         copy.replaceQueryParam(name, null);
      }
      else
      {
         String[] stringValues = toStringValues(values);
         copy = uriBuilder.clone().queryParam(name, stringValues);
      }
      return new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      client.abortIfClosed();
      if (parameters == null) throw new NullPointerException(Messages.MESSAGES.parametersWasNull());
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
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      String[] stringValues = toStringValues(values);
      ResteasyUriBuilder copy;
      if (uriBuilder instanceof ResteasyUriBuilder) {
          copy = (ResteasyUriBuilder)uriBuilder.clone();
      } else {
          copy = (ResteasyUriBuilder)ResteasyUriBuilder.fromTemplate(uriBuilder.toTemplate());
      }
      for (String obj : stringValues)
      {
         copy.clientQueryParam(name, obj);
      }
      return  new ClientWebTarget(client, copy, configuration);
   }

   @Override
   public ResteasyWebTarget queryParamsNoTemplate(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException
   {
      client.abortIfClosed();
      if (parameters == null) throw new NullPointerException(Messages.MESSAGES.parametersWasNull());
      ResteasyUriBuilder copy;
      if (uriBuilder instanceof ResteasyUriBuilder) {
          copy = (ResteasyUriBuilder)uriBuilder.clone();
      } else {
          copy = (ResteasyUriBuilder)ResteasyUriBuilder.fromTemplate(uriBuilder.toTemplate());
      }
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
   
   private ClientInvocationBuilderInterface newInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration)
   {
      try {
         Class<?> clazz = Class.forName("org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder");
         return (ClientInvocationBuilderInterface)clazz.getConstructor(ResteasyClient.class, URI.class, ClientConfiguration.class).newInstance(client, uri, configuration);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Invocation.Builder request()
   {
      client.abortIfClosed();
      ClientInvocationBuilderInterface builder = newInvocationBuilder(client, uriBuilder.build(), configuration);
      builder.setChunked(chunked);
      return builder;
   }

   @Override
   public Invocation.Builder request(String... acceptedResponseTypes)
   {
      client.abortIfClosed();
      ClientInvocationBuilderInterface builder = newInvocationBuilder(client, uriBuilder.build(), configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      builder.setChunked(chunked);
      return builder;
   }

   @Override
   public Invocation.Builder request(MediaType... acceptedResponseTypes)
   {
      client.abortIfClosed();
      ClientInvocationBuilderInterface builder = newInvocationBuilder(client, uriBuilder.build(), configuration);
      builder.getHeaders().accept(acceptedResponseTypes);
      builder.setChunked(chunked);
      return builder;
   }

   @Override
   public ResteasyWebTarget property(String name, Object value)
   {
      client.abortIfClosed();
      if (name == null) throw new NullPointerException(Messages.MESSAGES.nameWasNull());
      configuration.property(name, value);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass)
   {
      client.abortIfClosed();
      configuration.register(componentClass);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass, int priority)
   {
      client.abortIfClosed();
      configuration.register(componentClass, priority);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass, Class<?>... contracts)
   {
      client.abortIfClosed();
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      client.abortIfClosed();
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component)
   {
      client.abortIfClosed();
      configuration.register(component);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component, int priority)
   {
      client.abortIfClosed();
      configuration.register(component, priority);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component, Class<?>... contracts)
   {
      client.abortIfClosed();
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget register(Object component, Map<Class<?>, Integer> contracts)
   {
      client.abortIfClosed();
      configuration.register(component, contracts);
      return this;
   }
   
   @Override
   public ResteasyWebTarget setChunked(boolean chunked)
   {
      this.chunked = chunked;
      return this;
   }
}
