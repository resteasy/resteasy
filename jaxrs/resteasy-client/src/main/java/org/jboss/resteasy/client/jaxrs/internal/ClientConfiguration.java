package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientConfiguration implements Configuration, Providers, HeaderValueProcessor
{
   protected ResteasyProviderFactory providerFactory;

   public ClientConfiguration(ResteasyProviderFactory factory)
   {
      if (factory instanceof ThreadLocalResteasyProviderFactory)
      {
         factory = ((ThreadLocalResteasyProviderFactory)factory).getDelegate();
      }
      this.providerFactory = new ResteasyProviderFactory(factory);
   }

   public ClientConfiguration(ClientConfiguration parent)
   {
      this(parent.getProviderFactory());
      setProperties(parent.getProperties());
   }

   protected ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Map<String, Object> getMutableProperties()
   {
      return providerFactory.getMutableProperties();
   }

   /**
    * Convert an object to a header string.  First try StringConverter, then HeaderDelegate, then object.toString()
    *
    * @param object
    * @return
    */
   public String toHeaderString(Object object)
   {
      return providerFactory.toHeaderString(object);
   }

   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return providerFactory.getMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return providerFactory.getMessageBodyReader(type, genericType, annotations, mediaType);
   }

   public WriterInterceptor[] getWriterInterceptors(Class declaring, AccessibleObject target)
   {
      return providerFactory.getClientWriterInterceptorRegistry().postMatch(declaring, target);
   }

   public ReaderInterceptor[] getReaderInterceptors(Class declaring, AccessibleObject target)
   {
      return providerFactory.getClientReaderInterceptorRegistry().postMatch(declaring, target);
   }

   public ClientRequestFilter[] getRequestFilters(Class declaring, AccessibleObject target)
   {
      return providerFactory.getClientRequestFilters().postMatch(declaring, target);
   }

   public ClientResponseFilter[] getResponseFilters(Class declaring, AccessibleObject target)
   {
      return providerFactory.getClientResponseFilters().postMatch(declaring, target);
   }

   public Set<DynamicFeature> getDynamicFeatures()
   {
      return providerFactory.getClientDynamicFeatures();
   }

   public String toString(Object object)
   {
      return providerFactory.toString(object);
   }




   // interface implementation

   // Providers

   @Override
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return providerFactory.getExceptionMapper(type);
   }

   @Override
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      return providerFactory.getContextResolver(contextType, mediaType);
   }

   // Configuration

   @Override
   public Map<String, Object> getProperties()
   {
      return providerFactory.getProperties();
   }

   @Override
   public Object getProperty(String name)
   {
      return providerFactory.getProperty(name);
   }

   @Override
   public Collection<Feature> getFeatures()
   {
      return providerFactory.getFeatures();
   }

   @Override
   public Set<Class<?>> getProviderClasses()
   {
      return providerFactory.getProviderClasses();
   }

   @Override
   public Set<Object> getProviderInstances()
   {
      return providerFactory.getProviderInstances();
   }

   @Override
   public Configuration updateFrom(Configurable configuration)
   {
      providerFactory = new ResteasyProviderFactory();
      setProperties(configuration.getProperties());
      for (Class c : configuration.getProviderClasses())
      {
         register(c);
      }
      for (Object obj : configuration.getProviderInstances())
      {
         register(obj);
      }
      return this;
   }

   @Override
   public Configuration register(Class<?> providerClass)
   {
      providerFactory.register(providerClass);
      return this;
   }

   @Override
   public Configuration register(Object provider)
   {
      providerFactory.register(provider);
      return this;
   }

   @Override
   public Configuration register(Class<?> providerClass, int bindingPriority)
   {
      providerFactory.register(providerClass, bindingPriority);
      return this;
   }

   @Override
   public <T> Configuration register(Class<T> providerClass, Class<? super T>... contracts)
   {
      providerFactory.register(providerClass, contracts);
      return this;
   }

   @Override
   public <T> Configuration register(Class<T> providerClass, int bindingPriority, Class<? super T>... contracts)
   {
      providerFactory.register(providerClass, bindingPriority, contracts);
      return this;
   }

   @Override
   public Configuration register(Object provider, int bindingPriority)
   {
      providerFactory.register(provider, bindingPriority);
      return this;
   }

   @Override
   public <T> Configuration register(Object provider, Class<? super T>... contracts)
   {
      providerFactory.register(provider, contracts);
      return this;
   }

   @Override
   public <T> Configuration register(Object provider, int bindingPriority, Class<? super T>... contracts)
   {
      providerFactory.register(provider, bindingPriority, contracts);
      return this;
   }

   @Override
   public Configuration setProperties(Map<String, ? extends Object> properties)
   {
      providerFactory.setProperties(properties);
      return this;
   }

   @Override
   public Configuration setProperty(String name, Object value)
   {
      providerFactory.setProperty(name, value);
      return this;
   }
}
