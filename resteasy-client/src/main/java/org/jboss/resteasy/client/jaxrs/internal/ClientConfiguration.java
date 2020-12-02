package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import jakarta.ws.rs.ext.WriterInterceptor;
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

   public ClientConfiguration(final ResteasyProviderFactory factory)
   {
      if (factory instanceof ThreadLocalResteasyProviderFactory)
      {
         this.providerFactory = new LocalResteasyProviderFactory(((ThreadLocalResteasyProviderFactory)factory).getDelegate());
      } else {
         this.providerFactory = new LocalResteasyProviderFactory(factory);
      }
   }

   public ClientConfiguration(final ClientConfiguration parent)
   {
      this(parent.getProviderFactory());
      setProperties(parent.getProperties());
   }

   public void setProperties(Map<String, Object> newProps)
   {
      if (newProps != null && !newProps.isEmpty())
      {
         providerFactory.setProperties(newProps);
      }
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
      JaxrsInterceptorRegistry<WriterInterceptor> writerInterceptors = providerFactory.getClientWriterInterceptorRegistry();
      return writerInterceptors == null ? null : writerInterceptors.postMatch(declaring, target);
   }

   public ReaderInterceptor[] getReaderInterceptors(Class declaring, AccessibleObject target)
   {
      JaxrsInterceptorRegistry<ReaderInterceptor> readerInterceptors = providerFactory.getClientReaderInterceptorRegistry();
      return readerInterceptors == null ? null : readerInterceptors.postMatch(declaring, target);
   }

   public ClientRequestFilter[] getRequestFilters(Class declaring, AccessibleObject target)
   {
      JaxrsInterceptorRegistry<ClientRequestFilter> requestFilters = providerFactory.getClientRequestFilterRegistry();
      return requestFilters == null ? null : requestFilters.postMatch(declaring, target);
   }

   public ClientResponseFilter[] getResponseFilters(Class declaring, AccessibleObject target)
   {
      JaxrsInterceptorRegistry<ClientResponseFilter> filters = providerFactory.getClientResponseFilters();
      return filters == null ? null : filters.postMatch(declaring, target);
   }

   public Set<DynamicFeature> getDynamicFeatures()
   {
      return providerFactory.getClientDynamicFeatures();
   }

   public ParamConverter getParamConverter(Class<?> clazz, Type genericType, Annotation[] annotations)
   {
      return providerFactory.getParamConverter(clazz, genericType, annotations);
   }

   public String toString(Object object)
   {
      return providerFactory.toString(object, object.getClass(), null, null);
   }

   public String toString(Object object, Type type, Annotation[] annotations)
   {
      return providerFactory.toString(object, object.getClass(), type, annotations);
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
      return providerFactory.getClasses();
   }

   @Override
   public Set<Object> getInstances()
   {
      return providerFactory.getInstances();
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

   public void addHeaderDelegate(Class<?> clazz, HeaderDelegate header)
   {
      providerFactory.addHeaderDelegate(clazz, header);
   }
}
