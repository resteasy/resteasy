package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
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
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.statistics.StatisticsController;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings({ "rawtypes" })
public abstract class ResteasyProviderFactory extends RuntimeDelegate
        implements Providers, HeaderValueProcessor, Configurable<ResteasyProviderFactory>, Configuration {
    private static volatile ResteasyProviderFactory instance;

    private static boolean registerBuiltinByDefault = true;

    public abstract Set<DynamicFeature> getServerDynamicFeatures();

    public abstract Set<DynamicFeature> getClientDynamicFeatures();

    public abstract Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders();

    public abstract Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders();

    public abstract Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders();

    public abstract Map<Type, ContextInjector> getContextInjectors();

    public abstract Map<Type, ContextInjector> getAsyncContextInjectors();

    public abstract Set<Class<?>> getProviderClasses();

    public abstract Set<Object> getProviderInstances();

    public abstract <T> T getContextData(Class<T> type);

    public abstract <T> T getContextData(Class<T> rawType, Type genericType, Annotation[] annotations,
            boolean unwrapAsync);

    /**
     * Will not initialize singleton if not set.
     *
     * @return provider factory singleton
     */
    public static ResteasyProviderFactory peekInstance() {
        return instance;
    }

    public static synchronized void clearInstanceIfEqual(ResteasyProviderFactory factory) {
        if (instance == factory) {
            instance = null;
            RuntimeDelegate.setInstance(null);
        }
    }

    public static synchronized void setInstance(ResteasyProviderFactory factory) {
        synchronized (RD_LOCK) {
            instance = factory;
        }
        RuntimeDelegate.setInstance(factory);
    }

    static final Object RD_LOCK = new Object();

    /**
     * Initializes ResteasyProviderFactory singleton if not set.
     *
     * @return singleton provider factory
     */
    public static ResteasyProviderFactory getInstance() {
        ResteasyProviderFactory result = instance;
        if (result == null) { // First check (no locking)
            synchronized (RD_LOCK) {
                result = instance;
                if (result == null) { // Second check (with locking)
                    RuntimeDelegate runtimeDelegate = RuntimeDelegate.getInstance();
                    if (runtimeDelegate instanceof ResteasyProviderFactory) {
                        instance = result = (ResteasyProviderFactory) runtimeDelegate;
                    } else {
                        instance = result = newInstance(); //TODO use reflection directly, to avoid circular dependency
                    }
                    if (registerBuiltinByDefault)
                        result.registerBuiltin();
                }
            }
        }
        return result;
    }

    public static ResteasyProviderFactory newInstance() {
        //TODO implement this differently: call getInstance(), retrieve the class, classloader, constructor from it, store locally in singletons, use those starting from now.
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            return (ResteasyProviderFactory) loader
                    .loadClass("org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl").getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setRegisterBuiltinByDefault(boolean registerBuiltinByDefault) {
        ResteasyProviderFactory.registerBuiltinByDefault = registerBuiltinByDefault;
    }

    @SuppressWarnings("unchecked")
    public static boolean isA(Class target, Class type, Set<Class<?>> contracts) {
        if (!type.isAssignableFrom(target))
            return false;
        if (contracts == null || contracts.size() == 0)
            return true;
        for (Class<?> contract : contracts) {
            if (contract.equals(type))
                return true;
        }
        return false;
    }

    public static boolean isA(Object target, Class type, Set<Class<?>> contracts) {
        return isA(target.getClass(), type, contracts);
    }

    protected abstract void registerBuiltin();

    public abstract boolean isRegisterBuiltins();

    public abstract void setRegisterBuiltins(boolean registerBuiltins);

    public abstract InjectorFactory getInjectorFactory();

    public abstract void setInjectorFactory(InjectorFactory injectorFactory);

    public abstract JaxrsInterceptorRegistry<ReaderInterceptor> getServerReaderInterceptorRegistry();

    public abstract JaxrsInterceptorRegistry<WriterInterceptor> getServerWriterInterceptorRegistry();

    public abstract JaxrsInterceptorRegistry<ContainerRequestFilter> getContainerRequestFilterRegistry();

    public abstract JaxrsInterceptorRegistry<ContainerResponseFilter> getContainerResponseFilterRegistry();

    public abstract JaxrsInterceptorRegistry<ReaderInterceptor> getClientReaderInterceptorRegistry();

    public abstract JaxrsInterceptorRegistry<WriterInterceptor> getClientWriterInterceptorRegistry();

    public abstract JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilterRegistry();

    public abstract JaxrsInterceptorRegistry<ClientResponseFilter> getClientResponseFilters();

    public abstract boolean isBuiltinsRegistered();

    public abstract void setBuiltinsRegistered(boolean builtinsRegistered);

    public abstract void addHeaderDelegate(Class clazz, HeaderDelegate header);

    @Deprecated
    public abstract <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType,
            Annotation[] annotations, MediaType mediaType);

    //   public <T> MessageBodyReader<T> getServerMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, RESTEasyTracingLogger tracingLogger);

    public abstract <T> MessageBodyReader<T> getClientMessageBodyReader(Class<T> type, Type genericType,
            Annotation[] annotations, MediaType mediaType);

    //   @Deprecated
    //   private <T> MessageBodyReader<T> resolveMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MediaTypeMap<SortedKey<MessageBodyReader>> availableReaders);

    public abstract List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type);

    public abstract ParamConverter getParamConverter(Class clazz, Type genericType, Annotation[] annotations);

    public abstract <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz);

    public abstract void registerProvider(Class provider);

    /**
     * Convert an object to a string. First try StringConverter then, object.ToString()
     *
     * @param object      object
     * @param clazz       class
     * @param genericType generic type
     * @param annotations array of annotation
     * @return string representation
     */
    public abstract String toString(Object object, Class clazz, Type genericType, Annotation[] annotations);

    /**
     * Checks to see if RuntimeDelegate is a ResteasyProviderFactory
     * If it is, then use that, otherwise use this.
     *
     * @param aClass class of the header
     * @return header delegate
     */
    public abstract HeaderDelegate getHeaderDelegate(Class<?> aClass);

    /**
     * Register a @Provider class. Can be a MessageBodyReader/Writer or ExceptionMapper.
     *
     * @param provider  provider class
     * @param isBuiltin built-in
     */
    public abstract void registerProvider(Class provider, boolean isBuiltin);

    public abstract void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin,
            Map<Class<?>, Integer> contracts);

    /**
     * Register a @Provider object. Can be a MessageBodyReader/Writer or ExceptionMapper.
     *
     * @param provider provider instance
     */
    public abstract void registerProviderInstance(Object provider);

    public abstract void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts,
            Integer priorityOverride, boolean builtIn);

    public abstract <T> AsyncResponseProvider<T> getAsyncResponseProvider(Class<T> type);

    public abstract <T> AsyncClientResponseProvider<T> getAsyncClientResponseProvider(Class<T> type);

    public abstract <T> AsyncStreamProvider<T> getAsyncStreamProvider(Class<T> type);

    public abstract MediaType getConcreteMediaTypeFromMessageBodyWriters(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType);

    public abstract Map<MessageBodyWriter<?>, Class<?>> getPossibleMessageBodyWritersMap(Class type, Type genericType,
            Annotation[] annotations, MediaType accept);

    // use the tracingLogger enabled version please
    @Deprecated
    public abstract <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType,
            Annotation[] annotations, MediaType mediaType);

    //   public <T> MessageBodyWriter<T> getServerMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, RESTEasyTracingLogger tracingLogger);

    public abstract <T> MessageBodyWriter<T> getClientMessageBodyWriter(Class<T> type, Type genericType,
            Annotation[] annotations, MediaType mediaType);

    //   @Deprecated
    //   private <T> MessageBodyWriter<T> resolveMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MediaTypeMap<SortedKey<MessageBodyWriter>> availableWriters);

    /**
     * Create an instance of a class using provider allocation rules of the specification as well as the InjectorFactory
     * only does constructor injection.
     *
     * @param clazz class
     * @param <T>   type
     * @return provider instance of type T
     */
    public abstract <T> T createProviderInstance(Class<? extends T> clazz);

    /**
     * Property and constructor injection using the InjectorFactory.
     *
     * @param clazz class
     * @param <T>   type
     * @return instance of type T
     */
    public abstract <T> T injectedInstance(Class<? extends T> clazz);

    /**
     * Property and constructor injection using the InjectorFactory.
     *
     * @param clazz    class
     * @param request  http request
     * @param response http response
     * @param <T>      type
     * @return instance of type T
     */
    public abstract <T> T injectedInstance(Class<? extends T> clazz, HttpRequest request, HttpResponse response);

    public abstract void injectProperties(Object obj);

    public abstract void injectProperties(Object obj, HttpRequest request, HttpResponse response);

    // Configurable
    public abstract Map<String, Object> getMutableProperties();

    public abstract ResteasyProviderFactory setProperties(Map<String, Object> properties);

    public abstract Collection<Feature> getEnabledFeatures();

    public abstract <I extends RxInvoker> RxInvokerProvider<I> getRxInvokerProvider(Class<I> clazz);

    public abstract RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz);

    public abstract boolean isReactive(Class<?> clazz);

    public abstract ResourceBuilder getResourceBuilder();

    public abstract void initializeClientProviders(ResteasyProviderFactory factory);

    public abstract StatisticsController getStatisticsController();

    /**
     * Returns an exception mapper which handles the generic {@linkplain Throwable throwable} which is typically the
     * default exception mapper.
     *
     * @return an exception mapper which handles a {@link Throwable}
     */
    public ExceptionMapper<Throwable> getThrowableExceptionMapper() {
        return getExceptionMapper(Throwable.class);
    }

    protected abstract boolean isOnServer();
}
