package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientConfiguration implements Configuration, Providers, HeaderValueProcessor
{
   protected HashMap<String, Object> properties = new HashMap<String, Object>();
   protected ResteasyProviderFactory providerFactory;
   protected Set<Feature> features = new HashSet<Feature>();

   // We have our own injectorFactory because InjectorFactory currently holds a providerFactory member and
   // there is no SPI to change it.  I'm not sure it is wise to re-use the one provided anyways as its possible
   // for a Client to be shared between multiple threads.
   protected InjectorFactory injectorFactory;

   public ClientConfiguration(ResteasyProviderFactory factory)
   {
      if (factory instanceof ThreadLocalResteasyProviderFactory)
      {
         factory = ((ThreadLocalResteasyProviderFactory)factory).getDelegate();
      }
      this.providerFactory = new ResteasyProviderFactory(factory);
      injectorFactory = new InjectorFactoryImpl(this.providerFactory);
      this.providerFactory.setInjectorFactory(injectorFactory);
   }

   public ClientConfiguration(ClientConfiguration parent)
   {
      this(parent.getProviderFactory());
      properties.putAll(parent.properties);
   }

   protected ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Map<String, Object> getMutableProperties()
   {
      return properties;
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

   public ClientRequestFilter[] getRequestFilters(Class declaring, AccessibleObject target)
   {
      return providerFactory.getClientRequestFilters().postMatch(declaring, target);
   }

   public ClientResponseFilter[] getResponseFilters(Class declaring, AccessibleObject target)
   {
      return providerFactory.getClientResponseFilters().postMatch(declaring, target);
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
      return Collections.unmodifiableMap(properties);
   }

   @Override
   public Object getProperty(String name)
   {
      return properties.get(name);
   }

   @Override
   public Set<Feature> getFeatures()
   {
      return Collections.unmodifiableSet(features);
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
   public Configuration update(Configuration configuration)
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
      if (Feature.class.isAssignableFrom(providerClass))
      {
         Feature feature = providerFactory.createProviderInstance((Class<? extends Feature>)providerClass);
         if (feature.onEnable(this))
         {
            features.add(feature);
         }
      }
      else
      {
         providerFactory.registerProvider(providerClass);
      }
      return this;
   }

   @Override
   public Configuration register(Object provider)
   {
      if (provider instanceof Feature)
      {
         Feature feature = (Feature)provider;
         if (feature.onEnable(this))
         {
            features.add(feature);
         }
      }
      else
      {
         providerFactory.registerProviderInstance(provider);
      }
      return this;
   }

   @Override
   public Configuration setProperties(Map<String, ? extends Object> properties)
   {
      if (properties == null)
      {
         this.properties = new HashMap<String, Object>();
         return this;
      }
      this.properties = new HashMap<String, Object>();
      this.properties.putAll(properties);
      return this;
   }

   @Override
   public Configuration setProperty(String name, Object value)
   {
      if (value == null)
      {
         properties.remove(name);
         return this;
      }
      properties.put(name, value);
      return this;
   }
}
