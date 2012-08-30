package org.jboss.resteasy.core;

import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.core.interception.ContainerRequestFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.WriterInterceptorRegistry;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.ThreadLocalStack;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

   public ResteasyProviderFactory getDelegate()
   {
      ResteasyProviderFactory factory = delegate.get();
      if (factory == null) return defaultFactory;
      return factory;
   }



   @Override
   public Set<Class<?>> getProviderClasses()
   {
      return getDelegate().getProviderClasses();
   }

   @Override
   public Set<Object> getProviderInstances()
   {
      return getDelegate().getProviderInstances();
   }

   @Override
   public ResteasyProviderFactory getParent()
   {
      return getDelegate().getParent();
   }

   @Override
   public String toString(Object object)
   {
      return getDelegate().toString(object);
   }

   @Override
   public String toHeaderString(Object object)
   {
      return getDelegate().toHeaderString(object);
   }

   @Override
   public JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilters()
   {
      return getDelegate().getClientRequestFilters();
   }

   @Override
   public JaxrsInterceptorRegistry<ClientResponseFilter> getClientResponseFilters()
   {
      return getDelegate().getClientResponseFilters();
   }

   @Override
   public boolean isRegisterBuiltins()
   {
      return getDelegate().isRegisterBuiltins();
   }

   @Override
   public void setRegisterBuiltins(boolean registerBuiltins)
   {
      getDelegate().setRegisterBuiltins(registerBuiltins);
   }

   @Override
   public InjectorFactory getInjectorFactory()
   {
      return getDelegate().getInjectorFactory();
   }

   @Override
   public void setInjectorFactory(InjectorFactory injectorFactory)
   {
      getDelegate().setInjectorFactory(injectorFactory);
   }

   @Override
   public void injectProperties(Object o)
   {
      getDelegate().injectProperties(o);
   }

   @Override
   public Map<String, Object> getMutableProperties()
   {
      return getDelegate().getMutableProperties();
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return getDelegate().getProperties();
   }

   @Override
   public Object getProperty(String name)
   {
      return getDelegate().getProperty(name);
   }

   @Override
   public Configurable setProperties(Map<String, ?> properties)
   {
      return getDelegate().setProperties(properties);
   }

   @Override
   public Configurable setProperty(String name, Object value)
   {
      return getDelegate().setProperty(name, value);
   }

   @Override
   public Collection<Feature> getFeatures()
   {
      return getDelegate().getFeatures();
   }

   @Override
   public Configurable register(Class<?> providerClass)
   {
      return getDelegate().register(providerClass);
   }

   @Override
   public Configurable register(Class<?> providerClass, int bindingPriority)
   {
      return getDelegate().register(providerClass, bindingPriority);
   }

   @Override
   public <T> Configurable register(Class<T> providerClass, Class<? super T>... contracts)
   {
      return getDelegate().register(providerClass, contracts);
   }

   @Override
   public <T> Configurable register(Class<T> providerClass, int bindingPriority, Class<? super T>... contracts)
   {
      return getDelegate().register(providerClass, bindingPriority, contracts);
   }

   @Override
   public Configurable register(Object provider)
   {
      return getDelegate().register(provider);
   }

   @Override
   public Configurable register(Object provider, int bindingPriority)
   {
      return getDelegate().register(provider, bindingPriority);
   }

   @Override
   public <T> Configurable register(Object provider, Class<? super T>... contracts)
   {
      return getDelegate().register(provider, contracts);
   }

   @Override
   public <T> Configurable register(Object provider, int bindingPriority, Class<? super T>... contracts)
   {
      return getDelegate().register(provider, bindingPriority, contracts);
   }

   @Override
   public void addStringParameterUnmarshaller(Class<? extends StringParameterUnmarshaller> resolver)
   {
      getDelegate().addStringParameterUnmarshaller(resolver);
   }

   @Override
   public void addClientErrorInterceptor(ClientErrorInterceptor handler)
   {
      getDelegate().addClientErrorInterceptor(handler);
   }

   @Override
   public List<ClientErrorInterceptor> getClientErrorInterceptors()
   {
      return getDelegate().getClientErrorInterceptors();
   }

   @Override
   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      return getDelegate().createStringParameterUnmarshaller(clazz);
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
   public InterceptorRegistry<ClientExecutionInterceptor> getClientExecutionInterceptorRegistry()
   {
      return getDelegate().getClientExecutionInterceptorRegistry();
   }

   @Override
   public ReaderInterceptorRegistry getServerReaderInterceptorRegistry()
   {
      return getDelegate().getServerReaderInterceptorRegistry();
   }

   @Override
   public WriterInterceptorRegistry getServerWriterInterceptorRegistry()
   {
      return getDelegate().getServerWriterInterceptorRegistry();
   }

   @Override
   public ContainerRequestFilterRegistry getContainerRequestFilterRegistry()
   {
      return getDelegate().getContainerRequestFilterRegistry();
   }

   @Override
   public ContainerResponseFilterRegistry getContainerResponseFilterRegistry()
   {
      return getDelegate().getContainerResponseFilterRegistry();
   }

   @Override
   public ReaderInterceptorRegistry getClientReaderInterceptorRegistry()
   {
      return getDelegate().getClientReaderInterceptorRegistry();
   }

   @Override
   public WriterInterceptorRegistry getClientWriterInterceptorRegistry()
   {
      return getDelegate().getClientWriterInterceptorRegistry();
   }

   @Override
   protected void initialize()
   {

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
   public UriBuilder createUriBuilder()
   {
      return getDelegate().createUriBuilder();
   }

   @Override
   public Response.ResponseBuilder createResponseBuilder()
   {
      return getDelegate().createResponseBuilder();
   }

   @Override
   public Variant.VariantListBuilder createVariantListBuilder()
   {
      return getDelegate().createVariantListBuilder();
   }

   @Override
   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass)
   {
      return getDelegate().createHeaderDelegate(tClass);
   }

   @Override
   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      getDelegate().addHeaderDelegate(clazz, header);
   }

   @Override
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   public List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type)
   {
      return getDelegate().getContextResolvers(clazz, type);
   }

   @Override
   public StringConverter getStringConverter(Class<?> clazz)
   {
      return getDelegate().getStringConverter(clazz);
   }

   @Override
   public void registerProvider(Class provider)
   {
      getDelegate().registerProvider(provider);
   }

   @Override
   public void registerProviderInstance(Object provider)
   {
      getDelegate().registerProviderInstance(provider);
   }

   @Override
   public void registerProviderInstance(Object provider, int bindingPriority, Class<?>... contracts)
   {
      getDelegate().registerProviderInstance(provider, bindingPriority, contracts);
   }

   @Override
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return getDelegate().getExceptionMapper(type);
   }
   
   @Override
   public <T extends Throwable> ClientExceptionMapper<T> getClientExceptionMapper(Class<T> type)
   {
      return getDelegate().getClientExceptionMapper(type);
   }

   @Override
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   @Override
   public <T> T createEndpoint(Application applicationConfig, Class<T> endpointType)
           throws IllegalArgumentException, UnsupportedOperationException
   {
      return getDelegate().createEndpoint(applicationConfig, endpointType);
   }

   @Override
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      return getDelegate().getContextResolver(contextType, mediaType);
   }

   @Override
   public <T> T createProviderInstance(Class<? extends T> clazz)
   {
      return getDelegate().createProviderInstance(clazz);
   }

   @Override
   public <T> ConstructorInjector createConstructorInjector(Class<? extends T> clazz)
   {
      return getDelegate().createConstructorInjector(clazz);
   }

   @Override
   public <T> T injectedInstance(Class<? extends T> clazz)
   {
      return getDelegate().injectedInstance(clazz);
   }

   @Override
   public void injectProperties(Class declaring, Object obj)
   {
      getDelegate().injectProperties(declaring, obj);
   }

   @Override
   public void registerProvider(Class provider, boolean isBuiltin)
   {
      getDelegate().registerProvider(provider, isBuiltin);
   }

   @Override
   public void registerProvider(Class provider, boolean isBuiltin, int bindingPriority, Class<?>... contracts)
   {
      getDelegate().registerProvider(provider, isBuiltin, bindingPriority, contracts);
   }

   @Override
   public void appendInterceptorPrecedence(String precedence)
   {
      getDelegate().appendInterceptorPrecedence(precedence);
   }

   @Override
   public void insertInterceptorPrecedenceAfter(String after, String newPrecedence)
   {
      getDelegate().insertInterceptorPrecedenceAfter(after, newPrecedence);
   }

   @Override
   public void insertInterceptorPrecedenceBefore(String before, String newPrecedence)
   {
      getDelegate().insertInterceptorPrecedenceBefore(before, newPrecedence);
   }

   @Override
   public Set<DynamicFeature> getServerDynamicFeatures()
   {
      return getDelegate().getServerDynamicFeatures();
   }

   @Override
   public void addClientExceptionMapper(Class<? extends ClientExceptionMapper<?>> providerClass)
   {
      getDelegate().addClientExceptionMapper(providerClass);
   }

   @Override
   public void addClientExceptionMapper(ClientExceptionMapper<?> provider)
   {
      getDelegate().addClientExceptionMapper(provider);
   }

   @Override
   public void addClientExceptionMapper(ClientExceptionMapper<?> provider, Class<?> providerClass)
   {
      getDelegate().addClientExceptionMapper(provider, providerClass);
   }

   @Override
   public void addClientExceptionMapper(ClientExceptionMapper<?> provider, Type exceptionType)
   {
      getDelegate().addClientExceptionMapper(provider, exceptionType);
   }
}
