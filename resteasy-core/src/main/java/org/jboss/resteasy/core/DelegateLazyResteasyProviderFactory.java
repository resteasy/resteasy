package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
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
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.ContextInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.LazyResteasyProviderFactory;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;

@SuppressWarnings("rawtypes")
public class DelegateLazyResteasyProviderFactory extends ResteasyProviderFactory implements ProviderFactoryDelegate, LazyResteasyProviderFactory
{
   private boolean referenceMode = true;
   private ResteasyProviderFactory delegate;
   private final RuntimeType runtimeType;

   public DelegateLazyResteasyProviderFactory(final ResteasyProviderFactory delegate)
   {
      this(delegate, null);
   }

   public DelegateLazyResteasyProviderFactory(final ResteasyProviderFactory delegate, final RuntimeType runtimeType)
   {
      this.delegate = delegate;
      this.delegate.registerListener(this);
      this.runtimeType = runtimeType;
   }

   public synchronized ResteasyProviderFactory getDelegate()
   {
      return delegate;
   }

   @Override
   public synchronized void onChange()
   {
      if (referenceMode)
      {
         ResteasyProviderFactory parent = delegate;
         while (parent instanceof DelegateLazyResteasyProviderFactory) {
            parent = ((ProviderFactoryDelegate)parent).getDelegate();
         }
         this.delegate = runtimeType == null ? new ResteasyProviderFactoryImpl(parent, true) : new ResteasyProviderFactoryImpl(parent, true) {
            @Override
            public RuntimeType getRuntimeType()
            {
               return runtimeType;
            }
         };
         referenceMode = false;
      }
   }

   @Override
   public MediaType getConcreteMediaTypeFromMessageBodyWriters(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      //return super.getConcreteMediaTypeFromMessageBodyWriters(type, genericType, annotations, mediaType);
      return getDelegate().getConcreteMediaTypeFromMessageBodyWriters(type, genericType, annotations, mediaType);
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

   @Override
   public JaxrsInterceptorRegistry<ContainerResponseFilter> getContainerResponseFilterRegistry()
   {
      return getDelegate().getContainerResponseFilterRegistry();
   }

   @Override
   public JaxrsInterceptorRegistry<ReaderInterceptor> getServerReaderInterceptorRegistry()
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
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().setBuiltinsRegistered(builtinsRegistered);
   }

   @Override
   public ResteasyProviderFactory register(Class<?> providerClass)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(providerClass);
      return this;
   }

   @Override
   public Set<DynamicFeature> getClientDynamicFeatures()
   {
      return getDelegate().getClientDynamicFeatures();
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(componentClass, contracts);
      return this;
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
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(componentClass, priority);
      return this;
   }

   @Override
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      return getDelegate().getContextResolver(contextType, mediaType);
   }

   @Override
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().registerProvider(provider, priorityOverride, isBuiltin, contracts);
   }

   @Override
   public JaxrsInterceptorRegistry<ContainerRequestFilter> getContainerRequestFilterRegistry()
   {
      return getDelegate().getContainerRequestFilterRegistry();
   }

   @Override
   public ResteasyProviderFactory register(Object component, Map<Class<?>, Integer> contracts)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(component, contracts);
      return this;
   }

   @Override
   public boolean isRegisterBuiltins()
   {
      return getDelegate().isRegisterBuiltins();
   }

   @Override
   public JaxrsInterceptorRegistry<ReaderInterceptor> getClientReaderInterceptorRegistry()
   {
      return getDelegate().getClientReaderInterceptorRegistry();
   }

   @Override
   public void setRegisterBuiltins(boolean registerBuiltins)
   {
      notifyListenersAndCleanUp(); //TODO maybe check if the new value for registerBuiltins is different from current one before notifying
      onChange();
      getDelegate().setRegisterBuiltins(registerBuiltins);
   }

   @Override
   public ResteasyProviderFactory register(Object component, int priority)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(component, priority);
      return this;
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
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(provider);
      return this;
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
      notifyListenersAndCleanUp();
      onChange();
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
   public JaxrsInterceptorRegistry<WriterInterceptor> getServerWriterInterceptorRegistry()
   {
      return getDelegate().getServerWriterInterceptorRegistry();
   }

   @Override
   public ResteasyProviderFactory setProperties(Map<String, ?> properties)
   {
      if (properties != null && !properties.isEmpty()) {
         notifyListenersAndCleanUp();
         onChange();
         getDelegate().setProperties(properties);
      }
      return this;
   }

   @Override
   public UriBuilder createUriBuilder()
   {
      return getDelegate().createUriBuilder();
   }

   @Override
   public ResteasyProviderFactory register(Class<?> componentClass, Class<?>... contracts)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(componentClass, contracts);
      return this;
   }

   @Override
   public <T> T injectedInstance(Class<? extends T> clazz)
   {
      return getDelegate().injectedInstance(clazz);
   }

   @Override
   public RuntimeType getRuntimeType()
   {
      return runtimeType != null ? runtimeType : getDelegate().getRuntimeType();
   }

   @Override
   public void injectProperties(Object obj)
   {
      getDelegate().injectProperties(obj);
   }

   @Override
   public ResteasyProviderFactory property(String name, Object value)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().property(name, value);
      return this;
   }

   @Override
   public JaxrsInterceptorRegistry<WriterInterceptor> getClientWriterInterceptorRegistry()
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
   public JaxrsInterceptorRegistry<ClientResponseFilter> getClientResponseFilters()
   {
      return getDelegate().getClientResponseFilters();
   }

   @Override
   public ResteasyProviderFactory register(Object component, Class<?>... contracts)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().register(component, contracts);
      return this;
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
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().registerProvider(provider);
   }

   @Override
   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      notifyListenersAndCleanUp();
      onChange();
      getDelegate().addHeaderDelegate(clazz, header);
   }

   @Override
   public void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts, Integer defaultPriority, boolean builtIn)
   {
      notifyListenersAndCleanUp();
      onChange();
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
   public JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilterRegistry()
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
      return this;
   }

   @Override
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   @Override
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return getDelegate().getExceptionMapper(type);
   }

//   @Override
   //TODO!!
   public Map<Class<?>, ExceptionMapper> getExceptionMappers()
   {
      return ((ResteasyProviderFactoryImpl)getDelegate()).getExceptionMappers();
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
      return getDelegate().getClientMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   @Override
   public <T> MessageBodyReader<T> getClientMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getClientMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   public Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders()
   {
      return getDelegate().getAsyncClientResponseProviders();
   }

   @Override
   public <T> T getContextData(Class<T> type)
   {
      return getDelegate().getContextData(type);
   }

   @Override
   public <T> AsyncClientResponseProvider<T> getAsyncClientResponseProvider(Class<T> type)
   {
      return getDelegate().getAsyncClientResponseProvider(type);
   }

   @Override
   public Map<MessageBodyWriter<?>, Class<?>> getPossibleMessageBodyWritersMap(Class type, Type genericType,
         Annotation[] annotations, MediaType accept)
   {
      return getDelegate().getPossibleMessageBodyWritersMap(type, genericType, annotations, accept);
   }

   @Override
   public <I extends RxInvoker> RxInvokerProvider<I> getRxInvokerProvider(Class<I> clazz)
   {
      return getDelegate().getRxInvokerProvider(clazz);
   }

   @Override
   public RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz)
   {
      return getDelegate().getRxInvokerProviderFromReactiveClass(clazz);
   }

   @Override
   public boolean isReactive(Class<?> clazz)
   {
      return getDelegate().isReactive(clazz);
   }

   @Override
   public ResourceBuilder getResourceBuilder()
   {
      return getDelegate().getResourceBuilder();
   }

   @Override
   protected void registerBuiltin()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   @Deprecated
   public <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
   {
      return getDelegate().getServerMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   @Deprecated
   public <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
   {
      return getDelegate().getServerMessageBodyWriter(type, genericType, annotations, mediaType);
   }

}
