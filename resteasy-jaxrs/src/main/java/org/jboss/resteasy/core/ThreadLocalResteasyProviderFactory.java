package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.resteasy.core.interception.jaxrs.ClientRequestFilterRegistry;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverter;

import org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ContainerRequestFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.jaxrs.WriterInterceptorRegistry;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.ContextInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.ThreadLocalStack;

/**
 * Allow applications to push/pop provider factories onto the stack.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public final class ThreadLocalResteasyProviderFactory extends ResteasyProviderFactory implements ProviderFactoryDelegate
{
   private static final ThreadLocalStack<ResteasyProviderFactory> delegate = new ThreadLocalStack<ResteasyProviderFactory>();

   private final ResteasyProviderFactory defaultFactory;


   public ThreadLocalResteasyProviderFactory(ResteasyProviderFactory defaultFactory)
   {
      this.defaultFactory = defaultFactory;
   }

   public ResteasyProviderFactory getDelegate()
   {
      ResteasyProviderFactory factory = delegate.get();
      if (factory == null) return defaultFactory;
      return factory;
   }

   @Override
   protected void initialize()
   {

   }

   @Override
   public MediaType getConcreteMediaTypeFromMessageBodyWriters(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return super.getConcreteMediaTypeFromMessageBodyWriters(type, genericType, annotations, mediaType);
   }

   @Override
   public HeaderDelegate<?> getHeaderDelegate(Class<?> aClass)
   {
      return getDelegate().getHeaderDelegate(aClass);
   }

   @Override
   public <T> T injectedInstance(Class<? extends T> clazz, HttpRequest request, HttpResponse response)
   {
      return getDelegate().injectedInstance(clazz, request, response);
   }

   @Override
   public void injectProperties(Object obj, HttpRequest request, HttpResponse response)
   {
      getDelegate().injectProperties(obj, request, response);
   }

   public static void push(ResteasyProviderFactory factory)
   {
      delegate.push(factory);
   }

   public static void pop()
   {
      delegate.pop();
   }

   @Override
   public ContainerResponseFilterRegistry getContainerResponseFilterRegistry()
   {
      return getDelegate().getContainerResponseFilterRegistry();
   }

   @Override
   public ReaderInterceptorRegistry getServerReaderInterceptorRegistry()
   {
      return getDelegate().getServerReaderInterceptorRegistry();
   }

   @Override
   public Variant.VariantListBuilder createVariantListBuilder()
   {
      return getDelegate().createVariantListBuilder();
   }

   @Override
   public List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type)
   {
      return getDelegate().getContextResolvers(clazz, type);
   }

   @Override
   public boolean isBuiltinsRegistered()
   {
      return getDelegate().isBuiltinsRegistered();
   }

   @Override
   public void setBuiltinsRegistered(boolean builtinsRegistered)
   {
      getDelegate().setBuiltinsRegistered(builtinsRegistered);
   }

   @Override
   public ResteasyProviderFactory register(Class<?> providerClass)
   {
      return getDelegate().register(providerClass);
   }

   @Override
   public Set<DynamicFeature> getClientDynamicFeatures()
   {
      return getDelegate().getClientDynamicFeatures();
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      return getDelegate().register(componentClass, contracts);
   }

   @Override
   public Collection<Feature> getEnabledFeatures()
   {
      return getDelegate().getEnabledFeatures();
   }

   @Override
   public Response.ResponseBuilder createResponseBuilder()
   {
      return getDelegate().createResponseBuilder();
   }

   @Override
   public void registerProviderInstance(Object provider)
   {
      getDelegate().registerProviderInstance(provider);
   }
   
   @Override
   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      return getDelegate().createStringParameterUnmarshaller(clazz);
   }

   @Override
   public void setInjectorFactory(InjectorFactory injectorFactory)
   {
      getDelegate().setInjectorFactory(injectorFactory);
   }

   @Override
   public Set<Object> getInstances()
   {
      return getDelegate().getInstances();
   }

   @Override
   public boolean isRegistered(Object component)
   {
      return getDelegate().isRegistered(component);
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, int priority)
   {
      return getDelegate().register(componentClass, priority);
   }

   @Override
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      return getDelegate().getContextResolver(contextType, mediaType);
   }

   @Override
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MessageBodyReader<T> reader = getDelegate().getMessageBodyReader(type, genericType, annotations, mediaType);
      if (reader!=null)
          LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
      return reader;
   }

   @Override
   public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts)
   {
      getDelegate().registerProvider(provider, priorityOverride, isBuiltin, contracts);
   }

   @Override
   public ContainerRequestFilterRegistry getContainerRequestFilterRegistry()
   {
      return getDelegate().getContainerRequestFilterRegistry();
   }

   @Override
   public ResteasyProviderFactory register(Object component, Map<Class<?>, Integer> contracts)
   {
      return getDelegate().register(component, contracts);
   }

   @Override
   public boolean isRegisterBuiltins()
   {
      return getDelegate().isRegisterBuiltins();
   }

   @Override
   public ReaderInterceptorRegistry getClientReaderInterceptorRegistry()
   {
      return getDelegate().getClientReaderInterceptorRegistry();
   }

   @Override
   public void setRegisterBuiltins(boolean registerBuiltins)
   {
      getDelegate().setRegisterBuiltins(registerBuiltins);
   }

   @Override
   public ResteasyProviderFactory register(Object component, int priority)
   {
      return getDelegate().register(component, priority);
   }

   @Override
   public void registerProvider(Class provider, boolean isBuiltin)
   {
      getDelegate().registerProvider(provider, isBuiltin);
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      return getDelegate().getPropertyNames();
   }

   @Override
   public ResteasyProviderFactory register(Object provider)
   {
      return getDelegate().register(provider);
   }

   @Override
   public <T> T createProviderInstance(Class<? extends T> clazz)
   {
      return getDelegate().createProviderInstance(clazz);
   }

   @Override
   public boolean isRegistered(Class<?> componentClass)
   {
      return getDelegate().isRegistered(componentClass);
   }

   @Override
   public <T> T createEndpoint(Application applicationConfig, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException
   {
      return getDelegate().createEndpoint(applicationConfig, endpointType);
   }

   @Override
   public Map<String, Object> getMutableProperties()
   {
      return getDelegate().getMutableProperties();
   }

   @Override
   public Set<DynamicFeature> getServerDynamicFeatures()
   {
      return getDelegate().getServerDynamicFeatures();
   }

   @Override
   public boolean isEnabled(Feature feature)
   {
      return getDelegate().isEnabled(feature);
   }

   @Override
   public Object getProperty(String name)
   {
      return getDelegate().getProperty(name);
   }

   @Override
   public WriterInterceptorRegistry getServerWriterInterceptorRegistry()
   {
      return getDelegate().getServerWriterInterceptorRegistry();
   }

   @Override
   public ResteasyProviderFactory setProperties(Map<String, ?> properties)
   {
      return getDelegate().setProperties(properties);
   }

   @Override
   public UriBuilder createUriBuilder()
   {
      return getDelegate().createUriBuilder();
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, Class<?>... contracts)
   {
      return getDelegate().register(componentClass, contracts);
   }

   @Override
   public <T> T injectedInstance(Class<? extends T> clazz)
   {
      return getDelegate().injectedInstance(clazz);
   }

   @Override
   public RuntimeType getRuntimeType()
   {
      return getDelegate().getRuntimeType();
   }

   @Override
   public void injectProperties(Object obj)
   {
      getDelegate().injectProperties(obj);
   }

   @Override
   public ResteasyProviderFactory property(String name, Object value)
   {
      return getDelegate().property(name, value);
   }

   @Override
   public WriterInterceptorRegistry getClientWriterInterceptorRegistry()
   {
      return getDelegate().getClientWriterInterceptorRegistry();
   }

   @Override
   public InjectorFactory getInjectorFactory()
   {
      return getDelegate().getInjectorFactory();
   }

   @Override
   public Map<Class<?>, Integer> getContracts(Class<?> componentClass)
   {
      return getDelegate().getContracts(componentClass);
   }

   @Override
   public ParamConverter getParamConverter(Class clazz, Type genericType, Annotation[] annotations)
   {
      return getDelegate().getParamConverter(clazz, genericType, annotations);
   }

   @Override
   public ClientResponseFilterRegistry getClientResponseFilters()
   {
      return getDelegate().getClientResponseFilters();
   }

   @Override
   public ResteasyProviderFactory register(Object component, Class<?>... contracts)
   {
      return getDelegate().register(component, contracts);
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return getDelegate().getClasses();
   }

   @Override
   public boolean isEnabled(Class<? extends Feature> featureClass)
   {
      return getDelegate().isEnabled(featureClass);
   }

   @Override
   public void registerProvider(Class provider)
   {
      getDelegate().registerProvider(provider);
   }

   @Override
   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      getDelegate().addHeaderDelegate(clazz, header);
   }

   @Override
   public void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts, Integer defaultPriority, boolean builtIn)
   {
      getDelegate().registerProviderInstance(provider, contracts, defaultPriority, builtIn);
   }

   @Override
   public Set<Class<?>> getProviderClasses()
   {
      return getDelegate().getProviderClasses();
   }

   @Override
   public String toString(Object object, Class clazz, Type genericType, Annotation[] annotations)
   {
      return getDelegate().toString(object, clazz, genericType, annotations);
   }

   @Override
   public ClientRequestFilterRegistry getClientRequestFilterRegistry()
   {
      return getDelegate().getClientRequestFilterRegistry();
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return getDelegate().getProperties();
   }

   @Override
   public String toHeaderString(Object object)
   {
      return getDelegate().toHeaderString(object);
   }

   @Override
   public Link.Builder createLinkBuilder()
   {
      return getDelegate().createLinkBuilder();
   }

   @Override
   public Set<Object> getProviderInstances()
   {
      return getDelegate().getProviderInstances();
   }

   @Override
   public Configuration getConfiguration()
   {
      return getDelegate().getConfiguration();
   }

   @Override
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MessageBodyWriter<T> writer = getDelegate().getMessageBodyWriter(type, genericType, annotations, mediaType);
      if (writer!=null)
          LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
      return writer;
   }

   @Override
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return getDelegate().getExceptionMapper(type);
   }

   @Override
   public Map<Class<?>, ExceptionMapper> getExceptionMappers()
   {
      return getDelegate().getExceptionMappers();
   }

   @Override
   public <T> AsyncResponseProvider<T> getAsyncResponseProvider(Class<T> type)
   {
      return getDelegate().getAsyncResponseProvider(type);
   }
   
   @Override
   public Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders()
   {
      return getDelegate().getAsyncResponseProviders();
   }

   @Override
   public <T> AsyncStreamProvider<T> getAsyncStreamProvider(Class<T> type)
   {
      return getDelegate().getAsyncStreamProvider(type);
   }
   
   @Override
   public Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders()
   {
      return getDelegate().getAsyncStreamProviders();
   }

   @Override
   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass)
   {
      return getDelegate().createHeaderDelegate(tClass);
   }

   @Override
   public <T> T getContextData(Class<T> rawType, Type genericType, Annotation[] annotations, boolean unwrapAsync)
   {
      return getDelegate().getContextData(rawType, genericType, annotations, unwrapAsync);
   }
   
   @Override
   public Map<Type, ContextInjector> getContextInjectors()
   {
      return getDelegate().getContextInjectors();
   }

   @Override
   public Map<Type, ContextInjector> getAsyncContextInjectors()
   {
      return getDelegate().getAsyncContextInjectors();
   }

   @Override
   public <T> MessageBodyWriter<T> getClientMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MessageBodyWriter<T> writer = getDelegate().getClientMessageBodyWriter(type, genericType, annotations, mediaType);
      if (writer!=null)
          LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
      return writer;
   }

   @Override
   public <T> MessageBodyReader<T> getClientMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      MessageBodyReader<T> reader = getDelegate().getClientMessageBodyReader(type, genericType, annotations, mediaType);
      if (reader!=null)
          LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());
      return reader;
   }

}
