package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
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
public class ClientConfiguration implements Configuration, Configurable<ClientConfiguration>, Providers, HeaderValueProcessor
{
   protected ResteasyProviderFactory providerFactory;

   public ClientConfiguration(ResteasyProviderFactory factory)
   {
      if (factory instanceof ThreadLocalResteasyProviderFactory)
      {
         factory = ((ThreadLocalResteasyProviderFactory)factory).getDelegate();
      }
      this.providerFactory = new LocalResteasyProviderFactory(factory);
   }

   public ClientConfiguration(ClientConfiguration parent)
   {
      this(parent.getProviderFactory());
      setProperties(parent.getProperties());
   }

   public void setProperties(Map<String, Object> newProps)
   {
      providerFactory.setProperties(newProps);
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
    * @param object header object
    * @return header string
    */
   public String toHeaderString(Object object)
   {
      if (object instanceof String) return (String)object;
      return providerFactory.toHeaderString(object);
   }

   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MessageBodyWriter<T> writer = providerFactory.getClientMessageBodyWriter(type, genericType, annotations, mediaType);
      if (writer!=null)
          LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
      return writer;
   }

   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MessageBodyReader<T> reader = providerFactory.getClientMessageBodyReader(type, genericType, annotations, mediaType);
      if (reader!=null)
          LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
      return reader;
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
      return providerFactory.getClientRequestFilterRegistry().postMatch(declaring, target);
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
      return providerFactory.toString(object, object.getClass(), null, null);
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
   public Set<Class<?>> getClasses()
   {
      return providerFactory.getProviderClasses();
   }

   @Override
   public Set<Object> getInstances()
   {
      return providerFactory.getProviderInstances();
   }

   @Override
   public ClientConfiguration register(Class<?> providerClass)
   {
      providerFactory.register(providerClass);
      return this;
   }

   @Override
   public ClientConfiguration register(Object provider)
   {
      providerFactory.register(provider);
      return this;
   }

   @Override
   public ClientConfiguration register(Class<?> providerClass, int priority)
   {
      providerFactory.register(providerClass, priority);
      return this;
   }

   @Override
   public ClientConfiguration register(Object provider, int Priority)
   {
      providerFactory.register(provider, Priority);
      return this;
   }

   @Override
   public ClientConfiguration property(String name, Object value)
   {
      providerFactory.property(name, value);
      return this;
   }

   @Override
   public Configuration getConfiguration()
   {
      return this;
   }

   @Override
   public ClientConfiguration register(Class<?> componentClass, Class<?>... contracts)
   {
      providerFactory.register(componentClass, contracts);
      return this;
   }

   @Override
   public ClientConfiguration register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      providerFactory.register(componentClass, contracts);
      return this;
   }

   @Override
   public ClientConfiguration register(Object component, Class<?>... contracts)
   {
      providerFactory.register(component, contracts);
      return this;
   }

   @Override
   public ClientConfiguration register(Object component, Map<Class<?>, Integer> contracts)
   {
      providerFactory.register(component, contracts);
      return this;
   }

   @Override
   public RuntimeType getRuntimeType()
   {
      return RuntimeType.CLIENT;
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      return providerFactory.getProperties().keySet();
   }

   @Override
   public boolean isEnabled(Feature feature)
   {
      return providerFactory.isEnabled(feature);
   }

   @Override
   public boolean isEnabled(Class<? extends Feature> featureClass)
   {
      return providerFactory.isEnabled(featureClass);
   }

   @Override
   public boolean isRegistered(Object component)
   {
      return providerFactory.isRegistered(component);
   }

   @Override
   public boolean isRegistered(Class<?> componentClass)
   {
      return providerFactory.isRegistered(componentClass);
   }

   @Override
   public Map<Class<?>, Integer> getContracts(Class<?> componentClass)
   {
      return providerFactory.getContracts(componentClass);
   }
   
   public <I extends RxInvoker<?>> RxInvokerProvider<I> getRxInvokerProvider(Class<I> clazz)
   {
      return providerFactory.getRxInvokerProvider(clazz);
   }

   public RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz)
   {
      return providerFactory.getRxInvokerProviderFromReactiveClass(clazz);
   }
   
   public boolean isReactive(Class<?> clazz)
   {
      return providerFactory.isReactive(clazz);
   }
}
