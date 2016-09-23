package org.jboss.resteasy.core;

import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.core.interception.ClientResponseFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerRequestFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.WriterInterceptorRegistry;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.ThreadLocalStack;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
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
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.Logger.Level;

/**
 * Allow applications to push/pop provider factories onto the stack
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ThreadLocalResteasyProviderFactory extends ResteasyProviderFactory implements ProviderFactoryDelegate
{
   private static final ThreadLocalStack<ResteasyProviderFactory> delegate = new ThreadLocalStack<ResteasyProviderFactory>();

   private ResteasyProviderFactory defaultFactory;


   public ThreadLocalResteasyProviderFactory(ResteasyProviderFactory defaultFactory)
   {
      this.defaultFactory = defaultFactory;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getDelegate .")
   public ResteasyProviderFactory getDelegate()
   {
      ResteasyProviderFactory factory = delegate.get();
      if (factory == null) return defaultFactory;
      return factory;
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : initialize .")
   protected void initialize()
   {

   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getConcreteMediaTypeFromMessageBodyWriters .")
   public MediaType getConcreteMediaTypeFromMessageBodyWriters(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return super.getConcreteMediaTypeFromMessageBodyWriters(type, genericType, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getHeaderDelegate .")
   public HeaderDelegate getHeaderDelegate(Class<?> aClass)
   {
      return getDelegate().getHeaderDelegate(aClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : injectedInstance .")
   public <T> T injectedInstance(Class<? extends T> clazz, HttpRequest request, HttpResponse response)
   {
      return getDelegate().injectedInstance(clazz, request, response);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : injectProperties .")
   public void injectProperties(Object obj, HttpRequest request, HttpResponse response)
   {
      getDelegate().injectProperties(obj, request, response);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : push .")
   public static void push(ResteasyProviderFactory factory)
   {
      delegate.push(factory);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : pop .")
   public static void pop()
   {
      delegate.pop();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getContainerResponseFilterRegistry .")
   public ContainerResponseFilterRegistry getContainerResponseFilterRegistry()
   {
      return getDelegate().getContainerResponseFilterRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getServerReaderInterceptorRegistry .")
   public ReaderInterceptorRegistry getServerReaderInterceptorRegistry()
   {
      return getDelegate().getServerReaderInterceptorRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createVariantListBuilder .")
   public Variant.VariantListBuilder createVariantListBuilder()
   {
      return getDelegate().createVariantListBuilder();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getContextResolvers .")
   public List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type)
   {
      return getDelegate().getContextResolvers(clazz, type);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : isBuiltinsRegistered .")
   public boolean isBuiltinsRegistered()
   {
      return getDelegate().isBuiltinsRegistered();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientExceptionMapper .")
   public <T extends Throwable> ClientExceptionMapper<T> getClientExceptionMapper(Class<T> type)
   {
      return getDelegate().getClientExceptionMapper(type);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getFeatureClasses .")
   public Set<Class<?>> getFeatureClasses()
   {
      return getDelegate().getFeatureClasses();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : setBuiltinsRegistered .")
   public void setBuiltinsRegistered(boolean builtinsRegistered)
   {
      getDelegate().setBuiltinsRegistered(builtinsRegistered);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Class<?> providerClass)
   {
      return getDelegate().register(providerClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientDynamicFeatures .")
   public Set<DynamicFeature> getClientDynamicFeatures()
   {
      return getDelegate().getClientDynamicFeatures();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addClientExceptionMapper .")
   public void addClientExceptionMapper(Class<? extends ClientExceptionMapper<?>> providerClass)
   {
      getDelegate().addClientExceptionMapper(providerClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      return getDelegate().register(componentClass, contracts);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getEnabledFeatures .")
   public Collection<Feature> getEnabledFeatures()
   {
      return getDelegate().getEnabledFeatures();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createResponseBuilder .")
   public Response.ResponseBuilder createResponseBuilder()
   {
      return getDelegate().createResponseBuilder();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : registerProviderInstance .")
   public void registerProviderInstance(Object provider)
   {
      getDelegate().registerProviderInstance(provider);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addClientExceptionMapper .")
   public void addClientExceptionMapper(ClientExceptionMapper<?> provider, Type exceptionType)
   {
      getDelegate().addClientExceptionMapper(provider, exceptionType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getStringConverter .")
   public StringConverter getStringConverter(Class<?> clazz)
   {
      return getDelegate().getStringConverter(clazz);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createStringParameterUnmarshaller .")
   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      return getDelegate().createStringParameterUnmarshaller(clazz);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getFeatureInstances .")
   public Set<Object> getFeatureInstances()
   {
      return getDelegate().getFeatureInstances();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addClientExceptionMapper .")
   public void addClientExceptionMapper(ClientExceptionMapper<?> provider)
   {
      getDelegate().addClientExceptionMapper(provider);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : setInjectorFactory .")
   public void setInjectorFactory(InjectorFactory injectorFactory)
   {
      getDelegate().setInjectorFactory(injectorFactory);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getInstances .")
   public Set<Object> getInstances()
   {
      return getDelegate().getInstances();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : isRegistered .")
   public boolean isRegistered(Object component)
   {
      return getDelegate().isRegistered(component);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Class<?> componentClass, int priority)
   {
      return getDelegate().register(componentClass, priority);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getContextResolver .")
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      return getDelegate().getContextResolver(contextType, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientExecutionInterceptorRegistry .")
   public InterceptorRegistry<ClientExecutionInterceptor> getClientExecutionInterceptorRegistry()
   {
      return getDelegate().getClientExecutionInterceptorRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getMessageBodyReader .")
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addClientErrorInterceptor .")
   public void addClientErrorInterceptor(ClientErrorInterceptor handler)
   {
      getDelegate().addClientErrorInterceptor(handler);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : registerProvider .")
   public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts)
   {
      getDelegate().registerProvider(provider, priorityOverride, isBuiltin, contracts);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClassContracts .")
   public Map<Class<?>, Map<Class<?>, Integer>> getClassContracts()
   {
      return getDelegate().getClassContracts();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getContainerRequestFilterRegistry .")
   public ContainerRequestFilterRegistry getContainerRequestFilterRegistry()
   {
      return getDelegate().getContainerRequestFilterRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Object component, Map<Class<?>, Integer> contracts)
   {
      return getDelegate().register(component, contracts);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : isRegisterBuiltins .")
   public boolean isRegisterBuiltins()
   {
      return getDelegate().isRegisterBuiltins();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientReaderInterceptorRegistry .")
   public ReaderInterceptorRegistry getClientReaderInterceptorRegistry()
   {
      return getDelegate().getClientReaderInterceptorRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : setRegisterBuiltins .")
   public void setRegisterBuiltins(boolean registerBuiltins)
   {
      getDelegate().setRegisterBuiltins(registerBuiltins);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Object component, int priority)
   {
      return getDelegate().register(component, priority);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : registerProvider .")
   public void registerProvider(Class provider, boolean isBuiltin)
   {
      getDelegate().registerProvider(provider, isBuiltin);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getPropertyNames .")
   public Collection<String> getPropertyNames()
   {
      return getDelegate().getPropertyNames();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addClientExceptionMapper .")
   public void addClientExceptionMapper(ClientExceptionMapper<?> provider, Class<?> providerClass)
   {
      getDelegate().addClientExceptionMapper(provider, providerClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : insertInterceptorPrecedenceAfter .")
   public void insertInterceptorPrecedenceAfter(String after, String newPrecedence)
   {
      getDelegate().insertInterceptorPrecedenceAfter(after, newPrecedence);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Object provider)
   {
      return getDelegate().register(provider);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createConstructorInjector .")
   public <T> ConstructorInjector createConstructorInjector(Class<? extends T> clazz)
   {
      return getDelegate().createConstructorInjector(clazz);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createProviderInstance .")
   public <T> T createProviderInstance(Class<? extends T> clazz)
   {
      return getDelegate().createProviderInstance(clazz);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : isRegistered .")
   public boolean isRegistered(Class<?> componentClass)
   {
      return getDelegate().isRegistered(componentClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : insertInterceptorPrecedenceBefore .")
   public void insertInterceptorPrecedenceBefore(String before, String newPrecedence)
   {
      getDelegate().insertInterceptorPrecedenceBefore(before, newPrecedence);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createEndpoint .")
   public <T> T createEndpoint(Application applicationConfig, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException
   {
      return getDelegate().createEndpoint(applicationConfig, endpointType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getMutableProperties .")
   public Map<String, Object> getMutableProperties()
   {
      return getDelegate().getMutableProperties();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getServerDynamicFeatures .")
   public Set<DynamicFeature> getServerDynamicFeatures()
   {
      return getDelegate().getServerDynamicFeatures();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : isEnabled .")
   public boolean isEnabled(Feature feature)
   {
      return getDelegate().isEnabled(feature);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getProperty .")
   public Object getProperty(String name)
   {
      return getDelegate().getProperty(name);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getServerWriterInterceptorRegistry .")
   public WriterInterceptorRegistry getServerWriterInterceptorRegistry()
   {
      return getDelegate().getServerWriterInterceptorRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : setProperties .")
   public ResteasyProviderFactory setProperties(Map<String, ?> properties)
   {
      return getDelegate().setProperties(properties);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientErrorInterceptors .")
   public List<ClientErrorInterceptor> getClientErrorInterceptors()
   {
      return getDelegate().getClientErrorInterceptors();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : injectProperties .")
   public void injectProperties(Class declaring, Object obj)
   {
      getDelegate().injectProperties(declaring, obj);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createUriBuilder .")
   public UriBuilder createUriBuilder()
   {
      return getDelegate().createUriBuilder();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Class<?> componentClass, Class<?>... contracts)
   {
      return getDelegate().register(componentClass, contracts);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : injectedInstance .")
   public <T> T injectedInstance(Class<? extends T> clazz)
   {
      return getDelegate().injectedInstance(clazz);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : appendInterceptorPrecedence .")
   public void appendInterceptorPrecedence(String precedence)
   {
      getDelegate().appendInterceptorPrecedence(precedence);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getParent .")
   public ResteasyProviderFactory getParent()
   {
      return getDelegate().getParent();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getRuntimeType .")
   public RuntimeType getRuntimeType()
   {
      return getDelegate().getRuntimeType();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : injectProperties .")
   public void injectProperties(Object obj)
   {
      getDelegate().injectProperties(obj);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : property .")
   public ResteasyProviderFactory property(String name, Object value)
   {
      return getDelegate().property(name, value);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientWriterInterceptorRegistry .")
   public WriterInterceptorRegistry getClientWriterInterceptorRegistry()
   {
      return getDelegate().getClientWriterInterceptorRegistry();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getInjectorFactory .")
   public InjectorFactory getInjectorFactory()
   {
      return getDelegate().getInjectorFactory();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getContracts .")
   public Map<Class<?>, Integer> getContracts(Class<?> componentClass)
   {
      return getDelegate().getContracts(componentClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getParamConverter .")
   public ParamConverter getParamConverter(Class clazz, Type genericType, Annotation[] annotations)
   {
      return getDelegate().getParamConverter(clazz, genericType, annotations);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientResponseFilters .")
   public ClientResponseFilterRegistry getClientResponseFilters()
   {
      return getDelegate().getClientResponseFilters();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : register .")
   public ResteasyProviderFactory register(Object component, Class<?>... contracts)
   {
      return getDelegate().register(component, contracts);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClasses .")
   public Set<Class<?>> getClasses()
   {
      return getDelegate().getClasses();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : isEnabled .")
   public boolean isEnabled(Class<? extends Feature> featureClass)
   {
      return getDelegate().isEnabled(featureClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : registerProvider .")
   public void registerProvider(Class provider)
   {
      getDelegate().registerProvider(provider);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addHeaderDelegate .")
   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      getDelegate().addHeaderDelegate(clazz, header);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : registerProviderInstance .")
   public void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts, Integer defaultPriority, boolean builtIn)
   {
      getDelegate().registerProviderInstance(provider, contracts, defaultPriority, builtIn);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : addStringParameterUnmarshaller .")
   public void addStringParameterUnmarshaller(Class<? extends StringParameterUnmarshaller> provider)
   {
      getDelegate().addStringParameterUnmarshaller(provider);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getProviderClasses .")
   public Set<Class<?>> getProviderClasses()
   {
      return getDelegate().getProviderClasses();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : toString .")
   public String toString(Object object, Class clazz, Type genericType, Annotation[] annotations)
   {
      return getDelegate().toString(object, clazz, genericType, annotations);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientRequestFilters .")
   public JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilters()
   {
      return getDelegate().getClientRequestFilters();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getProperties .")
   public Map<String, Object> getProperties()
   {
      return getDelegate().getProperties();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : toHeaderString .")
   public String toHeaderString(Object object)
   {
      return getDelegate().toHeaderString(object);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createLinkBuilder .")
   public Link.Builder createLinkBuilder()
   {
      return getDelegate().createLinkBuilder();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getProviderInstances .")
   public Set<Object> getProviderInstances()
   {
      return getDelegate().getProviderInstances();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getConfiguration .")
   public Configuration getConfiguration()
   {
      return getDelegate().getConfiguration();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getMessageBodyWriter .")
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getExceptionMapper .")
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return getDelegate().getExceptionMapper(type);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getExceptionMappers .")
   public Map<Class<?>, ExceptionMapper> getExceptionMappers()
   {
      return getDelegate().getExceptionMappers();
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : createHeaderDelegate .")
   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass)
   {
      return getDelegate().createHeaderDelegate(tClass);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientMessageBodyWriter .")
   public <T> MessageBodyWriter<T> getClientMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getClientMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getClientMessageBodyReader .")
   public <T> MessageBodyReader<T> getClientMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getClientMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getServerMessageBodyReader .")
   public <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getServerMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider Factory : org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory , method call : getServerMessageBodyWriter .")
   public <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getServerMessageBodyWriter(type, genericType, annotations, mediaType);
   }
}
